package com.machinehunterdev.game.Gameplay;
import com.badlogic.gdx.Input.Keys;

/**
 * Clase de configuración global del juego.
 * Contiene constantes y configuraciones compartidas en todo el juego.
 * Implementa el patrón Singleton.
 * 
 * @author MachineHunterDev
 */
public class GlobalSettings
{
    /** Resolución virtual del juego */
    public static final int VIRTUAL_WIDTH = 480;
    public static final int VIRTUAL_HEIGHT = 270;

    /** Nombre del jugador (para clasificaciones) */
    public static String playerName;
    
    /** Salud inicial del jugador */
    public static final int PLAYER_HEALTH = 3;
    
    /** Salud inicial de enemigos */
    public static final int PATROLLER_HEALTH = 150;
    public static final int SHOOTER_HEALTH = 120;
    public static final int FLYING_HEALTH = 210;

    /** Salud de los jefes */
    public static final int BOSS_GEMINI_HEALTH = 3000;
    public static final int BOSS_CHATGPT_HEALTH = 4500;

    /** Controles para el jugador */
    public static final int CONTROL_MOVE_LEFT = Keys.A;
    public static final int CONTROL_MOVE_RIGHT = Keys.D;
    public static final int CONTROL_JUMP = Keys.W;
    public static final int CONTROL_CROUCH = Keys.S;
    public static final int CONTROL_ATTACK = Keys.SPACE;
    public static final int CONTROL_INTERACT = Keys.E;
    public static final int CONTROL_CONFIRM = Keys.ENTER;
    public static final int CONTROL_CANCEL = Keys.Q;
    public static final int CONTROL_BACKSPACE = Keys.BACKSPACE;
    public static final int CONTROL_PAUSE = Keys.ESCAPE;
    public static final int CHANGE_WEAPON_LASER = Keys.J;
    public static final int CHANGE_WEAPON_ION = Keys.K;
    public static final int CHANGE_WEAPON_RAILGUN = Keys.L;

    /** Archivo del nivel actual */
    public static String currentLevelFile;

    /** Ancho del nivel actual */
    public static int levelWidth;

    /** Nivel Y del suelo principal */
    public static final int GROUND_LEVEL = 32;

    /** Instancia singleton de la configuración global */
    public static GlobalSettings instance = new GlobalSettings();

    /**
     * Constructor privado para implementar el patrón Singleton.
     */
    private GlobalSettings()
    {
        instance = this;
    }
}