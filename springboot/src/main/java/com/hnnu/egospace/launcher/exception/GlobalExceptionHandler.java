package com.hnnu.egospace.launcher.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import com.hnnu.egospace.launcher.common.Result;

import javax.servlet.http.HttpServletRequest;

@ControllerAdvice(basePackages = "com.egospace.launcher.controller.AdminController")
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(Exception.class)
    @ResponseBody
    public Result error(HttpServletRequest request, Exception e) {
        log.error("Exception: ", e);
        return Result.error("Internal server error");
    }

    @ExceptionHandler(CustomException.class)
    @ResponseBody
    public Result customError(HttpServletRequest request, CustomException e) {
        return Result.error(e.getMsg());
    }

}
