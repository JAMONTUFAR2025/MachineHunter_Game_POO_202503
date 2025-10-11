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

    public EnemyController(Character enemyCharacter, ArrayList<Vector2> patrolPoints, float runTime, float waitTime) {
        super(enemyCharacter);
        this.patrolPoints = patrolPoints;
        isWaiting = false;
        runTimer = 0f;
        waitTimer = 0f;
        this.runTime = runTime;
        this.waitTime = waitTime;
    }

    @Override
    public void update(float delta, ArrayList<SolidObject> solidObjects) {
        // Actualizar física y animaciones del personaje
        character.update(delta);
        checkCollisions(solidObjects);

        if (patrolPoints == null || patrolPoints.isEmpty() || !character.onGround) {
            character.stopMoving();
            return;
        }

        // Obtener puntos extremos
        Vector2 leftPoint = patrolPoints.get(0);
        Vector2 rightPoint = patrolPoints.get(patrolPoints.size() - 1);
        
        if(isWaiting) {
            waitTimer += delta;
            character.stopMoving();
            if (waitTimer >= waitTime) {
                isWaiting = false;
                waitTimer = 0f;
            }

            return; // Salir del método para no moverse mientras espera
        }

        runTimer += delta;

        if (runTimer >= runTime) { // Cada 3 segundos, esperar
            isWaiting = true;
            runTimer = 0f;
            character.stopMoving();
        }

        // Determinar dirección basada en posición actual
        if (character.position.x <= leftPoint.x + 5f) {
            // En el punto izquierdo, mover a la derecha
            character.moveRight();
            if (character.position.x >= rightPoint.x - 5f) {
                character.stopMoving();
            }
        } else if (character.position.x >= rightPoint.x - 5f) {
            // En el punto derecho, mover a la izquierda
            character.moveLeft();
            if (character.position.x <= leftPoint.x + 5f) {
                character.stopMoving();
            }
        } else {
            // Entre puntos, continuar en la dirección actual
            if (character.isSeeingRight) {
                character.moveRight();
            } else {
                character.moveLeft();
            }
        }
        
        // Límites del mapa
        float enemyWidth = character.getWidth();
        if (character.position.x < 0) character.position.x = 0;
        else if (character.position.x > 1440 - enemyWidth) character.position.x = 1440 - enemyWidth;
    }

    // Metodo para que el enemigo dañe al jugador
    public void dealDamage(Character player, int damage) {
        player.takeDamage(damage);
    }
}