package com.machinehunterdev.game.Character;

/**
 * Implementación de un jefe.
 * 
 * @author MachineHunterDev
 */
public class BossEnemy extends BaseEnemy
{
    /**
     * Constructor del Jefe.
     * @param character El personaje del jefe.
     * @param type El tipo de enemigo.
     */
    public BossEnemy(Character character, EnemyType type) {
        super(character, new BossEnemyController(character, type), type);
    }
    
}
