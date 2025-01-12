package com.hnnu.egospace.launcher.game.core;

import com.hnnu.egospace.launcher.game.entity.bullets.Bullet;
import com.hnnu.egospace.launcher.game.entity.ships.Player;
import com.hnnu.egospace.launcher.game.powerup.PowerUp;
import com.hnnu.egospace.launcher.game.powerup.PowerUpType;
import com.hnnu.egospace.launcher.utils.ColoredLogger;
import com.hnnu.egospace.launcher.game.config.GameConfig;
import com.hnnu.egospace.launcher.game.data.GameRecord;
import com.hnnu.egospace.launcher.game.data.RecordManager;
import com.hnnu.egospace.launcher.game.effects.BulletEffect;
import com.hnnu.egospace.launcher.game.effects.Effect;
import com.hnnu.egospace.launcher.game.effects.Explosion;
import com.hnnu.egospace.launcher.game.entity.ships.Enemy;
import com.hnnu.egospace.launcher.game.entity.ships.EnemyType;

import java.util.ArrayList;
import java.util.List;
import java.util.Iterator;
import lombok.Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Data
public class GameState {
    private static final Logger logger = LoggerFactory.getLogger(GameState.class);
    
    private boolean connected;
    private boolean gameStarted;
    private boolean gameOver;
    private int score;
    private int wave;
    private Player player;
    private List<Enemy> enemies;
    private List<Bullet> bullets;
    private List<Effect> effects;
    private List<PowerUp> powerUps;
    private WaveManager waveManager;
    private RecordManager recordManager;

    private long gameStartTime;
    private long totalPlayTime;
    private boolean defeatedBoss;
    private boolean defeatedMothership;
    private boolean paused;
    private long lastPlayerShootTime;
    private boolean isAdvancedShooting = false;
    private static final long BASIC_SHOOT_COOLDOWN = 800;  // 0.8s
    private static final long ADVANCED_SHOOT_COOLDOWN = 2000; // 2.0s

    public GameState(RecordManager recordManager) {
        this.connected = false;
        this.gameStarted = false;
        this.gameOver = false;
        this.score = 0;
        this.wave = 1;
        this.player = new Player();
        this.enemies = new ArrayList<>();
        this.bullets = new ArrayList<>();
        this.effects = new ArrayList<>();
        this.powerUps = new ArrayList<>();
        this.waveManager = new WaveManager();
        this.recordManager = recordManager;
    }

    public void startGame() {
        gameStarted = true;
        gameOver = false;
        paused = false;
        gameStartTime = System.currentTimeMillis();
    }

    public void togglePause() {
        if (gameStarted && !gameOver) {
            paused = !paused;
        }
    }

    public void update() {
        if (!gameStarted || gameOver || paused) return;
        
        if (gameStartTime == 0) {
            gameStartTime = System.currentTimeMillis();
        }
        totalPlayTime = (System.currentTimeMillis() - gameStartTime) / 1000;
        
        // Auto shooting
        long currentTime = System.currentTimeMillis();
        if (currentTime - lastPlayerShootTime > (isAdvancedShooting ? ADVANCED_SHOOT_COOLDOWN : BASIC_SHOOT_COOLDOWN)) {
            autoShoot();
            lastPlayerShootTime = currentTime;
        }

        // Enemy shooting
        enemies.forEach(enemy -> {
            if (enemy.getType() != EnemyType.BASIC && Math.random() < 0.02) {
                bullets.addAll(enemy.attack());
            }
        });

        // Spawn new wave if needed
        if (waveManager.shouldSpawnWave() && enemies.isEmpty()) {
            enemies.addAll(waveManager.spawnWave());
            wave++;
        }
        
        updateEntities();
        checkCollisions();
        cleanupEntities();
    }
    
    private void checkCollisions() {
        Iterator<Bullet> bulletIt = bullets.iterator();
        while (bulletIt.hasNext()) {
            Bullet bullet = bulletIt.next();
            
            if (bullet.isFromPlayer()) {
                handlePlayerBulletCollisions(bullet, bulletIt);
            } else {
                handleEnemyBulletCollisions(bullet, bulletIt);
            }
        }
        handlePowerUpCollisions();
        handleEnemyCollisions();
    }

