package org.example.teampack.dto;

import lombok.Data;

import java.sql.Timestamp;

@Data
public class UserProfileImageDto {

    private Long userImageId;
    private Long userId;
    private String userImageUrl;
    private Timestamp uploadedAt;

}
