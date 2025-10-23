package com.machinehunterdev.game.Character;

import com.badlogic.gdx.math.Vector2;

import java.util.ArrayList;

/**
 * Implementación de un enemigo volador.
 * Utiliza un controlador específico para comportamiento de vuelo y patrullaje.
 * 
 * @author MachineHunterDev
 */
public class FlyingEnemy extends BaseEnemy {
    /**
     * Constructor del enemigo volador.
     * @param character El personaje asociado al enemigo.
     * @param patrolPoints Puntos de patrullaje para el enemigo.
     * @param waitTime Tiempo de espera entre patrullas.
     */
    public FlyingEnemy(Character character, ArrayList<Vector2> patrolPoints, float waitTime) {
        super(character, new FlyingEnemyController(character, patrolPoints, waitTime));
    }
}
