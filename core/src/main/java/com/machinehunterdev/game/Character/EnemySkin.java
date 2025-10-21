package com.machinehunterdev.game.Character;

public enum EnemySkin {
    PATROLLER("Enemy/Patroller/PatrollerIdle", "Enemy/Patroller/PatrollerRun", "Enemy/Explosion", "Enemy/Patroller/PatrollerHurt", null, null),
    SHOOTER("Enemy/Shooter/ShooterIdle", "Enemy/Shooter/ShooterRun", "Enemy/Explosion", "Enemy/Shooter/ShooterHurt", null, null),
    FLYING("Enemy/Flying/FlyingIdle", null, "Enemy/Explosion", "Enemy/Flying/FlyingHurt", null, null);

    public final String idleFrames;
    public final String runFrames;
    public final String deadFrames;
    public final String hurtFrames;
    public final String jumpFrames;
    public final String fallFrames;

    /* Constructor principal */
    EnemySkin(String idleFrames, String runFrames, String deadFrames, String hurtFrames, String jumpFrames, String fallFrames) {
        this.idleFrames = idleFrames;
        this.runFrames = runFrames;
        this.deadFrames = deadFrames;
        this.hurtFrames = hurtFrames;
        this.jumpFrames = jumpFrames;
        this.fallFrames = fallFrames;
    }

    /* Constructor para el enemigo patrullero */
    EnemySkin(String idleFrames, String runFrames, String deadFrames, String hurtFrames) {
        this(idleFrames, runFrames, deadFrames, hurtFrames, null, null);
    }

    /* Constructor para el enemigo volador */
    EnemySkin(String idleFrames, String deadFrames, String hurtFrames) {
        this(idleFrames, null, deadFrames, hurtFrames, null, null);
    }


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
