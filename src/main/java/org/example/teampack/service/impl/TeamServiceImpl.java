package org.example.teampack.service.impl;

import lombok.RequiredArgsConstructor;
import org.example.teampack.dao.TeamDao;
import org.example.teampack.dao.UserProfileImageDao;
import org.example.teampack.dto.MembersDto;
import org.example.teampack.dto.TeamDto;
import org.example.teampack.dto.UserProfileImageDto;
import org.example.teampack.service.TeamService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TeamServiceImpl implements TeamService {

    private final TeamDao teamDao;
    private final UserProfileImageDao userProfileImageDao;

    @Override
    @Transactional
    public void createTeamAndLeader(TeamDto teamDto, Long userId, String memberRole) {
        // 팀 생성 (자동으로 teamDto.teamId 채워짐)
        teamDao.insertTeam(teamDto);

        // 조장 등록
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
        String memberType = teamDao.selectMemberType(userId, teamId);
        team.setMemberType(memberType);
        return team;
    }

    @Override
    public List<TeamDto> getTeamByUserId(Long userId) {
       List<TeamDto> teams = teamDao.selectTeamByUserId(userId);
        LocalDateTime now = LocalDateTime.now();

        for (TeamDto team : teams){
            if (team.getDueDate() != null && team.getDueDate().isBefore(now)){
                team.setStatus("종료");
            }else {
                team.setStatus("진행중");
            }
        }
        return teams;
    }

    @Override
    public TeamDto getTeamByIdWithMemberType(Long teamId, Long userId) {
        return teamDao.selectTeamWithMemberType(teamId, userId);
    }

    @Override
    public List<MembersDto> getMembersByTeamId(Long teamId) {
        List<MembersDto> members = teamDao.selectMembersByTeamId(teamId);

        //  각 멤버별 프로필 이미지 세팅 (마이페이지 로직과 동일)
        for (MembersDto member : members) {
            UserProfileImageDto imageDto = userProfileImageDao.findByUserId(member.getUserId());

            if (imageDto != null && imageDto.getUserImageUrl() != null) {
                // DB에는 파일명만 저장되어 있으므로 /uploads/ 경로 붙이기
                member.setUserImageUrl("/uploads/" + imageDto.getUserImageUrl());
            } else {
                // 프로필 이미지 없을 경우 기본 이미지 사용
                member.setUserImageUrl("/images/default-profile.png");
            }
        }

        return members;
    }

    @Override
    public void kickMember(Long teamId, Long targetUserId, Long loginUserId) {
        String loginUserRole = teamDao.getMemberType(teamId, loginUserId);

        if (!"LEADER".equalsIgnoreCase(loginUserRole)) {
            throw new RuntimeException("팀장만 팀원을 내보낼 수 있습니다.");
        }

        teamDao.deleteMemberFromTeam(teamId, targetUserId);
    }

    @Override
    public List<TeamDto> getTeamByUserIdAndStatus(Long userId, String status) {
       List<TeamDto> teams = teamDao.selectTeamByUserIdAndStatus(userId, status);
       LocalDateTime now = LocalDateTime.now();

       for (TeamDto team : teams){
           if(team.getDueDate() != null && team.getDueDate().isBefore(now)){
               team.setStatus("종료");
           }else {
               team.setStatus("진행중");
           }
       }
       return teams;
    }

    @Override
    public void closeTeam(Long teamId, Long userId) {
        String role = teamDao.getMemberType(teamId, userId);
        if(!"LEADER".equalsIgnoreCase(role)){
            throw new RuntimeException("팀장만 마감할 수 있습니다.");
        }
        teamDao.updateClosedAt(teamId, LocalDateTime.now());
    }

    @Override
    public List<TeamDto> getPagedTeams(Long userId, int offset, int limit) {
        return teamDao.selectPagedTeams(userId, offset, limit);
    }

    @Override
    public List<TeamDto> getPagedTeamsByStatus(Long userId, String status, int offset, int limit) {
        return teamDao.selectPagedTeamsByStatus(userId, status, offset, limit);
    }

    @Override
    public int countTeamsByUserId(Long userId) {
        return teamDao.countTeamsByUserId(userId);
    }

    @Override
    public int countTeamsByUserIdAndStatus(Long userId, String status) {
        return teamDao.countTeamsByUserIdAndStatus(userId, status);
    }


}
