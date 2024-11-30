
package com.example.musing.Repository;
import com.example.musing.Entity.BCategory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BCategoryRepository extends JpaRepository<BCategory, Long> {


    List<BCategory> findByCategory(Long id);
}