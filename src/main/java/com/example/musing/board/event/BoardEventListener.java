package com.example.musing.board.event;

import com.example.musing.board.service.BoardService;
import com.example.musing.exception.CustomException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import static com.example.musing.exception.ErrorCode.ERROR;

@Slf4j
@RequiredArgsConstructor
@Component
public class BoardEventListener {
    private final BoardService boardService;

    // 리뷰 작성 트랜잭션 커밋이 되기전에 업데이트 되도록
    @TransactionalEventListener(phase = TransactionPhase.BEFORE_COMMIT)
    public void handleUpdateReplyState(UpdateReplyStateEvent event) {
        try {
            boardService.updateReplyStateWithRetry(
                    event.board(),
                    event.oldRating(),
                    event.newRating(),
                    event.state()
            );
        } catch (CustomException e) {
            log.error("게시글의 리뷰 변경사항 반영 실패: {}", event, e);
            throw e;
        }
    }
}
