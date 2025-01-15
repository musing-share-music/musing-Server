package com.example.musing.mood.repository;

import com.example.musing.mood.entity.Mood;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MoodRepository extends JpaRepository<Mood, Long> {
}
