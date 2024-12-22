package com.hnnu.egospace.launcher.controller;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.github.pagehelper.PageInfo;
import javax.annotation.Resource;
import com.hnnu.egospace.launcher.common.Result;
import com.hnnu.egospace.launcher.entity.Params;
import com.hnnu.egospace.launcher.entity.User;
import com.hnnu.egospace.launcher.service.UserService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;


@RestController
@RequestMapping("/{lang}/user")
public class UserController {


// test
    @GetMapping("/home")
    public String userHome(@PathVariable String lang) {
        // Example route to test language param
        return "zh-cn".equals(lang) ? "用户主页" : "User Home";
    }

    @GetMapping("/profile")
    public String userProfile(@PathVariable String lang) {
        // Handle the request based on the language parameter
        if ("zh-cn".equals(lang)) {
            return "用户资料";
        } else {
            return "User Profile";
        }
    }
//test

    @Resource
    private UserService userService;

    @GetMapping("/search")
    public Result searchUser(Params params) {
         PageInfo<User> info = userService.searchUser(params);
         return Result.success(info);
    }

    @PostMapping("/add")
    public Result addUser(@RequestBody User user) {
        if (user.getId() == null || user.getId().trim().isEmpty()) {
            userService.addUser(user);
        }
        return Result.success();
    }

    @PostMapping("/update")
    public Result updateUser(@RequestBody User user) {
        userService.updateUser(user);
        return Result.success();
    }
    
    @DeleteMapping("/delete/{id}")
    public Result deleteUser(@PathVariable String id) {
        userService.deleteUser(id);
        return Result.success();
    }
}
