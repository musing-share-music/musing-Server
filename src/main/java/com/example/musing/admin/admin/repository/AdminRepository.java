package com.example.musing.admin.admin.repository;

import com.example.musing.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AdminRepository extends JpaRepository<User, String> {
}
