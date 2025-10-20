package com.machinehunterdev.game.Character;

import com.badlogic.gdx.math.Vector2;
import com.machinehunterdev.game.DamageTriggers.Bullet;
import com.machinehunterdev.game.Environment.SolidObject;

import java.util.ArrayList;

public class FlyingEnemyController extends CharacterController {
    private ArrayList<Vector2> patrolPoints;
    private enum State { PATROLLING, WAITING }
    private State currentState;
    private float waitTimer;
    private float waitTime;
    private Vector2 currentTarget;
    private int currentTargetIndex;

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
        // Disable gravity for flying enemies
        character.gravity = 0;
    }

    @Override
    public void update(float delta, ArrayList<SolidObject> solidObjects, ArrayList<Bullet> bullets, Character playerCharacter) {
        character.update(delta);
        // No collision check for flying enemies

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
        }
    }

    private void handlePatrollingState(float delta) {
        float tolerance = 1.0f;
        if (Math.abs(character.position.y - currentTarget.y) <= tolerance) {
            character.stopMoving();
            currentState = State.WAITING;
            waitTimer = 0f;
            return;
        }

        // Move only vertically
        character.velocity.x = 0;
        if (character.position.y < currentTarget.y) {
            character.velocity.y = character.speed;
        } else {
            character.velocity.y = -character.speed;
        }
    }

    private void handleWaitingState(float delta) {
        waitTimer += delta;
        character.stopMoving();
        if (waitTimer >= waitTime) {
            currentTargetIndex = (currentTargetIndex + 1) % patrolPoints.size();
            currentTarget = patrolPoints.get(currentTargetIndex);
            currentState = State.PATROLLING;
        }
    }
}
