package com.machinehunterdev.game.Character;

import com.machinehunterdev.game.DamageTriggers.Bullet;
import com.machinehunterdev.game.Environment.SolidObject;

import java.util.ArrayList;

/**
 * Controlador específico para enemigos que disparan.
 * Maneja el comportamiento de disparo hacia el jugador.
 * 
 * @author MachineHunterDev
 */
public class ShooterEnemyController extends CharacterController {
    private float shootDuration;
    private float shootTime;
    private float shootCooldown;
    private float shootInterval;

    /**
     * Constructor del controlador de enemigos tiradores.
     * @param character El personaje asociado al enemigo.
     * @param shootInterval Intervalo de tiempo entre disparos.
     * @param shootTime Duración del ataque.
     */
    public ShooterEnemyController(Character character, float shootInterval, float shootTime) {
        super(character);
        this.shootInterval = shootInterval;
        this.shootCooldown = this.shootInterval; // Tiempo inicial antes del primer disparo
        this.shootTime = shootTime;
        this.shootDuration = this.shootTime;
    }

    /**
     * Actualiza el estado del enemigo tirador.
     * @param delta Tiempo transcurrido desde la última actualización.
     * @param solidObjects Objetos sólidos en el entorno.
     * @param bullets Balas en el entorno.
     * @param playerCharacter El personaje jugador.
     */
    @Override
    public void update(float delta, ArrayList<SolidObject> solidObjects, ArrayList<Bullet> bullets, Character playerCharacter) {
        checkCollisions(solidObjects);

        shootCooldown -= delta;

        if (shootCooldown <= 0 && character.onGround && !character.isInvulnerable()) {
            character.shoot(bullets);
            character.stopMoving();
            shootDuration -= delta;

            if(shootDuration <= 0) {
                shootCooldown = this.shootInterval;
                shootDuration = this.shootTime; // Duración del ataque
            }
        } else {
            character.stopAttacking();
        }

        // Hacer que el enemigo mire hacia el jugador
        if (playerCharacter.position.x > character.position.x) {
            character.isSeeingRight = true;
        } else {
            character.isSeeingRight = false;
        }
    }
}
