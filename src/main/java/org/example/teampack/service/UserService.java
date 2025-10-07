package org.example.teampack.service;

import org.example.teampack.dto.UserDto;

public interface UserService {

    boolean register(UserDto userDto);
    UserDto findByEmail(String email);
    UserDto getMyPageInfo(String email);
}
