package com.example.musing.board.repository;

import com.example.musing.board.entity.Board;
import com.example.musing.genre.entity.GerneEnum;
import com.example.musing.mood.entity.MoodEnum;
import com.example.musing.music.entity.Music;
import com.example.musing.reply.entity.Reply;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;


public interface BoardRepository extends JpaRepository<Board, Long>, JpaSpecificationExecutor<Board> {
    @Modifying(flushAutomatically = true, clearAutomatically = true)
    @Query("UPDATE Board b SET " +
            "b.replyCount = b.replyCount -1, " +
            "b.rating = CASE " +
            "WHEN (b.replyCount -1) = 0 THEN 0 " +
            "ELSE ((b.rating * b.replyCount) - :deletedRating) / (b.replyCount -1) " +
            "END " +
            "WHERE b.id = :boardId")
    void updateReplyStatsOnDelete(@Param("boardId") Long boardId, @Param("deletedRating") float deletedRating);

    @Modifying(flushAutomatically = true, clearAutomatically = true)
    @Query("UPDATE Board b SET " +
            "b.rating = ((b.rating * b.replyCount) - :oldRating + :newRating) / b.replyCount " +
            "WHERE b.id = :boardId")
    void updateReplyStatsOnUpdate(@Param("boardId") Long boardId, @Param("oldRating") float oldRating,
                              @Param("newRating") float newRating);

    @Modifying(flushAutomatically = true, clearAutomatically = true)
    @Query("UPDATE Board b SET b.replyCount = b.replyCount +1," +
            " b.rating = (b.rating * b.replyCount + :newRating)/(b.replyCount +1)" +
            " WHERE b.id = :boardId")
    void updateReplyStatsOnCreate(@Param("boardId") Long boardId, @Param("newRating") float newRating);

    @Modifying
    @Query("UPDATE Board b SET b.recommendCount = b.recommendCount + :delta WHERE b.id = :boardId")
    int updateRecommendCount(@Param("boardId") Long boardId, @Param("delta") int delta);

    @Modifying
    @Query(value = "UPDATE Board b SET b.viewCount = b.viewCount + 1 WHERE b.id = :boardId")
    void incrementBoardViewCount(@Param("boardId") Long boardId);

    @EntityGraph(attributePaths = {"music", "user"})
    @Query("SELECT b FROM Board b WHERE b.activeCheck = true AND b.id = :boardId")
    Optional<Board> findById(@Param("boardId") long boardId);

    @EntityGraph(attributePaths = {"user"})
    @Query("SELECT b FROM Board b JOIN FETCH b.music m WHERE b.activeCheck = true AND m IN :musicList")
    List<Board> findBoardsByMusicList(@Param("musicList") List<Music> musicList);

    @EntityGraph(attributePaths = {"music", "user"})
    @Query("SELECT b FROM Board b WHERE b.activeCheck = true ORDER BY b.createdAt DESC")
    Page<Board> findActiveBoardPage(Pageable pageable);

    @EntityGraph(attributePaths = {"music", "user"})
    @Query("SELECT b FROM Board b WHERE b.activeCheck = true AND b.user.username LIKE %:username% ORDER BY b.createdAt DESC")
    Page<Board> findActiveBoardsByUsername(@Param("username") String username, Pageable pageable);

    @EntityGraph(attributePaths = {"music", "user"})
    @Query("SELECT b FROM Board b WHERE b.activeCheck = true AND b.title LIKE %:title% ORDER BY b.createdAt DESC")
    Page<Board> findActiveBoardsByTitle(@Param("title") String title, Pageable pageable);

    @Query("SELECT b FROM Board b " +
            "JOIN FETCH b.music m " +
            "JOIN Artist_Music am ON am.music.id = m.id " +
            "WHERE am.artist.name = :artistName AND b.activeCheck = true " +
            "ORDER BY b.createdAt DESC")
    Page<Board> findActiveBoardsByArtist(@Param("artistName") String artistName, Pageable pageable);

    @Query("SELECT b FROM Board b " +
            "JOIN FETCH b.music m " +
            "JOIN Genre_Music gm ON m.id = gm.music.id " +
            "WHERE gm.genre.genreName = :genreName AND b.activeCheck = true " +
            "ORDER BY b.createdAt DESC")
    Page<Board> findActiveBoardsByGenre(@Param("genreName") GerneEnum genreName, Pageable pageable);

    @Query("SELECT b FROM Board b " +
            "JOIN FETCH b.music m " +
            "JOIN Mood_Music mm ON m.id = mm.music.id " +
            "WHERE mm.mood.moodName = :moodName AND b.activeCheck = true " +
            "ORDER BY b.createdAt DESC")
    Page<Board> findActiveBoardsByMood(@Param("moodName") MoodEnum moodName, Pageable pageable);

    @Query("SELECT DISTINCT b FROM Board b " +
            "JOIN FETCH b.music m " +
            "JOIN FETCH m.artists a " +
            "JOIN FETCH b.user u " +
            "WHERE b.activeCheck = true AND b.id = :boardId")
    Board findBoardWithMusicAndArtist(@Param("boardId") Long boardId);

    @EntityGraph(attributePaths = {"music", "user"})
    @Query("SELECT b FROM Board b WHERE b.activeCheck = true AND b.user.id = :id")
    List<Board> findActiveBoardsByUserId(@Param("id") String id, Pageable pageable);

    @EntityGraph(attributePaths = {"music"})
    @Query("SELECT b FROM Board b WHERE b.activeCheck = true AND b.user.id = :id")
    Page<Board> findActiveBoardsPageByUserId(@Param("id") String id, Pageable pageable);

    @EntityGraph(attributePaths = {"music"})
    @Query("SELECT b FROM Board b WHERE b.activeCheck = true AND b.user.id = :id AND b.title LIKE %:title%")
    Page<Board> findActiveBoardsPageByUserIdAndTitle(@Param("id") String id, @Param("title") String title, Pageable pageable);

    @EntityGraph(attributePaths = {"music", "user"})
    @Query("SELECT b FROM Board b WHERE b.activeCheck = true AND b.user.id = :id AND " +
            "b.music.name LIKE %:musicName%")
    Page<Board> findActiveBoardsPageByUserIdAndMusicName(@Param("id") String id, @Param("musicName") String musicName, Pageable pageable);

    @EntityGraph(attributePaths = {"music", "user"})
    @Query("SELECT b FROM Board b " +
            "JOIN b.music.artists am " +
            "WHERE b.activeCheck = true AND b.user.id = :id " +
            "AND am.artist.name LIKE %:artistName%")
    Page<Board> findActiveBoardsPageByUserIdAndArtistName(@Param("id") String id, @Param("artistName") String artistName, Pageable pageable);
}
