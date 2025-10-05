package org.example.teampack.dao;

import org.example.teampack.dto.UserDto;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.stereotype.Repository;

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
}
