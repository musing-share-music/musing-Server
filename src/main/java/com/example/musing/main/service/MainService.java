package com.example.musing.main.service;

import com.example.musing.board.dto.GenreBoardDto;
import com.example.musing.main.dto.LoginMainPageDto;
import com.example.musing.main.dto.NotLoginMainPageDto;

import java.util.List;

public interface MainService {
    NotLoginMainPageDto notLoginMainPage(String modalCheck);

    LoginMainPageDto LoginMainPage(String userId, String modalCheck);

    List<GenreBoardDto> selcetGenre(String genre);
}
