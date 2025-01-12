package com.hnnu.egospace.launcher.game.entity.ships;

import com.hnnu.egospace.launcher.game.config.GameConfig;
import com.hnnu.egospace.launcher.game.core.Position;
import com.hnnu.egospace.launcher.game.entity.bullets.Bullet;

import java.util.ArrayList;
import java.util.List;

public class Player extends Ship {
    private long lastShotTime;
    private int bulletCount = 1;
    private int bulletSize = GameConfig.BULLET_SIZE;
    private int bulletDamage = GameConfig.BULLET_DAMAGE;
    private boolean hasShield = false;
    private int speedBoost = 0;
    private long shieldDuration = 0;
    private long speedBoostDuration = 0;

    public Player() {
        super(new Position(GameConfig.CANVAS_WIDTH/2, GameConfig.CANVAS_HEIGHT - 50),
              GameConfig.PLAYER_HEALTH,
              GameConfig.PLAYER_SPEED,
              GameConfig.PLAYER_SIZE);
        this.bulletDamage = GameConfig.BULLET_DAMAGE;
    }

    @Override
    public void update() {
        // Update shield status
        if (hasShield && System.currentTimeMillis() > shieldDuration) {
            hasShield = false;
        }
        
        // Update speed boost
        if (System.currentTimeMillis() > speedBoostDuration) {
            speedBoost = 0;
        }
        
        // Position bounds check
        if (position.getX() < size/2) position.setX(size/2);
        if (position.getX() > GameConfig.CANVAS_WIDTH - size/2) 
            position.setX(GameConfig.CANVAS_WIDTH - size/2);
    }

    @Override
    public void takeDamage(int damage) {
        if (!hasShield) {
            health -= damage;
            isAlive = health > 0;
        }
    }

    public boolean canShoot() {
        return System.currentTimeMillis() - lastShotTime > GameConfig.PLAYER_SHOOT_COOLDOWN;
    }

    public List<Bullet> shoot() {
        if (!canShoot()) return new ArrayList<>();
        
        lastShotTime = System.currentTimeMillis();
        List<Bullet> bullets = new ArrayList<>();
        
        // Calculate bullet positions based on bulletCount
        double[] xOffsets = {0}; // Single bullet
        if (bulletCount == 2) xOffsets = new double[]{-0.5, 0.5};
        if (bulletCount == 3) xOffsets = new double[]{-1, 0, 1};
        
        for (double xOffset : xOffsets) {
            bullets.add(new Bullet(
                new Position(
                    position.getX() + xOffset * size/2,
                    position.getY() - size/2
                ),
                GameConfig.BULLET_SPEED,
                bulletDamage,
                bulletSize,
                true
            ));
        }
        
        return bullets;
    }

    public double getActualSpeed() {
        return speed + speedBoost;
    }

    // Getters and setters
    public int getBulletCount() { return bulletCount; }
    public void setBulletCount(int count) { this.bulletCount = count; }
    public int getBulletSize() { return bulletSize; }
    public void setBulletSize(int size) { this.bulletSize = size; }
    public int getBulletDamage() { return bulletDamage; }
    public void setBulletDamage(int damage) { this.bulletDamage = damage; }
    public boolean hasShield() { return hasShield; }
    public void setHasShield(boolean shield) { this.hasShield = shield; }
    public void setShieldDuration(long duration) { this.shieldDuration = duration; }
    public void setSpeedBoost(int boost) { this.speedBoost = boost; }
    public void setSpeedBoostDuration(long duration) { this.speedBoostDuration = duration; }

    @Override
    public List<Bullet> attack() {
        return shoot();
    }
}