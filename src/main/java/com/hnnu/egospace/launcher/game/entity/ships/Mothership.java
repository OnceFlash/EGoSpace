package com.hnnu.egospace.launcher.game.entity.ships;

import com.hnnu.egospace.launcher.game.config.GameConfig;
import com.hnnu.egospace.launcher.game.core.Position;
import com.hnnu.egospace.launcher.game.entity.bullets.Bullet;

import java.util.ArrayList;
import java.util.List;

public class Mothership extends Enemy {
    private static final int MAX_HEALTH = 1000;
    private static final int SKILL_COOLDOWN = 5000; // 5 seconds
    private long[] lastSkillTime = new long[4]; // For Q,W,E,R skills
    private boolean hasShield;
    private List<Enemy> escorts;

    public Mothership(Position position) {
        super(position, EnemyType.MOTHERSHIP);
        this.health = MAX_HEALTH;
        this.speed = GameConfig.ENEMY_BASIC_SPEED / 3;
        this.size = GameConfig.ENEMY_BASIC_SIZE * 4;
        this.scoreValue = 1400;
        this.hasShield = true;
        this.escorts = new ArrayList<>();
    }

    @Override
    public void update() {
        // Mothership movement pattern - slow horizontal movement
        position.setX(position.getX() + Math.sin(System.currentTimeMillis() / 2000.0) * speed);
        
        // Keep in bounds
        if (position.getX() < size) position.setX(size);
        if (position.getX() > GameConfig.CANVAS_WIDTH - size) {
            position.setX(GameConfig.CANVAS_WIDTH - size);
        }

        // Spawn escorts occasionally
        if (Math.random() < GameConfig.MOTHERSHIP_ESCORT_SPAWN_CHANCE && 
            escorts.size() < GameConfig.MOTHERSHIP_ESCORT_LIMIT) {
            spawnEscort();
        }

        // Update escorts
        escorts.removeIf(escort -> !escort.isAlive());
        escorts.forEach(Enemy::update);
    }

    public List<Bullet> useSkill(int skillNumber) {
        if (!canUseSkill(skillNumber)) {
            return new ArrayList<>();
        }
        lastSkillTime[skillNumber] = System.currentTimeMillis();

        return switch(skillNumber) {
            case 0 -> timeDistortion();
            case 1 -> stellarVortex();
            case 2 -> planetaryBeam();
            case 3 -> massRecall();
            default -> new ArrayList<>();
        };
    }

    private List<Bullet> timeDistortion() {
        List<Bullet> bullets = new ArrayList<>();
        for (int angle = -90; angle <= 90; angle += GameConfig.MOTHERSHIP_TIME_DISTORTION_ANGLE_STEP) {
            double rad = Math.toRadians(angle);
            Position bulletPos = new Position(
                position.getX() + Math.cos(rad) * size,
                position.getY() - Math.sin(rad) * size
            );
            bullets.add(createBullet(bulletPos, angle));
        }
        return bullets;
    }

    private List<Bullet> stellarVortex() {
        List<Bullet> bullets = new ArrayList<>();
        Position vortexCenter = new Position(position.getX(), position.getY() - size * 2);
        
        for (int angle = 0; angle < 360; angle += GameConfig.MOTHERSHIP_VORTEX_ANGLE_STEP) {
            double rad = Math.toRadians(angle);
            Position bulletPos = new Position(
                vortexCenter.getX() + Math.cos(rad) * size,
                vortexCenter.getY() + Math.sin(rad) * size
            );
            bullets.add(createBullet(bulletPos, angle));
        }
        return bullets;
    }

    private List<Bullet> planetaryBeam() {
        List<Bullet> bullets = new ArrayList<>();
        for (int i = 0; i < GameConfig.MOTHERSHIP_BEAM_COUNT; i++) {
            Position bulletPos = new Position(
                position.getX(),
                position.getY() + i * GameConfig.MOTHERSHIP_BEAM_SPACING
            );
            bullets.add(createBullet(bulletPos, 0));
        }
        return bullets;
    }

    private List<Bullet> massRecall() {
        double[] xOffsets = {-2, -1, 1, 2};
        for (double offset : xOffsets) {
            double x = position.getX() + offset * size;
            if (x > 0 && x < GameConfig.CANVAS_WIDTH) {
                spawnEscort(new Position(x, position.getY()));
            }
        }
        return new ArrayList<>();
    }

    private void spawnEscort() {
        spawnEscort(new Position(position.getX(), position.getY() + size));
    }

    private void spawnEscort(Position pos) {
        if (escorts.size() < 4) {
            escorts.add(new Enemy(pos, EnemyType.ESCORT));
        }
    }

    @Override
    public void takeDamage(int damage) {
        if (hasShield) {
            damage /= GameConfig.MOTHERSHIP_SHIELD_REDUCTION;
            if (health < MAX_HEALTH * 0.3) {
                hasShield = false;
            }
        }
        super.takeDamage(damage);
    }

    public List<Enemy> getEscorts() {
        return escorts;
    }

    public boolean hasShield() {
        return hasShield;
    }

    private Bullet createBullet(Position pos, double angle) {
        return new Bullet(
            pos,
            (int)(GameConfig.BULLET_SPEED * GameConfig.MOTHERSHIP_BULLET_SPEED_MULT),
            (int)(GameConfig.BULLET_DAMAGE * GameConfig.MOTHERSHIP_BULLET_DAMAGE_MULT),
            (int)(GameConfig.BULLET_SIZE * GameConfig.MOTHERSHIP_BULLET_SIZE_MULT),
            false,
            angle,
            true
        );
    }

    public boolean canUseSkill(int skillNumber) {
        return System.currentTimeMillis() - lastSkillTime[skillNumber] >= SKILL_COOLDOWN;
    }

    public long getSkillCooldown(int skillNumber) {
        return Math.max(0, SKILL_COOLDOWN - 
               (System.currentTimeMillis() - lastSkillTime[skillNumber]));
    }

    public int getBattlePhase() {
        if (health > MAX_HEALTH * 0.7) return 1;
        if (health > MAX_HEALTH * 0.3) return 2;
        return 3;
    }

    public List<Enemy> getActiveEscorts() {
        return escorts.stream()
                     .filter(Enemy::isAlive)
                     .toList();
    }
}