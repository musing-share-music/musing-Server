package com.example.musing.admin.admin.controller;

import com.example.musing.admin.admin.dto.AdminInfoDto;
import com.example.musing.admin.admin.repository.AdminRepository;
import com.example.musing.admin.admin.service.AdminService;
import com.example.musing.common.dto.ResponseDto;
import com.example.musing.exception.CustomException;
import com.example.musing.exception.ErrorCode;
import com.example.musing.user.dto.UserResponseDto;
import com.example.musing.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/musing/admin")
public class AdminController {

    private final AdminService adminService;

    @GetMapping("/info")
    public ResponseDto<AdminInfoDto> getAdminInfo() {
        AdminInfoDto adminInfoDto = adminService.getUserInfo();
        return ResponseDto.of(adminInfoDto);
    }
}
