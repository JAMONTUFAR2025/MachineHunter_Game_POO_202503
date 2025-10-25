package com.machinehunterdev.game.Character;

/**
 * Implementación de un enemigo tirador.
 * Utiliza un controlador específico para comportamiento de disparo.
 * 
 * @author MachineHunterDev
 */
public class ShooterEnemy extends BaseEnemy {
    /**
     * Constructor del enemigo tirador.
     * @param character El personaje asociado al enemigo.
     * @param shootInterval Intervalo de tiempo entre disparos.
     * @param shootTime Duración del ataque.
     */
    public ShooterEnemy(Character character, float shootInterval, float shootTime, EnemyType type) {
        super(character, new ShooterEnemyController(character, shootInterval, shootTime), type);
    }
}
