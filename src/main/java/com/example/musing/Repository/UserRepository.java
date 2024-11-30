
package com.example.musing.Repository;
import com.example.musing.Entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {


    User findByUsername(String username);
}