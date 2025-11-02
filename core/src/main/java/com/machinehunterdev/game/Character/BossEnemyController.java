package com.machinehunterdev.game.Character;

import com.badlogic.gdx.math.Vector2;
import com.machinehunterdev.game.DamageTriggers.Bullet;
import com.machinehunterdev.game.DamageTriggers.WeaponType;
import com.machinehunterdev.game.Environment.SolidObject;
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

    private boolean summonWarningActive = false;
    private float summonWarningTimer = 0f;
    private EnemyType pendingEnemyToSummon = null;

    private EnemyType enemyToSummon = null;

    public EnemyType getEnemyToSummon() { return enemyToSummon; }
    public void clearSummonRequest() { this.enemyToSummon = null; }

    public boolean isLightningAttackActive() { return lightningAttackActive; }
    public float getLightningPlayerX() { return lightningPlayerX; }

    public boolean isSummonWarning() {
        if (!summonWarningActive) return false;

        float warningDuration = 1.2f; // 3 flashes

        if (summonWarningTimer < warningDuration) {
            int flashCount = (int) (summonWarningTimer / 0.2f);
            return flashCount % 2 == 0;
        }
        return false;
    }

    public boolean isLightningWarning() {
        if (!lightningAttackActive) return false;

        boolean isPhaseTwo = (float) character.getHealth() / maxHealth <= 0.5f;
        float warningDuration = isPhaseTwo ? 1.2f : 1.6f;

        if (lightningAttackTimer < warningDuration) {
            int flashCount = (int) (lightningAttackTimer / 0.2f);
            return flashCount % 2 == 0;
        }
        return false;
    }

    public boolean isLightningStriking() {
        if (!lightningAttackActive) return false;

        boolean isPhaseTwo = (float) character.getHealth() / maxHealth <= 0.5f;
        float warningDuration = isPhaseTwo ? 1.2f : 1.6f;

        return lightningAttackTimer >= warningDuration;
    }

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
    public void update(float delta, ArrayList<SolidObject> solidObjects, ArrayList<Bullet> bullets, Character playerCharacter, int enemyCount) {
        if (playerCharacter != null) {
            if (playerCharacter.position.x > character.position.x) {
                character.setSeeingRight(true);
            } else {
                character.setSeeingRight(false);
            }
        }

        // Handle rage animation based on health
        boolean isPhaseTwo = (float) character.getHealth() / maxHealth <= 0.5f;
        CharacterAnimator.AnimationState currentAnimation = character.characterAnimator.getCurrentState();

        if (isPhaseTwo) {
            if (currentAnimation == CharacterAnimator.AnimationState.IDLE) {
                character.characterAnimator.setCurrentAnimation(CharacterAnimator.AnimationState.IDLE_RAGE);
            }
        } else {
            if (currentAnimation == CharacterAnimator.AnimationState.IDLE_RAGE) {
                character.characterAnimator.setCurrentAnimation(CharacterAnimator.AnimationState.IDLE);
            }
        }

        // Return to idle after attack animations
        if ((currentAnimation == CharacterAnimator.AnimationState.ATTACK1 || 
             currentAnimation == CharacterAnimator.AnimationState.ATTACK2 || 
             currentAnimation == CharacterAnimator.AnimationState.SUMMON) && 
             character.characterAnimator.isAnimationFinished(currentAnimation)) {
            
            character.isPerformingSpecialAttack = false;
            if (isPhaseTwo) {
                character.characterAnimator.setCurrentAnimation(CharacterAnimator.AnimationState.IDLE_RAGE);
            } else {
                character.characterAnimator.setCurrentAnimation(CharacterAnimator.AnimationState.IDLE);
            }
        }

        if (lightningAttackActive) {
            lightningAttackTimer += delta;

            float lightningDuration = isPhaseTwo ? 1.7f : 2.1f; // 3 flashes (1.2s) + 0.5s strike OR 4 flashes (1.6s) + 0.5s strike

            if (lightningAttackTimer >= lightningDuration) {
                lightningAttackActive = false;
            }
        }

        if (summonWarningActive) {
            summonWarningTimer += delta;
            if (summonWarningTimer >= 1.2f) {
                enemyToSummon = pendingEnemyToSummon;
                summonWarningActive = false;
                pendingEnemyToSummon = null;
            }
        }

        character.velocity.set(0, 0);
        character.stopMoving();

        if (!character.isPerformingSpecialAttack) {
            attackTimer += delta;
        }

        float currentAttackInterval = isPhaseTwo ? attackIntervalPhase2 : attackInterval;

        if (enemyCount > 1) {
            currentAttackInterval *= 2;
        }

        // Si esta vivo, atacar cada cierto intervalo
        if (attackTimer >= currentAttackInterval && character.getHealth() > 0) {
            attackTimer = 0f;
            performRandomAttack(bullets, playerCharacter, enemyCount);
        }
    }

    private void performRandomAttack(ArrayList<Bullet> bullets, Character playerCharacter, int enemyCount) {
        boolean isPhaseTwo = (float) character.getHealth() / maxHealth <= 0.5f;
        int numberOfAttacks = isPhaseTwo ? 4 : 3;
        int attackType = random.nextInt(numberOfAttacks);

        switch (attackType) {
            case 0:
                // Nada
                break;
            case 1:
                attackType1(bullets, playerCharacter, enemyCount);
            break;
            case 2:
                attackType2(bullets, playerCharacter, enemyCount);
            break;
            case 3:
                attackType3(bullets, playerCharacter, enemyCount);
                break;
            default:
                break;
        }
    }
        
    private void attackType1(ArrayList<Bullet> bullets, Character playerCharacter, int enemyCount) {
        character.isPerformingSpecialAttack = true;
        character.characterAnimator.setCurrentAnimation(CharacterAnimator.AnimationState.ATTACK1);
        System.out.println("Boss Attack 1");
        if (playerCharacter != null) {
            lightningAttackActive = true;
            lightningAttackTimer = 0f;
            lightningPlayerX = playerCharacter.position.x;
        }
    }
    
    private void attackType2(ArrayList<Bullet> bullets, Character playerCharacter, int enemyCount) {
        character.isPerformingSpecialAttack = true;
        character.characterAnimator.setCurrentAnimation(CharacterAnimator.AnimationState.ATTACK2);
        System.out.println("Boss Attack 2");
        if (playerCharacter == null) return;

        Vector2 bossTop = new Vector2(character.position.x + character.getWidth() / 2, character.position.y + character.getHeight());
        Vector2 playerCenter = new Vector2(playerCharacter.position.x + playerCharacter.getWidth() / 2, playerCharacter.position.y + 35);

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

    private void attackType3(ArrayList<Bullet> bullets, Character playerCharacter, int enemyCount) {
        character.isPerformingSpecialAttack = true;
        character.characterAnimator.setCurrentAnimation(CharacterAnimator.AnimationState.SUMMON);
        if (enemyCount > 1) {
            // Do nothing if there are other enemies
            return;
        }

        summonWarningActive = true;
        summonWarningTimer = 0f;

        int randomEnemy = random.nextInt(3);
        switch (randomEnemy) {
            case 0:
                pendingEnemyToSummon = EnemyType.PATROLLER;
                break;
            case 1:
                pendingEnemyToSummon = EnemyType.SHOOTER;
                break;
            case 2:
                pendingEnemyToSummon = EnemyType.FLYING;
                break;
        }
    }
}
