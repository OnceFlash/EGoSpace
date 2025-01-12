package com.hnnu.egospace.launcher.controller;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.github.pagehelper.PageInfo;
import com.hnnu.egospace.launcher.common.Result;
import com.hnnu.egospace.launcher.entity.Admin;
import com.hnnu.egospace.launcher.entity.Params;
import com.hnnu.egospace.launcher.entity.User;
import com.hnnu.egospace.launcher.exception.CustomException;
import com.hnnu.egospace.launcher.service.AdminService;
import com.hnnu.egospace.launcher.service.UserService;
import com.hnnu.egospace.launcher.utils.ColoredLogger;


@RestController
@RequestMapping("/backend/{lang}")
public class AdminController {

    private static final Logger logger = LoggerFactory.getLogger(AdminController.class);

//back-end admin management

    // admin management
    @Resource
    private AdminService adminService;

    @GetMapping("/admin/search")
    public Result searchAdmin(Params params) {
        PageInfo<Admin> info = adminService.searchAdmin(params);
        ColoredLogger.success(logger, "Admin search successful");
        return Result.success(info);
    }

    @PostMapping("/admin/add")
    public Result addAdmin(@RequestBody Admin admin) {
        if (admin.getID() == null || admin.getID().trim().isEmpty()) {
            adminService.addAdmin(admin);
        }
        return Result.success();
    }

    @PostMapping("/admin/update")
    public Result updateAdmin(@RequestBody Admin admin) {
        adminService.updateAdmin(admin);
        return Result.success();
    }
    
    @DeleteMapping("/admin/delete/{id}")
    public Result deleteAdmin(@PathVariable String id) {
        adminService.deleteAdmin(id);
        return Result.success();
    }

    @PostMapping("/admin/login")
    public Result login(@RequestBody Admin admin, @PathVariable String lang) {
        try {
            Admin result = adminService.login(admin);
            return Result.success(result);
        } catch (CustomException e) {
            throw new CustomException("admin.notFound");
        }
    }
    
    // user management
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

}