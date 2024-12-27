package com.example.musing.notice.service;

import com.example.musing.notice.dto.NoticeDto;
import com.example.musing.notice.entity.Notice;
import com.example.musing.notice.repository.NoticeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class NoticeServiceImpl implements NoticeService {

    private final NoticeRepository noticeRepository;

    @Override
    @Transactional
    public NoticeDto entityToDto(Notice notice) {
        return NoticeDto.toDto(notice);
    }

    @Override
    @Transactional
    public Optional<Notice> findNotice() {
        return noticeRepository.findFirstByActiveCheckFalseOrderByCreatedAtDesc();
    }
}
