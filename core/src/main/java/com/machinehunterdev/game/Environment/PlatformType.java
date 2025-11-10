package com.machinehunterdev.game.Environment;

import java.util.HashMap;
import java.util.Map;

/**
 * Representa un tipo especifico de plataforma, combinando un tamano y un color.
 * Esta clase utiliza un patron de diseno Flyweight (a traves de un cache) para
 * evitar crear multiples instancias del mismo tipo de plataforma, optimizando la memoria.
 * 
 * @author MachineHunterDev
 */
public class PlatformType {

    // === PROPIEDADES DE LA PLATAFORMA ===
    public final PlatformSize size; // El tamano de la plataforma (HUGE, LARGE, etc.).
    public final String color; // El color de la plataforma (ej. "Red", "Blue").
    public final float width; // El ancho, obtenido del tamano.
    public final float height; // El alto, obtenido del tamano.
    public final String texturePath; // La ruta al archivo de textura, construida a partir del color y tamano.

    // Cache para almacenar y reutilizar instancias de PlatformType ya creadas.
    private static final Map<String, PlatformType> cache = new HashMap<>();

    /**
     * Constructor privado para crear una nueva instancia de PlatformType.
     * Solo se llama desde el metodo 'parse' cuando se encuentra un nuevo tipo.
     * @param size El tamano de la plataforma.
     * @param color El color de la plataforma.
     */
    private PlatformType(PlatformSize size, String color) {
        this.size = size;
        this.color = color;
        this.width = size.getWidth();
        this.height = size.getHeight();
        // Construye la ruta de la textura siguiendo una convencion de nombres.
        this.texturePath = "Environment/Platform" + color + size.getName() + ".png";
    }

    /**
     * Metodo estatico de fabrica que analiza una cadena de texto y devuelve un objeto PlatformType.
     * Utiliza un sistema de cache para mejorar el rendimiento.
     * @param typeString La cadena de texto a analizar (ej. "Platform_Red_Small").
     * @return Un objeto PlatformType con las propiedades deducidas.
     */
    public static PlatformType parse(String typeString) {
        if (typeString == null || typeString.isEmpty()) {
            return null;
        }
        // Si el tipo ya esta en el cache, lo devuelve directamente.
        if (cache.containsKey(typeString)) {
            return cache.get(typeString);
        }

        try {
            String[] parts = typeString.split("_");
            if (parts.length != 3 || !parts[0].equalsIgnoreCase("Platform")) {
                throw new IllegalArgumentException("El formato del tipo debe ser 'Platform_Color_Size'");
            }
            
            String color = parts[1];
            // Formatea el color para que la primera letra sea mayuscula y el resto minusculas.
            String formattedColor = color.substring(0, 1).toUpperCase() + color.substring(1).toLowerCase();

            // Convierte la parte del tamano del string al enum PlatformSize.
            PlatformSize size = PlatformSize.fromString(parts[2]);

            // Crea una nueva instancia de PlatformType.
            PlatformType newType = new PlatformType(size, formattedColor);
            // La almacena en el cache para futuras solicitudes.
            cache.put(typeString, newType);
            return newType;

        } catch (Exception e) {
            System.err.println("Error al analizar el PlatformType: '" + typeString + "'. " + e.getMessage());
            return null; // Devuelve null si el formato de la cadena es incorrecto.
        }
    }
}
