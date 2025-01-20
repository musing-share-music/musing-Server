package com.example.musing.mood.service;

import com.example.musing.genre.dto.GenreDto;
import com.example.musing.genre.entity.Genre;
import com.example.musing.mood.dto.MoodDto;
import com.example.musing.mood.entity.Mood;
import com.example.musing.mood.repository.MoodRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class MoodServiceImpl implements MoodService{
    private final MoodRepository moodRepository;
    @Override
    public List<MoodDto> getMoodDtos(){
        return findAll().stream().map(MoodDto::toDto).toList();
    }

    private List<Mood> findAll(){
        return moodRepository.findAll();
    }
}
