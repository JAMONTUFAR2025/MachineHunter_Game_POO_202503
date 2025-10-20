package com.machinehunterdev.game.Character;

import com.badlogic.gdx.math.Vector2;

import java.util.ArrayList;

public class FlyingEnemy extends BaseEnemy {
    public FlyingEnemy(Character character, ArrayList<Vector2> patrolPoints, float waitTime) {
        super(character, new FlyingEnemyController(character, patrolPoints, waitTime));
    }
}
