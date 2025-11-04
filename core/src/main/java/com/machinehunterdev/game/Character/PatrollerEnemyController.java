package com.machinehunterdev.game.Character;

import java.util.ArrayList;
import com.badlogic.gdx.math.Vector2;
import com.machinehunterdev.game.DamageTriggers.Bullet;
import com.machinehunterdev.game.Environment.SolidObject;
import com.machinehunterdev.game.Gameplay.GlobalSettings;
import com.machinehunterdev.game.Levels.LevelData;

/**
 * Controlador específico para enemigos con sistema de patrullaje.
 * Implementa una máquina de estados para gestionar patrullaje y espera.
 * 
 * @author MachineHunterDev
 */
public class PatrollerEnemyController extends CharacterController {
    /** Lista de puntos de patrullaje */
    private ArrayList<PatrolPoint> patrolPoints;
    
    /** Estados de la máquina de estados */
    private enum State { PATROLLING, WAITING }
    private State currentState;
    
    /** Temporizador solo para la espera */
    private float waitTimer;
    private float waitTime;
    
    /** Puntos de referencia para patrullaje */
    private PatrolPoint currentTarget;
    private int currentTargetIndex; // Índice del punto actual en la lista

    /**
     * Constructor del controlador de enemigos.
     * @param enemyCharacter Personaje enemigo a controlar
     * @param patrolPointsData Lista de puntos de patrullaje
     * @param waitTime Tiempo de espera en cada punto
     */
    public PatrollerEnemyController(Character enemyCharacter, ArrayList<LevelData.Point> patrolPointsData, float waitTime) {
        super(enemyCharacter);
        this.patrolPoints = new ArrayList<PatrolPoint>();
        for (LevelData.Point pointData : patrolPointsData) {
            this.patrolPoints.add(new PatrolPoint(new Vector2(pointData.x, pointData.y), pointData.action));
        }
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
    public void update(float delta, ArrayList<SolidObject> solidObjects, ArrayList<Bullet> bullets, Character playerCharacter, int enemyCount) {
        handleHurtAnimation();
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
        if (Math.abs(character.position.x - currentTarget.position.x) <= tolerance) {
            character.stopMoving();
            character.velocity.x = 0; // Asegurar que el movimiento horizontal se detenga
            currentState = State.WAITING;
            waitTimer = 0f;
            return;
        }

        // Moverse hacia el objetivo
        if (character.position.x < currentTarget.position.x) {
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
            if (currentTarget.action != null) {
                if (currentTarget.action.equalsIgnoreCase("Jump")) {
                    if (character.onGround) { // Solo puede saltar si está en el suelo
                        character.jump();
                    }
                } else if (currentTarget.action.equalsIgnoreCase("Fall")) {
                    if (character.onGround) { // Solo puede bajar si está en el suelo
                        character.fallThroughPlatform();
                    }
                }
            }

            // Avanzar al siguiente punto (cíclico)
            currentTargetIndex = (currentTargetIndex + 1) % patrolPoints.size();
            currentTarget = patrolPoints.get(currentTargetIndex);
            currentState = State.PATROLLING;
        }
    }
}
