package com.example.musing.notice.service;

import com.example.musing.common.utils.S3.AWS_S3_Util;
import com.example.musing.exception.CustomException;
import com.example.musing.notice.dto.NoticeDto;
import com.example.musing.notice.dto.NoticeRequestDto;
import com.example.musing.notice.entity.Notice;
import com.example.musing.notice.repository.NoticeRepository;
import com.example.musing.user.entity.Role;
import com.example.musing.user.entity.User;
import com.example.musing.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;

import static com.example.musing.exception.ErrorCode.*;

@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class NoticeServiceImpl implements NoticeService {

    private static int PAGESIZE = 10;
    private final NoticeRepository noticeRepository;
    private final UserRepository userRepository;
    private final AWS_S3_Util awsS3Util;

    private static String S3BUCKETURL = "notice";

    @Override
    public NoticeDto findNotice() {
        Optional<Notice> notice = noticeRepository.findFirstByActiveCheckTrueOrderByCreatedAtDesc();
        return notice.map(this::entityToDto).orElse(null);
    }

    @Override
    public Page<NoticeDto> getNoticeList(int page) {
        if (page < 1) { // 잘못된 접근으로 throw할때 쿼리문 실행을 안하기 위해 나눠서 체크
            throw new CustomException(BAD_REQUEST_REPLY_PAGE);
        }

        Pageable pageable = PageRequest.of(page - 1, PAGESIZE);
        Page<Notice> notices = noticeRepository.findAllByActiveCheckTrue(pageable);

        int totalPages = notices.getTotalPages();
        if (page - 1 > totalPages) {
            throw new CustomException(BAD_REQUEST_REPLY_PAGE);
        }
        return notices.map(this::entityToDto);
    }

    @Override
    public Page<NoticeDto> search(int page, String keyword) {
        if (page < 1) { // 잘못된 접근으로 throw할때 쿼리문 실행을 안하기 위해 나눠서 체크
            throw new CustomException(BAD_REQUEST_REPLY_PAGE);
        }

        Pageable pageable = PageRequest.of(page - 1, PAGESIZE);

        Page<Notice> notices = noticeRepository.findByTitleAndActiveCheckTrue(keyword, pageable);

        int totalPages = notices.getTotalPages();

        if (page - 1 > totalPages) {
            throw new CustomException(BAD_REQUEST_BOARD_PAGE);
        }

        return notices.map(this::entityToDto);
    }
    @Override
    public NoticeDto getNotice(long noticeId) {
        Notice notice = noticeRepository.findById(noticeId).orElseThrow(() -> new CustomException(NOT_FOUND_NOTICE));
        return entityToDto(notice);
    }

    @Override
    @Transactional
    public void writeNotice(NoticeRequestDto requestDto, List<MultipartFile> files) {
        Notice notice = Notice.builder()
                .title(requestDto.title())
                .content(requestDto.content())
                .user(getUser())
                .images(uploadImages(files) == null ? null : uploadImages(files).toString())
                .build();
        noticeRepository.save(notice);
    }

    @Override
    @Transactional
    public void modifyNotice(long noticeId, NoticeRequestDto requestDto,
                             List<String> deleteFileLinks, List<MultipartFile> newFiles) {
        Notice notice = noticeRepository.findById(noticeId).orElseThrow(() -> new CustomException(NOT_FOUND_NOTICE));

        List<String> images = notice.getImageList();

        if (images == null) {
            images = new ArrayList<>();
        }

        if (deleteFileLinks != null&& !deleteFileLinks.isEmpty()) {
            images.removeIf(imageUrl -> {
                if (deleteFileLinks.contains(imageUrl)) {
                    String filename = extractFilename(imageUrl);
                    awsS3Util.deleteFile("notice", filename);
                    return true;
                }
                return false;
            });
        }

        if (newFiles != null && !newFiles.isEmpty()) {
            List<String> newImageUrls = uploadImages(newFiles);
            images.addAll(newImageUrls);

        }

        notice.updateNotice(requestDto.title(), requestDto.content(),
                images.isEmpty() ? null : images.toString());
    }

    @Override
    @Transactional
    public void deleteNotice(long noticeId) {
        if(getUser().getRole().equals(Role.ADMIN)){
            noticeRepository.findById(noticeId)
                    .orElseThrow(() -> new CustomException(NOT_FOUND_NOTICE))
                    .softDelete();
        } else {
            throw new CustomException(INVALID_AUTHORITY);
        }
    }

    private String extractFilename(String imageUrl) {
        return imageUrl.substring(imageUrl.lastIndexOf("/") + 1);
    }

    private List<String> uploadImages(List<MultipartFile> files) {
        if (files == null || files.isEmpty()) {
            return null;
        }

        List<String> urlList = new ArrayList<>();

        for (MultipartFile file : files) {
            // 이미지 파일명 생성
            UUID uuid = UUID.randomUUID();
            String fileName = uuid.toString() + "_" + file.getOriginalFilename().lastIndexOf(".");//uuid+확장자명으로 이름지정

            String imageUrl = awsS3Util.uploadImageToS3(file,S3BUCKETURL,fileName);//파일 업로드

            urlList.add(imageUrl);
        }
        return urlList;
    }

    private User getUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return userRepository.findById(authentication.getName()).orElseThrow(() -> new CustomException(NOT_FOUND_USER));
    }

    private NoticeDto entityToDto(Notice notice) {
        return NoticeDto.from(notice);
    }
}
