package com.machinehunterdev.game.Environment;

import java.util.HashMap;
import java.util.Map;

public class PlatformType {

    public final PlatformSize size;
    public final String color;
    public final float width;
    public final float height;
    public final String texturePath;

    private static final Map<String, PlatformType> cache = new HashMap<>();

    private PlatformType(PlatformSize size, String color) {
        this.size = size;
        this.color = color;
        this.width = size.getWidth();
        this.height = size.getHeight();
        this.texturePath = "Enviorment/Platform" + color + size.getName() + ".png";
    }

    /**
     * Analiza un string de tipo (ej. "Platform_Red_Small") y devuelve un objeto PlatformType.
     * Utiliza un sistema de caché para no repetir el análisis del mismo string.
     * @param typeString El string a analizar.
     * @return Un objeto PlatformType con las propiedades deducidas.
     */
    public static PlatformType parse(String typeString) {
        if (typeString == null || typeString.isEmpty()) {
            return null;
        }
        if (cache.containsKey(typeString)) {
            return cache.get(typeString);
        }

        try {
            String[] parts = typeString.split("_");
            if (parts.length != 3 || !parts[0].equalsIgnoreCase("Platform")) {
                throw new IllegalArgumentException("El formato del tipo debe ser 'Platform_Color_Size'");
            }
            
            String color = parts[1];
            // Capitalizar el color para que coincida con el nombre del archivo (ej. "red" -> "Red")
            String formattedColor = color.substring(0, 1).toUpperCase() + color.substring(1).toLowerCase();

            PlatformSize size = PlatformSize.fromString(parts[2]);

            PlatformType newType = new PlatformType(size, formattedColor);
            cache.put(typeString, newType);
            return newType;

        } catch (Exception e) {
            System.err.println("Error al analizar el PlatformType: '" + typeString + "'. " + e.getMessage());
            return null; // Devuelve null si el formato es incorrecto
        }
    }
}
