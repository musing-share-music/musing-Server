package com.example.musing.reply.controller;

import com.example.musing.alarm.service.AlarmService;
import com.example.musing.board.dto.BoardReplyDto;
import com.example.musing.board.entity.Board;
import com.example.musing.common.dto.ResponseDto;
import com.example.musing.reply.dto.ReplyRequestDto;
import com.example.musing.reply.dto.ReplyResponseDto;
import com.example.musing.reply.entity.Reply;
import com.example.musing.reply.service.ReplyService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import static com.example.musing.alarm.entity.AlarmType.APPLYPERMIT;

@RequestMapping("/musing")
@RequiredArgsConstructor
@RestController
public class ReplyController {

    private final ReplyService replyService;

    @GetMapping("/reply/myReply")
    public ResponseDto<ReplyResponseDto.ReplyDto> findMyReply(@RequestParam("boardId") long boardId) {
        if (checkUserEmail()) {
            ReplyResponseDto.ReplyDto replyDto = replyService.findMyReplyByBoardId(boardId);
            if (replyDto == null) {
                return ResponseDto.of(null, "작성한 Reply이 없습니다.");
            }
            return ResponseDto.of(replyDto);
        }
        return ResponseDto.of(null, "로그인 이후 작성 가능합니다.");
    }

    @PostMapping("/reply/write")
    public ResponseDto<ReplyResponseDto.ReplyAndUpdatedBoardDto> writeReply(@RequestParam("boardId") long boardId,
                                                                            ReplyRequestDto replyDto) {
        // 서비스단에서 User 정보 확인함
        ReplyResponseDto.ReplyAndUpdatedBoardDto replyAndUpdatedBoardDto = replyService.writeReply(boardId, replyDto);

        return ResponseDto.of(replyAndUpdatedBoardDto, "성공적으로 작성하였습니다.");
    }

    @GetMapping("/reply/modify")
    public ResponseDto<ReplyResponseDto.ReplyDto> modifyReplyForm(@RequestParam("replyId") long replyId) {
        ReplyResponseDto.ReplyDto replyDto = replyService.findMyReplyByReplyId(replyId);
        return ResponseDto.of(replyDto, "작성했던 리뷰를 불러옵니다.");
    }

    @PutMapping("/reply/modify")
    public ResponseDto<BoardReplyDto> modifyReply(@RequestParam("replyId") long replyId, ReplyRequestDto replyDto) {
        Reply reply = replyService.findByReplyId(replyId);

        BoardReplyDto boardReplyDto = replyService.modifyReply(replyId, replyDto);
        return ResponseDto.of(boardReplyDto, "성공적으로 리뷰를 수정했습니다.");
    }

    @DeleteMapping("/reply")
    public ResponseDto<BoardReplyDto> deleteReply(@RequestParam("replyId") long replyId) {
        Reply reply = replyService.findByReplyId(replyId);

        BoardReplyDto boardReplyDto = replyService.deleteReply(replyId);
        ;
        return ResponseDto.of(boardReplyDto, "성공적으로 리뷰를 삭제했습니다.");
    }

    @GetMapping("/board/{boardId}/reply")
    public ResponseDto<Page<ReplyResponseDto.ReplyDto>> findReplyPage(
            @PathVariable long boardId,
            @RequestParam(name = "page", defaultValue = "1") int page,
            @RequestParam(defaultValue = "date") String sortType,
            @RequestParam(defaultValue = "DESC") String sort) {
        Page<ReplyResponseDto.ReplyDto> replyDtos = replyService.findReplies(boardId, page, sortType, sort);
        return ResponseDto.of(replyDtos, "");
    }

    private boolean checkUserEmail() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication != null && !authentication.getName().equals("anonymousUser");
    }

}
