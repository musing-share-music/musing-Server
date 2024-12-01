
package com.example.musing.repository.Board;
import com.example.musing.entity.Board.Reply;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReplyRepository extends JpaRepository<Reply, Long> {


    Reply findByUsername(String username);
}