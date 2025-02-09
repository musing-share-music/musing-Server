package com.example.musing.notice.controller;

import com.example.musing.common.dto.ResponseDto;
import com.example.musing.notice.dto.NoticeDto;
import com.example.musing.notice.dto.NoticeRequestDto;
import com.example.musing.notice.service.NoticeService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RequestMapping("/musing")
@RequiredArgsConstructor
@RestController
public class NoticeController {
    private final NoticeService noticeService;

    @GetMapping("/notice/list")
    public ResponseDto<Page<NoticeDto>> noticeList(@RequestParam(name = "page", defaultValue = "1") int page) {
        Page<NoticeDto> noticeDtos = noticeService.getNoticeList(page);
        return ResponseDto.of(noticeDtos);
    }

    @GetMapping("/notice")
    public ResponseDto<NoticeDto> notice(@RequestParam int noticeId) {
        NoticeDto noticeDtos = noticeService.getNotice(noticeId);
        return ResponseDto.of(noticeDtos);
    }

    @PostMapping(value = "/notice", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseDto<String> notice(@RequestPart NoticeRequestDto requestDto, @RequestPart List<MultipartFile> files) {
        noticeService.writeNotice(requestDto, files);
        return ResponseDto.of("", "공지사항 작성에 성공했습니다.");
    }
}
