
package com.example.musing.reply.repository;
import com.example.musing.reply.entity.Reply;
import com.example.musing.user.entity.User;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReplyRepository extends JpaRepository<Reply, Long> {
    Optional<Reply> findByIdAndUser_Email(long replyId, String email);
    Optional<Reply>  findByBoard_IdAndUser_Email(long boardId, String email);

    List<Reply> findByUserId(String userId);

    boolean existsByBoard_IdAndUser_Email(long boardId, String email);
    boolean existsByIdAndUser_Email(long boardId, String email);
    Page<Reply> findByBoard_Id(long boardId, Pageable pageable);
}
