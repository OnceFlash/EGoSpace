package com.hnnu.egospace.launcher.service;

import com.github.pagehelper.PageHelper;

import com.github.pagehelper.PageInfo;
import com.hnnu.egospace.launcher.entity.Feedback;
import com.hnnu.egospace.launcher.entity.Params;
import com.hnnu.egospace.launcher.mapper.FeedbackMapper;

import cn.hutool.core.util.StrUtil;
import com.auth0.jwt.JWT;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.time.LocalDateTime;

@Service
@Slf4j
public class FeedbackService {
    @Resource
    private FeedbackMapper feedbackMapper;

    public void submitFeedback(Feedback feedback, HttpServletRequest request) {
        String token = request.getHeader("token");
        feedback.setUserId(StrUtil.isBlank(token) ? "VISITOR" : getUserIdFromToken(token));
        feedback.setCreatedAt(LocalDateTime.now());
        feedback.setUpdatedAt(LocalDateTime.now());
        feedbackMapper.insert(feedback);
    }

    public String saveAttachment(MultipartFile file) throws IOException {
        Feedback feedback = new Feedback();
        feedback.setAttachment(file.getBytes());
        feedback.setFileName(file.getOriginalFilename());
        feedback.setFileType(file.getContentType());
        feedbackMapper.insert(feedback);
        return feedback.getId().toString();
    }

    private String getUserIdFromToken(String token) {
        return JWT.decode(token).getAudience().get(0);
    }

    public PageInfo<Feedback> searchFeedback(Params params) {
        PageHelper.startPage(params.getPageNum(), params.getPageSize());
        return new PageInfo<>(feedbackMapper.selectAll(params));
    }

    public Feedback getFeedback(Long id) {
        return feedbackMapper.selectById(id);
    }

    public void updateFeedback(Feedback feedback) {
        feedback.setUpdatedAt(LocalDateTime.now());
        feedbackMapper.updateById(feedback);
    }

    public void deleteFeedback(Long id) {
        feedbackMapper.deleteById(id);
    }
}