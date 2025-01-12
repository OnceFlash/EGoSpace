package com.hnnu.egospace.launcher.game.entity.ships;

import com.hnnu.egospace.launcher.game.config.GameConfig;
import com.hnnu.egospace.launcher.game.core.Position;
import com.hnnu.egospace.launcher.game.entity.bullets.Bullet;

import java.util.ArrayList;
import java.util.List;

public class Boss extends Enemy {
    private int phase = 1;
    private long lastAttackTime;
    private static final int ATTACK_COOLDOWN = GameConfig.BOSS_ATTACK_COOLDOWN;
    private static final int MAX_HEALTH = GameConfig.BOSS_MAX_HEALTH;
    

    public Boss(Position position) {
        super(position, EnemyType.BOSS);
        this.health = MAX_HEALTH;
        this.speed = GameConfig.ENEMY_BASIC_SPEED / 2;
        this.size = GameConfig.ENEMY_BASIC_SIZE * 3;
        this.scoreValue = GameConfig.BOSS_BASE_SCORE;
    }

    @Override
    public void update() {
        updatePhase();
        updateMovement();
    }

    private void updatePhase() {
        int oldPhase = phase;
        if (health > MAX_HEALTH * 0.7) phase = 1;
        else if (health > MAX_HEALTH * 0.3) phase = 2;
        else phase = 3;
        
        // Update score value when phase changes
        if (oldPhase != phase) {
            scoreValue = (int)(GameConfig.BOSS_BASE_SCORE * 
                Math.pow(GameConfig.BOSS_PHASE_SCORE_MULTIPLIER, phase - 1));
        }
    }

    private void updateMovement() {
        if (position.getY() < size) {
            position.setY(position.getY() + speed);
        } else {
            double time = System.currentTimeMillis() / (1000.0 * phase); // Faster movement in later phases
            position.setX(position.getX() + Math.sin(time) * speed);
        }
        
        // Keep boss in bounds
        if (position.getX() < size) position.setX(size);
        if (position.getX() > GameConfig.CANVAS_WIDTH - size) {
            position.setX(GameConfig.CANVAS_WIDTH - size);
        }
    }

    public List<Bullet> attack() {
        if (!canAttack()) return new ArrayList<>();
        
        lastAttackTime = System.currentTimeMillis();
        return switch(phase) {
            case 1 -> createPhaseOneAttack();
            case 2 -> createPhaseTwoAttack();
            case 3 -> createPhaseThreeAttack();
            default -> new ArrayList<>();
        };
    }

    private boolean canAttack() {
        return System.currentTimeMillis() - lastAttackTime >= ATTACK_COOLDOWN;
    }

    private List<Bullet> createPhaseOneAttack() {
        return List.of(createBullet(0));
    }

    private List<Bullet> createPhaseTwoAttack() {
        List<Bullet> bullets = new ArrayList<>();
        bullets.add(createBullet(-15));
        bullets.add(createBullet(15));
        return bullets;
    }

    private List<Bullet> createPhaseThreeAttack() {
        List<Bullet> bullets = new ArrayList<>();
        for (int angle = -30; angle <= 30; angle += 15) {
            bullets.add(createBullet(angle));
        }
        return bullets;
    }

    private Bullet createBullet(double angle) {
        double rad = Math.toRadians(angle);
        Position bulletPos = new Position(
            position.getX() + Math.sin(rad) * size/2,
            position.getY() + Math.cos(rad) * size/2
        );
        
        return new Bullet(
            bulletPos,
            (int)(GameConfig.BULLET_SPEED * GameConfig.BOSS_BULLET_SPEED_MULT),
            (int)(GameConfig.BULLET_DAMAGE * GameConfig.BOSS_BULLET_DAMAGE_MULT),
            (int)(GameConfig.BULLET_SIZE * GameConfig.BOSS_BULLET_SIZE_MULT),
            false,
            angle,
            true
        );
    }
}