package com.hnnu.egospace.launcher;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.mybatis.spring.annotation.MapperScan;
import com.hnnu.egospace.launcher.utils.ColoredLogger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@SpringBootApplication
@MapperScan("com.hnnu.egospace.launcher.mapper")
public class LauncherApplication {

    private static final Logger logger = LoggerFactory.getLogger(LauncherApplication.class);

    public static void main(String[] args) {
        SpringApplication.run(LauncherApplication.class, args);

            ColoredLogger.banner(logger, "********************************************************");
            ColoredLogger.success(logger, "Spring Boot project was started successfully in 8081");
            ColoredLogger.success(logger, "Created by EGoSpace, Yu is the project owner");
            ColoredLogger.banner(logger, "********************************************************");
            
            // logger.info("Spring Boot project was started successful in 8081");
            // logger.warn("Created by EGoSpace, Yu is the project owner");
            // logger.error("********************************************************");
        
    }
}