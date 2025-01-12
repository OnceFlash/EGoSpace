package com.hnnu.egospace.launcher.mapper;

import com.hnnu.egospace.launcher.entity.Params;
import com.hnnu.egospace.launcher.entity.User;

import java.util.List;

import javax.persistence.criteria.CriteriaBuilder.In;

import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;
import tk.mybatis.mapper.common.Mapper;

@Repository
public interface UserMapper extends Mapper<User> {

    List<User> searchUser(@Param("params") Params params);
    
    User checkUUID(@Param("id") String id);
    int insertUser(User user);

    int updateUser(User user);
    int deleteUser(String id);

    User checkStudentId(@Param("studentId") String studentId);
    User checkUser(@Param("username") String username, @Param("password") String password);

    User checkUsernameT(@Param("username") String username);
    
    int updateGameScore(@Param("username") String username, @Param("score") Integer score);

    User checkId(String id);
    User checkUid(Integer uid);

    int updateUserProfile(User user);
    User loadUserProfile(String id);

}
