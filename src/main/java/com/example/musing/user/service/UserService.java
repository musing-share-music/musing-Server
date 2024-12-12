package com.example.musing.user.service;

import com.example.musing.user.entity.User;

public interface UserService {
    boolean checkInputTags(String userId);
    User findById(String userId);
}
