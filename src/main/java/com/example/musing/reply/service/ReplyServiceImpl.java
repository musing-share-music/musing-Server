package com.example.musing.reply.service;

import com.example.musing.alarm.event.SentAlarmEvent;
import com.example.musing.board.dto.BoardReplyDto;
import com.example.musing.board.entity.Board;
import com.example.musing.board.event.UpdateReplyStateEvent;
import com.example.musing.board.repository.BoardRepository;
import com.example.musing.exception.CustomException;
import com.example.musing.reply.dto.ReplyRequestDto;
import com.example.musing.reply.dto.ReplyResponseDto;
import com.example.musing.reply.entity.Reply;
import com.example.musing.reply.repository.ReplyRepository;
import com.example.musing.user.entity.User;
import com.example.musing.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static com.example.musing.alarm.entity.AlarmType.APPLYPERMIT;
import static com.example.musing.board.event.CommitState.*;
import static com.example.musing.exception.ErrorCode.*;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class ReplyServiceImpl implements ReplyService {

    private static final int PAGE_SIZE = 10;
    private static final String ALARM_API_URL = "/musing/board/selectDetail?boardId=";
    private final ReplyRepository replyRepository;
    private final UserRepository userRepository;
    private final BoardRepository boardRepository;
    private final ApplicationEventPublisher publisher;

    @Override
    public Reply findByReplyId(long replyId) {
        return replyRepository.findById(replyId).orElseThrow(() -> new CustomException(NOT_FOUND_REPLY));
    }

    @Transactional
    @Override
    public ReplyResponseDto.ReplyAndUpdatedBoardDto writeReply(long boardId, ReplyRequestDto replyDto) {
        User user = getUser();

        Board board = boardRepository.findById(boardId)
                .orElseThrow(() -> new CustomException(NOT_FOUND_BOARD));

        boolean replyExist = replyRepository.existsByBoard_IdAndUser(boardId, user);

        if (replyExist) {
            throw new CustomException(EXIST_REPLY);
        }

        Reply reply = replyRepository.save(Reply.from(replyDto, user, board));

        publisher.publishEvent(UpdateReplyStateEvent.of(board, null, (float) replyDto.starScore(), CREATE));
        // 이벤트 내에서 변경된 데이터를 다시 갱신하기 위해 한번 더 조회를 시도
        Board updatedBoard = boardRepository.findById(boardId).orElseThrow(() -> new CustomException(NOT_FOUND_BOARD));

        String boardUrl = ALARM_API_URL + board.getId();
        // 게시글 작성자에게 알람 전송
        publisher.publishEvent(SentAlarmEvent.of(board.getUser(), APPLYPERMIT, boardUrl));

        return ReplyResponseDto.ReplyAndUpdatedBoardDto.of(updatedBoard.getReplyCount(), updatedBoard.getRating(), reply);
    }

    @Transactional
    @Override
    public BoardReplyDto modifyReply(long replyId, ReplyRequestDto replyDto) {
        Reply reply = replyRepository.findByIdAndUser(replyId, getUser())
                .orElseThrow(() -> new CustomException(NOT_FOUND_REPLY));

        publisher.publishEvent(UpdateReplyStateEvent
                .of(reply.getBoard(), (float) reply.getStarScore(), (float) replyDto.starScore(), UPDATE));

        reply.updateReply(replyDto.starScore(), replyDto.content());
        
        // 이벤트 내에서 변경된 데이터를 다시 갱신하기 위해 한번 더 조회를 시도
        Board updatedBoard = boardRepository.findById(reply.getBoard().getId())
                .orElseThrow(() -> new CustomException(NOT_FOUND_BOARD));

        return BoardReplyDto.of(updatedBoard.getReplyCount(), updatedBoard.getRating());
    }

    @Transactional
    @Override
    public BoardReplyDto deleteReply(long replyId) {
        Reply reply = replyRepository.findByIdAndUser(replyId, getUser())
                .orElseThrow(() -> new CustomException(NOT_FOUND_REPLY));

        replyRepository.delete(reply);

        publisher.publishEvent(UpdateReplyStateEvent
                .of(reply.getBoard(), (float) reply.getStarScore(), null, DELETE));

        // 이벤트 내에서 변경된 데이터를 다시 갱신하기 위해 한번 더 조회를 시도
        Board updatedBoard = boardRepository.findById(reply.getBoard().getId())
                .orElseThrow(() -> new CustomException(NOT_FOUND_BOARD));

        return BoardReplyDto.of(updatedBoard.getReplyCount(), updatedBoard.getRating());
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
        String properties = (sortType.equals("starScore") ? "starScore" : "createdAt");
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