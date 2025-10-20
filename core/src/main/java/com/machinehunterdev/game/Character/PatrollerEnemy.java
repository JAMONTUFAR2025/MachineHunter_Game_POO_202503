package com.machinehunterdev.game.Character;

import com.badlogic.gdx.math.Vector2;

import java.util.ArrayList;

public class PatrollerEnemy extends BaseEnemy {
    public PatrollerEnemy(Character character, ArrayList<Vector2> patrolPoints, float waitTime) {
        super(character, new PatrollerEnemyController(character, patrolPoints, waitTime));
    }
}
