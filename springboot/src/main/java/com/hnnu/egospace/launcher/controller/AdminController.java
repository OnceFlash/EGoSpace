package com.hnnu.egospace.launcher.controller;

import javax.annotation.Resource;

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
import com.hnnu.egospace.launcher.service.AdminService;


@RestController
@RequestMapping("/{lang}/admin")
public class AdminController {

    @Resource
    private AdminService adminService;

    @GetMapping("/search")
    public Result searchAdmin(Params params) {
         PageInfo<Admin> info = adminService.searchAdmin(params);
         return Result.success(info);
    }

    @PostMapping("/add")
    public Result addAdmin(@RequestBody Admin admin) {
        if (admin.getID() == null || admin.getID().trim().isEmpty()) {
            adminService.addAdmin(admin);
        }
        return Result.success();
    }

    @PostMapping("/update")
    public Result updateAdmin(@RequestBody Admin admin) {
        adminService.updateAdmin(admin);
        return Result.success();
    }
    
    @DeleteMapping("/delete/{id}")
    public Result deleteAdmin(@PathVariable String id) {
        adminService.deleteAdmin(id);
        return Result.success();
    }
    
}