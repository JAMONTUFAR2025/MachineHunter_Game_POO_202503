package com.machinehunterdev.game.Character;

import com.badlogic.gdx.math.Vector2;
import com.machinehunterdev.game.DamageTriggers.Bullet;
import com.machinehunterdev.game.Environment.SolidObject;
import com.machinehunterdev.game.Gameplay.GlobalSettings;
import com.machinehunterdev.game.Audio.AudioManager;
import com.machinehunterdev.game.Audio.AudioId;

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
    private int previousFrameIndex = -1;

    private enum State { IDLE, SHOOTING }
    private State currentState = State.IDLE;

    /**
     * Constructor del controlador de enemigos tiradores.
     * @param character El personaje asociado al enemigo.
     * @param shootInterval Intervalo de tiempo entre disparos.
     * @param shootTime Duración del ataque.
     */
    public ShooterEnemyController(Character character, float shootInterval, float shootTime) {
        super(character);
        this.shootInterval = shootInterval;
        this.shootTime = shootTime;
        this.shootCooldown = this.shootInterval; // Tiempo inicial antes del primer disparo
    }

    /**
     * Actualiza el estado del enemigo tirador.
     * @param delta Tiempo transcurrido desde la última actualización.
     * @param solidObjects Objetos sólidos en el entorno.
     * @param bullets Balas en el entorno.
     * @param playerCharacter El personaje jugador.
     */
    @Override
    public void update(float delta, ArrayList<SolidObject> solidObjects, ArrayList<Bullet> bullets, Character playerCharacter, int enemyCount) {
        if (!character.isAlive()) return;

        handleHurtAnimation();
        checkCollisions(solidObjects);

        switch (currentState) {
            case IDLE:
                shootCooldown -= delta;
                if (shootCooldown <= 0) {
                    currentState = State.SHOOTING;
                    shootDuration = shootTime;
                    character.attack();
                }
                break;
            case SHOOTING:
                shootDuration -= delta;
                if (shootDuration <= 0) {
                    currentState = State.IDLE;
                    shootCooldown = shootInterval;
                    character.stopAttacking();
                }

                int currentFrame = character.characterAnimator.getCurrentFrameIndex();
                if (currentFrame == 1 && previousFrameIndex != 1) {
                    AudioManager.getInstance().playSfx(AudioId.EnemyAttack, character, GlobalSettings.GLOBAL_SHOOT_VOLUME);
                    Vector2 startPos = new Vector2(character.position.x + character.getWidth() / 2, character.position.y + 35);
                    Vector2 targetPos = new Vector2(playerCharacter.position.x + playerCharacter.getWidth() / 2, playerCharacter.position.y + 35);

                    Vector2 direction = targetPos.sub(startPos).nor();
                    float bulletSpeed = 100f; // From WeaponType.SHOOTER

                    Vector2 velocity = direction.scl(bulletSpeed);

                    bullets.add(new Bullet(startPos.x, startPos.y, velocity, com.machinehunterdev.game.DamageTriggers.WeaponType.SHOOTER, character));
                }
                previousFrameIndex = currentFrame;
                break;
        }

        // Hacer que el enemigo mire hacia el jugador
        if (playerCharacter.position.x > character.position.x) {
            character.isSeeingRight = true;
        } else {
            character.isSeeingRight = false;
        }
    }
}
