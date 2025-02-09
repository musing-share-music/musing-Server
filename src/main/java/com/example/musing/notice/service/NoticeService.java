package com.example.musing.notice.service;

import com.example.musing.notice.dto.NoticeDto;
import com.example.musing.notice.dto.NoticeRequestDto;
import org.springframework.data.domain.Page;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface NoticeService {
    NoticeDto findNotice();

    Page<NoticeDto> getNoticeList(int page);

    NoticeDto getNotice(long noticeId);

    void writeNotice(NoticeRequestDto requestDto, List<MultipartFile> files);
}
