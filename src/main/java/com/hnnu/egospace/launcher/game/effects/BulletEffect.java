package com.hnnu.egospace.launcher.game.effects;

import com.hnnu.egospace.launcher.game.config.GameConfig;
import com.hnnu.egospace.launcher.game.core.Position;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BulletEffect extends Effect {
    private boolean fromPlayer;
    private int size;
    private static final int EFFECT_DURATION = GameConfig.BULLET_EFFECT_DURATION;
    private String type = "BULLET_EFFECT";

    public BulletEffect(Position position, boolean fromPlayer, int size) {
        super(position, EFFECT_DURATION);
        this.fromPlayer = fromPlayer;
        this.size = size;
    }

    @Override
    public void update() {
        super.update();
        position.setY(position.getY() + (fromPlayer ? -GameConfig.BULLET_EFFECT_SPEED 
                                                   : GameConfig.BULLET_EFFECT_SPEED));
    }

    @Override
    public void draw(Object context) {
        // Implementation handled by frontend
    }
}
