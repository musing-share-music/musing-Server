
package com.example.musing.repository.music;
import com.example.musing.entity.music.HashTag;
import com.example.musing.entity.music.Prefer_Music;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PreferMusicRepository extends JpaRepository<Prefer_Music, Long> {



}