package com.machinehunterdev.game.Character;

import com.machinehunterdev.game.DamageTriggers.Bullet;
import com.machinehunterdev.game.Environment.SolidObject;

import java.util.ArrayList;

public class ShooterEnemyController extends CharacterController {
    private float shootTimer;
    private float shootInterval;

    public ShooterEnemyController(Character character, float shootInterval) {
        super(character);
        this.shootInterval = shootInterval;
        this.shootTimer = 0f;
    }

    @Override
    public void update(float delta, ArrayList<SolidObject> solidObjects, ArrayList<Bullet> bullets, Character playerCharacter) {
        character.update(delta);
        checkCollisions(solidObjects);

        shootTimer += delta;
        if (shootTimer >= shootInterval) {
            shootTimer = 0f;
            character.shoot(bullets, com.machinehunterdev.game.DamageTriggers.WeaponType.THUNDER);
        }

        // Make Enemy face the player
        if (playerCharacter.position.x > character.position.x) {
            character.isSeeingRight = true;
        } else {
            character.isSeeingRight = false;
        }
    }
}
