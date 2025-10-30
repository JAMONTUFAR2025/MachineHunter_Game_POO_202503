package com.machinehunterdev.game.Character;

import java.util.ArrayList;
import com.badlogic.gdx.math.Vector2;
import com.machinehunterdev.game.DamageTriggers.Bullet;
import com.machinehunterdev.game.Environment.SolidObject;

public class BossEnemyController extends CharacterController {

    public BossEnemyController(Character character, ArrayList<Vector2> patrolPoints, float waitTime, float shootInterval, float shootTime) {
        super(character);
    }

    @Override
    public void update(float delta, ArrayList<SolidObject> solidObjects, ArrayList<Bullet> bullets, Character playerCharacter) {
        // Boss behavior will be implemented here
    }
}