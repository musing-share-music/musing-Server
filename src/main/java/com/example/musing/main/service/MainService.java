package com.example.musing.main.service;

import com.example.musing.board.dto.GenreBoardDto;
import com.example.musing.main.dto.LoginMainPageDto;
import com.example.musing.main.dto.NotLoginMainPageDto;

import java.util.List;

public interface MainService {
    NotLoginMainPageDto notLoginMainPage();

    LoginMainPageDto LoginMainPage(String userId);

    List<GenreBoardDto> selcetGenre(String genre);
}
