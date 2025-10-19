package com.machinehunterdev.game.Levels;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Json;

/**
 * Carga niveles desde archivos JSON.
 * @author MachineHunterDev
 */
public class LevelLoader {
    
    /**
     * Carga un nivel desde un archivo JSON.
     * @param levelFile Ruta del archivo JSON del nivel
     * @return Datos del nivel cargado
     */
    public static LevelData loadLevel(String levelFile) {
        Json json = new Json();
        try {
            return json.fromJson(LevelData.class, Gdx.files.internal(levelFile));
        } catch (Exception e) {
            Gdx.app.error("LevelLoader", "Error al cargar el nivel: " + levelFile, e);
            // Devolver nivel por defecto si falla la carga
            return createDefaultLevel();
        }
    }
    
    /**
     * Crea un nivel por defecto en caso de error.
     */
    private static LevelData createDefaultLevel() {
        LevelData defaultLevel = new LevelData();
        
        // Suelo básico
        LevelData.SolidObjectData ground = new LevelData.SolidObjectData();
        ground.x = 0;
        ground.y = 0;
        ground.width = 1440;
        ground.height = 32;
        defaultLevel.solidObjectsData.add(ground);
        
        // Enemigo básico
        LevelData.EnemyData enemy = new LevelData.EnemyData();
        enemy.x = 300;
        enemy.y = 100;

        LevelData.Point p1 = new LevelData.Point();
        p1.x = 100;
        p1.y = 100;

        LevelData.Point p2 = new LevelData.Point();
        p2.x = 500;
        p2.y = 100;

        enemy.patrolPoints = java.util.Arrays.asList(p1, p2);
        defaultLevel.enemies.add(enemy);
        
        return defaultLevel;
    }
}