package org.example.teampack.dao;

import org.apache.catalina.User;
import org.example.teampack.dto.UserDto;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.Map;

@Repository
public class UserDao {

    private final SqlSessionTemplate sqlSession;

    public UserDao(SqlSessionTemplate sqlSession) {
        this.sqlSession = sqlSession;
    }

    public int insertUser(UserDto userDto) {

        return sqlSession.insert("UserDao.insertUser", userDto);
    }

    public UserDto selectByEmail(String email) {

        return sqlSession.selectOne("UserDao.selectByEmail", email);
    }

    public UserDto findUserInfoByEmail(String email){
        return sqlSession.selectOne("UserDao.findUserInfoByEmail",email);
    }

    public void updateUser(UserDto userDto){
        sqlSession.update("UserDao.updateUser", userDto);
    }

    public void updatePassword(Long userId, String newPassword){
        Map<String, Object> map = new HashMap<>();
        map.put("userId", userId);
        map.put("newPassword", newPassword);
        sqlSession.update("UserDao.updatePassword", map);
    }

    public UserDto findById(Long userId){
        return sqlSession.selectOne("UserDao.selectById",userId);
    }

}
