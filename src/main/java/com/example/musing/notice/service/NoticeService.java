package com.example.musing.notice.service;

import com.example.musing.notice.dto.NoticeDto;
import org.springframework.data.domain.Page;

public interface NoticeService {
    NoticeDto findNotice();

    Page<NoticeDto> getNoticeList(int page);

    NoticeDto getNotice(long noticeId);
}
