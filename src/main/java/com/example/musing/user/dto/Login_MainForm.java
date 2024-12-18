package com.example.musing.user.dto;

import com.example.musing.board.entity.Board;
import com.example.musing.like_music.entity.Like_Music;
import com.example.musing.notice.entity.Notice;
import com.example.musing.user.entity.User;
import lombok.Data;

import java.util.List;

@Data
public class Login_MainForm {
    //공지사항 최신 1개
    private Notice notice;
    //로그인한 유저가 고른 원하는 장르, 좋아요한 음악 총 개수, 플레이리스트 수
    private UserMainFormDto userDto; //유저 Dto
    //유저 알고리즘에 맞는 장르 관련 추천 음악 4개
    private List<Board> userTypeRecommend;
    //좋아요한 음악 최신순 10개
    private List<Board> likeMusics;
    //추천으로 띄울 장르의 게시글 5개 (피그마 : 지금 뮤징에서 가장 음악 모음)//랜덤으로 장르하나 집어서 추천할 예정
    private List<Board> randomGenreRcommends;
    //음악추천 게시판 최신글 5개
    private List<Board> createAtBoards;
    //인기 게시글1개
    private Board hotBoard;

    //아티스트 추천은 보류
}
