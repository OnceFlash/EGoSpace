package com.hnnu.egospace.launcher.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.auth0.jwt.JWT;
import com.github.pagehelper.PageInfo;
import javax.annotation.Resource;
import com.hnnu.egospace.launcher.common.Result;
import com.hnnu.egospace.launcher.entity.Params;
import com.hnnu.egospace.launcher.entity.User;
import com.hnnu.egospace.launcher.service.UserService;
import com.hnnu.egospace.launcher.utils.ColoredLogger;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

@RestController
@RequestMapping("/frontend/{lang}")
public class UserController {
    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

// test
    @GetMapping("/home")
    public String userHome(@PathVariable String lang) {
        // Example route to test language param
        return "zh-cn".equals(lang) ? "用户主页" : "User Home";
    }
//test

    @Resource
    private UserService userService;

    @GetMapping("/user/search")
    public Result searchUser(Params params) {
         PageInfo<User> info = userService.searchUser(params);
         return Result.success(info);
    }

    @PostMapping("/user/add")
    public Result addUser(@RequestBody User user) {
        if (user.getId() == null || user.getId().trim().isEmpty()) {
            userService.addUser(user);
        }
        return Result.success();
    }

    @PostMapping("/user/update")
    public Result updateUser(@RequestBody User user) {
        userService.updateUser(user);
        return Result.success();
    }
    
    @DeleteMapping("/user/delete/{id}")
    public Result deleteUser(@PathVariable String id) {
        userService.deleteUser(id);
        return Result.success();
    }

    @PostMapping("/user/login")
    public Result login(@RequestBody User user) {
        User result = userService.login(user);
        return Result.success(result);
    }
    
    @PostMapping("/user/register")
    public Result register(@RequestBody User user) {
        userService.addUser(user);
        return Result.success();
    }
    
    @GetMapping("/user/profile")
    public Result loadUserProfile(@RequestHeader("Authorization") String token) {
    try {
        // Extract user info from token
        String username = JWT.decode(token).getAudience().get(0);
        ColoredLogger.info(logger, String.format("Loading user profile for: %s", username));
        
        // Load user profile
        User user = userService.checkUsernameT(username);
        if (user == null) {
            ColoredLogger.warning(logger, "User not found: " + username);
            return Result.error("User not found");
        }
        
        // Clear sensitive data
        user.setPassword(null);
        user.setToken(null);
        user.setGameToken(null);
        
        return Result.success(user);
    } catch (Exception e) {
        ColoredLogger.error(logger, "Error loading user profile", e);
        return Result.error("Failed to load profile");
    }
}

    @PostMapping("/user/profile")
    public Result updateUserProfile(@RequestBody User user) {
        userService.updateUserProfile(user);
        return Result.success();
    }
    
}