    private void handlePlayerBulletCollisions(Bullet bullet, Iterator<Bullet> bulletIt) {
        Iterator<Enemy> enemyIt = enemies.iterator();
        while (enemyIt.hasNext()) {
            Enemy enemy = enemyIt.next();
            if (checkCollision(bullet.getPosition(), bullet.getSize(),
                             enemy.getPosition(), enemy.getSize())) {
                // Apply damage
                enemy.takeDamage(bullet.getDamage());
                // Add hit effect
                effects.add(new Explosion(bullet.getPosition(), bullet.getSize() * 2));
                
                if (!enemy.isAlive()) {
                    score += enemy.getScoreValue();
                    // Add death explosion
                    effects.add(new Explosion(enemy.getPosition(), enemy.getSize()));
                    // Track boss/mothership defeats
                    if (enemy.getType() == EnemyType.BOSS) {
                        defeatedBoss = true;
                    } else if (enemy.getType() == EnemyType.MOTHERSHIP) {
                        defeatedMothership = true;
                    }
                    enemyIt.remove();
                    
                    // Chance to spawn power-up
                    if (Math.random() < GameConfig.POWERUP_SPAWN_CHANCE) {
                        PowerUpType type = PowerUpType.values()[(int)(Math.random() * PowerUpType.values().length)];
                        powerUps.add(new PowerUp(enemy.getPosition().clone(), type));
                    }
                }
                bulletIt.remove();
                break;
            }
        }
    }

    private void handleEnemyBulletCollisions(Bullet bullet, Iterator<Bullet> bulletIt) {
        if (checkCollision(bullet.getPosition(), bullet.getSize(),
                         player.getPosition(), player.getSize())) {
            player.takeDamage(bullet.getDamage());
            effects.add(new Explosion(bullet.getPosition(), bullet.getSize() * 2));
            if (!player.isAlive()) {
                gameOver = true;
                effects.add(new Explosion(player.getPosition(), player.getSize() * 2));
                endGame();
            }
            bulletIt.remove();
        }
    }

    private void handlePowerUpCollisions() {
        Iterator<PowerUp> powerUpIt = powerUps.iterator();
        while (powerUpIt.hasNext()) {
            PowerUp powerUp = powerUpIt.next();
            if (checkCollision(powerUp.getPosition(), powerUp.getSize(),
                             player.getPosition(), player.getSize())) {
                powerUp.applyTo(player);
                effects.add(new Explosion(powerUp.getPosition(), powerUp.getSize()));
                powerUpIt.remove();
            }
        }
    }
    
    private boolean checkCollision(Position pos1, int size1, Position pos2, int size2) {
        return pos1.distanceTo(pos2) < (size1 + size2) / 2.0;
    }
    
    private void updateEntities() {
        player.update();
        enemies.forEach(Enemy::update);
        bullets.forEach(Bullet::update);
        effects.forEach(Effect::update);
        effects.removeIf(Effect::isFinished);
        powerUps.forEach(PowerUp::update);
        
        // Random powerup spawning
        if (Math.random() < 0.001) { // 0.1% chance per update
            double x = Math.random() * (GameConfig.CANVAS_WIDTH - 20) + 10;
            PowerUpType type = PowerUpType.values()[(int)(Math.random() * PowerUpType.values().length)];
            powerUps.add(new PowerUp(new Position(x, -10), type));
        }

        // Update bullets and create trail effects
        bullets.forEach(bullet -> {
            bullet.update();
            if (Math.random() < 0.3) { // 30% chance to create trail effect
                effects.add(new BulletEffect(
                    new Position(bullet.getPosition().getX(), bullet.getPosition().getY()),
                    bullet.isFromPlayer(),
                    bullet.getSize()
                ));
            }
        });

        // Clean up finished effects
        effects.removeIf(Effect::isFinished);

        // Update enemy target positions and behaviors
        enemies.forEach(enemy -> {
            if (enemy.getType() == EnemyType.BASIC) {
                enemy.setPlayerPosition(player.getPosition());
            }
            enemy.update();
        });
    }
    
