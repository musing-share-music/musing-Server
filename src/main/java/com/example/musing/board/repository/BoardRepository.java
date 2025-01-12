
package com.example.musing.board.repository;

import com.example.musing.board.dto.MusicBoardDto;
import com.example.musing.board.entity.Board;
import com.example.musing.music.entity.Music;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;


public interface BoardRepository extends JpaRepository<Board, Long>, JpaSpecificationExecutor<Board> {
    
    //Specification 복잡한 쿼리 사용에 제약 많아서 QueryDsl 쓰는거로 알아봐야함 <- 우선 JPQL로 구현
    String getCommonQuery =
         "SELECT b FROM Board b " + "JOIN FETCH b.music m " +
                "JOIN FETCH m.artist a " + "WHERE b.activeCheck = false ";
    String getDescQuery ="ORDER BY b.createdAt DESC";

    Page<Board> findByUser_Id(Specification<Board> spec, Pageable pageable, String id);

    @Query("SELECT b FROM Board b JOIN FETCH b.music m WHERE m IN :musicList")
    List<Board> findBoardsByMusicList(@Param("musicList") List<Music> musicList);


    @Query(value = getCommonQuery + getDescQuery)
    Page<Board> findActiveBoardPage(Pageable pageable);

    @Query(value = getCommonQuery + "AND b.user.username LIKE %:username% " +
            getDescQuery)
    Page<Board> findActiveBoardsByUsername(@Param("username") String username, Pageable pageable);

    @Query(value = getCommonQuery + "AND b.title LIKE %:title% " +
            getDescQuery)
    Page<Board> findActiveBoardsByTitle(@Param("title") String title, Pageable pageable);

    @Query(value = getCommonQuery + "AND m.artist.name LIKE %:artist% " +
            getDescQuery)
    Page<Board> findActiveBoardsByArtist(@Param("artist") String artist, Pageable pageable);

    @Query(value = getCommonQuery + "AND m.genre LIKE %:genre% " +
            getDescQuery)
    Page<Board> findActiveBoardsByGenre(@Param("genre") String genre, Pageable pageable);

    @Query(value = getCommonQuery + "AND m.mood LIKE %:mood% " +
            getDescQuery)
    Page<Board> findActiveBoardsByMood(@Param("mood") String mood, Pageable pageable);
}