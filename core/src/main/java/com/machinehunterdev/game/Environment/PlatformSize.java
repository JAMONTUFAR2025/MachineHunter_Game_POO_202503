package com.machinehunterdev.game.Environment;

/**
 * Enumeracion que define los diferentes tamanos estandar para las plataformas en el juego.
 * Cada tamano tiene un nombre y dimensiones (ancho y alto) asociadas.
 * Esto ayuda a estandarizar la creacion de plataformas en los niveles.
 * 
 * @author MachineHunterDev
 */
public enum PlatformSize {
    /**
     * La plataforma mas grande.
     */
    HUGE("Huge", 288, 16),
    
    /**
     * Una plataforma grande.
     */
    LARGE("Large", 224, 16),
    
    /**
     * Una plataforma de tamano mediano.
     */
    MEDIUM("Medium", 160, 16),
    
    /**
     * Una plataforma pequena.
     */
    SMALL("Small", 96, 16),
    
    /**
     * La plataforma mas pequena.
     */
    TINY("Tiny", 64, 16);

    private final String name; // El nombre del tamano (ej. "Huge").
    private final float width; // El ancho de la plataforma en unidades del juego.
    private final float height; // El alto de la plataforma en unidades del juego.

    /**
     * Constructor para cada tamano de plataforma.
     * @param name El nombre del tamano.
     * @param width El ancho de la plataforma.
     * @param height El alto de la plataforma.
     */
    PlatformSize(String name, float width, float height) {
        this.name = name;
        this.width = width;
        this.height = height;
    }

    /**
     * Obtiene el nombre del tamano.
     * @return El nombre del tamano como una cadena de texto.
     */
    public String getName() {
        return name;
    }

    /**
     * Obtiene el ancho de la plataforma.
     * @return El ancho de la plataforma.
     */
    public float getWidth() {
        return width;
    }

    /**
     * Obtiene el alto de la plataforma.
     * @return El alto de la plataforma.
     */
    public float getHeight() {
        return height;
    }

    /**
     * Convierte una cadena de texto al tamano de plataforma correspondiente.
     * Es insensible a mayusculas y minusculas.
     * @param text El texto que representa el tamano (ej. "Huge", "small").
     * @return El objeto PlatformSize correspondiente.
     * @throws IllegalArgumentException si el texto no corresponde a ningun tamano valido.
     */
    public static PlatformSize fromString(String text) {
        if (text == null) {
            throw new IllegalArgumentException("El texto del tamano no puede ser nulo");
        }
        for (PlatformSize s : PlatformSize.values()) {
            if (s.name.equalsIgnoreCase(text)) {
                return s;
            }
        }
        throw new IllegalArgumentException("No se encontro un tamano para: " + text);
    }
}
