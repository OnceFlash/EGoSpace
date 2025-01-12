package com.hnnu.egospace.launcher.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.hnnu.egospace.launcher.entity.Feedback;
import com.hnnu.egospace.launcher.entity.Params;

@Mapper
public interface FeedbackMapper {
    int insert(Feedback feedback);
    List<Feedback> selectAll(Params params);
    Feedback selectById(Long id);
    int updateById(Feedback feedback);
    int deleteById(Long id);
}