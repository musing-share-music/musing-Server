package com.example.musing.like_music.service;

import com.example.musing.like_music.entity.Like_Music;
import com.example.musing.like_music.repository.Like_MusicRepository;
import com.example.musing.music.entity.Music;
import com.example.musing.user.entity.User;
import com.example.musing.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class Like_MusicServiceImpl implements Like_MusicService {
    private final Like_MusicRepository likeMusicRepository;

    @Override
    @Transactional(isolation = Isolation.READ_COMMITTED)
    public boolean toggleRecommend(User user, Music music) {
        try {
            Optional<Like_Music> likeMusic = likeMusicRepository.findByUserAndMusic(user, music);
            if (likeMusic.isEmpty()) {
                likeMusic(user, music);
                return true;
            } else {
                canceledLikeMusic(likeMusic.get());
                return false;
            }
        } catch (DataIntegrityViolationException e) {
            //유니크 제약조건 에러, 중복되는 데이터 생성을 막기위해 추가
            return false;
        }
    }

    private void likeMusic(User user, Music music) {
        Like_Music likeMusic = Like_Music.of(user, music);
        likeMusicRepository.save(likeMusic);
    }

    private void canceledLikeMusic(Like_Music likeMusic) {
        likeMusicRepository.delete(likeMusic);
    }
}
