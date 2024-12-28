package com.example.musing.notice.service;

import com.example.musing.notice.dto.NoticeDto;
import com.example.musing.notice.entity.Notice;
import com.example.musing.notice.repository.NoticeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class NoticeServiceImpl implements NoticeService {

    private final NoticeRepository noticeRepository;

    @Override
    public NoticeDto findNotice() {
        Optional<Notice> notice = noticeRepository.findFirstByActiveCheckFalseOrderByCreatedAtDesc();
        return notice.map(this::entityToDto).orElse(null);
    }

    private NoticeDto entityToDto(Notice notice) {
        return NoticeDto.toDto(notice);
    }
}
