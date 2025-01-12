package com.hnnu.egospace.launcher.game.entity.bullets;

import com.hnnu.egospace.launcher.game.config.GameConfig;
import com.hnnu.egospace.launcher.game.core.Position;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class Bullet {
    private Position position;
    private double speed;
    private int damage;
    private int size;
    private boolean fromPlayer;
    private double angle;
    private boolean hasEffect;

    public boolean hasEffect() {
        return hasEffect;
    }

    private int lifespan = 300; // frames
    private boolean isSpecial;

    public Bullet(Position position, int speed, int damage, int size, boolean fromPlayer) {
        this(position, speed, damage, size, fromPlayer, 0, false);
    }

    public Bullet(Position position, int speed, int damage, int size, boolean fromPlayer, 
                 double angle, boolean isSpecial) {
        this.position = position;
        this.speed = speed;
        this.damage = damage;
        this.size = size;
        this.fromPlayer = fromPlayer;
        this.angle = angle;
        this.isSpecial = isSpecial;
    }

    public void update() {
        double rad = Math.toRadians(angle);
        position.setX(position.getX() + Math.sin(rad) * speed);
        position.setY(position.getY() + (fromPlayer ? -Math.cos(rad) * speed : Math.cos(rad) * speed));
        lifespan--;
    }

    public boolean isExpired() {
        return lifespan <= 0 || 
               position.getY() < 0 || 
               position.getY() > GameConfig.CANVAS_HEIGHT;
    }

    public Position predictPosition(int frames) {
        double rad = Math.toRadians(angle);
        double futureX = position.getX() + Math.sin(rad) * speed * frames;
        double futureY = position.getY() + (fromPlayer ? -Math.cos(rad) : Math.cos(rad)) * speed * frames;
        return new Position(futureX, futureY);
    }

    public void rotate(double deltaAngle) {
        this.angle = (this.angle + deltaAngle) % 360;
    }

    public void setTrajectory(Position target) {
        double dx = target.getX() - position.getX();
        double dy = target.getY() - position.getY();
        this.angle = Math.toDegrees(Math.atan2(dx, dy));
    }

    public static Bullet createHomingBullet(Position start, Position target, int speed, int damage, int size, boolean fromPlayer) {
        Bullet bullet = new Bullet(start, speed, damage, size, fromPlayer);
        bullet.setTrajectory(target);
        return bullet;
    }

    public static List<Bullet> createSpiralPattern(Position center, int count, double startAngle, 
                                                  int speed, int damage, int size, boolean fromPlayer) {
        List<Bullet> bullets = new ArrayList<>();
        double angleStep = 360.0 / count;
        for (int i = 0; i < count; i++) {
            double angle = startAngle + i * angleStep;
            bullets.add(new Bullet(center.clone(), speed, damage, size, fromPlayer, angle, true));
        }
        return bullets;
    }

    public static List<Bullet> createSpreadPattern(Position start, double centerAngle, 
                                                 double spreadAngle, int count,
                                                 int speed, int damage, int size, boolean fromPlayer) {
        List<Bullet> bullets = new ArrayList<>();
        if (count == 1) {
            bullets.add(new Bullet(start.clone(), speed, damage, size, fromPlayer, centerAngle, false));
            return bullets;
        }
        
        double angleStep = spreadAngle / (count - 1);
        double startAngle = centerAngle - spreadAngle/2;
        
        for (int i = 0; i < count; i++) {
            bullets.add(new Bullet(start.clone(), speed, damage, size, fromPlayer, 
                                 startAngle + i * angleStep, false));
        }
        return bullets;
    }

    public static List<Bullet> createWavePattern(Position start, double angle, int count, 
                                               double spacing, int speed, int damage, int size, 
                                               boolean fromPlayer) {
        List<Bullet> bullets = new ArrayList<>();
        Position spawnPos = start.clone();
        Position offset = Position.fromAngle(angle + 90).scale(spacing);
        
        for (int i = 0; i < count; i++) {
            bullets.add(new Bullet(spawnPos.clone(), speed, damage, size, fromPlayer, angle, false));
            spawnPos = spawnPos.add(offset);
        }
        return bullets;
    }

    public static Bullet createTrackingBullet(Position start, Position target, int speed, 
                                            int damage, int size, boolean fromPlayer) {
        Bullet bullet = new Bullet(start.clone(), speed, damage, size, fromPlayer, 0, true);
        bullet.setTrajectory(target);
        return bullet;
    }
}