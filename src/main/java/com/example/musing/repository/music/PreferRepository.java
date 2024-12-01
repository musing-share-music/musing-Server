
package com.example.musing.repository.music;
import com.example.musing.entity.music.Prefer;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PreferRepository extends JpaRepository<Prefer, Long> {


    Prefer findByUsername(String username);
}