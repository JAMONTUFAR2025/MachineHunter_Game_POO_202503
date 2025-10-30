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

    private EnemyType enemyToSummon = null;

    public EnemyType getEnemyToSummon() { return enemyToSummon; }
    public void clearSummonRequest() { this.enemyToSummon = null; }

    public boolean isLightningAttackActive() { return lightningAttackActive; }
    public float getLightningPlayerX() { return lightningPlayerX; }

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

        if (lightningAttackActive) {
            lightningAttackTimer += delta;

            boolean isPhaseTwo = (float) character.getHealth() / maxHealth <= 0.5f;
            float lightningDuration = isPhaseTwo ? 1.7f : 2.1f; // 3 flashes (1.2s) + 0.5s strike OR 4 flashes (1.6s) + 0.5s strike

            if (lightningAttackTimer >= lightningDuration) {
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
            performRandomAttack(bullets, playerCharacter, enemyCount);
        }
    }

    private void performRandomAttack(ArrayList<Bullet> bullets, Character playerCharacter, int enemyCount) {
        boolean isPhaseTwo = (float) character.getHealth() / maxHealth <= 0.5f;
        int numberOfAttacks = isPhaseTwo ? 4 : 2;
        int attackType = random.nextInt(numberOfAttacks);

                switch (attackType) {
                    case 0:
                        attackType1(bullets, playerCharacter, enemyCount);
                        break;
                    case 1:
                        attackType2(bullets, playerCharacter, enemyCount);
                        break;
                    case 2:
                        attackType3(bullets, playerCharacter, enemyCount);
                        break;
                    case 3:
                        attackType4(bullets, playerCharacter, enemyCount);
                        break;
                }
            }
        
            private void attackType1(ArrayList<Bullet> bullets, Character playerCharacter, int enemyCount) {
                System.out.println("Boss Attack 1");
                if (playerCharacter != null) {
                    lightningAttackActive = true;
                    lightningAttackTimer = 0f;
                    lightningPlayerX = playerCharacter.position.x;
                    lightningFlashCount = 0;
                }
            }
        
            private void attackType2(ArrayList<Bullet> bullets, Character playerCharacter, int enemyCount) {
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
        
            private void attackType3(ArrayList<Bullet> bullets, Character playerCharacter, int enemyCount) {
                if (enemyCount > 1) {
                    // Do nothing if there are other enemies
                    return;
                }
        
                int randomEnemy = random.nextInt(3);
                switch (randomEnemy) {
                    case 0:
                        enemyToSummon = EnemyType.PATROLLER;
                        break;
                    case 1:
                        enemyToSummon = EnemyType.SHOOTER;
                        break;
                    case 2:
                        enemyToSummon = EnemyType.FLYING;
                        break;
                }
            }
        
            private void attackType4(ArrayList<Bullet> bullets, Character playerCharacter, int enemyCount) {
                System.out.println("Boss Attack 4 (Phase 2)");
            }
        }
