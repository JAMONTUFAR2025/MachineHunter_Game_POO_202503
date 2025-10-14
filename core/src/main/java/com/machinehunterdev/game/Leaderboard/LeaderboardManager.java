package com.machinehunterdev.game.Leaderboard;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Json;

import java.util.Comparator;

public class LeaderboardManager {
    private static final String PREFS_NAME = "leaderboard_prefs";
    private static final String KEY_SCORES = "scores";
    private static final int MAX_ENTRIES = 10;

    private Preferences prefs;
    private Array<ScoreEntry> scores;

    public LeaderboardManager() {
        prefs = Gdx.app.getPreferences(PREFS_NAME);
        loadScores();
    }

    public void addScore(String playerName, int score) {
        scores.add(new ScoreEntry(playerName, score));
        // Ordenar de mayor a menor
        scores.sort(new Comparator<ScoreEntry>() {
            @Override
            public int compare(ScoreEntry o1, ScoreEntry o2) {
                return Integer.compare(o2.score, o1.score);
            }
        });

        // Mantener solo los primeros MAX_ENTRIES
        while (scores.size > MAX_ENTRIES) {
            scores.removeIndex(scores.size - 1);
        }

        saveScores();
    }

    private void loadScores() {
        scores = new Array<ScoreEntry>();
        String json = prefs.getString(KEY_SCORES, null);
        if (json != null && !json.isEmpty()) {
            Json jsonParser = new Json();
            ScoreEntry[] loaded = jsonParser.fromJson(ScoreEntry[].class, json);
            if (loaded != null) {
                for (ScoreEntry entry : loaded) {
                    scores.add(entry);
                }
            }
        }
    }

    private void saveScores() {
        Json json = new Json();
        String jsonString = json.toJson(scores.toArray(ScoreEntry.class));
        prefs.putString(KEY_SCORES, jsonString);
        prefs.flush();
    }

    public Array<ScoreEntry> getTopScores() {
        return new Array<ScoreEntry>(scores); // copia
    }

    public static class ScoreEntry {
        public String name;
        public int score;

        public ScoreEntry() {} // Necesario para Json

        public ScoreEntry(String name, int score) {
            this.name = name;
            this.score = score;
        }
    }
}
