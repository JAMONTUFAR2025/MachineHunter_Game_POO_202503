package com.machinehunterdev.game.Character;

import com.machinehunterdev.game.DamageTriggers.Bullet;
import com.machinehunterdev.game.Environment.SolidObject;

import java.util.ArrayList;
import java.util.Random;

public class BossEnemyController extends CharacterController {

    private float attackTimer;
    private static final float ATTACK_INTERVAL = 5.0f;
    private final int maxHealth;
    private final Random random = new Random();

    public BossEnemyController(Character enemyCharacter) {
        super(enemyCharacter);
        this.maxHealth = enemyCharacter.getHealth();
        this.attackTimer = 0f;
        character.gravity = 0;
    }

    @Override
    public void update(float delta, ArrayList<SolidObject> solidObjects, ArrayList<Bullet> bullets, Character playerCharacter) {
        character.velocity.set(0, 0);
        character.stopMoving();

        attackTimer += delta;

        // Si esta vivo, atacar cada cierto intervalo
        if (attackTimer >= ATTACK_INTERVAL && character.getHealth() > 0) {
            attackTimer = 0f;
            performRandomAttack();
        }
    }

    private void performRandomAttack() {
        boolean isPhaseTwo = (float) character.getHealth() / maxHealth <= 0.5f;
        int numberOfAttacks = isPhaseTwo ? 4 : 2;
        int attackType = random.nextInt(numberOfAttacks);

        switch (attackType) {
            case 0:
                System.out.println("Boss Attack 1");
                break;
            case 1:
                System.out.println("Boss Attack 2");
                break;
            case 2:
                System.out.println("Boss Attack 3 (Phase 2)");
                break;
            case 3:
                System.out.println("Boss Attack 4 (Phase 2)");
                break;
        }
    }
}
