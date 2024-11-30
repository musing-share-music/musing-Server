
package com.example.musing.Repository;
import com.example.musing.Entity.BCategory;
import com.example.musing.Entity.Board;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BoardRepository extends JpaRepository<Board, Long> {


    List<Board> findById(int id);
}