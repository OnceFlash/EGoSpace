package com.hnnu.egospace.launcher.utils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.slf4j.Logger;


public class ColoredLogger {
    public static final String RED = "\u001B[91m";          // "\u001B[31m"
    public static final String GREEN = "\u001B[92m";        // "\u001B[32m"
    public static final String BLUE = "\u001B[94m";         // "\u001B[34m"
    public static final String YELLOW = "\u001B[93m";     // "\u001B[33m"
    public static final String PURPLE = "\u001B[95m";       // "\u001B[35m"
    public static final String GRAY = "\u001B[90m";    
    public static final String RESET = "\u001B[0m";         // "\u001B[0m"
    public static final String CYAN = "\u001B[96m";         // "\u001B[36m"
    public static final String WHITE = "\u001B[97m";        // "\u001B[37m"

    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");

    private static String getLocation() {
        StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
        if (stackTrace.length >= 4) {
            StackTraceElement element = stackTrace[3];
            return String.format("at %s.%s(%s:%d)", 
                element.getClassName(),
                element.getMethodName(),
                element.getFileName(),
                element.getLineNumber());
        }
        return "";
    }

    public static void error(Logger logger, String message) {
        System.out.println(GRAY + getTimestamp() + RESET + PURPLE + " [ERROR] " + RESET +
            RED + message + RESET + CYAN + " " + getLocation() + RESET);
    }

    public static void success(Logger logger, String message) {
        System.out.println(GRAY + getTimestamp() + RESET + PURPLE + " [SUCCESS] " + RESET +
            GREEN + message + RESET + CYAN + " " + getLocation() + RESET);
    }

    public static void banner(Logger logger, String message) {
        System.out.println(GRAY + getTimestamp() + RESET + PURPLE + " [BANNER] " + RESET +
            BLUE + message + RESET + CYAN + " " + getLocation() + RESET);
    }

    public static void warning(Logger logger, String message) {
        System.out.println(GRAY + getTimestamp() + RESET + PURPLE + " [WARNING] " + RESET +
            YELLOW + message + RESET + CYAN + " " + getLocation() + RESET);
    }

    public static void info(Logger logger, String message) {
        System.out.println(GRAY + getTimestamp() + RESET + PURPLE + " [INFO] " + RESET +
            WHITE + message + RESET + CYAN + " " + getLocation() + RESET);
    }

    private static String getTimestamp() {
        return LocalDateTime.now().format(formatter);
    }

    //  game related logs
    public static void error(Logger logger, String string, Exception e) {
        System.out.println(GRAY + getTimestamp() + RESET + PURPLE + " [ERROR] " + RESET + RED + string + RESET);
        e.printStackTrace();
    }

    public static void banner(Logger logger, String string, String token) {
        System.out.println(GRAY + getTimestamp() + RESET + PURPLE + " [BANNER] " + RESET + BLUE + string + RESET);
    }

    public static void banner(Logger logger, String string, String token, Exception e) {
        System.out.println(GRAY + getTimestamp() + RESET + PURPLE + " [BANNER] " + RESET + BLUE + string + RESET);
        e.printStackTrace();
    }

    public static void warning(Logger logger, String string, Exception e) {
        System.out.println(GRAY + getTimestamp() + RESET + PURPLE + " [WARNING] " + RESET + YELLOW + string + RESET);
        e.printStackTrace();
    }

        //          // Before
        // ColoredLogger.banner(logger, "Generate user token for: {}", username);

        //          // After
        // ColoredLogger.banner(logger, String.format("Generate user token for: %s", username));

    //  CostumeException
    public static void error(Logger logger, String string, String message, Exception e) {
        System.out.println(GRAY + getTimestamp() + RESET + PURPLE + " [ERROR] " + RESET + RED + string + RESET);
        e.printStackTrace();
    }

    public static void warning(Logger logger, String string, String msg) {
        System.out.println(GRAY + getTimestamp() + RESET + PURPLE + " [WARNING] " + RESET + YELLOW + string + RESET);
    }
        
}