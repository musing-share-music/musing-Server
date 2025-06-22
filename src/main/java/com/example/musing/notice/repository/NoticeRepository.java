package com.example.musing.notice.repository;

import com.example.musing.notice.entity.Notice;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface NoticeRepository extends JpaRepository<Notice, Long> {
    Optional<Notice> findFirstByActiveCheckTrueOrderByCreatedAtDesc(); //삭제처리 확인 및 최신글 가져오기
    @EntityGraph(attributePaths = {"user"})
    Page<Notice> findAllByActiveCheckTrueOrderByCreatedAtDesc(Pageable pageable);

    @EntityGraph(attributePaths = {"user"})
    @Query("SELECT n FROM Notice n WHERE n.title LIKE %:keyword% AND n.activeCheck = true")
    Page<Notice> findByTitleAndActiveCheckTrue(@Param("keyword") String keyword, Pageable pageable);
}