package com.hnnu.egospace.launcher.service;

import java.util.List;
import java.util.UUID;

import javax.annotation.Resource;
import org.springframework.transaction.annotation.Transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.hnnu.egospace.launcher.common.JwtTokenUtils;
import com.hnnu.egospace.launcher.entity.Params;
import com.hnnu.egospace.launcher.entity.User;
import com.hnnu.egospace.launcher.exception.CustomException;
import com.hnnu.egospace.launcher.mapper.UserMapper;
import com.hnnu.egospace.launcher.utils.ColoredLogger;

@Service
@Transactional
public class UserService {

    // back-end admin management and front-end user management
    private static final int MAX_RETRY_COUNT = 3;
    private static final Logger logger = LoggerFactory.getLogger(UserService.class);
    
    @Resource
    private UserMapper userMapper;

    public PageInfo<User> searchUser(Params params) {
        PageHelper.startPage(params.getPageNum(), params.getPageSize());
        List<User> list = userMapper.searchUser(params);
        return PageInfo.of(list);
    }

    private String generateUniqueUUID() {
        String uuid;
        int retryCount = 0;
        do {
            uuid = UUID.randomUUID().toString();
            User existingUser = userMapper.checkUUID(uuid);
            if (existingUser == null) {
                return uuid;
            }
            retryCount++;
        } while (retryCount < MAX_RETRY_COUNT);
            ColoredLogger.error(logger, "Failed to generate unique UUID after " + MAX_RETRY_COUNT + " attempts");
            throw new CustomException("Failed to generate unique UUID after " + MAX_RETRY_COUNT + " attempts");
    }

    public void addUser(User user) {
        // Check username uniqueness
        User existedUser = userMapper.checkUsernameT(user.getUsername());
        if (existedUser != null) {
            ColoredLogger.error(logger, "User with username already exists");
            throw new CustomException("User with username already exists");
        }

        String uuid = generateUniqueUUID();
        user.setId(uuid);

        // Handle studentId for social registration
        if (user.getStudentId() == null || user.getStudentId().trim().isEmpty()) {
            ColoredLogger.warning(logger, "Social user detected, setting student ID to NULL");
            user.setStudentId("NULL");
        } else {
            ColoredLogger.banner(logger, "Checking student ID uniqueness");
            User existingUser = userMapper.checkStudentId(user.getStudentId());
            if (existingUser != null) {
                ColoredLogger.error(logger, "User with student ID already exists");
                throw new CustomException("User with student ID already exists");
            }
        }

        if (user.getPassword() == null || user.getPassword().trim().isEmpty()) {
            user.setPassword("123456");
        }

        // Let database handle uid auto-increment
        user.setUid(null); 

        int result = userMapper.insertUser(user);
        if (result == 0) {
            ColoredLogger.error(logger, "Failed to insert user");
            throw new CustomException("Failed to insert user");
        }

        ColoredLogger.success(logger, "User added successfully with id: " + uuid);
    }

    public void updateUser(User user) {
        int result = userMapper.updateUser(user);
        if (result == 0) {
            ColoredLogger.error(logger, "Update failed");
            throw new CustomException("Update failed");
        }
    }

    public void deleteUser(String id) {        
        int result = userMapper.deleteUser(id);
        if (result == 0) {
            ColoredLogger.error(logger, "Delete failed");
            throw new CustomException("Delete failed");
        }
    }

    public User login(User user) {
        ColoredLogger.info(logger, "Attempting login frontend with: " + "username="
         + user.getUsername() + ", " + "password=" + user.getPassword());

        User visitor = userMapper.checkUser(user.getUsername(), user.getPassword());
        if (visitor == null) {
            ColoredLogger.error(logger, "Invalid username or password");
            throw new CustomException("Invalid username or password");
        }

        String token = JwtTokenUtils.genUserToken(visitor.getUsername(), visitor.getId());
        visitor.setToken(token);

        String gameToken = JwtTokenUtils.generateGameToken(visitor.getUid().toString(), visitor.getId());
        visitor.setGameToken(gameToken);
        
        return visitor;
    }


    // front-end user management
    public User checkUsernameT(String username) {
        return userMapper.checkUsernameT(username);
    }
    
    public User checkId(String id) {
        return userMapper.checkId(id);
    }

    public User checkUid(Integer uid) {
        return userMapper.checkUid(uid);
    }

    public User loadUserProfile(User user) {
        User result = userMapper.loadUserProfile(user.getId());
        if (result == null) {
            ColoredLogger.error(logger, "User profile not found");
            throw new CustomException("User profile not found");
        }
        return result;
    }

    public void updateUserProfile(User user) {
        int result = userMapper.updateUserProfile(user);
        if (result == 0) {
            ColoredLogger.error(logger, "User info update failed");
            throw new CustomException("User info update failed");
        }
    }

    // email verification


}
