package com.hnnu.egospace.launcher.game.powerup;

import com.hnnu.egospace.launcher.game.config.GameConfig;
import com.hnnu.egospace.launcher.game.core.Position;
import com.hnnu.egospace.launcher.game.entity.ships.Player;

import lombok.Data;

@Data 
public class PowerUp {
    private Position position;
    private PowerUpType type;
    private int size = GameConfig.POWERUP_SIZE;
    private float speed = GameConfig.POWERUP_SPEED;
    private static final long POWERUP_DURATION = GameConfig.POWERUP_DURATION;
    
    public PowerUp(Position position, PowerUpType type) {
        this.position = position;
        this.type = type;
    }
    
    public void update() {
        position.setY(position.getY() + speed);
    }
    
    public void applyTo(Player player) {
        switch(type) {
            case BULLET_COUNT:
                player.setBulletCount(Math.min(player.getBulletCount() + 1, GameConfig.POWERUP_BULLET_COUNT_MAX));
                break;
            case BULLET_SIZE:
                player.setBulletSize(player.getBulletSize() + GameConfig.POWERUP_BULLET_SIZE_INCREASE);
                break;
            case BULLET_DAMAGE:
                player.setBulletDamage(player.getBulletDamage() + GameConfig.POWERUP_BULLET_DAMAGE_INCREASE);
                break;
            case SHIELD:
                player.setHasShield(true);
                player.setShieldDuration(System.currentTimeMillis() + POWERUP_DURATION);
                break;
            case SPEED_BOOST:
                player.setSpeedBoost(GameConfig.POWERUP_SPEED_BOOST);
                player.setSpeedBoostDuration(System.currentTimeMillis() + POWERUP_DURATION);
                break;
        }
    }

    public boolean isOffScreen() {
        return position.getY() > GameConfig.CANVAS_HEIGHT + size;
    }
}