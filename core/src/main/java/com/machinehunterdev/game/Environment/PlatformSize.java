package com.machinehunterdev.game.Environment;

public enum PlatformSize {
    HUGE("Huge", 288, 16),
    LARGE("Large", 224, 16),
    MEDIUM("Medium", 160, 16),
    SMALL("Small", 96, 16),
    TINY("Tiny", 64, 16);

    private final String name;
    private final float width;
    private final float height;

    PlatformSize(String name, float width, float height) {
        this.name = name;
        this.width = width;
        this.height = height;
    }

    public String getName() {
        return name;
    }

    public float getWidth() {
        return width;
    }

    public float getHeight() {
        return height;
    }

    public static PlatformSize fromString(String text) {
        if (text == null) {
            throw new IllegalArgumentException("El texto del tamaño no puede ser nulo");
        }
        for (PlatformSize s : PlatformSize.values()) {
            if (s.name.equalsIgnoreCase(text)) {
                return s;
            }
        }
        throw new IllegalArgumentException("No se encontró un tamaño para: " + text);
    }
}
