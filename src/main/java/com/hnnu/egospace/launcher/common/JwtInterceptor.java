package com.hnnu.egospace.launcher.common;

import cn.hutool.core.util.StrUtil;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;

import com.hnnu.egospace.launcher.entity.Admin;
import com.hnnu.egospace.launcher.entity.User;
import com.hnnu.egospace.launcher.exception.CustomException;
import com.hnnu.egospace.launcher.service.AdminService;
import com.hnnu.egospace.launcher.service.UserService;
import com.hnnu.egospace.launcher.utils.ColoredLogger;

import java.util.Arrays;
import java.util.List;


@Component
public class JwtInterceptor implements HandlerInterceptor {
    
    private static final Logger logger = LoggerFactory.getLogger(JwtInterceptor.class);

    private static final List<String> PUBLIC_PATHS = Arrays.asList(
        "/media",
        "/upload", 
        "/feedback/upload"
    );

    // admin token verification
    @Resource
    private AdminService adminService;

    // user token verification
    @Resource
    private UserService userService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        String path = request.getRequestURI();
        ColoredLogger.info(logger, "Processing request for path: " + path);
        
        // Check if path is public
        if (PUBLIC_PATHS.stream().anyMatch(path::startsWith)) {
            ColoredLogger.info(logger, "Allowing public access path: " + path);
            return true;
        }

        try {
            if (path.startsWith("/backend")) {
                ColoredLogger.info(logger, "Validating admin token for backend request");
                validateAdminToken(request);
            } else if (!path.startsWith("/media")) {
                ColoredLogger.info(logger, "Validating user tokens for protected request");
                validateUserTokens(request);
            }
            return true;

        } catch (CustomException e) {
            ColoredLogger.error(logger, "Token validation failed with CustomException: " + e.getMessage());
            throw e;
        } catch (Exception e) {
            ColoredLogger.error(logger, "Unexpected error during token validation", e);
            throw new CustomException("Token validation failed");
        }
    }

    private void validateAdminToken(HttpServletRequest request) {
        String adminToken = getToken(request, "adminToken");
        
        if (StrUtil.isBlank(adminToken)) {
            String errMsg = "No adminToken provided";
            ColoredLogger.warning(logger, errMsg);
            throw new CustomException(errMsg);
        }

        try {
            String realName = JWT.decode(adminToken).getAudience().get(0);
            ColoredLogger.info(logger, "Decoded admin real name: " + realName);
            
            Admin admin = adminService.checkRealName(realName);
            if (admin == null) {
                String errMsg = "Admin not found";
                ColoredLogger.error(logger, errMsg);
                throw new CustomException(errMsg);
            }

            JWTVerifier jwtVerifier = JWT.require(Algorithm.HMAC256(admin.getID())).build();
            jwtVerifier.verify(adminToken);
            ColoredLogger.success(logger, "Admin token verified successfully");
            
        } catch (JWTVerificationException e) {
            String errMsg = "Invalid admin token, login again";
            ColoredLogger.warning(logger, errMsg + "! \nadmin token=" + adminToken);
            throw new CustomException(errMsg);
        } catch (Exception e) {
            String errMsg = "Admin token validation failed";
            ColoredLogger.error(logger, errMsg, e);
            throw new CustomException(errMsg);
        }
    }


    private String getToken(HttpServletRequest request, String tokenName) {
        String token = request.getHeader(tokenName);
        if (StrUtil.isBlank(token)) {
            token = request.getParameter(tokenName);
        }
        return token;
    }

    private void validateTokenExistence(String token, String tokenType) {
        if (StrUtil.isBlank(token)) {
            ColoredLogger.warning(logger, "Missing " + tokenType);
            throw new CustomException("Missing " + tokenType);
        }
    }

    private void validateUserTokens(HttpServletRequest request) {
        String token = getToken(request, "token");
        String gameToken = getToken(request, "gameToken");
        
        validateTokenExistence(token, "user token");
        validateTokenExistence(gameToken, "game token");
        
        try {
            // Get user data
            String username = JWT.decode(token).getAudience().get(0);
            String uid = JWT.decode(gameToken).getAudience().get(0);
            
            User user = userService.checkUsernameT(username);
            if (user == null) {
                String errMsg = "User not found for username: " + username;
                ColoredLogger.error(logger, errMsg);
                throw new CustomException(errMsg);
            }

            if (StrUtil.isBlank(user.getId())) {
                String errMsg = "User id is missing";
                ColoredLogger.error(logger, errMsg);
                throw new CustomException(errMsg);
            }

            // Validate user token
            ColoredLogger.info(logger, "Verifying user token for: " + username);
            JWTVerifier userVerifier = JWT.require(Algorithm.HMAC256(user.getId())).build();
            userVerifier.verify(token);
            ColoredLogger.success(logger, "User token verified successfully");

            // Validate game token
            ColoredLogger.info(logger, "Verifying game token for uid: " + uid);
            JWTVerifier gameVerifier = JWT.require(Algorithm.HMAC256(user.getId())).build();
            gameVerifier.verify(gameToken);
            ColoredLogger.success(logger, "Game token verified successfully");

        } catch (JWTVerificationException e) {
            String errMsg = "Invalid token verification, login again";
            ColoredLogger.warning(logger, errMsg + "! \ntoken=" + token + ", \ngame token=" + gameToken);
            throw new CustomException(errMsg);
        } catch (Exception e) {
            String errMsg = "Token validation failed";
            ColoredLogger.error(logger, errMsg, e);
            throw new CustomException(errMsg);
        }
    }
    
}
