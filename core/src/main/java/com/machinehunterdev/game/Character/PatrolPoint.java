package com.machinehunterdev.game.Character;

import com.badlogic.gdx.math.Vector2;

/**
 * Representa un punto en una ruta de patrullaje para un enemigo.
 * Contiene la posicion del punto y una accion opcional que el enemigo
 * debe realizar al llegar a el (como saltar o caer).
 * 
 * @author MachineHunterDev
 */
public class PatrolPoint {
    
    /**
     * La coordenada (x, y) del punto de patrullaje en el mundo del juego.
     */
    public Vector2 position;
    
    /**
     * Una cadena de texto que define una accion especial a realizar en este punto.
     * Puede ser "Jump" (saltar), "Fall" (caer a traves de una plataforma), o null si no hay accion.
     */
    public String action;

    /**
     * Constructor para crear un nuevo punto de patrullaje.
     * @param position La posicion del punto.
     * @param action La accion a realizar en este punto (puede ser null).
     */
    public PatrolPoint(Vector2 position, String action) {
        this.position = position;
        this.action = action;
    }
}
