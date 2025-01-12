package com.hnnu.egospace.launcher.entity;

import java.time.LocalDateTime;

import lombok.Data;

@Data
public class Feedback {
    private Long id;
    private String userId;
    private String category;
    private Integer priority;
    private String title;
    private String content;
    private byte[] attachment;
    private String fileName;
    private String fileType;
    private String contactType;
    private String contactInfo;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}