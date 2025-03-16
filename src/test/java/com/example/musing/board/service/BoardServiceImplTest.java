package com.example.musing.board.service;

import com.example.musing.board.entity.Board;
import com.example.musing.board.repository.BoardRepository;
import com.example.musing.music.entity.Music;
import com.example.musing.music.repository.MusicRepository;
import com.example.musing.user.entity.User;
import com.example.musing.user.repository.UserRepository;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

@SpringBootTest
class BoardServiceImplTest {
    /*@Autowired
    private BoardRepository boardRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private MusicRepository musicRepository;

    @Autowired
    private BoardService boardService;

    @Autowired
    private EntityManager entityManager;

    private Board createBoard() {
        return Board.builder()
                .title("테스트 제목")
                .content("테스트 내용")
                .user(createUser())
                .music(createMusic())
                .activeCheck(true)
                .viewCount(0)
                .build();
    }

    private User createUser() {
        User user = User.builder()
                .id("구글 더미 아이디")
                .username("더미")
                .email("dummy@email.com")
                .profile("프로필 사진 링크")
                .build();
        return userRepository.save(user);
    }

    private Music createMusic() {
        Music music = Music.builder()
                .name("제목")
                .songLink("유튜브 링크")
                .thumbNailLink("썸네일 링크")
                .albumName(null)
                .playtime("0:00")
                .build();
        return musicRepository.save(music);
    }

    @Transactional
    @Test
    @DisplayName("음악 추천 게시글에 100명의 동시 접속으로 조회 수의 동시성을 확인한다.")
    void selectDetail() throws InterruptedException {
        // given
        final int THREAD_COUNT = 100;
        Board board = boardRepository.save(createBoard());
        entityManager.flush();
        entityManager.clear();

        ExecutorService executor = Executors.newFixedThreadPool(THREAD_COUNT);
        CountDownLatch latch = new CountDownLatch(THREAD_COUNT);

        // When
        for (int i = 0; i < THREAD_COUNT; i++) {
            executor.execute(() -> {
                try {
                    boardService.selectDetail(board.getId());
                } finally {
                    latch.countDown();
                }
            });
        }
        latch.await(); // 모든 스레드 작업 완료 대기

        // Then
        entityManager.clear(); // 영속성 컨텍스트 초기화
        Board updated = boardRepository.findById(board.getId()).orElseThrow();
        assertThat(updated.getViewCount())
                .as("%d명 동시 접속 시 조회수 검증", THREAD_COUNT)
                .isEqualTo(THREAD_COUNT);
    }*/
}