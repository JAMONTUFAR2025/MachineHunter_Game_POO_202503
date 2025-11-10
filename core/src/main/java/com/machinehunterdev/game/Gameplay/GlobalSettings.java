package com.machinehunterdev.game.Gameplay;
import com.badlogic.gdx.Input.Keys;

/**
 * Clase de configuracion global para el juego.
 * Contiene constantes estaticas y configuraciones que son compartidas
 * a traves de diferentes partes del codigo, como la resolucion, los controles,
 * la salud de los personajes, etc.
 * 
 * @author MachineHunterDev
 */
public class GlobalSettings
{
    // === RESOLUCION Y PANTALLA ===
    /** La resolucion virtual del juego en pixeles. El juego se renderiza a esta resolucion y luego se escala a la ventana. */
    public static final int VIRTUAL_WIDTH = 480;
    public static final int VIRTUAL_HEIGHT = 270;

    // === AUDIO ===
    /** Un volumen base para sonidos que pueden ser repetitivos o molestos, como los disparos. */
    public static final float ANNOYING_VOLUME = 0.2f;

    // === DATOS DEL JUGADOR Y NIVELES ===
    /** El nombre del jugador, que puede ser establecido por el usuario. */
    public static String playerName;
    /** El archivo del nivel que se esta jugando actualmente. */
    public static String currentLevelFile;
    /** El ancho total del nivel actual en unidades del juego. */
    public static int levelWidth;
    /** La coordenada Y del suelo principal del nivel. */
    public static final int GROUND_LEVEL = 32;

    // === SALUD DE PERSONAJES ===
    /** La salud inicial del jugador. */
    public static final int PLAYER_HEALTH = 3;
    
    /** La salud inicial para cada tipo de enemigo estandar. */
    public static final int PATROLLER_HEALTH = 180;
    public static final int SHOOTER_HEALTH = 120;
    public static final int FLYING_HEALTH = 150;

    /** La salud para los jefes. */
    public static final int BOSS_GEMINI_HEALTH = 3000;
    public static final int BOSS_CHATGPT_HEALTH = 4500;

    // === CONTROLES DEL JUEGO ===
    /** Mapeo de las acciones del juego a las teclas del teclado. */
    public static final int CONTROL_MOVE_LEFT = Keys.A; // Mover a la izquierda
    public static final int CONTROL_MOVE_RIGHT = Keys.D; // Mover a la derecha
    public static final int CONTROL_JUMP = Keys.W; // Saltar
    public static final int CONTROL_CROUCH = Keys.S; // Agacharse o bajar de una plataforma
    public static final int CONTROL_ATTACK = Keys.SPACE; // Atacar
    public static final int CONTROL_INTERACT = Keys.E; // Interactuar con NPCs o elementos
    public static final int CONTROL_CONFIRM = Keys.ENTER; // Confirmar en los menus
    public static final int CONTROL_CANCEL = Keys.Q; // Cancelar o volver atras
    public static final int CONTROL_BACKSPACE = Keys.BACKSPACE; // Borrar texto
    public static final int CONTROL_PAUSE = Keys.ESCAPE; // Pausar el juego
    
    /** Teclas para cambiar de arma. */
    public static final int CHANGE_WEAPON_LASER = Keys.J;
    public static final int CHANGE_WEAPON_ION = Keys.K;
    public static final int CHANGE_WEAPON_RAILGUN = Keys.L;

    /**
     * Constructor privado para prevenir la instanciacion de esta clase de utilidades.
     * Todos los miembros son estaticos.
     */
    private GlobalSettings()
    {
        // Constructor privado para una clase de configuracion estatica.
    }
}