package com.machinehunterdev.game.Character;

import java.util.ArrayList;
import com.badlogic.gdx.math.Vector2;
import com.machinehunterdev.game.DamageTriggers.Bullet;
import com.machinehunterdev.game.Environment.SolidObject;

/**
 * Controlador específico para enemigos con sistema de patrullaje.
 * Implementa una máquina de estados para gestionar patrullaje y espera.
 * 
 * @author MachineHunterDev
 */
public class EnemyController extends CharacterController {
    /** Lista de puntos de patrullaje */
    private ArrayList<Vector2> patrolPoints;
    
    /** Estados de la máquina de estados */
    private enum State { PATROLLING, WAITING }
    private State currentState;
    
    /** Temporizadores para patrullaje y espera */
    private float runTimer;
    private float waitTimer;
    private float runTime;
    private float waitTime;
    
    /** Puntos de referencia para patrullaje */
    private Vector2 currentTarget;
    private Vector2 leftPoint;
    private Vector2 rightPoint;

    /**
     * Constructor del controlador de enemigos.
     * @param enemyCharacter Personaje enemigo a controlar
     * @param patrolPoints Lista de puntos de patrullaje
     * @param runTime Tiempo de patrullaje entre esperas
     * @param waitTime Tiempo de espera en cada extremo
     */
    public EnemyController(Character enemyCharacter, ArrayList<Vector2> patrolPoints, float runTime, float waitTime) {
        super(enemyCharacter);
        this.patrolPoints = patrolPoints;
        this.runTime = runTime;
        this.waitTime = waitTime;
        
        this.currentState = State.PATROLLING;
        this.waitTimer = 0f;
        this.runTimer = 0f;

        if (patrolPoints != null && !patrolPoints.isEmpty()) {
            this.leftPoint = patrolPoints.get(0);
            this.rightPoint = patrolPoints.get(patrolPoints.size() - 1);
            this.currentTarget = this.rightPoint; 
        }
    }

    /**
     * Actualiza el estado del enemigo cada frame.
     * @param delta Tiempo transcurrido desde el último frame
     * @param solidObjects Lista de objetos sólidos para colisiones
     * @param bullets Lista de balas activas (no usada en enemigos básicos)
     */
    @Override
    public void update(float delta, ArrayList<SolidObject> solidObjects, ArrayList<Bullet> bullets) {
        character.update(delta);
        checkCollisions(solidObjects);

        if (patrolPoints == null || patrolPoints.isEmpty() || !character.onGround) {
            character.stopMoving();
            return;
        }

        // Máquina de estados para patrullaje
        switch (currentState) {
            case PATROLLING:
                handlePatrollingState(delta);
                break;
            case WAITING:
                handleWaitingState(delta);
                break;
        }

        // Aplicar límites del mapa
        float enemyWidth = character.getWidth();
        if (character.position.x < 0) character.position.x = 0;
        else if (character.position.x > 1440 - enemyWidth) character.position.x = 1440 - enemyWidth;
    }

    /**
     * Maneja el estado de patrullaje.
     * @param delta Tiempo transcurrido desde el último frame
     */
    private void handlePatrollingState(float delta) {
        runTimer += delta;
        if (runTimer >= runTime) {
            currentState = State.WAITING;
            runTimer = 0f;
            character.stopMoving();
            return;
        }

        // Moverse hacia el objetivo actual
        if (currentTarget == rightPoint) {
            character.moveRight();
            if (character.position.x >= rightPoint.x) {
                currentTarget = leftPoint;
            }
        } else {
            character.moveLeft();
            if (character.position.x <= leftPoint.x) {
                currentTarget = rightPoint;
            }
        }
    }

    /**
     * Maneja el estado de espera.
     * @param delta Tiempo transcurrido desde el último frame
     */
    private void handleWaitingState(float delta) {
        waitTimer += delta;
        character.stopMoving();
        if (waitTimer >= waitTime) {
            currentState = State.PATROLLING;
            waitTimer = 0f;
        }
    }

    /**
     * Aplica daño al jugador.
     * @param player Jugador a dañar
     * @param damage Cantidad de daño
     */
    public void dealDamage(Character player, int damage) {
        player.takeDamage(damage);
    }
}