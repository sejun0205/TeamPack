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
    private LocalDateTime closedAt; //db x
    private String status;

    //현재 시간 기준 계산 메서드
  public String getStatus(){
      if (closedAt != null) return "종료";
      if (dueDate == null) return "미정";
      return dueDate.isBefore(LocalDateTime.now()) ? "종료" : "진행중";
  }

}
