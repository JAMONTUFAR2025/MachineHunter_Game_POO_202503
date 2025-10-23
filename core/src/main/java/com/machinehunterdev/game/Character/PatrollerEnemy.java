package com.machinehunterdev.game.Character;

import com.badlogic.gdx.math.Vector2;

import java.util.ArrayList;

/**
 * Implementación de un enemigo patrullero.
 * Utiliza un controlador específico para comportamiento de patrullaje.
 * 
 * @author MachineHunterDev
 */
public class PatrollerEnemy extends BaseEnemy {
    /**
     * Constructor del enemigo patrullero.
     * @param character El personaje del enemigo patrullero.
     * @param patrolPoints Puntos de patrullaje.
     * @param waitTime Tiempo de espera en cada punto.
     */
    public PatrollerEnemy(Character character, ArrayList<Vector2> patrolPoints, float waitTime) {
        super(character, new PatrollerEnemyController(character, patrolPoints, waitTime));
    }
}
