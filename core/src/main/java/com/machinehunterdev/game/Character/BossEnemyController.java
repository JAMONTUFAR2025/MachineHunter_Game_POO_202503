package com.machinehunterdev.game.Character;

import com.badlogic.gdx.math.Vector2;
import com.machinehunterdev.game.DamageTriggers.Bullet;
import com.machinehunterdev.game.DamageTriggers.WeaponType;
import com.machinehunterdev.game.Environment.SolidObject;
import com.machinehunterdev.game.Character.EnemyType;
import java.util.ArrayList;
import java.util.Random;

public class BossEnemyController extends CharacterController {

    private float attackTimer;
    private final float attackInterval;
    private final float attackIntervalPhase2;
    private final int maxHealth;
    private final Random random = new Random();

    private boolean lightningAttackActive = false;
    private float lightningAttackTimer = 0f;
    private float lightningPlayerX = 0f;
    private int lightningFlashCount = 0;

    public boolean isLightningAttackActive() { return lightningAttackActive; }
    public float getLightningAttackTimer() { return lightningAttackTimer; }
    public float getLightningPlayerX() { return lightningPlayerX; }
    public int getLightningFlashCount() { return lightningFlashCount; }

    public BossEnemyController(Character enemyCharacter, EnemyType type) {
        super(enemyCharacter);
        this.maxHealth = enemyCharacter.getHealth();
        this.attackTimer = 0f;
        character.gravity = 0;

        switch (type) {
            case BOSS_GEMINI:
                this.attackInterval = 4.0f;
                this.attackIntervalPhase2 = 2.0f;
                break;
            case BOSS_CHATGPT:
                this.attackInterval = 3.0f;
                this.attackIntervalPhase2 = 1.5f;
                break;
            default:
                this.attackInterval = 5.0f;
                this.attackIntervalPhase2 = 2.5f;
                break;
        }
    }

    @Override
    public void update(float delta, ArrayList<SolidObject> solidObjects, ArrayList<Bullet> bullets, Character playerCharacter) {
        if (playerCharacter != null) {
            if (playerCharacter.position.x > character.position.x) {
                character.setSeeingRight(true);
            } else {
                character.setSeeingRight(false);
            }
        }

        if (lightningAttackActive) {
            lightningAttackTimer += delta;
            if (lightningAttackTimer >= 1.7f) { // 3 flashes (0.4s each) + 0.5s strike
                lightningAttackActive = false;
            }
        }

        character.velocity.set(0, 0);
        character.stopMoving();

        attackTimer += delta;

        boolean isPhaseTwo = (float) character.getHealth() / maxHealth <= 0.5f;
        float currentAttackInterval = isPhaseTwo ? attackIntervalPhase2 : attackInterval;

        // Si esta vivo, atacar cada cierto intervalo
        if (attackTimer >= currentAttackInterval && character.getHealth() > 0) {
            attackTimer = 0f;
            performRandomAttack(bullets, playerCharacter);
        }
    }

    private void performRandomAttack(ArrayList<Bullet> bullets, Character playerCharacter) {
        boolean isPhaseTwo = (float) character.getHealth() / maxHealth <= 0.5f;
        int numberOfAttacks = isPhaseTwo ? 4 : 2;
        int attackType = random.nextInt(numberOfAttacks);

        switch (attackType) {
            case 0:
                attackType1(bullets, playerCharacter);
                break;
            case 1: 
                attackType1(bullets, playerCharacter);
                break;
            case 2:
                attackType2(bullets, playerCharacter);
                break;
            case 3:
                attackType3(bullets, playerCharacter);
                break;
        }
    }

    private void attackType0(ArrayList<Bullet> bullets, Character playerCharacter) {
        if (playerCharacter != null) {
            lightningAttackActive = true;
            lightningAttackTimer = 0f;
            lightningPlayerX = playerCharacter.position.x;
            lightningFlashCount = 0;
        }
    }

    private void attackType1(ArrayList<Bullet> bullets, Character playerCharacter) {
        System.out.println("Boss Attack 2");
        if (playerCharacter == null) return;

        Vector2 bossTop = new Vector2(character.position.x + character.getWidth() / 2, character.position.y + character.getHeight());
        Vector2 playerCenter = new Vector2(playerCharacter.position.x + playerCharacter.getWidth() / 2, playerCharacter.position.y + playerCharacter.getHeight() / 2);

        Vector2 direction = playerCenter.sub(bossTop).nor();
        float bulletSpeed = 100f; // From WeaponType.SHOOTER

        int bulletCount;
        float angleIncrement;

        if ((float) character.getHealth() / maxHealth <= 0.5f) {
            bulletCount = 12;
            angleIncrement = 30f;
        } else {
            bulletCount = 10;
            angleIncrement = 36f;
        }

        for (int i = 0; i < bulletCount; i++) {
            Vector2 bulletVelocity = direction.cpy().rotateDeg(i * angleIncrement).scl(bulletSpeed);
            Bullet bullet = new Bullet(bossTop.x, bossTop.y, bulletVelocity, WeaponType.SHOOTER, character);
            bullets.add(bullet);
        }
    }

    private void attackType2(ArrayList<Bullet> bullets, Character playerCharacter) {
        System.out.println("Boss Attack 3 (Phase 2)");
    }

    private void attackType3(ArrayList<Bullet> bullets, Character playerCharacter) {
        System.out.println("Boss Attack 4 (Phase 2)");
    }
}
