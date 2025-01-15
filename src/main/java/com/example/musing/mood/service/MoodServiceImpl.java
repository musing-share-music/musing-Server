package com.example.musing.mood.service;

import com.example.musing.mood.repository.MoodRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class MoodServiceImpl implements MoodService{
    private final MoodRepository moodRepository;

}
