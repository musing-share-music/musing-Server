package com.example.musing.admin.board.repository;

import com.example.musing.board.entity.Board;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface AdminBoardRepository extends JpaRepository<Board, Long>, JpaSpecificationExecutor<Board> {
    @EntityGraph(attributePaths = {"music", "user"})
    @Query("SELECT b FROM Board b WHERE b.activeCheck = true AND b.id = :boardId")
    Optional<Board> findById(@Param("boardId") long boardId);
}