package com.example.musing.alarm.repository;

import com.example.musing.alarm.entity.Alarm;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface AlarmRepository extends JpaRepository<Alarm, Long> {
    @EntityGraph(attributePaths = {"user"})
    @Query("SELECT a FROM Alarm a JOIN FETCH a.user WHERE a.isRead = false AND a.user.id = :userId")
    List<Alarm> findByUserId(@Param("userId") String userId);
}
