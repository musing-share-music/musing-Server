package com.example.musing.admin.admin.service;

import com.example.musing.admin.admin.dto.AdminInfoDto;
import com.example.musing.admin.admin.repository.AdminRepository;
import com.example.musing.exception.CustomException;
import com.example.musing.exception.ErrorCode;
import com.example.musing.user.dto.UserResponseDto;
import com.example.musing.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class AdminServiceImpl implements AdminService{

    private final AdminRepository adminRepository;

    @Override
    public AdminInfoDto getUserInfo() {
        User user = getAdmin();
        return AdminInfoDto.of(user);
    }

    private User getAdmin() {
        String id = SecurityContextHolder.getContext().getAuthentication().getName();

        return adminRepository.findById(id)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_USER));
    }
}
