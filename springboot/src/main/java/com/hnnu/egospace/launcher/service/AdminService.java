package com.hnnu.egospace.launcher.service;

import java.util.List;
import java.util.UUID;

import javax.annotation.Resource;
import org.springframework.stereotype.Service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.hnnu.egospace.launcher.entity.Admin;
import com.hnnu.egospace.launcher.entity.Params;
import com.hnnu.egospace.launcher.mapper.AdminMapper;

@Service
public class AdminService{
    
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
        throw new RuntimeException("Failed to generate unique UUID after " + MAX_RETRY_COUNT + " attempts");
    }

    public void addAdmin(Admin admin) {
        String uuid = generateUniqueUUID();
        admin.setID(uuid);
        int result = adminMapper.insertAdmin(admin);
        if (result == 0) {
            throw new RuntimeException("Insert failed");
        }
    }
    
    public void updateAdmin(Admin admin) {
        int result = adminMapper.updateAdmin(admin);
        if (result == 0) {
            throw new RuntimeException("Update failed");
        }
    }

    public void deleteAdmin(String ID) {
        int result = adminMapper.deleteAdmin(ID);
        if (result == 0) {
            throw new RuntimeException("Delete failed");
        }
    }

}
