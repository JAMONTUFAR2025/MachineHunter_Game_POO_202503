package com.machinehunterdev.game.Gameplay;

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

    /** Salud inicial del jugador */
    public static final int PLAYER_HEALTH = 3;

    /** Nivel Y del suelo principal */
    public static final int GROUND_LEVEL = 32;

    /** Instancia singleton de la configuración global */
    public static GlobalSettings instance = new GlobalSettings();

    /** Nombre del jugador (para clasificaciones) */
    public static String playerName;

    /** Archivo del nivel actual */
    public static String currentLevelFile;

    /**
     * Constructor privado para implementar el patrón Singleton.
     */
    private GlobalSettings()
    {
        instance = this;
    }
}