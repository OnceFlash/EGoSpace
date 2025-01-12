package com.hnnu.egospace.launcher.game.data;

import lombok.Data;
import java.io.Serializable;
import java.io.IOException;

@Data
public class GameRecord implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private int score;
    private int playTime;
    private int wave;
    private boolean defeatedBoss;
    private boolean defeatedMothership;
    private long timestamp;

    public GameRecord(int score, int playTime, int wave) {
        this.score = score;
        this.playTime = playTime;
        this.wave = wave;
        this.defeatedBoss = false;
        this.defeatedMothership = false;
        this.timestamp = System.currentTimeMillis();
    }

    private void writeObject(java.io.ObjectOutputStream out) throws IOException {
        out.defaultWriteObject();
    }

    private void readObject(java.io.ObjectInputStream in) 
        throws IOException, ClassNotFoundException {
        in.defaultReadObject();
    }
}
