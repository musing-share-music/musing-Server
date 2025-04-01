package com.example.musing.reply.controller;

import com.example.musing.alarm.service.AlarmService;
import com.example.musing.board.dto.BoardReplyDto;
import com.example.musing.board.entity.Board;
import com.example.musing.common.dto.ResponseDto;
import com.example.musing.reply.dto.ReplyRequestDto;
import com.example.musing.reply.dto.ReplyResponseDto;
import com.example.musing.reply.entity.Reply;
import com.example.musing.reply.service.ReplyService;
import io.swagger.v3.oas.annotations.Operation;
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

    @Operation(summary = "음악 추천 게시글의 작성한 내 리뷰 조회",
            description = "음악 추천 게시글의 작성한 리뷰를 조회합니다." +
                    "로그인을 하지않았거나 작성한 Reply이 없을 경우 null을 반환하며," +
                    "작성한 리뷰가 있을 시 해당 내용을 Dto로 보여줍니다.")
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

    @Operation(summary = "음악 추천 게시글의 리뷰 작성",
            description = "해당 게시글의 별점과 리뷰를 남깁니다.")
    @PostMapping("/reply/write")
    public ResponseDto<ReplyResponseDto.ReplyAndUpdatedBoardDto> writeReply(@RequestParam("boardId") long boardId,
                                                                            ReplyRequestDto replyDto) {
        // 서비스단에서 User 정보 확인함
        ReplyResponseDto.ReplyAndUpdatedBoardDto replyAndUpdatedBoardDto = replyService.writeReply(boardId, replyDto);

        return ResponseDto.of(replyAndUpdatedBoardDto, "성공적으로 작성하였습니다.");
    }

    @Operation(summary = "음악 추천 게시글의 리뷰 수정 페이지",
            description = "수정하기를 누르면 이전에 작성한 리뷰를 가져옵니다.")
    @GetMapping("/reply/modify")
    public ResponseDto<ReplyResponseDto.ReplyDto> modifyReplyForm(@RequestParam("replyId") long replyId) {
        ReplyResponseDto.ReplyDto replyDto = replyService.findMyReplyByReplyId(replyId);
        return ResponseDto.of(replyDto, "작성했던 리뷰를 불러옵니다.");
    }

    @Operation(summary = "음악 추천 게시글의 리뷰 수정",
            description = "해당 게시글의 별점과 리뷰를 수정합니다." +
                    "수정을 하면서 변경되는 리뷰 내용과 게시글의 평균 별점을 같이 반환합니다")
    @PutMapping("/reply/modify")
    public ResponseDto<BoardReplyDto> modifyReply(@RequestParam("replyId") long replyId, ReplyRequestDto replyDto) {
        Reply reply = replyService.findByReplyId(replyId);

        BoardReplyDto boardReplyDto = replyService.modifyReply(replyId, replyDto);
        return ResponseDto.of(boardReplyDto, "성공적으로 리뷰를 수정했습니다.");
    }
    
    @Operation(summary = "음악 추천 게시글의 리뷰 삭제",
            description = "해당 게시글의 별점과 리뷰를 삭제합니다." +
                    "수정을 하면서 변경되는 리뷰 내용과 게시글의 평균 별점, 댓글 수를 같이 반환합니다")
    public ResponseDto<BoardReplyDto> deleteReply(@RequestParam("replyId") long replyId) {
        Reply reply = replyService.findByReplyId(replyId);

        BoardReplyDto boardReplyDto = replyService.deleteReply(replyId);
        return ResponseDto.of(boardReplyDto, "성공적으로 리뷰를 삭제했습니다.");
    }

    @Operation(summary = "음악 추천 게시글의 리뷰 및 별점 리스트 조회",
            description = "해당 게시글의 작성된 리뷰 및 별점을 조회합니다." +
                    "sortType [date, starScore, onlyReview]로 파라미터를 받습니다." +
                    "sort는 [DESC, ASC]로 파라미터를 받으며, 디폴트값으로 DESC로 최신순을 보여줍니다." +
                    "sortType이 'starScore'일 경우 sort값은 '별점 높은 순, 낮은 순'" +
                    "나머지는 작성일자를 기준으로 최신 순, 오래된 순 입니다.")
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
