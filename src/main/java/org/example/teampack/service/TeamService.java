package org.example.teampack.service;

import org.example.teampack.dto.MembersDto;
import org.example.teampack.dto.TeamDto;

import java.util.List;

public interface TeamService {
    void createTeamAndLeader(TeamDto teamDto, Long userId,String memberRole);
    TeamDto getTeamById(Long teamId);
    TeamDto getTeamById(Long teamId, Long userId);
    List<TeamDto> getTeamByUserId(Long userId);
    TeamDto getTeamByIdWithMemberType(Long teamId, Long userId);
    List<MembersDto> getMembersByTeamId(Long teamId);
    void  kickMember(Long teamId, Long targetUserId, Long loginUserId);

}
