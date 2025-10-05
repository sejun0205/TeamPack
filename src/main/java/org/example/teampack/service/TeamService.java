package org.example.teampack.service;

import org.example.teampack.dto.TeamDto;

import java.util.List;

public interface TeamService {
    void createTeamAndLeader(TeamDto teamDto, Long userId,String memberRole);
    TeamDto getTeamById(Long teamId);
    List<TeamDto> getTeamByUserId(Long userId);
}
