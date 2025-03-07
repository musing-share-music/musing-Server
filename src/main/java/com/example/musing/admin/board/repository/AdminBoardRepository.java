package com.example.musing.admin.board.repository;

import com.example.musing.board.entity.Board;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface AdminBoardRepository extends JpaRepository<Board, Long>, JpaSpecificationExecutor<Board> {
    @Query("SELECT DISTINCT b FROM Board b " +
            "JOIN FETCH b.music m " +
            "JOIN FETCH m.artists a " +
            "JOIN FETCH b.user u " +
            "WHERE b.activeCheck = true AND b.id = :boardId")
    Board findBoardWithMusicAndArtist(@Param("boardId") Long boardId);

    @EntityGraph(attributePaths = {"user"})
    @Query("SELECT b FROM Board b WHERE b.activeCheck = false ORDER BY b.createdAt DESC")
    Page<Board> findDeleteBoardPage(Pageable pageable);

    @EntityGraph(attributePaths = {"user"})
    @Query("SELECT b FROM Board b WHERE b.activeCheck = false AND b.user.username LIKE %:username% ORDER BY b.createdAt DESC")
    Page<Board> findDeleteBoardsByUsername(@Param("username") String username, Pageable pageable);

    @EntityGraph(attributePaths = {"user"})
    @Query("SELECT b FROM Board b WHERE b.activeCheck = false AND b.title LIKE %:title% ORDER BY b.createdAt DESC")
    Page<Board> findDeleteBoardsByTitle(@Param("title") String title, Pageable pageable);

    @EntityGraph(attributePaths = {"music", "user"})
    @Query("SELECT b FROM Board b WHERE b.activeCheck = true AND b.permitRegister = NON_CHECK AND b.id = :boardId")
    Optional<Board> findById(@Param("boardId") long boardId);

    @EntityGraph(attributePaths = {"music", "user"})
    @Query("SELECT b FROM Board b WHERE b.activeCheck = true AND b.permitRegister = NON_CHECK ORDER BY b.createdAt DESC")
    Page<Board> findActiveBoardPage(Pageable pageable);

    @EntityGraph(attributePaths = {"music", "user"})
    @Query("SELECT b FROM Board b WHERE b.activeCheck = true AND b.permitRegister = NON_CHECK AND b.user.username LIKE %:username% ORDER BY b.createdAt DESC")
    Page<Board> findActiveBoardsByUsername(@Param("username") String username, Pageable pageable);

    @EntityGraph(attributePaths = {"music", "user"})
    @Query("SELECT b FROM Board b WHERE b.activeCheck = true AND b.permitRegister = NON_CHECK AND b.title LIKE %:title% ORDER BY b.createdAt DESC")
    Page<Board> findActiveBoardsByTitle(@Param("title") String title, Pageable pageable);
}