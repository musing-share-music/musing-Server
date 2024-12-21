package com.example.musing.notice.service;

import com.example.musing.notice.dto.NoticeDto;
import com.example.musing.notice.entity.Notice;

import java.util.Optional;

public interface NoticeService {
    NoticeDto entityToDto(Notice notice);
    Optional<Notice> findNotice();
}
