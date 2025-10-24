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
    private float shootTimer; // Temporizador para controlar el intervalo de disparo
    private float shootInterval; // Intervalo entre disparos
    private int shotsToFire; // Número de disparos a realizar en una ráfaga
    private float shotDelayTimer; // Temporizador para el retraso entre disparos en una ráfaga

    /**
     * Constructor del controlador de enemigos tiradores.
     * @param character El personaje asociado al enemigo.
     * @param shootInterval Intervalo de tiempo entre disparos.
     */
    public ShooterEnemyController(Character character, float shootInterval) {
        super(character);
        this.shootInterval = shootInterval;
        this.shootTimer = 0f;
        this.shotsToFire = 0;
        this.shotDelayTimer = 0f;
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
        character.update(delta);
        checkCollisions(solidObjects);

        shootTimer += delta;
        if (shootTimer >= shootInterval) {
            shootTimer = 0f;
            shotsToFire = 3; // Iniciar ráfaga de 3 disparos
        }

        // Manejar disparos en ráfaga
        if (shotsToFire > 0) {
            shotDelayTimer -= delta;
            if (shotDelayTimer <= 0) {
                character.shoot(bullets, com.machinehunterdev.game.DamageTriggers.WeaponType.THUNDER); // Usar la lógica de disparo del enemigo sin comprobación de cooldown
                shotsToFire--;
                shotDelayTimer = 0.5f; // Pequeño retraso entre disparos
            }
        } else {
            character.stopMoving(); // Asegurar que el enemigo esté en IDLE cuando no dispara
            character.stopAttacking(); // Detener la animación de ataque cuando la ráfaga termina
        }

        // Hacer que el enemigo mire hacia el jugador
        if (playerCharacter.position.x > character.position.x) {
            character.isSeeingRight = true;
        } else {
            character.isSeeingRight = false;
        }
    }
}
