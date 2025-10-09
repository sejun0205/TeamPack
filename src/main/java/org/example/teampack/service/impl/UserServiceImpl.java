package org.example.teampack.service.impl;

import org.example.teampack.dao.UserDao;
import org.example.teampack.dto.UserDto;
import org.example.teampack.service.UserService;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {

    private final UserDao userDao;

    public UserServiceImpl(UserDao userDao) {
        this.userDao = userDao;
    }


    @Override
    public boolean register(UserDto userDto) {
        //이메일 중복 체크
        UserDto existingUser = userDao.selectByEmail(userDto.getUserEmail());
        if(existingUser != null){
            return  false; //이미 존재
        }
        userDao.insertUser(userDto);
        return true;
    }

    @Override
    public UserDto findByEmail(String email) {
        return userDao.selectByEmail(email);
    }

    @Override
    public UserDto getMyPageInfo(String email) {
        return userDao.findUserInfoByEmail(email);
    }

    // 회원 정보 수정
    @Override
    public void updateUser(UserDto userDto) {
        userDao.updateUser(userDto);
    }

    //비밀번호 수정
    @Override
    public void updatePassword(Long userId, String newPassword) {
        userDao.updatePassword(userId, newPassword);
    }

    @Override
    public UserDto findById(Long userId) {
        return userDao.findById(userId);
    }

}
