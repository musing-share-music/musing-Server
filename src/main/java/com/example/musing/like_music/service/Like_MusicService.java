package com.example.musing.like_music.service;

import com.example.musing.music.entity.Music;
import com.example.musing.user.entity.User;

public interface Like_MusicService {
    boolean toggleRecommend(User user, Music music);
}
