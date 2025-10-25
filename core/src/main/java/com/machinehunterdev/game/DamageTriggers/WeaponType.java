package com.machinehunterdev.game.DamageTriggers;

/**
 * Define los tipos de armas disponibles en el juego.
 * 
 * @author MachineHunterDev
 */
public enum WeaponType {
    LASER(15),      // Bala a larga distancia
    ION(10),        // Disparo múltiple a corta distancia
    RAILGUN(5),     // Bala perforante de impacto
    THUNDER(1),     // Proyectil enemigo predeterminado
    ELECTRIC(1);    // Daño por contacto patrullero

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
