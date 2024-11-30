
package com.example.musing.Repository;
import com.example.musing.Entity.Prefer;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PreferRepository extends JpaRepository<Prefer, Long> {


    Prefer findByUsername(String username);
}