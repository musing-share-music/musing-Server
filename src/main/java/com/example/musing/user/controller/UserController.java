package com.example.musing.user.controller;

import com.example.musing.common.dto.ResponseDto;
import com.example.musing.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/musing/user")
@RequiredArgsConstructor
@RestController
public class UserController {

    private final UserService userService;

    /*@GetMapping
    public ResponseDto<UserD>*/
}
