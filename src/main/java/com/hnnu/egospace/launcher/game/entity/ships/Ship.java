package com.hnnu.egospace.launcher.game.entity.ships;

import com.hnnu.egospace.launcher.game.core.Position;
import com.hnnu.egospace.launcher.game.entity.bullets.Bullet;

import lombok.Data;

import java.util.List;

@Data
public abstract class Ship {
    protected Position position;
    protected int health;
    protected int maxHealth;
    protected double speed;
    protected int size;
    protected boolean isAlive;

    public Ship(Position position, int health, double speed, int size) {
        this.position = position;
        this.health = health;
        this.maxHealth = health;
        this.speed = speed;
        this.size = size;
        this.isAlive = true;
    }

    public int getMaxHealth() {
        return maxHealth;
    }

    public abstract void update();
    public abstract void takeDamage(int damage);
    public abstract List<Bullet> attack();
}