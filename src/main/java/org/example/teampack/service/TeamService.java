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
    List<TeamDto> getTeamByUserIdAndStatus(Long userId, String status);
    void closeTeam(Long teamId, Long userId);
    List<TeamDto> getPagedTeams(Long userId, int offset, int limit);
    List<TeamDto> getPagedTeamsByStatus(Long userId, String status, int offset, int limit);
    int countTeamsByUserId(Long userId);
    int countTeamsByUserIdAndStatus(Long userId, String status);
}
