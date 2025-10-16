package org.example.teampack.dao;

import lombok.RequiredArgsConstructor;
import org.apache.ibatis.session.SqlSession;
import org.example.teampack.dto.MembersDto;
import org.example.teampack.dto.TeamDto;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Repository
@RequiredArgsConstructor
public class TeamDao {

    private final SqlSession sqlSession;

    //팀 생성
    public void  insertTeam(TeamDto teamDto){
        sqlSession.insert("TeamDao.insertTeam", teamDto); //team id  자동 생성

    }

    //조원(조장 등록)
    public void insertMember(MembersDto membersDto){
        sqlSession.insert("TeamDao.insertMember",membersDto);
    }

    //팀 찾기
    public TeamDto selectTeamById(Long teamId){
        return sqlSession.selectOne("TeamDao.selectTeamById",teamId);
    }

    public List<TeamDto> selectTeamByUserId(Long userId){
        return sqlSession.selectList("TeamDao.selectTeamByUserId", userId);
    }

    // 유저가 특정 팀에서 어떤 memberType(LEADER인지 MEMBER인지)인지 조회
    public String selectMemberType(Long userId, Long teamId) {
        Map<String, Object> params = new HashMap<>();
        params.put("userId", userId);
        params.put("teamId", teamId);
        return sqlSession.selectOne("TeamDao.selectMemberType", params);
    }

    public TeamDto selectTeamWithMemberType(Long teamId, Long userId) {
        Map<String, Object> params = new HashMap<>();
        params.put("teamId", teamId);
        params.put("userId", userId);
        return sqlSession.selectOne("TeamDao.selectTeamWithMemberType", params);
    }

    public List<MembersDto> selectMembersByTeamId(Long teamId){
        return sqlSession.selectList("TeamDao.selectMembersByTeamId",teamId);
    }

    //멤버의 역할 가져오기
    public String getMemberType(Long teamId, Long userId){
        Map<String, Object> map = new HashMap<>();
        map.put("teamId",teamId);
        map.put("userId",userId);
        return sqlSession.selectOne("TeamDao.getMemberType",map);
    }

    //멤버 팀에서 삭제
    public  void deleteMemberFromTeam(Long teamId, Long userId){
        Map<String,Object> map = new HashMap<>();
        map.put("teamId",teamId);
        map.put("userId",userId);
        sqlSession.delete("TeamDao.deleteMemberFromTeam",map);
    }

    //마감 상태 필터링
    public List<TeamDto> selectTeamByUserIdAndStatus(Long userId, String status){
        Map<String,Object> map = new HashMap<>();
        map.put("userId", userId);
        map.put("status",status); //진행중 or 종료
        return sqlSession.selectList("TeamDao.selectTeamByUserIdAndStatus",map);
    }

    //마감일
    public void updateClosedAt(Long teamId, LocalDateTime closedAt){
        Map<String,Object> params = new HashMap<>();
        params.put("teamId", teamId);
        params.put("closedAt", closedAt);
        sqlSession.update("TeamDao.updateClosedAt",params);
    }

}
