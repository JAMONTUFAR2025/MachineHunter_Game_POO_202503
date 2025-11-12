package com.machinehunterdev.game.Character;

/**
 * Implementacion de un enemigo tirador.
 * Este tipo de enemigo es capaz de disparar proyectiles al jugador.
 * Utiliza un controlador especifico para su comportamiento de disparo.
 * 
 * @author MachineHunterDev
 */
public class ShooterEnemy extends BaseEnemy {
    /**
     * Constructor del enemigo tirador.
     * @param character El personaje asociado al enemigo, que contiene su estado (posicion, vida, etc.).
     * @param shootInterval El intervalo de tiempo en segundos entre cada disparo.
     * @param shootTime La duracion del estado de ataque.
     * @param type El tipo de enemigo, que en este caso sera SHOOTER.
     */
    public ShooterEnemy(Character character, float shootInterval, float shootTime, EnemyType type, boolean wasSummoned) {
        // Llama al constructor de la clase base (BaseEnemy) para inicializar el enemigo.
        // Le pasa el personaje y un nuevo controlador especifico para enemigos tiradores.
        super(character, new ShooterEnemyController(character, shootInterval, shootTime, wasSummoned), type);
    }
}
