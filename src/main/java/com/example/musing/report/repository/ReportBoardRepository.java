package com.example.musing.report.repository;

import com.example.musing.report.entity.ReportBoard;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReportBoardRepository extends JpaRepository<ReportBoard, Long> {
    @EntityGraph(attributePaths = {"user", "board"})
    Page<ReportBoard> findAll(Pageable pageable);

    @EntityGraph(attributePaths = {"board"})
    List<ReportBoard> findByBoard_Id(Long boardId);
}
