package com.hnnu.egospace.launcher.game.entity.ships;

import com.hnnu.egospace.launcher.game.config.GameConfig;
import com.hnnu.egospace.launcher.game.core.Position;
import com.hnnu.egospace.launcher.game.entity.bullets.Bullet;
import com.hnnu.egospace.launcher.utils.ColoredLogger;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Data
public class Enemy extends Ship {
    private static final Logger logger = LoggerFactory.getLogger(Enemy.class);
    private static final AtomicInteger ID_GENERATOR = new AtomicInteger(0);
    private final int id;
    private EnemyType type;
    protected int scoreValue;
    private long lastShotTime;
    private static final int SHOOT_COOLDOWN = 2000;
    private boolean isCharging = false;
    private Position targetPosition = null;
    private static final double CHARGE_TRIGGER_DISTANCE = 200;
    private static final double SHOOT_CHANCE = 0.01; // 1% chance per update to shoot
    private long lastShootTime = 0;
    private Enemy parent; // For escorts
    private double orbitAngle = 0;
    private double baseX; // Base X position for boss movement
    private long lastEscortSpawnTime = 0;
    private List<Enemy> escorts = new ArrayList<>();
    private double horizontalSpeed;
    private double verticalSpeed;

    public Enemy(Position position, EnemyType type) {
        super(position, 
            type == EnemyType.BASIC ? GameConfig.ENEMY_BASIC_HEALTH :
            type == EnemyType.ESCORT ? GameConfig.ENEMY_ESCORT_HEALTH :
            type == EnemyType.BOSS ? GameConfig.ENEMY_BOSS_HEALTH : GameConfig.ENEMY_BASIC_HEALTH,
            
            type == EnemyType.BASIC ? GameConfig.ENEMY_BASIC_SPEED :
            type == EnemyType.ESCORT ? GameConfig.ENEMY_ESCORT_SPEED :
            type == EnemyType.BOSS ? GameConfig.ENEMY_BOSS_SPEED : GameConfig.ENEMY_BASIC_SPEED,
            
            type == EnemyType.BASIC ? GameConfig.ENEMY_BASIC_SIZE :
            type == EnemyType.ESCORT ? GameConfig.ENEMY_ESCORT_SIZE :
            type == EnemyType.BOSS ? GameConfig.ENEMY_BOSS_SIZE : GameConfig.ENEMY_BASIC_SIZE
        );
        this.id = ID_GENERATOR.incrementAndGet();
        this.type = type;
        this.scoreValue = 
            type == EnemyType.BASIC ? 100 :
            type == EnemyType.ESCORT ? 200 :
            type == EnemyType.BOSS ? 1000 : 100;
    }

    @Override
    public void update() {
        if (!isAlive) return;
        
        if (type == EnemyType.BASIC) {
            updateBasicEnemy();
            tryShoot();
        } else if (type == EnemyType.ESCORT) {
            updateEscort();
            tryShoot();
        } else if (type == EnemyType.BOSS) {
            updateBoss();
        } else {
            // Original movement for other types
            position.setY(position.getY() + speed);
        }
        
        if (type == EnemyType.ESCORT) {
            position.setX(position.getX() + Math.sin(System.currentTimeMillis() / 1000.0) * speed/2);
        }
        
        if (position.getY() > GameConfig.CANVAS_HEIGHT + size) {
            isAlive = false;
        }
    }

    private void updateBasicEnemy() {
        if (!isCharging) {
            // Smooth vertical movement
            position.setY(position.getY() + GameConfig.BASIC_ENEMY_SPEED);
            // Limited horizontal movement
            position.setX(position.getX() + 
                Math.sin(position.getY() * GameConfig.BASIC_ENEMY_WAVE_FREQUENCY) * 
                GameConfig.BASIC_ENEMY_WAVE_AMPLITUDE);
        }
    }

    private void updateEscort() {
        if (parent != null && parent.isAlive()) {
            orbitAngle += GameConfig.ESCORT_ORBIT_SPEED;
            position.setX(parent.getPosition().getX() + 
                Math.cos(orbitAngle) * GameConfig.ESCORT_ORBIT_RADIUS);
            position.setY(parent.getPosition().getY() + 
                Math.sin(orbitAngle) * GameConfig.ESCORT_ORBIT_RADIUS);
        } else {
            isCharging = true;
            updateBasicEnemy();
        }
    }

