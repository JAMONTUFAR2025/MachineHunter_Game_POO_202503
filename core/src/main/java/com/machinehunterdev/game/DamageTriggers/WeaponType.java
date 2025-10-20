package com.machinehunterdev.game.DamageTriggers;

/**
 * Define los tipos de armas disponibles en el juego.
 * @author MachineHunterDev
 */

/**
 * Enumeración que define los diferentes tipos de armas disponibles.
 */
public enum WeaponType {
    LASER(15),      // Bala a larga distancia
    ION(10),        // Disparo múltiple a corta distancia
    RAILGUN(5),     // Bala perforante de impacto
    THUNDER(1);     // Proyectil enemigo predeterminado

    private final int damage;

    WeaponType(int damage) {
        this.damage = damage;
    }

    public int getDamage() {
        return damage;
    }
}
