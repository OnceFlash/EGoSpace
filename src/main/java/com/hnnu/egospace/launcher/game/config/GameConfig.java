package com.hnnu.egospace.launcher.game.config;

public class GameConfig {
    // Enemy Colors
    public static final String PLAYER_COLOR = "#00FF00";  // Lime
    public static final String BASIC_ENEMY_COLOR = "#FF0000";  // Red
    public static final String ESCORT_ENEMY_COLOR = "#FF4500";  // Orange Red
    public static final String BOSS_ENEMY_COLOR = "#8B0000";   // Dark Red
    public static final String MOTHERSHIP_COLOR = "#4B0082";   // Indigo

    // Bullet Colors
    public static final String ENEMY_BULLET_COLOR = "#FF4500";  // Orange Red
    public static final String ENEMY_SPECIAL_BULLET_COLOR = "#FFA500"; // Orange
    public static final String PLAYER_BULLET_COLOR = "#00BFFF";  // Deep Sky Blue
    public static final String PLAYER_SPECIAL_BULLET_COLOR = "#87CEEB"; // Sky Blue

    // Canvas Properties
    public static final int CANVAS_WIDTH = 800;
    public static final int CANVAS_HEIGHT = 600;
    public static final int RENDER_PADDING = 50; // Extra space for rendering
    
    public static final int PLAYER_SPEED = 3;  // Reduced from 5
    public static final int PLAYER_SIZE = 30;
    public static final int PLAYER_HEALTH = 100;
    
    public static final int BULLET_SPEED = 7;
    public static final int BULLET_SIZE = 5;
    
    public static final int ENEMY_BASIC_SPEED = 2;  // Vertical speed
    public static final double ENEMY_BASIC_HORIZONTAL_SPEED = 1.0;  // Horizontal speed
    public static final double ENEMY_CHARGE_SPEED_MULTIPLIER = 2.0; // Speed multiplier when charging
    public static final int ENEMY_BASIC_SIZE = 25;
    public static final int ENEMY_BASIC_HEALTH = 30;

    public static final int ENEMY_ESCORT_SPEED = 2;
    public static final int ENEMY_ESCORT_SIZE = 30;
    public static final int ENEMY_ESCORT_HEALTH = 50;
    public static final double ESCORT_SPEED = 2.0;

    public static final int ENEMY_BOSS_SPEED = 1;
    public static final int ENEMY_BOSS_SIZE = 50;
    public static final int ENEMY_BOSS_HEALTH = 500;
    
    public static final int WAVE_SPAWN_DELAY = 2000;
    public static final double MOTHERSHIP_SPAWN_CHANCE = 100; // 0.02%
    
    // Mothership Configuration
    public static final int MOTHERSHIP_SPEED = 1;
    public static final int MOTHERSHIP_SIZE = 80;
    public static final int MOTHERSHIP_HEALTH = 1000;
    public static final int MOTHERSHIP_SHIELD_REDUCTION = 2;
    public static final int MOTHERSHIP_SKILL_COOLDOWN = 5000;
    public static final int MOTHERSHIP_ESCORT_LIMIT = 4;
    public static final double MOTHERSHIP_ESCORT_SPAWN_CHANCE = 0.01;
    
    // Mothership Attack Configuration
    public static final double MOTHERSHIP_BULLET_SPEED_MULT = 1.5;
    public static final double MOTHERSHIP_BULLET_DAMAGE_MULT = 2.0;
    public static final double MOTHERSHIP_BULLET_SIZE_MULT = 2.0;
    public static final int MOTHERSHIP_TIME_DISTORTION_ANGLE_STEP = 15;
    public static final int MOTHERSHIP_VORTEX_ANGLE_STEP = 30;
    public static final int MOTHERSHIP_BEAM_COUNT = 5;
    public static final int MOTHERSHIP_BEAM_SPACING = 20;
    
