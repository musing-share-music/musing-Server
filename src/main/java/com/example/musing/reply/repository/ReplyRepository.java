
package com.example.musing.reply.repository;
import com.example.musing.board.entity.Board;
import com.example.musing.reply.entity.Reply;
import com.example.musing.user.entity.User;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ReplyRepository extends JpaRepository<Reply, Long> {
    Optional<Reply> findByIdAndUser_Email(long replyId, String email);
    Optional<Reply>  findByBoard_IdAndUser_Email(long boardId, String email);

    @EntityGraph(attributePaths = {"board"})
    List<Reply> findByUserId(String userId, Pageable pageable);

    @EntityGraph(attributePaths = {"board"})
    @Query("SELECT r FROM Reply r WHERE r.user.id = :id")
    Page<Reply> findPageByUserId(@Param("id") String id, Pageable pageable);

    @EntityGraph(attributePaths = {"board"})
    @Query("SELECT r FROM Reply r WHERE r.user.id = :id AND r.content LIKE %:content%")
    Page<Reply> findPageByUserIdAndTitle(@Param("id") String id, @Param("content") String content, Pageable pageable);

    boolean existsByBoard_IdAndUser_Email(long boardId, String email);
    boolean existsByIdAndUser_Email(long boardId, String email);
    Page<Reply> findByBoard_Id(long boardId, Pageable pageable);
}
