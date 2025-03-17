package com.example.musing.reply.service;

import com.example.musing.board.entity.Board;
import com.example.musing.board.repository.BoardRepository;
import com.example.musing.exception.CustomException;
import com.example.musing.reply.dto.ReplyRequestDto;
import com.example.musing.reply.dto.ReplyResponseDto;
import com.example.musing.reply.entity.Reply;
import com.example.musing.reply.repository.ReplyRepository;
import com.example.musing.user.entity.User;
import com.example.musing.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static com.example.musing.exception.ErrorCode.*;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class ReplyServiceImpl implements ReplyService {

    private static int PAGE_SIZE = 10;
    private final ReplyRepository replyRepository;
    private final UserRepository userRepository;
    private final BoardRepository boardRepository;

    public Reply findByReplyId(long replyId) {
        return replyRepository.findById(replyId).orElseThrow(() -> new CustomException(NOT_FOUND_REPLY));
    }

    @Transactional
    @Override
    public void writeReply(long boardId, ReplyRequestDto replyDto) {
        User user = getUser();

        Board board = boardRepository.findById(boardId)
                .orElseThrow(() -> new CustomException(NOT_FOUND_BOARD));

        boolean replyExist = replyRepository.existsByBoard_IdAndUser(boardId, user);

        if (replyExist) {
            throw new CustomException(EXIST_REPLY);
        }

        Reply reply = Reply.from(replyDto, user, board);

        replyRepository.save(reply);
    }

    @Override
    public ReplyResponseDto.ReplyDto findMyReplyByBoardId(long boardId) {
        Optional<Reply> reply = replyRepository.findByBoard_IdAndUser(boardId, getUser());
        return reply.map(ReplyResponseDto.ReplyDto::from).orElse(null); //없으면 null리턴
    }

    @Override
    public ReplyResponseDto.ReplyDto findMyReplyByReplyId(long replyId) {
        Reply reply = replyRepository.findByIdAndUser(replyId, getUser())
                .orElseThrow(() -> new CustomException(NOT_FOUND_REPLY));

        return ReplyResponseDto.ReplyDto.from(reply);
    }

    @Transactional
    @Override
    public void modifyReply(long replyId, ReplyRequestDto replyDto) {
        Reply reply = replyRepository.findByBoard_IdAndUser(replyId, getUser())
                .orElseThrow(() -> new CustomException(NOT_FOUND_REPLY));

        reply.updateReply(replyDto.starScore(), replyDto.content());
    }

    @Transactional
    @Override
    public void deleteReply(long replyId) {
        if (replyRepository.existsByIdAndUser(replyId, getUser())) {
            throw new CustomException(NOT_MATCHED_REPLY_AND_USER);
        }
        replyRepository.deleteById(replyId);
    }

    @Override
    public Page<ReplyResponseDto.ReplyDto> findReplies(long boardId, int page, String sortType, String sort) {
        if (page < 1) { // 잘못된 접근으로 throw할때 쿼리문 실행을 안하기 위해 나눠서 체크
            throw new CustomException(BAD_REQUEST_REPLY_PAGE);
        }

        Pageable pageable = createPageable(page - 1, sort, sortType);

        Page<Reply> pageReply = sortReplys(boardId, sortType, pageable);
        int totalPages = pageReply.getTotalPages();

        if (page - 1 > totalPages) {
            throw new CustomException(BAD_REQUEST_REPLY_PAGE);
        }

        return pageReply.map(ReplyResponseDto.ReplyDto::from);
    }

    private Page<Reply> sortReplys(long boardId, String sortType, Pageable pageable) {
        switch (sortType) {
            case "date", "starScore":
                return replyRepository.findByBoard_Id(boardId, pageable);
            case "onlyReview":
                return replyRepository.findByBoardIdWithContent(boardId, pageable);
            default:
                throw new CustomException(NOT_FOUND_KEYWORD);
        }
    }

    private Pageable createPageable(int page, String sort, String sortType) {
        Sort.Direction direction = (sort != null && sort.equals("ASC")) ? Sort.Direction.ASC : Sort.Direction.DESC;
        String properties = (sortType.equals("starScore")? "starScore" : "createdAt");
        return PageRequest.of(page, PAGE_SIZE, Sort.by(direction, properties));
    }

    private User getUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        //유저 정보 확인 및 로그인 상태 여부 확인
        if (authentication == null || authentication.getName().equals("anonymousUser")) {
            throw new CustomException(NOT_FOUND_USER);
        }

        return userRepository.findById(authentication.getName())
                .orElseThrow(() -> new CustomException(NOT_FOUND_USER));
    }
}