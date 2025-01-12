package com.hnnu.egospace.launcher.common;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;

import com.hnnu.egospace.launcher.service.AdminService;
import com.hnnu.egospace.launcher.service.UserService;
import com.hnnu.egospace.launcher.utils.ColoredLogger;
import com.hnnu.egospace.launcher.entity.Admin;
import com.hnnu.egospace.launcher.entity.User;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.Date;

@Component
public class JwtTokenUtils {

    private static final Logger logger = LoggerFactory.getLogger(JwtTokenUtils.class);

    private static AdminService staticAdminService;
    private static UserService staticUserService;

    @Resource
    private AdminService adminService;

    @Resource
    private UserService userService;

    @PostConstruct
    public void setVisitorService() {
        staticAdminService = adminService;
        staticUserService = userService;
    }

    //  admin token generation
    public static String genAdminToken(String realName, String ID) {
        ColoredLogger.banner(logger, String.format("Generate admin token for realName: %s, ID: %s", realName, ID));
        return JWT.create().withAudience(realName)
                    .withExpiresAt(DateUtil.offsetHour(new Date(), 6))
                    .sign(Algorithm.HMAC256(ID));
    }

    //  user token generation
    public static String genUserToken(String username, String id) {
        ColoredLogger.banner(logger, String.format("Generate user token for username: %s, id: %s", username, id));
        return JWT.create().withAudience(username)
            .withExpiresAt(DateUtil.offsetDay(new Date(), 2))
            .sign(Algorithm.HMAC256(id));
    }

    //  game token generation
    public static String generateGameToken(String uid, String id) {
        ColoredLogger.banner(logger, String.format("Generate game token for uid: %s, id: %s", uid, id));
        return JWT.create().withAudience(uid)
                .withExpiresAt(DateUtil.offsetDay(new Date(), 2))
                .sign(Algorithm.HMAC256(id));
    }


    //  get current admin
    public static Admin getCurrentAdmin() {
        String adminToken = null;
        try {
            HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
            adminToken = request.getHeader("adminToken");

            if (StrUtil.isBlank(adminToken)) {
                adminToken = request.getParameter("adminToken");
            }

            if (StrUtil.isBlank(adminToken)) {
                ColoredLogger.warning(logger, String.format("Get login fail, adminToken=%s", adminToken));
                return null;
            }

            String ID = JWT.decode(adminToken).getAudience().get(0);

            ColoredLogger.banner(logger, String.format("Get admin info, adminToken=%s", adminToken));
            return staticAdminService.checkID(ID);

        } catch (Exception e) {
            ColoredLogger.banner(logger, "Get admin info fail, adminToken={}", adminToken, e);
            return null;
        }
    }

    //  get current user
    public static User getCurrentUser() {
        String token = null;
        try {
            HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
            token = request.getHeader("token");

            if (StrUtil.isBlank(token)) {
                token = request.getParameter("token");
            }

            if (StrUtil.isBlank(token)) {
                ColoredLogger.warning(logger, String.format("Get login fail, token=%s", token));
                return null;
            }

            String id = JWT.decode(token).getAudience().get(0);

            ColoredLogger.banner(logger, String.format("Get user info, token=%s", token));
            return staticUserService.checkId(id);

        } catch (Exception e) {
            ColoredLogger.banner(logger, "Get user info fail, token={}", token, e);
            return null;
        }
    }

    //  get current game player
    public static User getCurrentPlayer() {
        String gameToken = null;
        try {
            HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
            gameToken = request.getHeader("gameToken");

            if (StrUtil.isBlank(gameToken)) {
                gameToken = request.getParameter("gameToken");
            }

            if (StrUtil.isBlank(gameToken)) {
                ColoredLogger.warning(logger, String.format("Get login fail, gameToken=%s", gameToken));
                return null;
            }

            String id = JWT.decode(gameToken).getAudience().get(0);

            ColoredLogger.banner(logger, String.format("Get game info, gameToken=%s", gameToken));
            return staticUserService.checkId(id);

        } catch (Exception e) {
            ColoredLogger.banner(logger, "Get game info fail, gameToken={}", gameToken, e);
            return null;
        }
    }
}