package com.machinehunterdev.game.Character;

import com.badlogic.gdx.math.Vector2;

import java.util.ArrayList;

/**
 * Implementacion de un enemigo volador.
 * Este tipo de enemigo no se ve afectado por la gravedad y se mueve por el aire.
 * Utiliza un controlador especifico para su comportamiento de vuelo y patrullaje.
 * 
 * @author MachineHunterDev
 */
public class FlyingEnemy extends BaseEnemy {
    /**
     * Constructor del enemigo volador.
     * @param character El personaje asociado al enemigo, que contiene su estado (posicion, vida, etc.).
     * @param patrolPoints Una lista de puntos que definen la ruta de patrullaje aerea del enemigo.
     * @param waitTime El tiempo en segundos que el enemigo esperara en cada punto de patrullaje.
     * @param type El tipo de enemigo, que en este caso sera FLYING.
     */
    public FlyingEnemy(Character character, ArrayList<Vector2> patrolPoints, float waitTime, EnemyType type) {
        // Llama al constructor de la clase base (BaseEnemy) para inicializar el enemigo.
        // Le pasa el personaje y un nuevo controlador especifico para enemigos voladores.
        super(character, new FlyingEnemyController(character, patrolPoints, waitTime), type);
    }
}
