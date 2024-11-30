
package com.example.musing.Repository;
import com.example.musing.Entity.PlayList;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PlayListRepository extends JpaRepository<PlayList, Long> {


    PlayList findByUsername(String username);
}