package com.machinehunterdev.game.Leaderboard;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Json;
import java.util.Comparator;

/**
 * Gestor de la tabla de clasificaciones (leaderboard).
 * Almacena los mejores puntajes usando Preferences de libGDX.
 * Implementa el patrón Singleton para asegurar una única instancia en todo el juego.
 */
public class LeaderboardManager {
    // Constantes de configuración
    private static final String PREFS_NAME = "leaderboard_prefs"; // Nombre del archivo de preferencias
    private static final String KEY_SCORES = "scores";            // Clave para almacenar los puntajes
    private static final int MAX_ENTRIES = 10;                    // Máximo número de entradas en la tabla

    // Instancia singleton
    private static LeaderboardManager instance;
    
    /**
     * Obtiene la instancia única del gestor de clasificaciones.
     * @return La instancia singleton de LeaderboardManager
     */
    public static LeaderboardManager getInstance() {
        if (instance == null) {
            instance = new LeaderboardManager();
        }
        return instance;
    }

    // Datos internos
    private Preferences prefs;                    // Sistema de preferencias de libGDX
    private Array<ScoreEntry> scores;             // Lista de puntajes almacenados

    /**
     * Constructor privado para implementar el patrón Singleton.
     * Carga los puntajes guardados al iniciar.
     */
    public LeaderboardManager() {
        prefs = Gdx.app.getPreferences(PREFS_NAME);
        loadScores();
    }

    /**
     * Añade un nuevo puntaje a la tabla de clasificaciones.
     * @param playerName Nombre del jugador
     * @param score Puntaje obtenido
     */
    public void addScore(String playerName, int score) {
        // Validación de entrada
        if (playerName == null || playerName.trim().isEmpty()) {
            playerName = "Jugador";
        }
        if (score < 0) score = 0;
        
        // Añadir nueva entrada
        scores.add(new ScoreEntry(playerName.trim(), score));
        
        // Ordenar de mayor a menor puntaje
        scores.sort(new Comparator<ScoreEntry>() {
            @Override
            public int compare(ScoreEntry o1, ScoreEntry o2) {
                return Integer.compare(o2.score, o1.score);
            }
        });

        // Mantener solo los mejores MAX_ENTRIES puntajes
        while (scores.size > MAX_ENTRIES) {
            scores.removeIndex(scores.size - 1);
        }

        // Guardar cambios persistentemente
        saveScores();
    }

    /**
     * Carga los puntajes desde las preferencias del sistema.
     * Se ejecuta automáticamente al crear la instancia.
     */
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

    /**
     * Guarda los puntajes actuales en las preferencias del sistema.
     * Los datos persisten entre sesiones del juego.
     */
    private void saveScores() {
        Json json = new Json();
        String jsonString = json.toJson(scores.toArray(ScoreEntry.class));
        prefs.putString(KEY_SCORES, jsonString);
        prefs.flush(); // Forzar escritura inmediata
    }

    /**
     * Obtiene una copia de los mejores puntajes.
     * @return Array con las entradas de la tabla de clasificaciones
     */
    public Array<ScoreEntry> getTopScores() {
        return new Array<ScoreEntry>(scores); // Devolver copia para evitar modificaciones externas
    }

    /**
     * Clase interna que representa una entrada en la tabla de clasificaciones.
     * Debe tener constructor sin parámetros para la serialización JSON.
     */
    public static class ScoreEntry {
        public String name;  // Nombre del jugador
        public int score;    // Puntaje obtenido

        /**
         * Constructor sin parámetros (requerido para la deserialización JSON).
         */
        public ScoreEntry() {}

        /**
         * Constructor con parámetros.
         * @param name Nombre del jugador
         * @param score Puntaje obtenido
         */
        public ScoreEntry(String name, int score) {
            this.name = name;
            this.score = score;
        }
    }
}