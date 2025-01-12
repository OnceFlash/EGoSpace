package com.hnnu.egospace.launcher.service;

import java.util.List;
import java.util.UUID;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.hnnu.egospace.launcher.common.JwtTokenUtils;
import com.hnnu.egospace.launcher.entity.Admin;
import com.hnnu.egospace.launcher.entity.Params;
import com.hnnu.egospace.launcher.exception.CustomException;
import com.hnnu.egospace.launcher.mapper.AdminMapper;
import com.hnnu.egospace.launcher.utils.ColoredLogger;

@Service
public class AdminService{
    
    private static final Logger logger = LoggerFactory.getLogger(AdminService.class);

    // Privilege
    public static final int RING_LEVEL = 0;
    public static final int CONFIG_LEVEL = 1;
    public static final int VISITOR_LEVEL = 3;

    // UUID
    private static final int MAX_RETRY_COUNT = 3;

    @Resource
    private AdminMapper adminMapper;

    public PageInfo<Admin> searchAdmin(Params params) {
        PageHelper.startPage(params.getPageNum(), params.getPageSize());
        List<Admin> list = adminMapper.searchAdmin(params);
        return PageInfo.of(list);
    }

    private String generateUniqueUUID() {
        String uuid;
        int retryCount = 0;
        do {
            uuid = UUID.randomUUID().toString();
            Admin existingAdmin = adminMapper.checkUUID(uuid);
            if (existingAdmin == null) {
                return uuid;
            }
            retryCount++;
        } while (retryCount < MAX_RETRY_COUNT);
            ColoredLogger.error(logger, "Failed to generate unique UUID after " + MAX_RETRY_COUNT + " attempts");
            throw new CustomException("Failed to generate unique UUID after " + MAX_RETRY_COUNT + " attempts");
    }

    public void addAdmin(Admin admin) {
        Admin currentAdmin = JwtTokenUtils.getCurrentAdmin();
        if (currentAdmin.getPrivilege() > CONFIG_LEVEL) {
            ColoredLogger.error(logger, "Insufficient privileges for add");
            throw new CustomException("Only super admin can add admins");
        }

        admin.setPrivilege(VISITOR_LEVEL);
        String uuid = generateUniqueUUID();
        admin.setID(uuid);
        int result = adminMapper.insertAdmin(admin);
        if (result == 0) {
            ColoredLogger.error(logger, "Insert failed");
            throw new CustomException("Insert failed");
        }

        ColoredLogger.success(logger, "Admin added successfully with ID: " + uuid);
    }
    
    public void updateAdmin(Admin admin) {
        Admin currentAdmin = JwtTokenUtils.getCurrentAdmin();
        if (currentAdmin.getPrivilege() > CONFIG_LEVEL) {
            ColoredLogger.error(logger, "Insufficient privileges for update");
            throw new CustomException("Insufficient privileges for update");
        }

        int result = adminMapper.updateAdmin(admin);
        if (result == 0) {
            ColoredLogger.error(logger, "Update failed");
            throw new CustomException("Update failed");
        }
    }

    public void deleteAdmin(String ID) {
        Admin currentAdmin = JwtTokenUtils.getCurrentAdmin();
        if (currentAdmin.getPrivilege() != RING_LEVEL) {
            ColoredLogger.error(logger, "Insufficient privileges for delete");
            throw new CustomException("Only super admin can delete admins");
        }

        Admin admin = adminMapper.checkUUID(ID);
        if (admin == null) {
            ColoredLogger.error(logger, "Admin not found");
            throw new CustomException("Admin not found");
        }

        int result = adminMapper.deleteAdmin(ID);
        if (result == 0) {
            ColoredLogger.error(logger, "Delete failed");
            throw new CustomException("Delete failed");
        }
    }

     public Admin login(Admin admin) {
        ColoredLogger.info(logger, "Attempting login backend with: " + "realName="
        + admin.getRealName() + ", " + "ID=" + admin.getID() + ", " + "privilege=" +admin.getPrivilege());

        Admin visitor = adminMapper.checkAdmin(
            admin.getRealName(),
            admin.getID(),
            admin.getPrivilege()
        );
        if (visitor == null) {
            ColoredLogger.error(logger, "Invalid admin credentials");
            throw new CustomException("Invalid admin credentials");
        }

        String adminToken = JwtTokenUtils.genAdminToken(visitor.getRealName(), visitor.getID());
        visitor.setAdminToken(adminToken);
        return visitor;
    }

    public Admin checkID(String ID) {
        return adminMapper.checkID(ID);
    }

    public Admin checkRealName(String realName) {
        return adminMapper.checkRealName(realName);
    }
}
