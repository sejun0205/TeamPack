package org.example.teampack.dao;

import lombok.RequiredArgsConstructor;
import org.apache.ibatis.session.SqlSession;
import org.example.teampack.dto.MembersDto;
import org.example.teampack.dto.TeamDto;
import org.springframework.stereotype.Repository;

import java.util.List;

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

}
