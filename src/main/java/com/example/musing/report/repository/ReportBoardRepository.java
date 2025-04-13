package com.example.musing.report.repository;

import com.example.musing.report.entity.ReportBoard;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ReportBoardRepository extends JpaRepository<ReportBoard, Long> {
    @EntityGraph(attributePaths = {"user", "board"})
    Page<ReportBoard> findAllByIsDeleteFalse(Pageable pageable);

    @EntityGraph(attributePaths = {"board"})
    List<ReportBoard> findByBoard_Id(Long boardId);

    @EntityGraph(attributePaths = {"board"})
    @Query("SELECT rb FROM ReportBoard rb JOIN FETCH rb.user u WHERE rb.isDelete = false" +
            " AND u.username LIKE %:keyword% ORDER BY rb.reportDate DESC")
    Page<ReportBoard> findByUsername(@Param("keyword") String keyword, Pageable pageable);

    // title로 검색 (board.title 기준)
    @EntityGraph(attributePaths = {"user"})
    @Query("SELECT rb FROM ReportBoard rb JOIN FETCH rb.board b WHERE rb.isDelete = false" +
            " AND b.title LIKE %:keyword% ORDER BY rb.reportDate DESC")
    Page<ReportBoard> findByTitle(@Param("keyword") String keyword, Pageable pageable);
}
