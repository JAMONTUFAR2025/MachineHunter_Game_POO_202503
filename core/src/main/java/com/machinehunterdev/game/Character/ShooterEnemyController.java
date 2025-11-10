package com.machinehunterdev.game.Character;

import com.badlogic.gdx.math.Vector2;
import com.machinehunterdev.game.DamageTriggers.Bullet;
import com.machinehunterdev.game.Environment.SolidObject;
import com.machinehunterdev.game.Gameplay.GlobalSettings;
import com.machinehunterdev.game.Audio.AudioManager;
import com.machinehunterdev.game.Audio.AudioId;

import java.util.ArrayList;

/**
 * Controlador especifico para enemigos que disparan.
 * Maneja el comportamiento de deteccion y disparo hacia el jugador.
 * Utiliza una maquina de estados para gestionar sus acciones.
 * 
 * @author MachineHunterDev
 */
public class ShooterEnemyController extends CharacterController {
    // Variables de temporizacion para el disparo.
    private float shootDuration; // Duracion del estado de disparo.
    private float shootTime; // Tiempo total que dura la animacion de disparo.
    private float shootCooldown; // Tiempo de enfriamiento entre disparos.
    private float shootInterval; // Intervalo de tiempo entre rafagas de disparos.
    
    // Variable para controlar la animacion de disparo.
    private int previousFrameIndex = -1;
    
    // Rango de vision del enemigo para detectar al jugador.
    private float visionRange = 220f;

    // Enumeracion para los estados de la maquina de estados del enemigo.
    private enum State { IDLE, DETECTING, READY, SHOOTING }
    private State currentState = State.IDLE; // Estado inicial.

    /**
     * Constructor del controlador de enemigos tiradores.
     * @param character El personaje asociado al enemigo.
     * @param shootInterval El intervalo de tiempo entre disparos.
     * @param shootTime La duracion del ataque.
     */
    public ShooterEnemyController(Character character, float shootInterval, float shootTime) {
        super(character);
        this.shootInterval = shootInterval;
        this.shootTime = shootTime;
        this.shootCooldown = this.shootInterval; // El enfriamiento inicial es igual al intervalo.
    }

    /**
     * Actualiza el estado del enemigo tirador en cada fotograma.
     * @param delta Tiempo transcurrido desde la ultima actualizacion.
     * @param solidObjects Objetos solidos en el entorno.
     * @param bullets Balas en el entorno.
     * @param playerCharacter El personaje del jugador.
     * @param enemyCount El numero de enemigos en el nivel.
     */
    @Override
    public void update(float delta, ArrayList<SolidObject> solidObjects, ArrayList<Bullet> bullets, Character playerCharacter, int enemyCount) {
        if (!character.isAlive()) return; // Si el enemigo no esta vivo, no hace nada.

        handleHurtAnimation(); // Gestiona la animacion de ser herido.
        checkCollisions(solidObjects); // Comprueba colisiones con el entorno.

        // Calcula la distancia en el eje X entre el enemigo y el jugador.
        float distanceX = Math.abs(playerCharacter.position.x - character.position.x);

        // Maquina de estados para el comportamiento del enemigo.
        switch (currentState) {
            case IDLE:
                // Si el jugador entra en el rango de vision, cambia al estado de deteccion.
                if (distanceX <= visionRange) {
                    currentState = State.DETECTING;
                    character.isPerformingSpecialAttack = true;
                    character.characterAnimator.setCurrentAnimation(CharacterAnimator.AnimationState.SUMMON);
                    AudioManager.getInstance().playSfx(AudioId.Exclamation, null);
                }
                break;

            case DETECTING:
                // Si el jugador sale del rango de vision, vuelve al estado de reposo.
                if (distanceX > visionRange) {
                    currentState = State.IDLE;
                    character.isPerformingSpecialAttack = false;
                    character.characterAnimator.setCurrentAnimation(CharacterAnimator.AnimationState.IDLE);
                    break;
                }

                // Cuando termina la animacion de deteccion, pasa al estado de disparo.
                if (character.characterAnimator.isAnimationFinished(CharacterAnimator.AnimationState.SUMMON)) {
                    character.isPerformingSpecialAttack = false;
                    currentState = State.SHOOTING;
                    shootDuration = shootTime;
                    character.attack();
                }
                break;

            case READY:
                // Si el jugador sale del rango de vision, vuelve al estado de reposo.
                if (distanceX > visionRange) {
                    currentState = State.IDLE;
                    break;
                }

                // Reduce el tiempo de enfriamiento.
                shootCooldown -= delta;
                // Si el enfriamiento ha terminado, pasa al estado de disparo.
                if (shootCooldown <= 0) {
                    currentState = State.SHOOTING;
                    shootDuration = shootTime;
                    character.attack();
                }
                break;

            case SHOOTING:
                // Reduce la duracion del estado de disparo.
                shootDuration -= delta;
                // Si el tiempo de disparo ha terminado, vuelve al estado de preparacion.
                if (shootDuration <= 0) {
                    currentState = State.READY;
                    shootCooldown = shootInterval; // Reinicia el enfriamiento.
                    character.stopAttacking();
                }

                // Dispara en un fotograma especifico de la animacion.
                int currentFrame = character.characterAnimator.getCurrentFrameIndex();
                if (currentFrame == 1 && previousFrameIndex != 1) {
                    AudioManager.getInstance().playSfx(AudioId.EnemyAttack, character, GlobalSettings.ANNOYING_VOLUME * 2f);
                    // Calcula la posicion y direccion del disparo.
                    Vector2 startPos = new Vector2(character.position.x + character.getWidth() / 2, character.position.y + 35);
                    Vector2 targetPos = new Vector2(playerCharacter.position.x + playerCharacter.getWidth() / 2, playerCharacter.position.y + 35);
                    Vector2 direction = targetPos.sub(startPos).nor();
                    float bulletSpeed = 100f; // Velocidad de la bala.
                    Vector2 velocity = direction.scl(bulletSpeed);

                    // Anade una nueva bala al juego.
                    bullets.add(new Bullet(startPos.x, startPos.y, velocity, com.machinehunterdev.game.DamageTriggers.WeaponType.SHOOTER, character));
                }
                previousFrameIndex = currentFrame;
                break;
        }

        // Hace que el enemigo mire siempre hacia el jugador.
        if (playerCharacter.position.x > character.position.x) {
            character.isSeeingRight = true;
        } else {
            character.isSeeingRight = false;
        }
    }
}
