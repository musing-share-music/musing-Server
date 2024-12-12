package com.example.musing.user.service;

import com.example.musing.user.entity.User;
import com.example.musing.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService{
    private final UserRepository userRepository;

    @Override
    public boolean checkInputTags(String userId) {
        User user = findById(userId);
        if(user.getActivated() == null){
            System.out.println("아직 장르 및 분위기, 아티스트를 고르지 않은게 있어요. ");
            //장르, 분위기 ,아티스트 순서로 갈 예정
            if(user.getLikeGenre() ==null){

            }
        }
        return true;
    }

    @Override
    public User findById(String userId) {
        return userRepository.findById(userId).get();
    }
}
