package com.machinehunterdev.game.Character;

import com.machinehunterdev.game.Levels.LevelData.HitboxData;

/**
 * Definicion de skins para diferentes tipos de enemigos.
 * Cada skin especifica las rutas de los frames de animacion.
 * Esta enumeracion centraliza la configuracion visual de los enemigos,
 * facilitando la gestion de sus apariencias y animaciones.
 * 
 * @author MachineHunterDev
 */
public enum EnemySkin {
    /**
     * Skin del enemigo patrullero.
     * Define las animaciones para estado de reposo, correr, morir, ser herido, saltar y caer.
     * Incluye la configuracion de su hitbox.
     */
    PATROLLER("Enemy/Patroller/PatrollerIdle", "Enemy/Patroller/PatrollerRun", "Enemy/Explosion", "Enemy/Patroller/PatrollerHurt", null, "Enemy/Patroller/PatrollerJump", "Enemy/Patroller/PatrollerFall", null, new HitboxData(5, 0, 30, 35), null, null, null, null),
    
    /**
     * Skin del enemigo tirador.
     * Define las animaciones para estado de reposo, correr, morir, ser herido y atacar.
     * Tambien incluye una animacion para cuando detecta al jugador.
     */
    SHOOTER("Enemy/Shooter/ShooterIdle", "Enemy/Shooter/ShooterRun", "Enemy/Explosion", "Enemy/Shooter/ShooterHurt", null, null, null, "Enemy/Shooter/ShooterAttack", new HitboxData(9, 0, 23, 35), null, "Enemy/Shooter/ShooterSpot", null, null),
    
    /**
     * Skin del enemigo volador.
     * Define las animaciones para estado de reposo, morir y ser herido.
     * Al ser un enemigo aereo, no tiene animaciones de correr, saltar o caer.
     */
    FLYING("Enemy/Flying/FlyingIdle", null, "Enemy/Explosion", "Enemy/Flying/FlyingHurt", null, null, null, null, new HitboxData(3, 7, 34, 29), null, null, null, null),
    
    /**
     * Skin del jefe Gemini.
     * Define animaciones para sus estados normales y de furia, asi como para sus ataques especiales.
     * Incluye animaciones para reposo, muerte, ser herido, ataque de trueno, ataque de bolas de energia e invocacion.
     */
    BOSS_GEMINI("Enemy/GeminiEXE/GeminiEXEIdle", null, "Enemy/GeminiEXE/GeminiEXEDeath", "Enemy/GeminiEXE/GeminiEXEHurt", "Enemy/GeminiEXE/GeminiEXEHurtRage", null, null, null, new HitboxData(0, 0, 80, 100), "Enemy/GeminiEXE/GeminiEXEIdleRage", "Enemy/GeminiEXE/GeminiEXEAttackThunder", "Enemy/GeminiEXE/GeminiEXEAttackBalls", "Enemy/GeminiEXE/GeminiEXESummon"),
    
    /**
     * Skin del jefe ChatGPT.
     * Define animaciones para sus diferentes comportamientos, incluyendo un modo de furia.
     * Incluye animaciones para reposo, muerte, ser herido, ataques normales y en modo furia, e invocacion.
     */
    BOSS_CHATGPT("Enemy/ChatGPTEXE/ChatGPTEXEIdle", null, "Enemy/ChatGPTEXE/ChatGPTEXEDeath", "Enemy/ChatGPTEXE/ChatGPTEXEHurt", "Enemy/ChatGPTEXE/ChatGPTEXEHurtRage", null, null, null, new HitboxData(0, 0, 80, 100), "Enemy/ChatGPTEXE/ChatGPTEXEIdleRage", "Enemy/ChatGPTEXE/ChatGPTEXEAttack", "Enemy/ChatGPTEXE/ChatGPTEXEAttackRage", "Enemy/ChatGPTEXE/ChatGPTEXESummon");

    // Rutas a los archivos de frames de animacion.
    public final String idleFrames; // Animacion de reposo.
    public final String runFrames; // Animacion de carrera.
    public final String deadFrames; // Animacion de muerte.
    public final String hurtFrames; // Animacion al recibir dano.
    public final String angryHurtFrames; // Animacion al recibir dano en modo furia.
    public final String jumpFrames; // Animacion de salto.
    public final String fallFrames; // Animacion de caida.
    public final String attackFrames; // Animacion de ataque basico.
    public final HitboxData hitbox; // Datos de la caja de colision.
    public final String idleRageFrames; // Animacion de reposo en modo furia.
    public final String attack1Frames; // Animacion para el primer ataque especial.
    public final String attack2Frames; // Animacion para el segundo ataque especial.
    public final String summonFrames; // Animacion de invocacion.

    /** 
     * Constructor completo para las skins de los enemigos.
     * @param idleFrames Ruta de los frames de animacion de reposo.
     * @param runFrames Ruta de los frames de animacion de carrera.
     * @param deadFrames Ruta de los frames de animacion de muerte.
     * @param hurtFrames Ruta de los frames de animacion al ser herido.
     * @param angryHurtFrames Ruta de los frames de animacion al ser herido en modo furia.
     * @param jumpFrames Ruta de los frames de animacion de salto.
     * @param fallFrames Ruta de los frames de animacion de caida.
     * @param attackFrames Ruta de los frames de animacion de ataque.
     * @param hitbox Hitbox por defecto para el tipo de enemigo.
     * @param idleRageFrames Ruta de los frames de animacion de reposo en modo furia.
     * @param attack1Frames Ruta de los frames de animacion para el ataque 1.
     * @param attack2Frames Ruta de los frames de animacion para el ataque 2.
     * @param summonFrames Ruta de los frames de animacion para la invocacion.
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

    /** 
     * Obtiene el skin correspondiente a un tipo de enemigo.
     * Este metodo actua como un puente entre el tipo de enemigo y su apariencia.
     * @param enemyType El tipo de enemigo del que se quiere obtener el skin.
     * @return El skin asociado al tipo de enemigo.
     * @throws IllegalArgumentException si no se encuentra un skin para el tipo de enemigo especificado.
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
                // Lanza una excepcion si el tipo de enemigo no tiene un skin definido.
                throw new IllegalArgumentException("No skin found for enemy type: " + enemyType);
        }
    }
}