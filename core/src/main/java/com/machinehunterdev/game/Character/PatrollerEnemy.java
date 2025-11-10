package com.machinehunterdev.game.Character;

import com.badlogic.gdx.math.Vector2;

import java.util.ArrayList;

/**
 * Implementacion de un enemigo patrullero.
 * Este tipo de enemigo se mueve entre puntos predefinidos en el nivel.
 * Utiliza un controlador especifico para su comportamiento de patrullaje.
 * 
 * @author MachineHunterDev
 */
public class PatrollerEnemy extends BaseEnemy {
    /**
     * Constructor del enemigo patrullero.
     * @param character El personaje del enemigo patrullero, que contiene su estado (posicion, vida, etc.).
     * @param patrolPoints Una lista de puntos que definen la ruta de patrullaje del enemigo.
     * @param waitTime El tiempo en segundos que el enemigo esperara en cada punto de patrullaje.
     * @param type El tipo de enemigo, que en este caso sera PATROLLER.
     */
    public PatrollerEnemy(Character character, java.util.ArrayList<com.machinehunterdev.game.Levels.LevelData.Point> patrolPoints, float waitTime, EnemyType type) {
        // Llama al constructor de la clase base (BaseEnemy) para inicializar el enemigo.
        // Le pasa el personaje y un nuevo controlador especifico para enemigos patrulleros.
        super(character, new PatrollerEnemyController(character, patrolPoints, waitTime), type);
    }
}