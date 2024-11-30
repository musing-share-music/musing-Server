
package com.example.musing.Repository;
import com.example.musing.Entity.Reply;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReplyRepository extends JpaRepository<Reply, Long> {


    Reply findByUsername(String username);
}