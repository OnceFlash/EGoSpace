package com.hnnu.egospace.launcher.game.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EntityDTO {
    private double x;
    private double y;
    private int size;
    private String type;
    private String color;
    private int health;
    private int maxHealth;
    private boolean hasEffect;
    private double angle;
    private boolean isFromPlayer;
}