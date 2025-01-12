package com.hnnu.egospace.launcher.game.core;

import com.hnnu.egospace.launcher.game.config.GameConfig;
import com.hnnu.egospace.launcher.game.entity.ships.Boss;
import com.hnnu.egospace.launcher.game.entity.ships.Enemy;
import com.hnnu.egospace.launcher.game.entity.ships.EnemyType;
import com.hnnu.egospace.launcher.game.entity.ships.Mothership;
import com.hnnu.egospace.launcher.utils.ColoredLogger;

import lombok.Data;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Random;
import java.util.ArrayList;
import java.util.Queue;
import java.util.LinkedList;

@Data
@Component
public class WaveManager {
    private static final Logger logger = LoggerFactory.getLogger(WaveManager.class);
    private int currentWave = 1;
    private int enemyCount = GameConfig.MIN_WAVE_ENEMIES;
    private List<Enemy> activeEnemies = new ArrayList<>();
    private long lastSpawnTime;
    private final int enemiesPerWave = 5;
    private final int MIN_ENEMIES = 4;
    private final int MAX_ENEMIES = 12;
    private boolean bossPhase = false;
    private boolean mothershipPhase = false;
    private static final Random random = new Random();
    private Queue<SpawnInfo> spawnQueue = new LinkedList<>();
    private static final long SPAWN_INTERVAL = 800; // ms between spawns

    private record SpawnInfo(Position position, long spawnTime) {}

    public void update() {
        // Clean up dead enemies
        activeEnemies.removeIf(enemy -> !enemy.isAlive());
        
        // Spawn new wave if needed
        if (activeEnemies.isEmpty()) {
            spawnNewWave();
        }
    }

    private void spawnNewWave() {
        currentWave++;
        enemyCount = Math.min(enemyCount + 1, GameConfig.MAX_WAVE_ENEMIES);
        
        for (int i = 0; i < enemyCount; i++) {
            Enemy enemy = createBasicEnemy();
            activeEnemies.add(enemy);
        }
        
        ColoredLogger.info(logger, "Spawned wave " + currentWave + 
            " with " + enemyCount + " enemies");
    }

    private Enemy createBasicEnemy() {
        double x = Math.random() * (GameConfig.CANVAS_WIDTH - 40) + 20;
        return new Enemy(new Position(x, -20), EnemyType.BASIC);
    }

    public List<Enemy> spawnWave() {
        List<Enemy> enemies = new ArrayList<>();
        currentWave++;
        
        for (int i = 0; i < enemiesPerWave; i++) {
            double x = Math.random() * (GameConfig.CANVAS_WIDTH - GameConfig.ENEMY_BASIC_SIZE) 
                      + GameConfig.ENEMY_BASIC_SIZE/2;
            enemies.add(new Enemy(new Position(x, -GameConfig.ENEMY_BASIC_SIZE), EnemyType.BASIC));
        }
        
        // Chance to spawn boss every 5 waves
        if (currentWave % 5 == 0 && random.nextDouble() < 0.5) {
            enemies.add(new Boss(new Position(
                GameConfig.CANVAS_WIDTH / 2, 
                -GameConfig.ENEMY_BASIC_SIZE * 3
            )));
            return enemies;
        }

        // Chance to spawn mothership (0.02%)
        if (random.nextDouble() < GameConfig.MOTHERSHIP_SPAWN_CHANCE) {
            enemies.add(new Mothership(new Position(
                GameConfig.CANVAS_WIDTH / 2,
                -GameConfig.ENEMY_BASIC_SIZE * 4
            )));
            return enemies;
        }

        // Normal wave with mix of basic and escort enemies
        int escortCount = currentWave > 3 ? random.nextInt(2) + 1 : 0;
        int basicCount = enemiesPerWave - escortCount;

        // Spawn basic enemies
        for (int i = 0; i < basicCount; i++) {
            double x = random.nextDouble() * (GameConfig.CANVAS_WIDTH - GameConfig.ENEMY_BASIC_SIZE) 
                      + GameConfig.ENEMY_BASIC_SIZE/2;
            enemies.add(new Enemy(new Position(x, -GameConfig.ENEMY_BASIC_SIZE), EnemyType.BASIC));
        }

        // Spawn escort enemies
        for (int i = 0; i < escortCount; i++) {
            double x = random.nextDouble() * (GameConfig.CANVAS_WIDTH - GameConfig.ENEMY_BASIC_SIZE) 
                      + GameConfig.ENEMY_BASIC_SIZE/2;
            enemies.add(new Enemy(new Position(x, -GameConfig.ENEMY_BASIC_SIZE), EnemyType.ESCORT));
        }

        lastSpawnTime = System.currentTimeMillis();
        return enemies;
    }

