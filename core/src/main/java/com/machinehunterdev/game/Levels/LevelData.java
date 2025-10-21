package com.machinehunterdev.game.Levels;

import com.badlogic.gdx.math.Vector2;
import com.machinehunterdev.game.Character.EnemyType;

import java.util.ArrayList;
import java.util.List;

/**
 * Contiene todos los datos necesarios para construir un nivel.
 * Incluye información sobre el fondo, el suelo, los objetos sólidos, los enemigos y los NPCs.
 * @author MachineHunterDev
 */
public class LevelData {
    public String backgroundTexture = "FondoJuego.png";
    public String groundTexture = "suelo.png";
    
    // Plataformas y suelo
    public ArrayList<SolidObjectData> solidObjectsData;
    
    // Posición inicial del jugador
    public float playerStartX = 50f;
    public float playerStartY = 100f;
    
    // Enemigos
    public List<EnemyData> enemies;
    
    // NPCs
    public List<NPCData> npcs;
    
    // Diálogos del nivel
    public String dialogueFile = "Dialogos/Dialogos_personajes.json";
    public String dialogueSection = "Dialogos_acto2";
    public String nextLevel = null;
    
    public LevelData() {
        solidObjectsData = new ArrayList<>();
        enemies = new ArrayList<>();
        npcs = new ArrayList<>();
    }
    
    public static class SolidObjectData {
        public float x, y, width, height;
        public String texture = "suelo.png";
        public boolean walkable = true;
    }
    
    public static class Point {
        public float x, y;
    }

    public static class EnemyData {
        public EnemyType type = EnemyType.PATROLLER;
        public float x, y;
        public List<Point> patrolPoints = new ArrayList<>();
        public float waitTime = 3.0f;
        public int health = 50;
        public float shootInterval = 2.0f;
    }
    
    public static class NPCData {
        public String idleFrames = "Player/PlayerIdle";
        public String runFrames = "Player/PlayerRun";
        public String hurtFrames = "Player/PlayerHurt";
        public String jumpFrames = "Player/PlayerJump";
        public String fallFrames = "Player/PlayerFall";
        public String crouchFrames = "Player/PlayerCrouch";
        public float x, y;
        public float interactionRadius = 50f;
        public List<String> dialogues;
    }
}