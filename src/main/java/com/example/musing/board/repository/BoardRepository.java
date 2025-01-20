package com.example.musing.board.repository;

import com.example.musing.board.entity.Board;
import com.example.musing.music.entity.Music;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
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
    String getActiveCheckQuery = "WHERE b.activeCheck = false ";
    String getDescQuery = "ORDER BY b.createdAt DESC";

    @EntityGraph(attributePaths = {"music", "user"})
    @Query("SELECT b FROM Board b WHERE b.activeCheck = false AND b.id = :boardId")
    Optional<Board> findById(@Param("boardId") long boardId);

    @EntityGraph(attributePaths = {"user"})
    @Query("SELECT b FROM Board b JOIN FETCH b.music m WHERE b.activeCheck = false AND m IN :musicList")
    List<Board> findBoardsByMusicList(@Param("musicList") List<Music> musicList);

    @EntityGraph(attributePaths = {"music", "user"})
    @Query("SELECT b FROM Board b WHERE b.activeCheck = false ORDER BY b.createdAt DESC")
    Page<Board> findActiveBoardPage(Pageable pageable);

    @EntityGraph(attributePaths = {"music", "user"})
    @Query("SELECT b FROM Board b WHERE b.activeCheck = false AND b.user.username LIKE %:username% ORDER BY b.createdAt DESC")
    Page<Board> findActiveBoardsByUsername(@Param("username") String username, Pageable pageable);

    @EntityGraph(attributePaths = {"music", "user"})
    @Query("SELECT b FROM Board b WHERE b.activeCheck = false AND b.title LIKE %:title% ORDER BY b.createdAt DESC")
    Page<Board> findActiveBoardsByTitle(@Param("title") String title, Pageable pageable);

    @Query("SELECT b FROM Board b " +
            "JOIN FETCH b.music m " +
            "JOIN Artist_Music am ON am.music.id = m.id " +
            "WHERE am.artist.name = :artistName AND b.activeCheck = false " +
            "ORDER BY b.createdAt DESC")
    Page<Board> findActiveBoardsByArtist(@Param("artistName") String artistName, Pageable pageable);

/*    @Query("SELECT b FROM Board b " +
            "JOIN b.music m " +
            "JOIN Genre_Music gm ON m.id = gm.music.id " +
            "JOIN Genre g ON gm.genre.id = g.id " +
            "WHERE g.genreName = :genre AND b.activeCheck = false")*/
    @Query("SELECT b FROM Board b " +
            "JOIN FETCH b.music m " +
            "JOIN Genre_Music gm ON m.id = gm.music.id " +
            "WHERE gm.genre.genreName = :genreName AND b.activeCheck = false " +
            "ORDER BY b.createdAt DESC")
    Page<Board> findActiveBoardsByGenre(@Param("genreName") String genreName, Pageable pageable);

/*    @Query("SELECT b FROM Board b " +
            "JOIN b.music m " +
            "JOIN Mood_Music mm ON m.id = mm.music.id " +
            "JOIN Mood mo ON mm.mood.id = mo.id " +
            "WHERE mo.moodName = :mood AND b.activeCheck = false")*/
@Query("SELECT b FROM Board b " +
        "JOIN FETCH b.music m " +
        "JOIN Mood_Music mm ON m.id = mm.music.id " +
        "WHERE mm.mood.moodName = :moodName AND b.activeCheck = false " +
        "ORDER BY b.createdAt DESC")
    Page<Board> findActiveBoardsByMood(@Param("moodName") String moodName, Pageable pageable);
}
