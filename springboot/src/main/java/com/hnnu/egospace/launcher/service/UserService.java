package com.hnnu.egospace.launcher.service;

import java.util.List;
import java.util.UUID;

import javax.annotation.Resource;
import org.springframework.stereotype.Service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.hnnu.egospace.launcher.entity.Params;
import com.hnnu.egospace.launcher.entity.User;
import com.hnnu.egospace.launcher.mapper.UserMapper;

@Service
public class UserService {
    
    private static final int MAX_RETRY_COUNT = 3;
    
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
        throw new RuntimeException("Failed to generate unique UUID after " + MAX_RETRY_COUNT + " attempts");
    }

    public void addUser(User user) {
        String uuid = generateUniqueUUID();
        user.setId(uuid);
        if (user.getPassword() == null || user.getPassword().trim().isEmpty()) {
            user.setPassword("123456");
        }
        int result = userMapper.insertUser(user);
        if (result == 0) {
            throw new RuntimeException("Insert failed");
        }
    }

    public void updateUser(User user) {
        int result = userMapper.updateUser(user);
        if (result == 0) {
            throw new RuntimeException("Update failed");
        }
    }

    public void deleteUser(String id) {
        int result = userMapper.deleteUser(id);
        if (result == 0) {
            throw new RuntimeException("Delete failed");
        }
    }
    
}
