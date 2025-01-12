package com.hnnu.egospace.launcher.game.data;

import org.springframework.stereotype.Component;
import java.util.*;
import java.io.*;
import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import javax.annotation.PostConstruct;

@Getter
@Component
public class RecordManager implements Serializable {
    private static final long serialVersionUID = 1L;
    private static final Logger logger = LoggerFactory.getLogger(RecordManager.class);
    private static final String RECORD_FILE = "game_records.dat";
    private List<GameRecord> records = new ArrayList<>();
    private GameRecord currentHighScore;
    private volatile boolean isLoaded = false;

    public RecordManager() {
        loadRecords();
    }

    public void addRecord(GameRecord record) {
        records.add(record);
        if (currentHighScore == null || record.getScore() > currentHighScore.getScore()) {
            currentHighScore = record;
        }
        saveRecords();
    }

    public GameRecord getHighScore() {
        return currentHighScore;
    }

    public List<GameRecord> getTopScores(int limit) {
        return records.stream()
                .sorted((r1, r2) -> Integer.compare(r2.getScore(), r1.getScore()))
                .limit(limit)
                .toList();
    }

    @PostConstruct
    private synchronized void loadRecords() {
        if (isLoaded) {
            return;
        }

        File file = new File(RECORD_FILE);
        if (!file.exists()) {
            records = new ArrayList<>();
            isLoaded = true;
            return;
        }

        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
            records = (List<GameRecord>) ois.readObject();
            isLoaded = true;
            if (logger.isInfoEnabled()) {
                logger.info("Records loaded successfully");
            }
        } catch (IOException | ClassNotFoundException e) {
            logger.error("Error loading records: {}", e.getMessage());
            records = new ArrayList<>();
        }
    }

    private void saveRecords() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(RECORD_FILE))) {
            oos.writeObject(records);
            logger.info("Records saved successfully");
        } catch (IOException e) {
            logger.error("Error saving records: {}", e.getMessage());
        }
    }

    private void updateHighScore() {
        currentHighScore = records.stream()
                .max(Comparator.comparingInt(GameRecord::getScore))
                .orElse(null);
    }
}
