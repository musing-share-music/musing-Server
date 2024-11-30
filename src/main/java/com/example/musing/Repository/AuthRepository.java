package com.example.musing.Repository;

import com.example.musing.Entity.Auth;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AuthRepository extends JpaRepository<Auth, Long> {

    List<Auth> findById(long id);

}