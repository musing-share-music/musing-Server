package com.example.musing.notice.controller;

import com.example.musing.common.dto.ResponseDto;
import com.example.musing.notice.dto.NoticeDto;
import com.example.musing.notice.dto.NoticeRequestDto;
import com.example.musing.notice.service.NoticeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
@Tag(name = "공지사항 API")
@RequestMapping("/musing")
@RequiredArgsConstructor
@RestController
public class NoticeController {
    private final NoticeService noticeService;

    @Operation(summary = "공지사항 게시판 리스트에 제목 검색",
            description = "검색 기능에는 제목만을 키워드로 두고있어 별도의 파라미터를 두지 않았습니다.<br>+" +
                    "keyword는 검색창에 입력한 단어입니다.")
    @GetMapping("/notice/list/search")
    public ResponseDto<Page<NoticeDto>> getBoardsByKeyword(
            @RequestParam(name = "page", defaultValue = "1") int page,
            @RequestParam(name = "keyword") String keyword) {
        Page<NoticeDto> noticeDtos = noticeService.search(page, keyword);
        return ResponseDto.of(noticeDtos);
    }

    @Operation(summary = "공지사항 게시판 리스트 조회")
    @GetMapping("/notice/list")
    public ResponseDto<Page<NoticeDto>> noticeList(@RequestParam(name = "page", defaultValue = "1") int page) {
        Page<NoticeDto> noticeDtos = noticeService.getNoticeList(page);
        return ResponseDto.of(noticeDtos);
    }

    @Operation(summary = "공지사항 게시판 상세 페이지 조회")
    @GetMapping("/notice")
    public ResponseDto<NoticeDto> notice(@RequestParam int noticeId) {
        NoticeDto noticeDtos = noticeService.getNotice(noticeId);
        return ResponseDto.of(noticeDtos);
    }

    @Operation(summary = "공지사항 게시글 작성",
            description = "공지사항 게시글 작성으로 Dto를 title, content를 포함하여 JSON 타입('application/json')으로 받습니다<br>" +
                    "이미지를 업로드할 수 있도록 이 API는 'Content-Type': 'multipart/form-data'입니다.")
    @PostMapping(value = "/admin/notice", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseDto<String> createNotice(@RequestPart NoticeRequestDto requestDto,
                                            @RequestPart(required = false) List<MultipartFile> files) {
        noticeService.writeNotice(requestDto, files);
        return ResponseDto.of("", "공지사항 작성에 성공했습니다.");
    }

    @Operation(summary = "공지사항 게시글 수정",
            description = "공지사항 게시글 작성과 동일하게 Dto를 title, content를 포함하여 JSON 타입('application/json')으로 받습니다<br>" +
                    "이미지를 업로드할 수 있도록 이 API는 'Content-Type': 'multipart/form-data'입니다." +
                    "이미 업로드한 사진을 삭제할 경우 url주소를 그대로 deleteFileLinks에 넣어주면 됩니다." +
                    "(※ 스웨거에서 deleteFileLinks에 값을 넣어 테스트 할 경우 List에 맞게 처음과 마지막에 '[', ']'를 따로 붙여주어야합니다)")
    @PutMapping(value = "/admin/notice/{noticeId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseDto<String> updateNotice(@PathVariable long noticeId, @RequestPart NoticeRequestDto requestDto,
                                            @RequestPart(required = false) List<String> deleteFileLinks,
                                            @RequestPart(required = false) List<MultipartFile> files) {
        noticeService.modifyNotice(noticeId, requestDto, deleteFileLinks, files);
        return ResponseDto.of("", "공지사항 수정에 성공했습니다.");
    }

    @Operation(summary = "공지사항 게시글 삭제",
            description = "softDelete방식이기 때문에 put요청을 사용합니다.")
    @PutMapping(value = "/admin/notice/remove/{noticeId}")
    public ResponseDto<String> deleteNotice(@PathVariable long noticeId) {
        noticeService.deleteNotice(noticeId);
        return ResponseDto.of("", "공지사항 삭제에 성공했습니다.");
    }
}
