package com.machinehunterdev.game.Character;

import java.util.ArrayList;
import com.badlogic.gdx.math.Vector2;

public class BossEnemy extends BaseEnemy {

    public BossEnemy(Character character, ArrayList<Vector2> patrolPoints, float waitTime, float shootInterval, float shootTime) {
        super(character, new BossEnemyController(character, patrolPoints, waitTime, shootInterval, shootTime), EnemyType.BOSS);
    }
}