    public List<Enemy> spawnEnemies() {
        List<Enemy> enemies = new ArrayList<>();
        long currentTime = System.currentTimeMillis();

        if (spawnQueue.isEmpty()) {
            // Random count between MIN and MAX
            int count = GameConfig.MIN_WAVE_ENEMIES + 
                       random.nextInt(GameConfig.MAX_WAVE_ENEMIES - GameConfig.MIN_WAVE_ENEMIES + 1);
            
            // Create staggered spawn points
            for (int i = 0; i < count; i++) {
                // Random x position along top edge
                double x = random.nextDouble() * (GameConfig.CANVAS_WIDTH - 40) + 20;
                double y = -20 - (random.nextDouble() * 40); // Varied starting heights
                
                spawnQueue.offer(new SpawnInfo(
                    new Position(x, y),
                    currentTime + i * SPAWN_INTERVAL
                ));
            }
        }

        // Process spawn queue
        while (!spawnQueue.isEmpty() && spawnQueue.peek().spawnTime <= currentTime) {
            SpawnInfo info = spawnQueue.poll();
            Enemy enemy = createBasicEnemy(info.position);
            enemies.add(enemy);
        }

        return enemies;
    }

    private Enemy createBasicEnemy(Position pos) {
        Enemy enemy = new Enemy(pos, EnemyType.BASIC);
        
        // Base vertical speed with small random variation
        double baseVerticalSpeed = GameConfig.BASIC_ENEMY_VERTICAL_SPEED;
        double speedVariation = 0.2; // 20% variation
        double finalVerticalSpeed = baseVerticalSpeed * 
            (1.0 - speedVariation/2 + random.nextDouble() * speedVariation);
        
        // Horizontal speed always less than vertical (30-40% of vertical)
        double horizontalRatio = 0.3 + random.nextDouble() * 0.1;
        
        enemy.setSpeed(finalVerticalSpeed);
        enemy.setHorizontalSpeed(finalVerticalSpeed * horizontalRatio);
        
        return enemy;
    }

    public List<Enemy> spawnBossWithEscorts() {
        List<Enemy> enemies = new ArrayList<>();
        
        // Spawn boss
        Enemy boss = new Enemy(
            new Position(GameConfig.CANVAS_WIDTH/2, 50), 
            EnemyType.BOSS
        );
        enemies.add(boss);

        // Spawn escorts in orbit
        double angleStep = Math.PI * 2 / GameConfig.ESCORT_COUNT;
        for (int i = 0; i < GameConfig.ESCORT_COUNT; i++) {
            Enemy escort = new Enemy(boss.getPosition(), EnemyType.ESCORT);
            escort.setParent(boss);
            escort.setOrbitAngle(angleStep * i);
            escort.setSpeed(GameConfig.ESCORT_SPEED);
            enemies.add(escort);
        }

        return enemies;
    }
    
    public boolean shouldSpawnWave() {
        return System.currentTimeMillis() - lastSpawnTime > GameConfig.WAVE_SPAWN_DELAY;
    }
}