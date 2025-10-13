package org.example.teampack.service;

import org.example.teampack.dto.UserProfileImageDto;
import org.springframework.web.multipart.MultipartFile;

public interface UserProfileImageService {
    void uploadProfileImage(Long userId, MultipartFile file);
    UserProfileImageDto getProfileImage(Long userId);
}
