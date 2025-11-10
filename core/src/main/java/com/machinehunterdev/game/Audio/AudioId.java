package com.machinehunterdev.game.Audio;

// Enumeracion que define todos los identificadores unicos para los sonidos del juego.
// Cada valor representa un evento de audio especifico, como acciones del jugador,
// enemigos, jefes o interacciones con la interfaz de usuario.
public enum AudioId {
    Talking,                    // Sonido de dialogo o voz
    PlayerJump, PlayerHurt, LaserAttack, IonAttack, RailgunAttack, PlayerLand,  // Sonidos del jugador
    EnemyJump, EnemyHurt, EnemyAttack, Explosion, EnemyLand, Exclamation,       // Sonidos de enemigos
    BossThunderWarning, BossThunderAttack, BossSummonWarning, BossSummonAttack, BossDeath, BossAngry, // Sonidos del jefe
    UIAccept, UIChange, UICancel, NotAvailable  // Sonidos de la interfaz de usuario
}