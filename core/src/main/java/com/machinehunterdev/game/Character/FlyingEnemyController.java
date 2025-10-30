package com.machinehunterdev.game.Character;

import com.badlogic.gdx.math.Vector2;
import com.machinehunterdev.game.DamageTriggers.Bullet;
import com.machinehunterdev.game.Environment.SolidObject;

import java.util.ArrayList;

/**
 * Controlador específico para enemigos voladores.
 * Maneja el comportamiento de patrullaje y espera en el aire.
 * 
 * @author MachineHunterDev
 */
public class FlyingEnemyController extends CharacterController {

    /* Puntos de patrullaje */
    private ArrayList<Vector2> patrolPoints;
    /* Estado del enemigo volador */
    private enum State { PATROLLING, WAITING, HURT }
    /* Estado actual del enemigo */
    private State currentState;

    /* Temporizadores para la lógica de espera */
    private float waitTimer;
    private float waitTime;

    /* Punto de destino actual */
    private Vector2 currentTarget;
    private int currentTargetIndex;

    /**
     * Constructor del controlador de enemigos voladores.
     * @param enemyCharacter El personaje enemigo asociado
     * @param patrolPoints Puntos de patrullaje
     * @param waitTime Tiempo de espera entre patrullas
     */
    public FlyingEnemyController(Character enemyCharacter, ArrayList<Vector2> patrolPoints, float waitTime) {
        super(enemyCharacter);
        this.patrolPoints = patrolPoints;
        this.waitTime = waitTime;
        this.currentState = State.PATROLLING;
        this.waitTimer = 0f;
        this.currentTargetIndex = 0;

        if (patrolPoints != null && !patrolPoints.isEmpty()) {
            this.currentTarget = patrolPoints.get(currentTargetIndex);
        }
        // Desactivar la gravedad para enemigos voladores
        character.gravity = 0;
    }

    /**
     * Actualiza el estado del enemigo volador.
     * @param delta Tiempo transcurrido desde la última actualización.
     * @param solidObjects Objetos sólidos en el entorno.
     * @param bullets Balas en el entorno.
     * @param playerCharacter El personaje jugador.
     */
    @Override
    public void update(float delta, ArrayList<SolidObject> solidObjects, ArrayList<Bullet> bullets, Character playerCharacter, int enemyCount) {
        // No se necesitan colisiones para enemigos voladores

        if (character.isHurt()) {
            currentState = State.HURT;
        }

        if (patrolPoints == null || patrolPoints.isEmpty()) {
            character.stopMoving();
            return;
        }

        switch (currentState) {
            case PATROLLING:
                handlePatrollingState(delta);
                break;
            case WAITING:
                handleWaitingState(delta);
                break;
            case HURT:
                handleHurtState(delta);
                break;
        }
    }

    /**
     * Maneja el estado de patrullaje.
     * @param delta Tiempo transcurrido desde la última actualización.
     */
    private void handlePatrollingState(float delta) {
        float tolerance = 5.0f;
        if (character.position.dst(currentTarget) <= tolerance) {
            character.velocity.set(0, 0);
            character.stopMoving();
            currentState = State.WAITING;
            waitTimer = 0f;
            return;
        }

        Vector2 direction = new Vector2(currentTarget).sub(character.position).nor();
        character.velocity.set(direction.scl(character.speed));
    }


    /**
     * Maneja el estado de espera.
     * @param delta Tiempo transcurrido desde la última actualización.
     */
    private void handleWaitingState(float delta) {
        waitTimer += delta;
        character.velocity.set(0, 0);
        character.stopMoving();
        if (waitTimer >= waitTime) {
            currentTargetIndex = (currentTargetIndex + 1) % patrolPoints.size();
            currentTarget = patrolPoints.get(currentTargetIndex);
            currentState = State.PATROLLING;
        }
    }

    private void handleHurtState(float delta) {
        character.stopMoving();
        if (!character.isHurt()) {
            currentState = State.PATROLLING;
        }
    }
}
