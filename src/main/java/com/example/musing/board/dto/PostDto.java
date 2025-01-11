package com.example.musing.board.dto;

import com.example.musing.board.entity.Board;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PostDto {

    @Schema(description = "음악추천 고유 ID", example = "1")
    private Long id; // 조회 시 활용

    @Schema(description = "음악추천 글 제목", example = "지듣노")
    private String title; // 등록/조회 공통

    @Schema(description = "음악 추천 글 내용", example = "쌈뽕한 노래")
    private String content; // 등록/조회 공통

    @Schema(description = "추천 수", example = "10")
    private int recommendCount; // 기본값 0

    @Schema(description = "조회수", example = "10000")
    private int viewCount; // 기본값 0

    @Schema(description = "활성여부", example = "YorN")
    private boolean activeCheck; // 등록/조회 공통

    @Schema(description = "승인여부", example = "관리자 승인 시 음악 데이터 생성")
    private boolean permitRegister; // 등록/조회 공통

    @Schema(description = "이미지", example = "글 등록 관련 이미지")
    private String image; // 등록/조회 공통

    /**
     * 엔티티 -> DTO 변환 (조회 시)
     */
    public static PostDto fromEntity(Board board) {
        return PostDto.builder()
                .id(board.getId())
                .title(board.getTitle())
                .content(board.getContent())
                .recommendCount(board.getRecommendCount())
                .viewCount(board.getViewCount())
                .activeCheck(board.isActiveCheck())
                .permitRegister(board.isPermitRegister())
                .image(board.getImage())
                .build();
    }

    /**
     * DTO -> 엔티티 변환 (등록 시)
     */
    public static Board toEntity(PostDto postDto) {
        return Board.builder()
                .title(postDto.getTitle())
                .content(postDto.getContent())
                .recommendCount(0)// 등록 시 기본값 설정
                .viewCount(0) // 등록 시 기본값 설정
                .activeCheck(postDto.isActiveCheck())
                .permitRegister(postDto.isPermitRegister())
                .image(postDto.getImage())
                .build();
    }
}
