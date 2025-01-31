package com.example.musing.report.repository;

import com.example.musing.report.entity.ReportReply;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReportReplyRepository extends JpaRepository<ReportReply, Long> {
    @EntityGraph(attributePaths = {"user", "reply"})
    Page<ReportReply> findAll(Pageable pageable);

    @EntityGraph(attributePaths = {"reply"})
    List<ReportReply> findByReply_Id(Long replyId);
}
