package com.hnnu.egospace.launcher.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ControllerAdvice;

import com.hnnu.egospace.launcher.common.Result;
import com.hnnu.egospace.launcher.utils.ColoredLogger;

import javax.servlet.http.HttpServletRequest;

@ControllerAdvice(basePackages = "com.hnnu.egospace.launcher.controller")
public class GlobalExceptionHandler {
    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(Exception.class)
    @ResponseBody
    public Result handleException(HttpServletRequest request, Exception e) {
        if (e instanceof NullPointerException) {
            ColoredLogger.error(logger, "NullPointerException: {}", e.getMessage(), e);
            return Result.error("1", "Resource not found or access denied");
        } else {
            ColoredLogger.error(logger, "System error: {}", e.getMessage(), e);
            return Result.error("-1", "Internal server error");
        }
    }

    @ExceptionHandler(CustomException.class)
    @ResponseBody
    public Result handleCustomException(HttpServletRequest request, CustomException e) {
        String msg = e.getMsg();
        if (msg == null) {
            msg = "Operation failed";
        }
        ColoredLogger.warning(logger, "Business error: {}", msg);
        return Result.error("1", msg);
    }

}