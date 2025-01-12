package com.example.musing.board.service;

import com.example.musing.board.entity.Board;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDateTime;

public class BoardSpecificaion {
    public static Specification<Board> hasGenre(String genre) { //장르에 대한 조회
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.like(root.get("music").get("genre"), "%" + genre + "%");
    }

    public static Specification<Board> isActiveCheckFalse() { //삭제 여부 확인
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.isFalse(root.get("activeCheck"));
    }

    public static Specification<Board> isCreateAtAfterWeek() { //생성일자가 1주이내인지 체크
        LocalDateTime oneWeekAgo = LocalDateTime.now().minusWeeks(1);
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.greaterThanOrEqualTo(root.get("createdAt"), oneWeekAgo);
    }

    public static Specification<Board> isCreateAtAfterMonth() { //생성일자가 한달이내인지 체크
        LocalDateTime oneMonthAgo = LocalDateTime.now().minusMonths(1);
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.greaterThanOrEqualTo(root.get("createdAt"), oneMonthAgo);
    }
    public static Specification<Board> findBoardsWithAtLeastTenRecommend() {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.greaterThanOrEqualTo(root.get("recommendCount"), 10);
        };

    public static Specification<Board> orderByRecommendCountDesc() {
        return (root, query, criteriaBuilder) -> {
            query.orderBy(criteriaBuilder.desc(root.get("recommendCount")));
            return criteriaBuilder.conjunction(); // 항상 true를 반환
        };
    }
}
