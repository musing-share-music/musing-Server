package com.example.musing.notice.service;

import com.example.musing.exception.CustomException;
import com.example.musing.notice.dto.NoticeDto;
import com.example.musing.notice.entity.Notice;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

import static com.example.musing.exception.ErrorCode.BAD_REQUEST_REPLY_PAGE;
import static com.example.musing.exception.ErrorCode.NOT_FOUND_NOTICE;

public interface NoticeService {
    NoticeDto findNotice();
    Page<NoticeDto> getNoticeList(int page);
    NoticeDto getNotice(long noticeId);
}
