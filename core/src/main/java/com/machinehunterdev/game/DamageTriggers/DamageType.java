package com.machinehunterdev.game.DamageTriggers;

/**
 * Enumeracion que define los diferentes tipos de dano que pueden ocurrir en el juego.
 * Esto permite al sistema de dano y a los personajes reaccionar de manera diferente
 * segun la fuente del dano.
 * 
 * @author MachineHunterDev
 */
public enum DamageType {
    /**
     * Dano infligido por el contacto fisico directo con un enemigo.
     * Generalmente, este tipo de dano tambien aplica un efecto de empuje (knockback).
     */
    CONTACT,
    
    /**
     * Dano causado por proyectiles, como balas disparadas por el jugador o los enemigos.
     */
    PROJECTILE,
    
    /**
     * Dano proveniente de elementos del entorno, como pinchos, lava, o trampas.
     */
    ENVIRONMENTAL
}