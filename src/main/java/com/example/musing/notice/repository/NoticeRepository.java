
package com.example.musing.notice.repository;
import com.example.musing.notice.entity.Notice;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface NoticeRepository extends JpaRepository<Notice, Long> {
    Optional<Notice> findFirstByActiveCheckFalseOrderByCreatedAtDesc(); //삭제처리 확인 및 최신글 가져오기

}