package com.machinehunterdev.game.Character;

import java.util.ArrayList;
import java.util.Random;

import com.badlogic.gdx.math.Vector2;
import com.machinehunterdev.game.Audio.AudioId;
import com.machinehunterdev.game.Audio.AudioManager;
import com.machinehunterdev.game.DamageTriggers.Bullet;
import com.machinehunterdev.game.DamageTriggers.WeaponType;
import com.machinehunterdev.game.Environment.SolidObject;

public class BossEnemyController extends CharacterController {

    private float attackTimer;
    private final float attackInterval;
    private final float attackIntervalPhase2;
    private final int maxHealth;
    private final Random random = new Random();
    private final EnemyType BossType;

    private boolean lightningAttackActive = false;
    private float lightningAttackTimer = 0f;
    private float lightningPlayerX = 0f;
    private int previousFlashCount = -1;

    private boolean summonWarningActive = false;
    private float summonWarningTimer = 0f;
    private int previousSummonFlashCount = -1;
    private EnemyType pendingEnemyToSummon = null;

    private boolean hasEnteredPhaseTwo = false;

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
                this.BossType = type;
                break;
            case BOSS_CHATGPT:
                this.attackInterval = 3.0f;
                this.attackIntervalPhase2 = 1.5f;
                this.BossType = type;
                break;
            default:
                this.attackInterval = 5.0f;
                this.attackIntervalPhase2 = 2.5f;
                this.BossType = type;
                break;
        }
    }

    @Override
    public void update(float delta, ArrayList<SolidObject> solidObjects, ArrayList<Bullet> bullets, Character playerCharacter, int enemyCount) {
        // This method is now empty, the logic is in the other update method
    }

    public void update(float delta, ArrayList<SolidObject> solidObjects, ArrayList<Bullet> bullets, Character playerCharacter, int enemyCount, ArrayList<IEnemy> enemies) {
        handleHurtAnimation();

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
            if (!hasEnteredPhaseTwo) {
                AudioManager.getInstance().playSfx(AudioId.BossAngry, character);
                hasEnteredPhaseTwo = true;
            }
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
            }
            else {
                character.characterAnimator.setCurrentAnimation(CharacterAnimator.AnimationState.IDLE);
            }
        }

        if (lightningAttackActive) {
            float oldTimer = lightningAttackTimer;
            lightningAttackTimer += delta;

            float warningDuration = isPhaseTwo ? 1.2f : 1.6f;
            int currentFlashCount = (int) (lightningAttackTimer / 0.2f);

            if (currentFlashCount > previousFlashCount && currentFlashCount % 2 == 0 && lightningAttackTimer < warningDuration) {
                AudioManager.getInstance().playSfx(AudioId.BossThunderWarning, character, 0.75f);
            }
            previousFlashCount = currentFlashCount;

            if (oldTimer < warningDuration && lightningAttackTimer >= warningDuration) {
                AudioManager.getInstance().playSfx(AudioId.BossThunderAttack, character);
            }

            float lightningDuration = isPhaseTwo ? 1.7f : 2.1f; // 3 flashes (1.2s) + 0.5s strike OR 4 flashes (1.6s) + 0.5s strike

            if (lightningAttackTimer >= lightningDuration) {
                lightningAttackActive = false;
                previousFlashCount = -1;
            }
        }

        if (summonWarningActive) {
            float oldTimer = summonWarningTimer;
            summonWarningTimer += delta;

            float warningDuration = 1.2f;
            int currentFlashCount = (int) (summonWarningTimer / 0.2f);

            if (currentFlashCount > previousSummonFlashCount && currentFlashCount % 2 == 0 && summonWarningTimer < warningDuration) {
                AudioManager.getInstance().playSfx(AudioId.BossSummonWarning, character, 0.75f);
            }
            previousSummonFlashCount = currentFlashCount;

            if (oldTimer < warningDuration && summonWarningTimer >= warningDuration) {
                AudioManager.getInstance().playSfx(AudioId.BossSummonAttack, character);
                enemyToSummon = pendingEnemyToSummon;
                summonWarningActive = false;
                pendingEnemyToSummon = null;
                previousSummonFlashCount = -1;
            }
        }

        character.velocity.set(0, 0);
        character.stopMoving();

        if (!character.isPerformingSpecialAttack) {
            attackTimer += delta;
        }

        float currentAttackInterval = isPhaseTwo ? attackIntervalPhase2 : attackInterval;

        // Si hay otros enemigos y su salud esta arriba del 30%, aumentar el intervalo de ataque
        // Nerfeo solo para GeminiEXE
        if (enemyCount > 1 && (float) character.getHealth() / maxHealth > 0.3f && BossType == EnemyType.BOSS_GEMINI) {
            currentAttackInterval *= 2;
        }

        // Si esta vivo, atacar cada cierto intervalo
        if (attackTimer >= currentAttackInterval && character.getHealth() > 0) {
            attackTimer = 0f;
            performRandomAttack(bullets, playerCharacter, enemyCount, enemies);
        }
    }

    /* Ejecutar ataque aleatorio */
    private void performRandomAttack(ArrayList<Bullet> bullets, Character playerCharacter, int enemyCount, ArrayList<IEnemy> enemies) {
        boolean isPhaseTwo = (float) character.getHealth() / maxHealth <= 0.5f;
        boolean isLowHealth = (float) character.getHealth() / maxHealth <= 0.25f;

        /* INVOCACION */
        // GeminiEXE invoca un solo enemigo en fase 2, invoca cualquier tipo de enemigo con salud baja
        // ChatGPT invoca un solo enemigo en fase 1, invoca cualquier tipo de enemigo en fase 2
        boolean canSummon = false;

        switch (BossType) {
            /* El jefe Gemini puede invocar dependiendo */
            case BOSS_GEMINI:
                if (isLowHealth) {
                    canSummon = true;
                } else if (enemyCount == 1 && isPhaseTwo) {
                    canSummon = true;
                }
                break;

            /* El jefe ChatGPT puede invocar dependiendo */
            case BOSS_CHATGPT:
                if (isPhaseTwo) {
                    canSummon = true;
                } else if (enemyCount == 1) {
                    canSummon = true;
                }
                break;
        
            default:
                break;
        }

        // Pre-verificación para prevenir la animación de invocación si todos los tipos de enemigos ya están presentes
        if (canSummon) {
            boolean summonAll = (BossType == EnemyType.BOSS_GEMINI && isLowHealth) || (BossType == EnemyType.BOSS_CHATGPT && isPhaseTwo);
            if (summonAll) {
                ArrayList<EnemyType> availableToSummon = new ArrayList<>();
                availableToSummon.add(EnemyType.PATROLLER);
                availableToSummon.add(EnemyType.SHOOTER);
                availableToSummon.add(EnemyType.FLYING);

                for (IEnemy enemy : enemies) {
                    if (enemy.getCharacter() != character) {
                        availableToSummon.remove(enemy.getType());
                    }
                }

                if (availableToSummon.isEmpty()) {
                    canSummon = false;
                }
            }
        }

        int numberOfAttacks = canSummon ? 3 : 2;
        int attackType = random.nextInt(numberOfAttacks);

        switch (attackType) {
            case 0:
                attackType1(bullets, playerCharacter, enemyCount);
                break;
            case 1:
                attackType2(bullets, playerCharacter, enemyCount);
                break;
            case 2:
                attackType3(enemyCount, enemies);
            break;
            default:
                break;
        }
    }

    /* Ataque rayo */
    private void attackType1(ArrayList<Bullet> bullets, Character playerCharacter, int enemyCount) {
        character.isPerformingSpecialAttack = true;
        character.characterAnimator.setCurrentAnimation(CharacterAnimator.AnimationState.ATTACK1);
        if (playerCharacter != null) {
            lightningAttackActive = true;
            lightningAttackTimer = 0f;
            lightningPlayerX = playerCharacter.position.x;
        }
    }

    /* Ataque volea de balas */
    private void attackType2(ArrayList<Bullet> bullets, Character playerCharacter, int enemyCount) {
        boolean isPhaseTwo = (float) character.getHealth() / maxHealth <= 0.5f;

        character.isPerformingSpecialAttack = true;
        character.characterAnimator.setCurrentAnimation(CharacterAnimator.AnimationState.ATTACK2);
        AudioManager.getInstance().playSfx(AudioId.EnemyAttack, character); // No cambiar esto
        if (playerCharacter == null) return;

        Vector2 bossTop = new Vector2(character.position.x + character.getWidth() / 2, character.position.y + character.getHeight());
        Vector2 playerCenter = new Vector2(playerCharacter.position.x + playerCharacter.getWidth() / 2, playerCharacter.position.y + 35);

        Vector2 direction = playerCenter.sub(bossTop).nor();
        float bulletSpeed = 100f; // From WeaponType.SHOOTER

        int bulletCount;
        float angleIncrement;
        
        switch (BossType) {
            /* Logica disparo GeminiEXE */
            case BOSS_GEMINI:
                if (isPhaseTwo) {
                    bulletCount = 12;
                    angleIncrement = 30f;
                } else {
                    bulletCount = 10;
                    angleIncrement = 36f;
                }
                break;

            /* Logica disparo ChatGPT */
            case BOSS_CHATGPT:
                if (isPhaseTwo) {
                    bulletCount = 14;
                    angleIncrement = 25.7f;
                } else {
                    bulletCount = 12;
                    angleIncrement = 30f;
                }
                break;
            
            /* Para evitar errores */
            default:
                bulletCount = 12;
                angleIncrement = 30f;
                break;
        }

        for (int i = 0; i < bulletCount; i++) {
            Vector2 bulletVelocity = direction.cpy().rotateDeg(i * angleIncrement).scl(bulletSpeed);
            Bullet bullet = new Bullet(bossTop.x, bossTop.y, bulletVelocity, WeaponType.SHOOTER, character);
            bullets.add(bullet);
        }
    }

    /* Ataque invocacion */
    private void attackType3(int enemyCount, ArrayList<IEnemy> enemies) {
        boolean isPhaseTwo = (float) character.getHealth() / maxHealth <= 0.5f;
        boolean isLowHealth = (float) character.getHealth() / maxHealth <= 0.25f;

        character.isPerformingSpecialAttack = true;
        character.characterAnimator.setCurrentAnimation(CharacterAnimator.AnimationState.SUMMON);

        summonWarningActive = true;
        summonWarningTimer = 0f;

        switch (BossType) {
            /* Logica invocacion GeminiEXE */
            case BOSS_GEMINI:
                if (isLowHealth) {
                summonAllMissingEnemies(enemies);
                } else if (enemyCount == 1 && isPhaseTwo) {
                    summonOneRandomEnemy();
                }
                break;

            /* Logica invocacion ChatGPT */
            case BOSS_CHATGPT:
                if (isPhaseTwo) {
                    summonAllMissingEnemies(enemies);
                } else if (enemyCount == 1 ) {
                    summonOneRandomEnemy();
                }
                break;
            /* Para evitar errores */
            default:
                break;
        }
    }

    /* INVOCACIONES */
    /* Invocar un solo enemigo */
    private void summonOneRandomEnemy() 
    {
        // Old logic: summon a random enemy
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

    /* Invocar cualquier tipo de enemigo */
    private void summonAllMissingEnemies(ArrayList<IEnemy> enemies) 
    {
        // New logic: summon an enemy type that is not present
        ArrayList<EnemyType> availableToSummon = new ArrayList<>();
        availableToSummon.add(EnemyType.PATROLLER);
        availableToSummon.add(EnemyType.SHOOTER);
        availableToSummon.add(EnemyType.FLYING);

        for (IEnemy enemy : enemies) {
            if (enemy.getCharacter() != character) { // Don't check the boss itself
                availableToSummon.remove(enemy.getType());
            }
        }

        if (!availableToSummon.isEmpty()) {
            int randomEnemy = random.nextInt(availableToSummon.size());
            pendingEnemyToSummon = availableToSummon.get(randomEnemy);
        } else {
            // All enemy types are present, do nothing
            character.isPerformingSpecialAttack = false;
            return;
        }
    }
}
