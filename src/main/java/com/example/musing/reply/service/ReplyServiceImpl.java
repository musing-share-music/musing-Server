package com.example.musing.reply.service;

import static com.example.musing.exception.ErrorCode.NOT_FOUND_REPLY;
import static com.example.musing.exception.ErrorCode.NOT_FOUND_USER;
import static com.example.musing.exception.ErrorCode.NOT_MATCHED_REPLY_AND_USER;

import com.example.musing.exception.CustomException;
import com.example.musing.reply.dto.ReplyDto;
import com.example.musing.reply.entity.Reply;
import com.example.musing.reply.repository.ReplyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class ReplyServiceImpl implements ReplyService{

    private final ReplyRepository replyRepository;

    @Override
    public Reply writeReply(ReplyDto replyDto) {

        return null;
    }

    @Override
    public ReplyDto findMyReply(long replyId) {
        String email = getUserEmail(); //유저 정보 확인 이후 이메일 가져오기
        Reply reply = replyRepository.findByIdAndUser_Email(replyId, email).orElseThrow(() -> new CustomException(NOT_MATCHED_REPLY_AND_USER));
        return ReplyDto.from(reply);
    }

    @Override
    public Reply modifyReply(long replyId, ReplyDto replyDto) {
        return null;
    }

    @Override
    public void deleteReply(long replyId) {
        String email = getUserEmail(); //유저 정보 확인 이후 이메일 가져오기

        Reply reply = replyRepository.findByIdAndUser_Email(replyId, email).orElseThrow(() -> new CustomException(NOT_MATCHED_REPLY_AND_USER));

        replyRepository.deleteById(replyId);
    }

    @Override
    public Page<ReplyDto> findReplies(int page) {
        return null;
    }

    private String getUserEmail(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        //유저 정보 확인 및 로그인 상태 여부 확인
        if(authentication==null || authentication.getName().equals("anonymousUser")){
            throw new CustomException(NOT_FOUND_USER);
        }
        return authentication.getName();
    }
}
