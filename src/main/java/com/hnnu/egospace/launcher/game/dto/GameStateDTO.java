package com.hnnu.egospace.launcher.game.dto;

import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.ArrayList;
import java.util.Objects;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GameStateDTO {
    private List<EntityDTO> enemies;
    private List<EntityDTO> bullets;
    private EntityDTO player;
    private int score;
    private int wave;
    private boolean gameOver;
    

}