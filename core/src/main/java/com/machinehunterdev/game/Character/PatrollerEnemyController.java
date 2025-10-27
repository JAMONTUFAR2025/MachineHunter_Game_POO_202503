package com.machinehunterdev.game.Character;

import java.util.ArrayList;
import com.badlogic.gdx.math.Vector2;
import com.machinehunterdev.game.DamageTriggers.Bullet;
import com.machinehunterdev.game.Environment.SolidObject;
import com.machinehunterdev.game.Gameplay.GlobalSettings;

/**
 * Controlador específico para enemigos con sistema de patrullaje.
 * Implementa una máquina de estados para gestionar patrullaje y espera.
 * 
 * @author MachineHunterDev
 */
public class PatrollerEnemyController extends CharacterController {
    /** Lista de puntos de patrullaje */
    private ArrayList<Vector2> patrolPoints;
    
    /** Estados de la máquina de estados */
    private enum State { PATROLLING, WAITING }
    private State currentState;
    
    /** Temporizador solo para la espera */
    private float waitTimer;
    private float waitTime;
    
    /** Puntos de referencia para patrullaje */
    private Vector2 currentTarget;
    private int currentTargetIndex; // Índice del punto actual en la lista

    /**
     * Constructor del controlador de enemigos.
     * @param enemyCharacter Personaje enemigo a controlar
     * @param patrolPoints Lista de puntos de patrullaje
     * @param waitTime Tiempo de espera en cada punto
     */
    public PatrollerEnemyController(Character enemyCharacter, ArrayList<Vector2> patrolPoints, float waitTime) {
        super(enemyCharacter);
        this.patrolPoints = patrolPoints;
        this.waitTime = waitTime;
        
        this.currentState = State.PATROLLING;
        this.waitTimer = 0f;
        this.currentTargetIndex = 0;

        if (patrolPoints != null && !patrolPoints.isEmpty()) {
            this.currentTarget = patrolPoints.get(currentTargetIndex);
        }
    }

    /**
     * Actualiza el estado del enemigo según la máquina de estados.
     * @param delta Tiempo transcurrido desde la última actualización
     * @param solidObjects Lista de objetos sólidos para colisiones
     * @param bullets Lista de balas en el juego
     */
    @Override
    public void update(float delta, ArrayList<SolidObject> solidObjects, ArrayList<Bullet> bullets, Character playerCharacter) {
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
        else if (character.position.x > GlobalSettings.levelWidth - enemyWidth) character.position.x = GlobalSettings.levelWidth - enemyWidth;
    }

    /**
     * Maneja el estado de patrullaje.
     * @param delta Tiempo transcurrido desde la última actualización
     */
    private void handlePatrollingState(float delta) {
        // Si ya está en el punto objetivo (con tolerancia)
        float tolerance = 5f; // Aumentar la tolerancia para evitar oscilaciones
        if (Math.abs(character.position.x - currentTarget.x) <= tolerance) {
            character.stopMoving();
            character.velocity.x = 0; // Asegurar que el movimiento horizontal se detenga
            currentState = State.WAITING;
            waitTimer = 0f;
            return;
        }

        // Moverse hacia el objetivo
        if (character.position.x < currentTarget.x) {
            character.moveRight();
        } else {
            character.moveLeft();
        }
    }

    /**
     * Maneja el estado de espera.
     * @param delta Tiempo transcurrido desde la última actualización
     */
    private void handleWaitingState(float delta) {
        waitTimer += delta;
        character.stopMoving();
        if (waitTimer >= waitTime) {
            // Decidir qué hacer a continuación
            double random = Math.random();
            if (random < 0.5) {
                // 50% de probabilidad: No hacer nada
            } else if (random < 0.75) {
                // 25% de probabilidad: Saltar
                if (character.onGround) { // Solo puede saltar si está en el suelo
                    character.jump();
                }
            } else {
                // 25% de probabilidad: Bajar de una plataforma
                if (character.onGround) { // Solo puede bajar si está en el suelo
                    character.fallThroughPlatform();
                }
            }

            // Avanzar al siguiente punto (cíclico)
            currentTargetIndex = (currentTargetIndex + 1) % patrolPoints.size();
            currentTarget = patrolPoints.get(currentTargetIndex);
            currentState = State.PATROLLING;
        }
    }
}