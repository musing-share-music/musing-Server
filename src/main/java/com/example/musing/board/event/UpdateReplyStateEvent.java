package com.example.musing.board.event;

import com.example.musing.board.entity.Board;
import lombok.Builder;

/**
 * @param oldRating update, delete 에 사용할 기존 별점
 * @param newRating create 에만 사용할 새로 넣을 별점
 */
@Builder
public record UpdateReplyStateEvent(Board board, Float oldRating, Float newRating, CommitState state) {
    public static UpdateReplyStateEvent of(Board board, Float oldRating, Float newRating, CommitState state) {
        return UpdateReplyStateEvent.builder()
                .board(board)
                .oldRating(oldRating)
                .newRating(newRating)
                .state(state)
                .build();
    }
}