    private void updateBoss() {
        // Boss movement
        position.setX(baseX + Math.sin(System.currentTimeMillis() * 0.001) * 200);
        
        // Escort management
        if (escorts.size() < GameConfig.MAX_ESCORTS && 
            System.currentTimeMillis() - lastEscortSpawnTime > GameConfig.ESCORT_RESPAWN_DELAY) {
            spawnEscort();
        }
        
        // Remove dead escorts
        escorts.removeIf(escort -> !escort.isAlive());
    }

    private void tryShoot() {
        if (System.currentTimeMillis() - lastShootTime > GameConfig.ENEMY_SHOOT_COOLDOWN 
            && Math.random() < SHOOT_CHANCE) {
            attack();
            lastShootTime = System.currentTimeMillis();
        }
    }

    @Override
    public void takeDamage(int damage) {
        health -= damage;
        isAlive = health > 0;
    }

    public int getScoreValue() {
        return GameConfig.BASIC_ENEMY_SCORE;
    }

    @Override
    public List<Bullet> attack() {
        List<Bullet> bullets = new ArrayList<>();
        lastShootTime = System.currentTimeMillis();

        switch(type) {
            case BASIC -> {
                bullets.add(createBullet(180)); // Straight down
            }
            case ESCORT -> {
                bullets.add(createBullet(165)); // -15 degrees
                bullets.add(createBullet(195)); // +15 degrees
            }
            case BOSS -> {
                for (int angle : new int[]{160, 180, 200}) {
                    bullets.add(createBullet(angle, true, 1.5));
                }
            }
            case MOTHERSHIP -> {
                for (int i = 0; i < 8; i++) {
                    double angle = i * 45;
                    bullets.add(createBullet(angle, true, 2.0));
                }
            }
        }
        
        ColoredLogger.info(logger, String.format("Enemy %s (%s) fired %d bullets", 
            getId(), type, bullets.size()));
        return bullets;
    }

    private Bullet createBullet(double angle) {
        return createBullet(angle, false, 1.0);
    }

    private Bullet createBullet(double angle, boolean hasEffect, double powerMultiplier) {
        // Normalize angle (0° = right, 90° = down)
        double normalizedAngle = angle;
        if (type == EnemyType.BASIC || type == EnemyType.ESCORT) {
            normalizedAngle = 90; // Always down for basic/escort
        }
        
        // Convert to radians for position calculation
        double rad = Math.toRadians(normalizedAngle);
        
        // Calculate spawn offset from enemy center
        double offsetX = Math.cos(rad) * size/2;
        double offsetY = Math.sin(rad) * size/2;
        
        Position bulletPos = new Position(position.getX() + offsetX, position.getY() + offsetY);
        
        return new Bullet(
            bulletPos,
            (int)(GameConfig.BULLET_SPEED * powerMultiplier),
            (int)(GameConfig.ENEMY_BULLET_DAMAGE * powerMultiplier),
            (int)(GameConfig.BULLET_SIZE * (hasEffect ? 1.5 : 1.0)),
            false,  // isFromPlayer
            normalizedAngle,  // angle in degrees
            hasEffect
        );
    }
    
    public EnemyType getType() {
        return type;
    }

    public void setPlayerPosition(Position playerPos) {
        this.targetPosition = playerPos;
    }

    public int getId() {
        return id;
    }

    private void spawnEscort() {
        Enemy escort = new Enemy(position.clone(), EnemyType.ESCORT);
        escort.setParent(this);
        escort.setOrbitAngle(Math.PI * 2 * escorts.size() / GameConfig.MAX_ESCORTS);
        escorts.add(escort);
        lastEscortSpawnTime = System.currentTimeMillis();
    }

    public void setHorizontalSpeed(double speed) {
        this.horizontalSpeed = Math.min(speed, verticalSpeed * 0.4);
    }
    
    public void setVerticalSpeed(double speed) {
        this.verticalSpeed = speed;
        // Ensure horizontal speed stays within ratio
        this.horizontalSpeed = Math.min(horizontalSpeed, verticalSpeed * 0.4);
    }
    
    @Override
    public void setSpeed(double speed) {
        this.verticalSpeed = speed;
        this.horizontalSpeed = speed * 0.4;
    }
}
