package org.example.teampack.dao;

import lombok.RequiredArgsConstructor;
import org.example.teampack.dto.UserProfileImageDto;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class UserProfileImageDao {

    private final SqlSessionTemplate sqlSession;

    public void insertProfileImage(UserProfileImageDto imageDto){
        sqlSession.insert("UserProfileImageDao.insertProfileImage",imageDto);
    }

    public UserProfileImageDto findByUserId(Long userId){
        return sqlSession.selectOne("UserProfileImageDao.findByUserId",userId);
    }

    public void updateProfileImage (UserProfileImageDto imageDto){
        sqlSession.update("UserProfileImageDao.updateProfileImage",imageDto);
    }

    public void deleteProfileImageByUserId(Long userId){
        sqlSession.delete("UserProfileImageDao.deleteProfileImageByUserId",userId);
    }

}
