package com.example.musing.board.repository;

import com.example.musing.board.entity.Board;
import com.example.musing.music.entity.Music;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;


public interface BoardRepository extends JpaRepository<Board, Long>, JpaSpecificationExecutor<Board> {

    //Specification 복잡한 쿼리 사용에 제약 많아서 QueryDsl 쓰는거로 알아봐야함 <- 우선 JPQL로 구현
    String getFetchBoardQuery =
            "SELECT b FROM Board b " + "JOIN FETCH b.music m " +
                    "JOIN FETCH m.artist a ";
    String getFetchUserQuery =
            "JOIN FETCH b.user u ";
    String getActiveCheckQuery = "WHERE b.activeCheck = false ";
    String getDescQuery = "ORDER BY b.createdAt DESC";

    @Query(getFetchBoardQuery + getFetchUserQuery + getActiveCheckQuery + "AND b.id = :boardId")
    Optional<Board> findById(@Param("boardId") long boardId);

    @Query("SELECT b FROM Board b JOIN FETCH b.music m WHERE m IN :musicList")
    List<Board> findBoardsByMusicList(@Param("musicList") List<Music> musicList);


    @Query(value = getFetchBoardQuery + getActiveCheckQuery + getDescQuery)
    Page<Board> findActiveBoardPage(Pageable pageable);

    @Query(value = getFetchBoardQuery + getActiveCheckQuery + "AND b.user.username LIKE %:username% " +
            getDescQuery)
    Page<Board> findActiveBoardsByUsername(@Param("username") String username, Pageable pageable);

    @Query(value = getFetchBoardQuery + getActiveCheckQuery + "AND b.title LIKE %:title% " +
            getDescQuery)
    Page<Board> findActiveBoardsByTitle(@Param("title") String title, Pageable pageable);

    @Query(value = getFetchBoardQuery + getActiveCheckQuery + "AND m.artist.name LIKE %:artist% " +
            getDescQuery)
    Page<Board> findActiveBoardsByArtist(@Param("artist") String artist, Pageable pageable);

    @Query(value = getFetchBoardQuery + getActiveCheckQuery + "AND m.genre LIKE %:genre% " +
            getDescQuery)
    Page<Board> findActiveBoardsByGenre(@Param("genre") String genre, Pageable pageable);

    @Query(value = getFetchBoardQuery + getActiveCheckQuery + "AND m.mood LIKE %:mood% " +
            getDescQuery)
    Page<Board> findActiveBoardsByMood(@Param("mood") String mood, Pageable pageable);
}