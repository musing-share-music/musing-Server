
package com.example.musing.reply.repository;
import com.example.musing.reply.entity.Reply;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReplyRepository extends JpaRepository<Reply, Long> {


    Reply findByUsername(String username);
}