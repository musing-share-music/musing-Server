
package com.example.musing.boardCategory.repository;
import com.example.musing.boardCategory.entity.BCategory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BCategoryRepository extends JpaRepository<BCategory, Long> {

}