    // Boss Configuration
    public static final int BOSS_MAX_HEALTH = 500;
    public static final int BOSS_ATTACK_COOLDOWN = 2000;
    public static final double BOSS_BULLET_SPEED_MULT = 1.5;
    public static final double BOSS_BULLET_DAMAGE_MULT = 2.0;
    public static final double BOSS_BULLET_SIZE_MULT = 2.0;
    
    // Boss Score Configuration
    public static final int BOSS_BASE_SCORE = 1000;
    public static final double BOSS_PHASE_SCORE_MULTIPLIER = 1.5;
    
    // Effect Configuration
    public static final int EXPLOSION_DURATION = 30;
    public static final int BULLET_EFFECT_SPEED = 2;
    public static final int BULLET_EFFECT_DURATION = 15;
    public static final double BULLET_EFFECT_SPAWN_CHANCE = 0.3;
    
    // PowerUp Configuration
    public static final int POWERUP_DURATION = 10000;
    public static final int POWERUP_SIZE = 15;
    public static final double POWERUP_SPAWN_CHANCE = 0.001;
    public static final int POWERUP_SPEED = 2;
    
    // PowerUp Effect Values
    public static final int POWERUP_BULLET_COUNT_MAX = 3;
    public static final int POWERUP_BULLET_SIZE_INCREASE = 2;
    public static final int POWERUP_BULLET_DAMAGE_INCREASE = 5;
    public static final int POWERUP_SPEED_BOOST = 3;

    // Player Configuration
    public static final int PLAYER_SHOOT_COOLDOWN = 200;
    public static final int PLAYER_MAX_BULLET_COUNT = 3;

    // Damage Configuration
    public static final int COLLISION_DAMAGE = 50; // 2 collisions to kill
    public static final int BULLET_DAMAGE = 100;  // 1 bullet to kill

    // Enemy Configuration
    public static final int BASIC_ENEMY_SCORE = 100;
    public static final int BOSS_SCORE = 800;
    public static final int MOTHERSHIP_SCORE = 1400;
    public static final double BOSS_SPAWN_SCORE = 1400.0;
    public static final int ESCORT_SPAWN_COOLDOWN = 5000;
    public static final int MAX_ESCORTS = 2;
    public static final double CHARGE_SPEED_MULTIPLIER = 1.8;
    public static final double ENEMY_BULLET_DAMAGE = 50;
    
    // Enemy Movement
    public static final double BASIC_ENEMY_SPEED = 1.2;
    public static final double ESCORT_ORBIT_SPEED = 0.02;
    public static final double BOSS_PATROL_SPEED = 1.5;

    // Enemy movement speeds
    public static final double BASIC_ENEMY_VERTICAL_SPEED = 1.5;
    public static final double BASIC_ENEMY_HORIZONTAL_SPEED = 0.6;
    public static final double BASIC_ENEMY_WAVE_AMPLITUDE = 30.0;
    public static final double BASIC_ENEMY_WAVE_FREQUENCY = 0.03;
    public static final double CHARGE_TRIGGER_DISTANCE = 200.0;
    public static final double CHARGE_VERTICAL_MULTIPLIER = 2.0;
    public static final double MAX_HORIZONTAL_CHARGE = 1.0;
    public static final double ESCORT_ORBIT_RADIUS = 80.0;
    public static final long ESCORT_RESPAWN_DELAY = 5000;
    
    // Wave configuration
    public static final int MIN_WAVE_ENEMIES = 4;
    public static final int MAX_WAVE_ENEMIES = 12;
    public static final int MIN_SPAWN_COUNT = 2;
    public static final int MAX_SPAWN_COUNT = 4;
    public static final int ESCORT_COUNT = 2;
    
    // Combat values
    public static final long ENEMY_SHOOT_COOLDOWN = 2000; // 2 seconds
    public static final double SHOOT_CHANCE = 0.01;

    // Visual Effects
    public static final int EFFECT_DURATION = 500;
    public static final String EXPLOSION_COLOR = "#FFA07A";
    public static final String SHIELD_COLOR = "#4169E1";
}