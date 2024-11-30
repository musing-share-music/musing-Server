
package com.example.musing.Repository;
import com.example.musing.Entity.Board;
import com.example.musing.Entity.HashTag;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface HashTagRepository extends JpaRepository<HashTag, Long> {


    List<HashTag> findById(int id);
}
