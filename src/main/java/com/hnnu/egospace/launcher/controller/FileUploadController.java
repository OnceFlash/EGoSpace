package com.hnnu.egospace.launcher.controller;

import com.hnnu.egospace.launcher.common.Result;
import com.hnnu.egospace.launcher.exception.CustomException;
import com.hnnu.egospace.launcher.utils.ColoredLogger;

import lombok.extern.slf4j.Slf4j;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.bind.annotation.RequestMapping;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;


@Slf4j
@RestController
@RequestMapping("/upload")
public class FileUploadController {
    private static final Logger logger = LoggerFactory.getLogger(FileUploadController.class);
    private static final long MAX_FILE_SIZE = 5 * 1024 * 1024; // 5MB
    private static final List<String> ALLOWED_TYPES = Arrays.asList("image/jpeg", "image/png", "application/pdf");

    @PostMapping
    public Result uploadFile(@RequestParam("file") MultipartFile file) {
        ColoredLogger.info(logger, "Processing file upload: " + file.getOriginalFilename());

        try {
            // Validate file
            if (file.isEmpty()) {
                throw new CustomException("File is empty");
            }
            if (file.getSize() > MAX_FILE_SIZE) {
                throw new CustomException("File size exceeds 5MB limit");
            }
            if (!ALLOWED_TYPES.contains(file.getContentType())) {
                throw new CustomException("File type not supported");
            }

            // Process file upload
            String fileName = UUID.randomUUID() + "_" + file.getOriginalFilename();
            String filePath = "uploads/" + fileName;
            
            File dest = new File(filePath);
            if (!dest.getParentFile().exists()) {
                dest.getParentFile().mkdirs();
            }
            file.transferTo(dest);

            ColoredLogger.success(logger, "File uploaded successfully: " + fileName);
            return Result.success(filePath);

        } catch (Exception e) {
            ColoredLogger.error(logger, "File upload failed", e);
            return Result.error(e.getMessage());
        }
    }
}