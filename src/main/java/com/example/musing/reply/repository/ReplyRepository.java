
package com.example.musing.reply.repository;
import com.example.musing.reply.entity.Reply;
import com.example.musing.user.entity.User;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReplyRepository extends JpaRepository<Reply, Long> {
    Optional<Reply> findByIdAndUser_Email(long replyId, String email);
}
