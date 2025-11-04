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
    public List<String> flashbackDialogues;
    public String flashbackDialogueSection;

    // Hitbox para el jugador
    public HitboxData playerHitbox = new HitboxData(9, 0, 25, 38);
    
    /**
     * Constructor de LevelData.
     */
    public LevelData() {
        solidObjectsData = new ArrayList<>();
        enemies = new ArrayList<>();
        npcs = new ArrayList<>();
    }

    /**
     * Datos para el hitbox de un personaje.
     */
    public static class HitboxData {
        public float offsetX = 0;
        public float offsetY = 0;
        public float width = 32;
        public float height = 32;

        public HitboxData() {}

        public HitboxData(float offsetX, float offsetY, float width, float height) {
            this.offsetX = offsetX;
            this.offsetY = offsetY;
            this.width = width;
            this.height = height;
        }
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
        public String action;
    }

    /**
     * Datos de un enemigo en el nivel.
     */
    public static class EnemyData {
        public EnemyType type = EnemyType.PATROLLER;
        public String name;
        public float x, y;
        public List<Point> patrolPoints = new ArrayList<>();
        public float waitTime = 3.0f;
        public float shootInterval = 2.0f;
        public float shootTime = 1.0f;
        public HitboxData hitbox;
    }
    
    /**
     * Datos de un NPC en el nivel.
     */
    public static class NPCData {
        public String idleFrames = "Player/PlayerIdle";
        public float x, y;
        public float interactionRadius = 50f;
        public List<String> dialogues;
    }
}