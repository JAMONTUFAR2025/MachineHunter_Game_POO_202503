package com.machinehunterdev.game.Environment;

import java.util.HashMap;
import java.util.Map;

/**
 * Representa un tipo especifico de cadena decorativa en el entorno.
 * Al igual que PlatformType, esta clase combina propiedades como el color y el tipo
 * para definir una cadena unica. Tambien utiliza un cache para optimizar la memoria.
 * 
 * @author MachineHunterDev
 */
public class ChainType {

    // === PROPIEDADES DE LA CADENA ===
    public final String color; // El color de la cadena (ej. "Red", "Blue").
    public final String type; // El tipo o estilo de la cadena (ej. "Type1").
    public final float width = 8; // Ancho fijo para todas las cadenas.
    public final float height = 272; // Alto fijo para todas las cadenas.
    public final String texturePath; // La ruta al archivo de textura, construida a partir del color y tipo.

    // Cache para almacenar y reutilizar instancias de ChainType ya creadas.
    private static final Map<String, ChainType> cache = new HashMap<>();

    /**
     * Constructor privado para crear una nueva instancia de ChainType.
     * @param color El color de la cadena.
     * @param type El tipo o estilo de la cadena.
     */
    private ChainType(String color, String type) {
        this.color = color;
        this.type = type;
        // Construye la ruta de la textura siguiendo una convencion de nombres.
        this.texturePath = "Environment/Chain" + color + type + ".png";
    }

    /**
     * Metodo estatico de fabrica que analiza una cadena de texto y devuelve un objeto ChainType.
     * Utiliza un sistema de cache para mejorar el rendimiento.
     * @param typeString La cadena de texto a analizar (ej. "Chain_Red_Type1").
     * @return Un objeto ChainType con las propiedades deducidas.
     */
    public static ChainType parse(String typeString) {
        if (typeString == null || typeString.isEmpty()) {
            return null;
        }
        // Si el tipo ya esta en el cache, lo devuelve directamente.
        if (cache.containsKey(typeString)) {
            return cache.get(typeString);
        }

        try {
            String[] parts = typeString.split("_");
            if (parts.length != 3 || !parts[0].equalsIgnoreCase("Chain")) {
                throw new IllegalArgumentException("El formato del tipo debe ser 'Chain_Color_Type'");
            }
            
            String color = parts[1];
            String type = parts[2];
            // Formatea el color para que la primera letra sea mayuscula y el resto minusculas.
            String formattedColor = color.substring(0, 1).toUpperCase() + color.substring(1).toLowerCase();

            // Crea una nueva instancia de ChainType.
            ChainType newType = new ChainType(formattedColor, type);
            // La almacena en el cache para futuras solicitudes.
            cache.put(typeString, newType);
            return newType;

        } catch (Exception e) {
            System.err.println("Error al analizar el ChainType: '" + typeString + "'. " + e.getMessage());
            return null; // Devuelve null si el formato de la cadena es incorrecto.
        }
    }
}
