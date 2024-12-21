package com.example.musing.user.service;

import com.example.musing.user.entity.User;
import com.example.musing.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService{
    private final UserRepository userRepository;

    @Override
    @Transactional
    public void saveGenres(String userId, String genres) {
        User user = userRepository.findById(userId).get();
        user.updateGenre(genres);
    }

    @Override
    @Transactional
    public void saveMoods(String userId, String moods) {
        User user = userRepository.findById(userId).get();
        user.updateMood(moods);
    }

    @Override
    @Transactional
    public void saveArtists(String userId, String artists) {
        User user = userRepository.findById(userId).get();
        user.updateArtists(artists);
    }

    @Override
    @Transactional
    public String checkInputTags(String userId) {
        User user = findById(userId);
        if(user.getActivated() == null){
            System.out.println("아직 장르 및 분위기, 아티스트를 고르지 않은게 있어요. ");
            //장르, 분위기 ,아티스트 순서로 갈 예정
            if(user.getLikegenre() ==null){
                return "genre";
            }else if (user.getLikemood() == null){
                return "mood";
            }else{
                return "artists";
            }
        }
        return "pass";
    }

    @Override
    public User findById(String userId) {
        return userRepository.findById(userId).get();
    }
}
