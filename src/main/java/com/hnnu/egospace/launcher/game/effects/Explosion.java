package com.hnnu.egospace.launcher.game.effects;

import com.hnnu.egospace.launcher.game.core.Position;

public class Explosion extends Effect {
    private static final int EXPLOSION_DURATION = 30; // frames
    private int size;
    
    public Explosion(Position position, int size) {
        super(position, EXPLOSION_DURATION);
        this.size = size;
    }
    
    @Override
    public void update() {
        super.update();
        // Explosion grows and fades
        size = (int)(size * (1 + currentFrame * 0.1));
    }
    
    @Override
    public void draw(Object context) {
        // Implementation will be handled by frontend
    }
}