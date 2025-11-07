package com.machinehunterdev.game.Character;

import com.machinehunterdev.game.Levels.LevelData.HitboxData;

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
    PATROLLER("Enemy/Patroller/PatrollerIdle", "Enemy/Patroller/PatrollerRun", "Enemy/Explosion", "Enemy/Patroller/PatrollerHurt", null, "Enemy/Patroller/PatrollerJump", "Enemy/Patroller/PatrollerFall", null, new HitboxData(5, 0, 30, 35), null, null, null, null),
    /**
     * Skin del enemigo tirador
     * Idle, Run, Dead, Hurt, Attack
     */
    SHOOTER("Enemy/Shooter/ShooterIdle", "Enemy/Shooter/ShooterRun", "Enemy/Explosion", "Enemy/Shooter/ShooterHurt", null, null, null, "Enemy/Shooter/ShooterAttack", new HitboxData(9, 0, 23, 35), null, null, null, "Enemy/Shooter/ShooterSpot"),
    /**
     * Skin del enemigo volador
     * Idle, Dead, Hurt
     */
    FLYING("Enemy/Flying/FlyingIdle", null, "Enemy/Explosion", "Enemy/Flying/FlyingHurt", null, null, null, null, new HitboxData(3, 7, 34, 29), null, null, null, null),
    /**
     * Skin del jefe
     * Idle, Dead, Hurt
     */
    BOSS_GEMINI("Enemy/GeminiEXE/GeminiEXEIdle", null, "Enemy/GeminiEXE/GeminiEXEDeath", "Enemy/GeminiEXE/GeminiEXEHurt", "Enemy/GeminiEXE/GeminiEXEHurtRage", null, null, null, new HitboxData(0, 0, 80, 100), "Enemy/GeminiEXE/GeminiEXEIdleRage", "Enemy/GeminiEXE/GeminiEXEAttackThunder", "Enemy/GeminiEXE/GeminiEXEAttackBalls", "Enemy/GeminiEXE/GeminiEXESummon"),
    /**
     * Skin del jefe ChatGPT
     * Idle, Dead, Hurt
     */
    BOSS_CHATGPT("Enemy/ChatGPTEXE/ChatGPTEXEIdle", null, "Enemy/ChatGPTEXE/ChatGPTEXEDeath", "Enemy/ChatGPTEXE/ChatGPTEXEHurt", "Enemy/ChatGPTEXE/ChatGPTEXEHurtRage", null, null, null, new HitboxData(0, 0, 80, 100), "Enemy/ChatGPTEXE/ChatGPTEXEIdleRage", "Enemy/ChatGPTEXE/ChatGPTEXEAttack", "Enemy/ChatGPTEXE/ChatGPTEXEAttackRage", "Enemy/ChatGPTEXE/ChatGPTEXESummon");

    /** Rutas de los frames de animación */
    public final String idleFrames;
    public final String runFrames;
    public final String deadFrames;
    public final String hurtFrames;
    public final String angryHurtFrames;
    public final String jumpFrames;
    public final String fallFrames;
    public final String attackFrames;
    public final HitboxData hitbox;
    public final String idleRageFrames;
    public final String attack1Frames;
    public final String attack2Frames;
    public final String summonFrames;

    /** Constructor completo para skins de enemigos 
     * @param idleFrames Ruta de los frames de animación de idle
     * @param runFrames Ruta de los frames de animación de run
     * @param deadFrames Ruta de los frames de animación de dead
     * @param hurtFrames Ruta de los frames de animación de hurt
     * @param jumpFrames Ruta de los frames de animación de jump
     * @param fallFrames Ruta de los frames de animación de fall
     * @param attackFrames Ruta de los frames de animación de attack
     * @param hitbox Hitbox por defecto para el tipo de enemigo
     * @param idleRageFrames Ruta de los frames de animación de idle en modo furia
     * @param attack1Frames Ruta de los frames de animación para el ataque 1
     * @param attack2Frames Ruta de los frames de animación para el ataque 2
     * @param summonFrames Ruta de los frames de animación para la invocación
    */
    EnemySkin(String idleFrames, String runFrames, String deadFrames, String hurtFrames, String angryHurtFrames, String jumpFrames, String fallFrames, String attackFrames, HitboxData hitbox, String idleRageFrames, String attack1Frames, String attack2Frames, String summonFrames) {
        this.idleFrames = idleFrames;
        this.runFrames = runFrames;
        this.deadFrames = deadFrames;
        this.hurtFrames = hurtFrames;
        this.angryHurtFrames = angryHurtFrames;
        this.jumpFrames = jumpFrames;
        this.fallFrames = fallFrames;
        this.attackFrames = attackFrames;
        this.hitbox = hitbox;
        this.idleRageFrames = idleRageFrames;
        this.attack1Frames = attack1Frames;
        this.attack2Frames = attack2Frames;
        this.summonFrames = summonFrames;
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
            case BOSS_GEMINI:
                return BOSS_GEMINI;
            case BOSS_CHATGPT:
                return BOSS_CHATGPT;
            default:
                throw new IllegalArgumentException("No skin found for enemy type: " + enemyType);
        }
    }
}