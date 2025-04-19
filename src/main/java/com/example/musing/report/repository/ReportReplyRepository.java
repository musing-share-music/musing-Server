package com.example.musing.report.repository;

import com.example.musing.report.entity.ReportReply;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ReportReplyRepository extends JpaRepository<ReportReply, Long> {
    @EntityGraph(attributePaths = {"user", "reply"})
    Page<ReportReply> findAllByIsDeleteFalse(Pageable pageable);

    @EntityGraph(attributePaths = {"reply"})
    List<ReportReply> findByReply_Id(Long replyId);

    @EntityGraph(attributePaths = {"reply"})
    @Query("SELECT rr FROM ReportReply rr JOIN FETCH rr.user u WHERE rr.isDelete = false" +
            " AND u.username LIKE %:keyword% ORDER BY rr.reportDate DESC")
    Page<ReportReply> findByUsername(@Param("keyword") String keyword, Pageable pageable);

    // content로 검색 (ReportReply.content 기준)
    @EntityGraph(attributePaths = {"user"})
    @Query("SELECT rr FROM ReportReply rr JOIN FETCH rr.reply r WHERE rr.isDelete = false" +
            " AND r.content LIKE %:keyword% ORDER BY rr.reportDate DESC")
    Page<ReportReply> findByContent(@Param("keyword") String keyword, Pageable pageable);
}
