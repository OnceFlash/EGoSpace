package com.hnnu.egospace.launcher.mapper;

import com.hnnu.egospace.launcher.entity.Admin;
import com.hnnu.egospace.launcher.entity.Params;

import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;
import tk.mybatis.mapper.common.Mapper;
import java.util.List;


@Repository
public interface AdminMapper extends Mapper<Admin> {
    
    //@Select("select * from user_info")
    List<Admin> searchAdmin(@Param("params")Params params);
    
    Admin checkUUID(@Param("ID") String ID);
    int insertAdmin(Admin user);

    int updateAdmin(Admin user);
    int deleteAdmin(String ID);

}
