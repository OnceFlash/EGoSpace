package com.hnnu.egospace.launcher.game.command;


import lombok.Data;

@Data
public class Command {
    private CommandType type;
    private String key;

    public enum CommandType {
        MOVE_LEFT,
        MOVE_RIGHT,
        SHOOT,
        START_GAME,
        PAUSE_GAME
    }
}
