package com.machinehunterdev.game.Character;

/**
 * Definición de skins para diferentes tipos de enemigos.
 * Cada skin especifica las rutas de los frames de animación.
 * 
 * @author MachineHunterDev
 */
public enum EnemySkin {
    /**
     * Skin del enemigo patrullero
     * Idle, Run, Dead, Hurt
    */
    PATROLLER("Enemy/Patroller/PatrollerIdle", "Enemy/Patroller/PatrollerRun", "Enemy/Explosion", "Enemy/Patroller/PatrollerHurt", null, null, null),
    /**
     * Skin del enemigo tirador
     * Idle, Run, Dead, Hurt, Attack
     */
    SHOOTER("Enemy/Shooter/ShooterIdle", "Enemy/Shooter/ShooterRun", "Enemy/Explosion", "Enemy/Shooter/ShooterHurt", null, null, "Enemy/Shooter/ShooterAttack"),
    /**
     * Skin del enemigo volador
     * Idle, Dead, Hurt
     */
    FLYING("Enemy/Flying/FlyingIdle", null, "Enemy/Explosion", "Enemy/Flying/FlyingHurt", null, null, null);

    /** Rutas de los frames de animación */
    public final String idleFrames;
    public final String runFrames;
    public final String deadFrames;
    public final String hurtFrames;
    public final String jumpFrames;
    public final String fallFrames;
    public final String attackFrames;

    /** Constructor completo para skins de enemigos 
     * @param idleFrames Ruta de los frames de animación de idle
     * @param runFrames Ruta de los frames de animación de run
     * @param deadFrames Ruta de los frames de animación de dead
     * @param hurtFrames Ruta de los frames de animación de hurt
     * @param jumpFrames Ruta de los frames de animación de jump
     * @param fallFrames Ruta de los frames de animación de fall
     * @param attackFrames Ruta de los frames de animación de attack
    */
    EnemySkin(String idleFrames, String runFrames, String deadFrames, String hurtFrames, String jumpFrames, String fallFrames, String attackFrames) {
        this.idleFrames = idleFrames;
        this.runFrames = runFrames;
        this.deadFrames = deadFrames;
        this.hurtFrames = hurtFrames;
        this.jumpFrames = jumpFrames;
        this.fallFrames = fallFrames;
        this.attackFrames = attackFrames;
    }

    /** Constructor para el enemigo patrullero 
     * @param idleFrames Ruta de los frames de animación de idle
     * @param runFrames Ruta de los frames de animación de run
     * @param deadFrames Ruta de los frames de animación de dead
     * @param hurtFrames Ruta de los frames de animación de hurt
    */
    EnemySkin(String idleFrames, String runFrames, String deadFrames, String hurtFrames) {
        this(idleFrames, runFrames, deadFrames, hurtFrames, null, null, null);
    }

    /** Constructor para el enemigo volador 
     * @param idleFrames Ruta de los frames de animación de idle
     * @param deadFrames Ruta de los frames de animación de dead
     * @param hurtFrames Ruta de los frames de animación de hurt
    */
    EnemySkin(String idleFrames, String deadFrames, String hurtFrames) {
        this(idleFrames, null, deadFrames, hurtFrames, null, null, null);
    }

    /** Obtiene el skin correspondiente a un tipo de enemigo 
     * @param enemyType Tipo de enemigo
     * @return Skin asociado al tipo de enemigo
    */
    public static EnemySkin getSkin(EnemyType enemyType) {
        switch (enemyType) {
            case PATROLLER:
                return PATROLLER;
            case SHOOTER:
                return SHOOTER;
            case FLYING:
                return FLYING;
            default:
                throw new IllegalArgumentException("No skin found for enemy type: " + enemyType);
        }
    }
}
