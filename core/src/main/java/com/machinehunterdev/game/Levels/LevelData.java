package com.machinehunterdev.game.Levels;

import com.machinehunterdev.game.Character.EnemyType;

import java.util.ArrayList;
import java.util.List;

/**
 * Contiene todos los datos necesarios para construir un nivel.
 * Incluye información sobre el fondo, el suelo, los objetos sólidos, los enemigos y los NPCs.
 * @author MachineHunterDev
 */
public class LevelData {
    // Texturas del nivel
    public String backgroundTexture = "FondoJuego.png";
    public String groundTexture = "suelo.png";
    
    // Ancho del nivel en píxeles
    public int levelWidth = 1440;

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
    
    /**
     * Constructor de LevelData.
     */
    public LevelData() {
        solidObjectsData = new ArrayList<>();
        enemies = new ArrayList<>();
        npcs = new ArrayList<>();
    }
    
    /**
     * Datos de un objeto sólido en el nivel.
     */
    public static class SolidObjectData {
        public float x, y;
        public boolean walkable = true;

        // Nuevo sistema basado en "type"
        public String type;

        // Sistema antiguo para objetos explícitos como el suelo
        public float width, height;
        public String texture;
    }
    
    /**
     * Punto 2D simple, alternativa porque JSON no maneja Vector2.
     */
    public static class Point {
        public float x, y;
    }

    /**
     * Datos de un enemigo en el nivel.
     */
    public static class EnemyData {
        public EnemyType type = EnemyType.PATROLLER;
        public float x, y;
        public List<Point> patrolPoints = new ArrayList<>();
        public float waitTime = 3.0f;
        public int health = 50;
        public float shootInterval = 2.0f;
        public float shootTime = 1.0f;
    }
    
    /**
     * Datos de un NPC en el nivel.
     */
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