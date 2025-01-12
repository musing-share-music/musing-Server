package com.example.musing.reply.service;

import com.example.musing.board.entity.Board;
import com.example.musing.board.repository.BoardRepository;
import com.example.musing.exception.CustomException;
import com.example.musing.reply.dto.ReplyDto;
import com.example.musing.reply.entity.Reply;
import com.example.musing.reply.repository.ReplyRepository;
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

import java.util.Optional;

import static com.example.musing.exception.ErrorCode.*;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class ReplyServiceImpl implements ReplyService {

    private final ReplyRepository replyRepository;
    private final UserRepository userRepository;
    private final BoardRepository boardRepository;
    private static int PAGE_SIZE = 10;

    @Transactional
    @Override
    public void writeReply(long boardId, ReplyDto replyDto) {
        String email = getUserEmail(); //유저 정보 확인 이후 이메일 가져오기

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new CustomException(NOT_FOUND_USER));

        Board board = boardRepository.findById(boardId)
                .orElseThrow(() -> new CustomException(NOT_FOUND_BOARDID));

        boolean replyExist = replyRepository.existsByBoard_IdAndUser_Email(boardId, email);

        if (replyExist) {
            throw new CustomException(EXIST_REPLY);
        }

        Reply reply = Reply.from(replyDto, user, board);

        replyRepository.save(reply);
    }

    @Override
    public ReplyDto findMyReplyByBoardId(long boardId) {
        String email = getUserEmail(); //유저 정보 확인 이후 이메일 가져오기
        Optional<Reply> reply = replyRepository.findByBoard_IdAndUser_Email(boardId, email);
        return reply.map(ReplyDto::from).orElse(null); //없으면 null리턴
    }

    @Override
    public ReplyDto findMyReplyByReplyId(long replyId) {
        String email = getUserEmail(); //유저 정보 확인 이후 이메일 가져오기
        Reply reply = replyRepository.findByIdAndUser_Email(replyId,email)
                .orElseThrow(() -> new CustomException(NOT_FOUND_REPLY));

        return Reply.toDto(reply);
    }

    @Transactional
    @Override
    public void modifyReply(long replyId, ReplyDto replyDto) {
        String email = getUserEmail(); //유저 정보 확인 이후 이메일 가져오기
        Reply reply = replyRepository.findByBoard_IdAndUser_Email(replyId, email)
                .orElseThrow(() -> new CustomException(NOT_FOUND_REPLY));

        reply.updateReply(replyDto.starScore(), replyDto.content());
    }

    @Transactional
    @Override
    public void deleteReply(long replyId) {
        String email = getUserEmail(); //유저 정보 확인 이후 이메일 가져오기

        if(replyRepository.existsByIdAndUser_Email(replyId, email)){
            new CustomException(NOT_MATCHED_REPLY_AND_USER);
        }
        replyRepository.deleteById(replyId);
    }

    @Override
    public Page<ReplyDto> findReplies(long boardId, int page) {
        if (page < 1) { // 잘못된 접근으로 throw할때 쿼리문 실행을 안하기 위해 나눠서 체크
            throw new CustomException(BAD_REQUEST_REPLY_PAGE);
        }
        
        Pageable pageable = PageRequest.of(page - 1, PAGE_SIZE);
        Page<Reply> pageReply = replyRepository.findByBoard_Id(boardId, pageable);
        int totalPages = pageReply.getTotalPages();

        if ( page > totalPages) {
            throw new CustomException(BAD_REQUEST_REPLY_PAGE);
        }

        return pageReply.map(Reply::toDto);
    }

    private String getUserEmail() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        //유저 정보 확인 및 로그인 상태 여부 확인
        if (authentication == null || authentication.getName().equals("anonymousUser")) {
            throw new CustomException(NOT_FOUND_USER);
        }
        return authentication.getName();
    }
}