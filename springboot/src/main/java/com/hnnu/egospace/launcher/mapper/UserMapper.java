package com.hnnu.egospace.launcher.mapper;

import com.hnnu.egospace.launcher.entity.Params;
import com.hnnu.egospace.launcher.entity.User;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;
import tk.mybatis.mapper.common.Mapper;

@Repository
public interface UserMapper extends Mapper<User> {

    List<User> searchUser(@Param("params")Params params);
    
    User checkUUID(@Param("id") String id);
    int insertUser(User user);

    int updateUser(User user);
    int deleteUser(String id);

}
