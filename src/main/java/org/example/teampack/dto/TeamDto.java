package org.example.teampack.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class TeamDto {
    private Long teamId;
    private String teamName;
    private String assignmentTitle;
    private LocalDateTime dueDate;
    private String memberType;

}
