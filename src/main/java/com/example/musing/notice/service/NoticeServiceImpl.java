package com.example.musing.notice.service;

import com.example.musing.artist.dto.ArtistDto;
import com.example.musing.board.dto.BoardListRequestDto;
import com.example.musing.exception.CustomException;
import com.example.musing.genre.dto.GenreDto;
import com.example.musing.mood.dto.MoodDto;
import com.example.musing.notice.dto.NoticeDto;
import com.example.musing.notice.entity.Notice;
import com.example.musing.notice.repository.NoticeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

import static com.example.musing.exception.ErrorCode.BAD_REQUEST_REPLY_PAGE;
import static com.example.musing.exception.ErrorCode.NOT_FOUND_NOTICE;

@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class NoticeServiceImpl implements NoticeService {

    private final NoticeRepository noticeRepository;
    private static int PAGESIZE = 10;
    @Override
    public NoticeDto findNotice() {
        Optional<Notice> notice = noticeRepository.findFirstByActiveCheckTrueOrderByCreatedAtDesc();
        return notice.map(this::entityToDto).orElse(null);
    }

    public Page<NoticeDto> getNoticeList(int page) {
        if (page < 1) { // 잘못된 접근으로 throw할때 쿼리문 실행을 안하기 위해 나눠서 체크
            throw new CustomException(BAD_REQUEST_REPLY_PAGE);
        }

        Pageable pageable = PageRequest.of(page - 1, PAGESIZE);
        Page<Notice> notices = noticeRepository.findAll(pageable);

        int totalPages = notices.getTotalPages();
        if (page - 1 > totalPages) {
            throw new CustomException(BAD_REQUEST_REPLY_PAGE);
        }
        return notices.map(this::entityToDto);
    }

    public NoticeDto getNotice(long noticeId){
        Notice notice = noticeRepository.findById(noticeId).orElseThrow(() -> new CustomException(NOT_FOUND_NOTICE));
        return entityToDto(notice);
    }

    private NoticeDto entityToDto(Notice notice) {
        return NoticeDto.toDto(notice);
    }
}
