package com.machinehunterdev.game.DamageTriggers;

/**
 * Define los tipos de armas y fuentes de dano en el juego.
 * Cada tipo de arma tiene una cantidad de dano base asociada.
 * Esto se usa tanto para las armas del jugador como para los ataques de los enemigos.
 * 
 * @author MachineHunterDev
 */
public enum WeaponType {
    /**
     * Arma laser: un disparo rapido y de largo alcance.
     */
    LASER(20),
    
    /**
     * Arma de iones: un disparo de corto alcance que se dispersa, similar a una escopeta.
     */
    ION(10),
    
    /**
     * Canon de riel: un disparo potente que puede atravesar a multiples enemigos.
     */
    RAILGUN(15),
    
    /**
     * El ataque del enemigo 'Shooter'.
     */
    SHOOTER(1),
    
    /**
     * El dano por contacto del enemigo 'Patroller'.
     */
    PATROLLER(1),
    
    /**
     * El dano por contacto del enemigo 'Flying'.
     */
    FLYING(1);

    // La cantidad de dano base que inflige este tipo de arma o ataque.
    private final int damage;

    /**
     * Constructor para cada tipo de arma.
     * @param damage La cantidad de dano base.
     */
    WeaponType(int damage) {
        this.damage = damage;
    }

    /**
     * Obtiene la cantidad de dano asociada a este tipo de arma.
     * @return El dano del arma.
     */
    public int getDamage() {
        return damage;
    }
}
