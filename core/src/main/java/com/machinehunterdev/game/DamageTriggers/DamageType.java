package com.machinehunterdev.game.DamageTriggers;

/**
 * Enumeración que define los diferentes tipos de daño en el juego.
 * @author MachineHunterDev
 */
public enum DamageType {
    CONTACT,    // Daño por contacto (enemigos, trampas)
    PROJECTILE, // Daño por proyectiles (balas, flechas)
    ENVIRONMENTAL, // Daño ambiental (lava, pinchos)
    EXPLOSIVE,  // Daño explosivo (granadas, bombas)
    MAGIC       // Daño mágico (hechizos, habilidades especiales)
}