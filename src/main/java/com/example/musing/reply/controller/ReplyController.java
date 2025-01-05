package com.example.musing.reply.controller;

import com.example.musing.common.dto.ResponseDto;
import com.example.musing.reply.dto.ReplyDto;
import com.example.musing.reply.service.ReplyService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/musing")
@RequiredArgsConstructor
@RestController
public class ReplyController {

    private final ReplyService replyService;

    @GetMapping("/reply/myReply")
    public ResponseDto<ReplyDto> findMyReply(@RequestParam("boardId") long boardId) {
        if (checkUserEmail()) {
            ReplyDto replyDto = replyService.findMyReplyByBoardId(boardId);
            if(replyDto == null){
                return ResponseDto.of(null,"작성한 Reply이 없습니다.");
            }
            return ResponseDto.of(replyDto);
        }
        return ResponseDto.of(null,"로그인 이후 작성 가능합니다.");
    }

    @PostMapping("/reply/write")
    public ResponseDto<ReplyDto> writeReply(@RequestParam("boardId") long boardId, ReplyDto replyDto){
        // 서비스단에서 User 정보 확인함
        replyService.writeReply(boardId, replyDto);
        return ResponseDto.of(null,"성공적으로 작성하였습니다.");
    }

    @GetMapping("/reply/modify")
    public ResponseDto<ReplyDto> modifyReplyForm(@RequestParam("replyId") long replyId) {
        ReplyDto replyDto = replyService.findMyReplyByReplyId(replyId);
        return ResponseDto.of(replyDto,"작성했던 리뷰를 불러옵니다.");
    }

    @PutMapping("/reply/modify")
    public ResponseDto<ReplyDto> modifyReply(@RequestParam("replyId") long replyId, ReplyDto replyDto) {
        replyService.modifyReply(replyId, replyDto);
        return ResponseDto.of(null,"성공적으로 리뷰를 수정했습니다.");
    }

    @DeleteMapping("/reply")
    public ResponseDto<ReplyDto> deleteReply(@RequestParam("replyId") long replyId){
        replyService.deleteReply(replyId);
        return ResponseDto.of(null, "성공적으로 리뷰를 삭제했습니다.");
    }

    @GetMapping("/board/{boardId}/reply")
    public ResponseDto<Page<ReplyDto>> findReplyPage(@PathVariable long boardId, @RequestParam(name = "page",defaultValue = "1") int page){
        Page<ReplyDto> replyDtos = replyService.findReplies(boardId,page);
        return ResponseDto.of(replyDtos, "");
    }

    private boolean checkUserEmail() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication != null && !authentication.getName().equals("anonymousUser");
    }

}
