package com.hnnu.egospace.launcher.game.websocket;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.hnnu.egospace.launcher.game.config.GameConfig;
import com.hnnu.egospace.launcher.game.core.GameState;
import com.hnnu.egospace.launcher.game.core.Position;
import com.hnnu.egospace.launcher.game.data.RecordManager;
import com.hnnu.egospace.launcher.game.dto.EntityDTO;
import com.hnnu.egospace.launcher.game.dto.GameStateDTO;
import com.hnnu.egospace.launcher.game.entity.bullets.Bullet;
import com.hnnu.egospace.launcher.game.entity.ships.Enemy;
import com.hnnu.egospace.launcher.game.entity.ships.EnemyType;
import com.hnnu.egospace.launcher.game.entity.ships.Player;
import com.hnnu.egospace.launcher.utils.ColoredLogger;

import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class GameWebSocket extends TextWebSocketHandler {
    private static final Logger logger = LoggerFactory.getLogger(GameWebSocket.class);
    private final RecordManager recordManager;
    private WebSocketSession session;
    private GameState gameState;
    private static final ObjectMapper mapper;
    private ScheduledExecutorService gameLoop;
    
    static {
        mapper = new ObjectMapper();
        mapper.disable(SerializationFeature.FAIL_ON_EMPTY_BEANS);
    }
    
    public GameWebSocket(RecordManager recordManager) {
        this.recordManager = recordManager;
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        if (this.session != null) {
            this.session.close();
        }
        this.session = session;
        this.gameState = new GameState(recordManager);
        this.gameState.setConnected(true);
        startGameLoop();
        sendGameState();
    }

    private void startGameLoop() {
        gameLoop = Executors.newSingleThreadScheduledExecutor();
        gameLoop.scheduleAtFixedRate(() -> {
            if (gameState != null && gameState.isGameStarted() && !gameState.isGameOver()) {
                gameState.update();
                sendGameState();
            }
        }, 0, 16, TimeUnit.MILLISECONDS); // 60 FPS
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        if (gameLoop != null) {
            gameLoop.shutdown();
            try {
                if (!gameLoop.awaitTermination(800, TimeUnit.MILLISECONDS)) {
                    gameLoop.shutdownNow();
                }
            } catch (InterruptedException e) {
                gameLoop.shutdownNow();
            }
        }
        this.session = null;
        this.gameState.setConnected(false);
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) {
        try {
            JsonNode node = mapper.readTree(message.getPayload());
            String type = node.get("type").asText();
            
            switch(type) {
                case "START_GAME" -> gameState.startGame();
                case "PAUSE_GAME" -> gameState.togglePause();
                case "MOVE_LEFT" -> gameState.movePlayerLeft();
                case "MOVE_RIGHT" -> gameState.movePlayerRight();
                case "SHOOT" -> gameState.playerShoot();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    private void sendGameState() {
        try {
            // Create game state using builder pattern
            GameStateDTO dto = GameStateDTO.builder()
                .enemies(gameState.getEnemies().stream()
                    .filter(e -> isOnScreen(e.getPosition()))
                    .map(this::convertToEnemyDTO)
                    .collect(Collectors.toList()))
                .bullets(gameState.getBullets().stream()
                    .filter(b -> isOnScreen(b.getPosition()))
                    .map(this::convertToBulletDTO)
                    .collect(Collectors.toList()))
                .player(convertToPlayerDTO(gameState.getPlayer()))
                .score(gameState.getScore())
                .wave(gameState.getWaveManager().getCurrentWave())
                .gameOver(gameState.isGameOver())
                .build();

            String state = mapper.writeValueAsString(dto);
            session.sendMessage(new TextMessage(state));
        } catch (Exception e) {
            ColoredLogger.error(logger, "Failed to send game state to client");
        }
    }

    private EntityDTO convertToEnemyDTO(Enemy enemy) {
        return EntityDTO.builder()
            .x(enemy.getPosition().getX())
            .y(enemy.getPosition().getY())
            .size(enemy.getSize())
            .type(enemy.getType().toString())
            .color(getEnemyColor(enemy.getType()))
            .health(enemy.getHealth())
            .maxHealth(enemy.getMaxHealth())
            .build();
    }

    private EntityDTO convertToBulletDTO(Bullet bullet) {
        return EntityDTO.builder()
            .x(bullet.getPosition().getX())
            .y(bullet.getPosition().getY())
            .size(bullet.getSize())
            .color(getBulletColor(bullet))
            .angle(bullet.getAngle())
            .hasEffect(bullet.hasEffect())
            .build();
    }

    private EntityDTO convertToPlayerDTO(Player player) {
        return EntityDTO.builder()
            .x(player.getPosition().getX())
            .y(player.getPosition().getY())
            .size(player.getSize())
            .type("PLAYER")
            .color(GameConfig.PLAYER_COLOR)
            .health(player.getHealth())
            .maxHealth(player.getMaxHealth())
            .build();
    }

    private boolean isOnScreen(Position pos) {
        int padding = 50; // Render padding for smooth transitions
        return pos.getX() >= -padding && 
               pos.getX() <= GameConfig.CANVAS_WIDTH + padding &&
               pos.getY() >= -padding && 
               pos.getY() <= GameConfig.CANVAS_HEIGHT + padding;
    }

    private String getBulletColor(Bullet bullet) {
        if (bullet.isFromPlayer()) {
            return bullet.hasEffect() ? 
                GameConfig.PLAYER_SPECIAL_BULLET_COLOR : 
                GameConfig.PLAYER_BULLET_COLOR;
        }
        return bullet.hasEffect() ? 
            GameConfig.ENEMY_SPECIAL_BULLET_COLOR : 
            GameConfig.ENEMY_BULLET_COLOR;
    }

    private String getEnemyColor(EnemyType type) {
        return switch(type) {
            case BASIC -> GameConfig.BASIC_ENEMY_COLOR;
            case ESCORT -> GameConfig.ESCORT_ENEMY_COLOR;
            case BOSS -> GameConfig.BOSS_ENEMY_COLOR;
            case MOTHERSHIP -> GameConfig.MOTHERSHIP_COLOR;
        };
    }

    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) {
        exception.printStackTrace();
        try {
            session.close(CloseStatus.SERVER_ERROR);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}