package org.example.teampack.dto;

import lombok.Data;

@Data
public class MembersDto {

    private Long memberId;
    private Long userId;
    private Long teamId;
    private String memberRole;
    private String memberType;
    private String userName;
    private String userImageUrl;
}
