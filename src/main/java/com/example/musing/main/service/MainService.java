package com.example.musing.main.service;

import com.example.musing.main.dto.MainPageDto;

public interface MainService {
    public MainPageDto.NotLoginMainPageDto notLoginMainPage();
    public MainPageDto.LoginMainPageDto LoginMainPage();
}
