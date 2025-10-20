package com.machinehunterdev.game.Character;

public class ShooterEnemy extends BaseEnemy {
    public ShooterEnemy(Character character, float shootInterval) {
        super(character, new ShooterEnemyController(character, shootInterval));
    }
}
