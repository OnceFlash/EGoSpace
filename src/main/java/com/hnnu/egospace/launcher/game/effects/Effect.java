package com.hnnu.egospace.launcher.game.effects;

import com.hnnu.egospace.launcher.game.core.Position;
import lombok.Data;

@Data
public abstract class Effect {
    protected Position position;
    protected int duration;
    protected int currentFrame;
    protected boolean finished;
    
    public Effect(Position position, int duration) {
        this.position = position;
        this.duration = duration;
        this.currentFrame = 0;
        this.finished = false;
    }
    
    public void update() {
        currentFrame++;
        if (currentFrame >= duration) {
            finished = true;
        }
    }
    
    public abstract void draw(Object context); // Will be used for frontend rendering
}
