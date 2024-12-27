package com.example.musing.main.service;

import com.example.musing.main.dto.LoginMainPageDto;
import com.example.musing.main.dto.NotLoginMainPageDto;

public interface MainService {
    public NotLoginMainPageDto notLoginMainPage();

    public LoginMainPageDto LoginMainPage();
}
