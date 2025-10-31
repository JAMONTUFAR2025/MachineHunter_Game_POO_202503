package com.machinehunterdev.game.DamageTriggers;

/**
 * Define los tipos de armas disponibles en el juego.
 * 
 * @author MachineHunterDev
 */
public enum WeaponType {
    LASER(20),      // Bala a larga distancia
    ION(10),        // Disparo múltiple a corta distancia
    RAILGUN(15),     // Bala perforante de impacto
    SHOOTER(1),     // Daño del Disparador
    PATROLLER(1),   // Daño del Patrullero
    FLYING(1);      // Daño del Volador

    private final int damage; // Daño asociado al tipo de arma

    /**
     * Constructor del tipo de arma.
     * @param damage Daño asociado al tipo de arma.
     */
    WeaponType(int damage) {
        this.damage = damage;
    }

    /**
     * Obtiene el daño asociado al tipo de arma.
     * @return Daño del arma.
     */
    public int getDamage() {
        return damage;
    }
}