    private void cleanupEntities() {
        // Remove off-screen bullets
        bullets.removeIf(bullet -> 
            bullet.getPosition().getY() < 0 || 
            bullet.getPosition().getY() > GameConfig.CANVAS_HEIGHT
        );
        
        // Remove dead or off-screen enemies
        enemies.removeIf(enemy -> 
            !enemy.isAlive() || 
            enemy.getPosition().getY() > GameConfig.CANVAS_HEIGHT
        );
    }

    public void endGame() {
        gameOver = true;
        long totalPlayTime = (System.currentTimeMillis() - gameStartTime) / 1000;
        
        GameRecord record = new GameRecord(score, (int)totalPlayTime, wave);
        record.setDefeatedBoss(defeatedBoss);
        record.setDefeatedMothership(defeatedMothership);
        recordManager.addRecord(record);
        
        ColoredLogger.info(logger, "Game ended - Score: " + score + 
            " Wave: " + wave + " Time: " + totalPlayTime + "s");
    }

    public void movePlayerLeft() {
        if (!gameStarted || gameOver || paused) return;
        Position pos = player.getPosition();
        double newX = Math.max(player.getSize()/2, 
                             pos.getX() - player.getActualSpeed());
        pos.setX(newX);
    }

    public void movePlayerRight() {
        if (!gameStarted || gameOver || paused) return;
        Position pos = player.getPosition();
        double newX = Math.min(GameConfig.CANVAS_WIDTH - player.getSize()/2, 
                             pos.getX() + player.getActualSpeed());
        pos.setX(newX);
    }

    public void playerShoot() {
        if (!gameStarted || gameOver || paused) return;
        if (player.canShoot()) {
            bullets.addAll(player.shoot());
        }
    }

    private void autoShoot() {
        if (isAdvancedShooting) {
            // Advanced shooting - 3 spread bullets
            double[] angles = {-15, 0, 15};
            for (double angle : angles) {
                bullets.add(new Bullet(
                    new Position(player.getPosition().getX(), 
                               player.getPosition().getY() - player.getSize()/2),
                    GameConfig.BULLET_SPEED,
                    GameConfig.BULLET_DAMAGE * 2,
                    GameConfig.BULLET_SIZE * 2,
                    true,
                    angle,
                    true
                ));
            }
            ColoredLogger.info(logger, "Player fired advanced shots");
        } else {
            // Basic shooting - 2 parallel bullets
            double[] xOffsets = {-5, 5};
            for (double offset : xOffsets) {
                bullets.add(new Bullet(
                    new Position(player.getPosition().getX() + offset, 
                               player.getPosition().getY() - player.getSize()/2),
                    GameConfig.BULLET_SPEED,
                    GameConfig.BULLET_DAMAGE,
                    GameConfig.BULLET_SIZE,
                    true
                ));
            }
            ColoredLogger.info(logger, "Player fired basic shots");
        }
    }

    public void toggleShootingMode() {
        isAdvancedShooting = !isAdvancedShooting;
        ColoredLogger.info(logger, "Shooting mode toggled to: " + 
            (isAdvancedShooting ? "Advanced" : "Basic"));
    }

    private void handleEnemyCollisions() {
        Iterator<Enemy> enemyIt = enemies.iterator();
        while (enemyIt.hasNext()) {
            Enemy enemy = enemyIt.next();
            if (checkCollision(enemy.getPosition(), enemy.getSize(),
                             player.getPosition(), player.getSize())) {
                player.takeDamage(GameConfig.COLLISION_DAMAGE);
                effects.add(new Explosion(enemy.getPosition(), enemy.getSize()));
                enemyIt.remove();
                
                if (!player.isAlive()) {
                    ColoredLogger.warning(logger, "Player died from enemy collision");
                    gameOver = true;
                    endGame();
                }
            }
        }
    }
}
