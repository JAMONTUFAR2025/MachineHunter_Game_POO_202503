package com.machinehunterdev.game.Gameplay;

public class GlobalSettings
{
    // Define la resolución virtual/interna del juego
    public static final int VIRTUAL_WIDTH = 480;
    public static final int VIRTUAL_HEIGHT = 270;

    // Salud inicial del jugador
    public static final int PLAYER_HEALTH = 3;

    // Pixel del suelo
    public static final int GROUND_LEVEL = 32;

    // Instancia singleton del GlobalSettings
    public static GlobalSettings instance = new GlobalSettings();

    // Constructor privado para evitar instanciación externa
    private GlobalSettings()
    {
        instance = this;
    }
}
