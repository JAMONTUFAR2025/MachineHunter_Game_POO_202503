package com.machinehunterdev.game.Environment;

import java.util.HashMap;
import java.util.Map;

public class ChainType {

    public final String color;
    public final float width = 8;
    public final float height = 272;
    public final String texturePath;

    private static final Map<String, ChainType> cache = new HashMap<>();

    private ChainType(String color) {
        this.color = color;
        this.texturePath = "Environment/Chain" + color + ".png";
    }

    /**
     * Parses a type string (e.g., "Chain_Red") and returns a ChainType object.
     * Uses a cache to avoid re-parsing the same string.
     * @param typeString The string to parse.
     * @return A ChainType object with the deduced properties.
     */
    public static ChainType parse(String typeString) {
        if (typeString == null || typeString.isEmpty()) {
            return null;
        }
        if (cache.containsKey(typeString)) {
            return cache.get(typeString);
        }

        try {
            String[] parts = typeString.split("_");
            if (parts.length != 2 || !parts[0].equalsIgnoreCase("Chain")) {
                throw new IllegalArgumentException("Type format must be 'Chain_Color'");
            }
            
            String color = parts[1];
            // Capitalize the color to match the file name (e.g., "red" -> "Red")
            String formattedColor = color.substring(0, 1).toUpperCase() + color.substring(1).toLowerCase();

            ChainType newType = new ChainType(formattedColor);
            cache.put(typeString, newType);
            return newType;

        } catch (Exception e) {
            System.err.println("Error parsing ChainType: '" + typeString + "'. " + e.getMessage());
            return null; // Return null if the format is incorrect
        }
    }
}
