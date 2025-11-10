package com.machinehunterdev.game.Character;

/**
 * Clase que representa a un enemigo de tipo jefe (Boss) en el juego.
 * Esta clase extiende 'BaseEnemy' y esta disenada para ser utilizada con un 'BossEnemyController',
 * que contiene la logica de IA compleja especifica para los jefes.
 * 
 * @author MachineHunterDev
 */
public class BossEnemy extends BaseEnemy
{
    /**
     * Constructor para crear un enemigo de tipo jefe.
     * @param character El objeto Character que define las propiedades base del jefe (salud, posicion, animaciones, etc.).
     * @param type El tipo de enemigo, que debe ser uno de los tipos de jefe definidos en 'EnemyType' (ej. BOSS_GEMINI).
     */
    public BossEnemy(Character character, EnemyType type) {
        // Llama al constructor de la clase padre (BaseEnemy) y le asigna un nuevo BossEnemyController.
        // Esto asegura que el jefe siempre este controlado por la logica de IA correcta.
        super(character, new BossEnemyController(character, type), type);
    }
    
}