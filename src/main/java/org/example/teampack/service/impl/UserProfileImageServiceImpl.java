package org.example.teampack.service.impl;

import lombok.RequiredArgsConstructor;
import org.example.teampack.dao.UserProfileImageDao;
import org.example.teampack.dto.UserProfileImageDto;
import org.example.teampack.service.UserProfileImageService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserProfileImageServiceImpl implements UserProfileImageService {

    private final UserProfileImageDao profileImageDao;

    @Value("${file.upload-dir}")
    private String uploadDir;

    @Override
    public void uploadProfileImage(Long userId, MultipartFile file) {
        if (file.isEmpty()) return;

        try {
            // ⛳ 빌드 디렉토리 기준으로 절대 경로 계산
            String realPath = System.getProperty("user.dir") + File.separator + uploadDir;
            File directory = new File(realPath);
            if (!directory.exists()) directory.mkdirs();

            // 확장자 추출
            String originalName = file.getOriginalFilename();
            String ext = "";
            if (originalName != null && originalName.contains(".")) {
                ext = originalName.substring(originalName.lastIndexOf("."));
            }

            // UUID 파일명 생성
            String filename = UUID.randomUUID().toString() + ext;

            // 파일 저장
            File dest = new File(directory, filename);
            file.transferTo(dest);

            // 정적 리소스 접근 경로 저장 (/uploads/{filename})
            String urlPath = "/uploads/" + filename;

            UserProfileImageDto imageDto = new UserProfileImageDto();
            imageDto.setUserId(userId);
            imageDto.setUserImageUrl(filename); // DB에는 파일명만 저장
            imageDto.setUploadedAt(new Timestamp(System.currentTimeMillis()));
            profileImageDao.insertProfileImage(imageDto);

            System.out.println("✅ 저장 경로: " + dest.getAbsolutePath());
            System.out.println("✅ 정적 접근 URL: " + urlPath);

        } catch (IOException e) {
            throw new RuntimeException("프로필 이미지 업로드 실패", e);
        }
    }

    @Override
    public UserProfileImageDto getProfileImage(Long userId) {
        return profileImageDao.findByUserId(userId);
    }
}
