package com.machinehunterdev.game.Character;

import java.util.ArrayList;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.math.Vector2;
import com.machinehunterdev.game.Environment.SolidObject;

// -- CONTROLADOR PARA ENEMIGOS --
public class EnemyController extends CharacterController {
    private ArrayList<Vector2> patrolPoints;
    private boolean isWaiting;
    private float runTimer;
    private float waitTimer;
    private float runTime;
    private float waitTime;

    // Nuevo enum para los estados
    private enum State { PATROLLING, WAITING }
    private State currentState;

    // Nuevo atributo para el objetivo actual
    private Vector2 currentTarget;
    private Vector2 leftPoint;
    private Vector2 rightPoint;

    public EnemyController(Character enemyCharacter, ArrayList<Vector2> patrolPoints, float runTime, float waitTime) {
        super(enemyCharacter);
        this.patrolPoints = patrolPoints;
        this.runTime = runTime;
        this.waitTime = waitTime;
        
        // Inicialización de estados y objetivos
        this.currentState = State.PATROLLING; // Empezar patrullando
        this.waitTimer = 0f;
        this.runTimer = 0f;

        if (patrolPoints != null && !patrolPoints.isEmpty()) {
            this.leftPoint = patrolPoints.get(0);
            this.rightPoint = patrolPoints.get(patrolPoints.size() - 1);
            // Empezar moviéndose hacia la derecha por defecto
            this.currentTarget = this.rightPoint; 
        }
    }

    @Override
    public void update(float delta, ArrayList<SolidObject> solidObjects) {
        character.update(delta);
        checkCollisions(solidObjects);

        if (patrolPoints == null || patrolPoints.isEmpty() || !character.onGround) {
            character.stopMoving();
            return;
        }

        // --- Máquina de Estados ---
        switch (currentState) {
            case PATROLLING:
                handlePatrollingState(delta);
                break;
            case WAITING:
                handleWaitingState(delta);
                break;
        }

        // Límites del mapa (sin cambios)
        float enemyWidth = character.getWidth();
        if (character.position.x < 0) character.position.x = 0;
        else if (character.position.x > 1440 - enemyWidth) character.position.x = 1440 - enemyWidth;
    }

    private void handlePatrollingState(float delta) {
        runTimer += delta;
        if (runTimer >= runTime) {
            currentState = State.WAITING;
            runTimer = 0f;
            character.stopMoving();
            return;
        }

        // Lógica de movimiento basada en el objetivo
        if (currentTarget == rightPoint) {
            character.moveRight();
            // Si llega o pasa el objetivo, cambiar de objetivo
            if (character.position.x >= rightPoint.x) {
                currentTarget = leftPoint;
            }
        } else { // El objetivo es leftPoint
            character.moveLeft();
            // Si llega o pasa el objetivo, cambiar de objetivo
            if (character.position.x <= leftPoint.x) {
                currentTarget = rightPoint;
            }
        }
    }

    private void handleWaitingState(float delta) {
        waitTimer += delta;
        character.stopMoving();
        if (waitTimer >= waitTime) {
            currentState = State.PATROLLING;
            waitTimer = 0f;
        }
    }

    // Metodo para que el enemigo dañe al jugador
    public void dealDamage(Character player, int damage) {
        player.takeDamage(damage);
    }
}