package com.hnnu.egospace.launcher.controller;

import com.hnnu.egospace.launcher.common.Result;
import com.hnnu.egospace.launcher.entity.Feedback;
import com.hnnu.egospace.launcher.entity.Params;
import com.hnnu.egospace.launcher.exception.CustomException;
import com.hnnu.egospace.launcher.service.FeedbackService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.http.MediaType;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import com.hnnu.egospace.launcher.utils.ColoredLogger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cn.hutool.core.util.StrUtil;
import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/feedback")
@Slf4j
public class FeedbackController {
    private static final Logger logger = LoggerFactory.getLogger(FeedbackController.class);
    private static final long MAX_FILE_SIZE = 5 * 1024 * 1024; // 5MB
    private static final List<String> ALLOWED_TYPES = Arrays.asList("image/jpeg", "image/png", "application/pdf");

    @Resource
    private FeedbackService feedbackService;

    @PostMapping("/submit")
    public Result submitFeedback(@RequestBody Feedback feedback, HttpServletRequest request) {
        try {
            ColoredLogger.info(logger, "Processing feedback submission: category=" + 
                             feedback.getCategory() + ", title=" + feedback.getTitle());
            validateFeedback(feedback);
            feedbackService.submitFeedback(feedback, request);
            return Result.success("Feedback submitted successfully");
        } catch (Exception e) {
            ColoredLogger.error(logger, "Failed to submit feedback", e);
            return Result.error(e.getMessage());
        }
    }

    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Result uploadAttachment(@RequestParam("file") MultipartFile file) {
        try {
            validateFile(file);
            String fileId = feedbackService.saveAttachment(file);
            return Result.success(fileId);
        } catch (Exception e) {
            ColoredLogger.error(logger, "File upload failed", e);
            return Result.error(e.getMessage());
        }
    }

    private void validateFeedback(Feedback feedback) {
        if (StrUtil.isBlank(feedback.getTitle()) || 
            StrUtil.isBlank(feedback.getContent()) ||
            StrUtil.isBlank(feedback.getContactInfo())) {
            throw new CustomException("Required fields missing");
        }
    }

    private void validateFile(MultipartFile file) {
        if (file.isEmpty() || file.getSize() > MAX_FILE_SIZE || 
            !ALLOWED_TYPES.contains(file.getContentType())) {
            throw new CustomException("Invalid file");
        }
    }

    @GetMapping("/search")
    public Result searchFeedback(Params params) {
        return Result.success(feedbackService.searchFeedback(params));
    }

    @GetMapping("/{id}")
    public Result getFeedback(@PathVariable Long id) {
        return Result.success(feedbackService.getFeedback(id));
    }

    @PostMapping("/update")
    public Result updateFeedback(@RequestBody Feedback feedback) {
        feedbackService.updateFeedback(feedback);
        return Result.success("Feedback updated successfully");
    }

    @DeleteMapping("/delete/{id}")
    public Result deleteFeedback(@PathVariable Long id) {
        feedbackService.deleteFeedback(id);
        return Result.success("Feedback deleted successfully");
    }
}