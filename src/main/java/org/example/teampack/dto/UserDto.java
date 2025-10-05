package org.example.teampack.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserDto {
    private Long userId;
    private String userEmail;
    private String userPassword;
    private String userName;
    private LocalDateTime joinedAt;
}
