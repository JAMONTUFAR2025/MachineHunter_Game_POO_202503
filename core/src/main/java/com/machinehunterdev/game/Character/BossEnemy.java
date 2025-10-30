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
     * @param controller Controlador.
     * @param type El tipo de enemigo.
     */
    public BossEnemy(Character character, CharacterController controller, EnemyType type) {
        super(character, controller, type);
    }
    
}
