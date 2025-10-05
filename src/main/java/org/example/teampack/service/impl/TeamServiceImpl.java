package org.example.teampack.service.impl;

import lombok.RequiredArgsConstructor;
import org.example.teampack.dao.TeamDao;
import org.example.teampack.dto.MembersDto;
import org.example.teampack.dto.TeamDto;
import org.example.teampack.service.TeamService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TeamServiceImpl implements TeamService {

    private final TeamDao teamDao;


    @Override
    @Transactional
    public void createTeamAndLeader(TeamDto teamDto, Long userId,String memberRole) {
        //1. 팀 생성 (자동으로 teamDto.teamId 채워짐)
        teamDao.insertTeam(teamDto);

        //2. 조장 등록
        MembersDto leader = new MembersDto();
        leader.setUserId(userId);
        leader.setTeamId(teamDto.getTeamId());
        leader.setMemberType("LEADER");
        leader.setMemberRole(memberRole);

        teamDao.insertMember(leader);

    }

    @Override
    public TeamDto getTeamById(Long teamId) {
        return teamDao.selectTeamById(teamId);
    }

    @Override
    public TeamDto getTeamById(Long teamId, Long userId) {
        TeamDto team = teamDao.selectTeamById(teamId);
        String memberType = teamDao.selectMemberType(userId,teamId);
        team.setMemberType(memberType);
        return team;
    }

    @Override
    public List<TeamDto> getTeamByUserId(Long userId) {
    return  teamDao.selectTeamByUserId(userId);
    }


    @Override
    public TeamDto getTeamByIdWithMemberType(Long teamId, Long userId) {
        return teamDao.selectTeamWithMemberType(teamId, userId);
    }

}
