package com.machinehunterdev.game.Character;

import java.util.ArrayList;
import java.util.Random;

import com.badlogic.gdx.math.Vector2;
import com.machinehunterdev.game.Audio.AudioId;
import com.machinehunterdev.game.Audio.AudioManager;
import com.machinehunterdev.game.DamageTriggers.Bullet;
import com.machinehunterdev.game.DamageTriggers.WeaponType;
import com.machinehunterdev.game.Environment.SolidObject;

/**
 * Controlador de IA especializado para los enemigos de tipo jefe.
 * Esta clase contiene la logica compleja que define el comportamiento de un jefe,
 * incluyendo sus patrones de ataque, transiciones de fase, invocacion de enemigos
 * y gestion de efectos visuales y de sonido especificos.
 * 
 * @author MachineHunterDev
 */
public class BossEnemyController extends CharacterController {

    // === ATRIBUTOS DE CONFIGURACION DEL JEFE ===
    private float attackTimer; // Temporizador para controlar el intervalo entre ataques.
    private final float attackInterval; // Intervalo de ataque en la fase 1.
    private final float attackIntervalPhase2; // Intervalo de ataque en la fase 2 (generalmente mas rapido).
    private final int maxHealth; // Salud maxima del jefe, usada para determinar las transiciones de fase.
    private final Random random = new Random(); // Generador de numeros aleatorios para seleccionar ataques.
    private final EnemyType BossType; // El tipo especifico de jefe (ej. BOSS_GEMINI o BOSS_CHATGPT).

    // === ESTADOS DE ATAQUES ESPECIALES ===
    // --- Ataque de Rayo (Lightning) ---
    private boolean lightningAttackActive = false; // Indica si el ataque de rayo esta en curso.
    private float lightningAttackTimer = 0f; // Temporizador para las fases del ataque de rayo (advertencia y golpe).
    private float lightningPlayerX = 0f; // Almacena la posicion del jugador cuando se inicia el ataque para apuntar el rayo.
    private int previousFlashCount = -1; // Controla los parpadeos y sonidos de la advertencia.

    // --- Invocacion de Enemigos (Summon) ---
    private boolean summonWarningActive = false; // Indica si la advertencia de invocacion esta activa.
    private float summonWarningTimer = 0f; // Temporizador para la advertencia.
    private int previousSummonFlashCount = -1; // Controla los parpadeos de la advertencia de invocacion.
    private EnemyType pendingEnemyToSummon = null; // El tipo de enemigo que se invocara despues de la advertencia.

    // === GESTION DE FASES ===
    private boolean hasEnteredPhaseTwo = false; // Bandera para asegurar que la transicion a la fase 2 ocurra solo una vez.

    // === COMUNICACION EXTERNA ===
    private EnemyType enemyToSummon = null; // El tipo de enemigo que se solicita invocar. Es leido por GameplayState.

    // Devuelve el tipo de enemigo que debe ser invocado.
    public EnemyType getEnemyToSummon() { return enemyToSummon; }
    // Limpia la solicitud de invocacion una vez que ha sido procesada.
    public void clearSummonRequest() { this.enemyToSummon = null; }

    // Indica si el ataque de rayo esta activo.
    public boolean isLightningAttackActive() { return lightningAttackActive; }
    // Devuelve la posicion X del jugador en el momento en que se inicio el ataque de rayo.
    public float getLightningPlayerX() { return lightningPlayerX; }

    // Indica si la advertencia de invocacion esta parpadeando (para efectos visuales).
    public boolean isSummonWarning() {
        if (!summonWarningActive) return false;
        float warningDuration = 1.2f; // Duracion de la advertencia.
        if (summonWarningTimer < warningDuration) {
            int flashCount = (int) (summonWarningTimer / 0.2f);
            return flashCount % 2 == 0; // Devuelve true en intervalos pares para crear un parpadeo.
        }
        return false;
    }

    // Indica si la advertencia de rayo esta parpadeando.
    public boolean isLightningWarning() {
        if (!lightningAttackActive) return false;
        boolean isPhaseTwo = (float) character.getHealth() / maxHealth <= 0.5f;
        float warningDuration = isPhaseTwo ? 1.2f : 1.6f; // La advertencia es mas corta en la fase 2.
        if (lightningAttackTimer < warningDuration) {
            int flashCount = (int) (lightningAttackTimer / 0.2f);
            return flashCount % 2 == 0;
        }
        return false;
    }

    // Indica si el rayo ya esta golpeando (la fase final del ataque).
    public boolean isLightningStriking() {
        if (!lightningAttackActive) return false;
        boolean isPhaseTwo = (float) character.getHealth() / maxHealth <= 0.5f;
        float warningDuration = isPhaseTwo ? 1.2f : 1.6f;
        return lightningAttackTimer >= warningDuration;
    }

    /**
     * Constructor del controlador del jefe.
     * @param enemyCharacter El objeto Character del jefe.
     * @param type El tipo de jefe.
     */
    public BossEnemyController(Character enemyCharacter, EnemyType type) {
        super(enemyCharacter);
        this.maxHealth = enemyCharacter.getHealth();
        this.attackTimer = 0f;
        character.gravity = 0; // Los jefes generalmente no se ven afectados por la gravedad.

        // Configura los intervalos de ataque segun el tipo de jefe.
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

    // Este metodo se mantiene por herencia, pero la logica principal esta en la sobrecarga de abajo.
    @Override
    public void update(float delta, ArrayList<SolidObject> solidObjects, ArrayList<Bullet> bullets, Character playerCharacter, int enemyCount) {
        // Vacio.
    }

    /**
     * Metodo principal de actualizacion del jefe, que incluye la lista de enemigos para la logica de invocacion.
     */
    public void update(float delta, ArrayList<SolidObject> solidObjects, ArrayList<Bullet> bullets, Character playerCharacter, int enemyCount, ArrayList<IEnemy> enemies) {
        handleHurtAnimation(); // Gestiona la animacion de recibir dano.

        // El jefe siempre mira hacia el jugador.
        if (playerCharacter != null) {
            character.setSeeingRight(playerCharacter.position.x > character.position.x);
        }

        // Gestion de la transicion a la fase 2 (salud <= 50%).
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

        // Vuelve al estado de reposo (IDLE o IDLE_RAGE) despues de que termina una animacion de ataque.
        if ((currentAnimation == CharacterAnimator.AnimationState.ATTACK1 ||
            currentAnimation == CharacterAnimator.AnimationState.ATTACK2 ||
            currentAnimation == CharacterAnimator.AnimationState.SUMMON) &&
            character.characterAnimator.isAnimationFinished(currentAnimation)) {
            character.isPerformingSpecialAttack = false;
            character.characterAnimator.setCurrentAnimation(isPhaseTwo ? CharacterAnimator.AnimationState.IDLE_RAGE : CharacterAnimator.AnimationState.IDLE);
        }

        // Actualiza la logica del ataque de rayo.
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

            float lightningDuration = isPhaseTwo ? 1.7f : 2.1f;
            if (lightningAttackTimer >= lightningDuration) {
                lightningAttackActive = false;
                previousFlashCount = -1;
            }
        }

        // Actualiza la logica de la advertencia de invocacion.
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

        // Nerfeo para Gemini: si hay otros enemigos y su salud esta por encima del 30%, ataca mas lento.
        if (enemyCount > 1 && (float) character.getHealth() / maxHealth > 0.3f && BossType == EnemyType.BOSS_GEMINI) {
            currentAttackInterval *= 2;
        }

        // Si ha pasado el tiempo suficiente, lanza un nuevo ataque.
        if (attackTimer >= currentAttackInterval && character.getHealth() > 0) {
            attackTimer = 0f;
            performRandomAttack(bullets, playerCharacter, enemyCount, enemies);
        }
    }

    /**
     * Selecciona y ejecuta un ataque aleatorio de entre las opciones disponibles.
     */
    private void performRandomAttack(ArrayList<Bullet> bullets, Character playerCharacter, int enemyCount, ArrayList<IEnemy> enemies) {
        boolean isPhaseTwo = (float) character.getHealth() / maxHealth <= 0.5f;
        boolean isLowHealth = (float) character.getHealth() / maxHealth <= 0.25f;
        boolean canSummon = false;

        // Logica para determinar si el jefe puede invocar enemigos.
        switch (BossType) {
            case BOSS_GEMINI:
                canSummon = (isLowHealth) || (enemyCount == 1 && isPhaseTwo);
                break;
            case BOSS_CHATGPT:
                canSummon = (isPhaseTwo) || (enemyCount == 1);
                break;
            default:
                break;
        }

        // Comprueba si se pueden invocar mas enemigos (evita duplicados).
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

        // Elige un tipo de ataque aleatorio.
        int numberOfAttacks = canSummon ? 3 : 2;
        int attackType = random.nextInt(numberOfAttacks);

        switch (attackType) {
            case 0: attackType1(bullets, playerCharacter, enemyCount); break; // Ataque de rayo
            case 1: attackType2(bullets, playerCharacter, enemyCount); break; // Ataque de balas
            case 2: attackType3(enemyCount, enemies); break; // Invocacion
            default: break;
        }
    }

    // Ataque 1: Inicia la secuencia del ataque de rayo.
    private void attackType1(ArrayList<Bullet> bullets, Character playerCharacter, int enemyCount) {
        character.isPerformingSpecialAttack = true;
        character.characterAnimator.setCurrentAnimation(CharacterAnimator.AnimationState.ATTACK1);
        if (playerCharacter != null) {
            lightningAttackActive = true;
            lightningAttackTimer = 0f;
            lightningPlayerX = playerCharacter.position.x;
        }
    }

    // Ataque 2: Dispara una oleada de balas en abanico.
    private void attackType2(ArrayList<Bullet> bullets, Character playerCharacter, int enemyCount) {
        boolean isPhaseTwo = (float) character.getHealth() / maxHealth <= 0.5f;
        character.isPerformingSpecialAttack = true;
        character.characterAnimator.setCurrentAnimation(CharacterAnimator.AnimationState.ATTACK2);
        AudioManager.getInstance().playSfx(AudioId.EnemyAttack, character);
        if (playerCharacter == null) return;

        Vector2 bossTop = new Vector2(character.position.x + character.getWidth() / 2, character.position.y + character.getHeight());
        Vector2 playerCenter = new Vector2(playerCharacter.position.x + playerCharacter.getWidth() / 2, playerCharacter.position.y + 35);
        Vector2 direction = playerCenter.sub(bossTop).nor();
        float bulletSpeed = 100f;

        int bulletCount;
        float angleIncrement;
        
        // La cantidad y dispersion de las balas depende del jefe y la fase.
        switch (BossType) {
            case BOSS_GEMINI:
                bulletCount = isPhaseTwo ? 12 : 10;
                angleIncrement = isPhaseTwo ? 30f : 36f;
                break;
            case BOSS_CHATGPT:
                bulletCount = isPhaseTwo ? 14 : 12;
                angleIncrement = isPhaseTwo ? 25.7f : 30f;
                break;
            default:
                bulletCount = 12;
                angleIncrement = 30f;
                break;
        }

        for (int i = 0; i < bulletCount; i++) {
            Vector2 bulletVelocity = direction.cpy().rotateDeg(i * angleIncrement).scl(bulletSpeed);
            bullets.add(new Bullet(bossTop.x, bossTop.y, bulletVelocity, WeaponType.SHOOTER, character));
        }
    }

    // Ataque 3: Inicia la secuencia de invocacion de enemigos.
    private void attackType3(int enemyCount, ArrayList<IEnemy> enemies) {
        boolean isPhaseTwo = (float) character.getHealth() / maxHealth <= 0.5f;
        boolean isLowHealth = (float) character.getHealth() / maxHealth <= 0.25f;
        character.isPerformingSpecialAttack = true;
        character.characterAnimator.setCurrentAnimation(CharacterAnimator.AnimationState.SUMMON);
        summonWarningActive = true;
        summonWarningTimer = 0f;

        // Decide que enemigo invocar.
        switch (BossType) {
            case BOSS_GEMINI:
                if (isLowHealth) summonAllMissingEnemies(enemies);
                else if (enemyCount == 1 && isPhaseTwo) summonOneRandomEnemy();
                break;
            case BOSS_CHATGPT:
                if (isPhaseTwo) summonAllMissingEnemies(enemies);
                else if (enemyCount == 1) summonOneRandomEnemy();
                break;
            default:
                break;
        }
    }

    // Invoca un enemigo aleatorio de los tres tipos basicos.
    private void summonOneRandomEnemy() {
        int randomEnemy = random.nextInt(3);
        switch (randomEnemy) {
            case 0: pendingEnemyToSummon = EnemyType.PATROLLER; break;
            case 1: pendingEnemyToSummon = EnemyType.SHOOTER; break;
            case 2: pendingEnemyToSummon = EnemyType.FLYING; break;
        }
    }

    // Invoca un tipo de enemigo que aun no este presente en la escena.
    private void summonAllMissingEnemies(ArrayList<IEnemy> enemies) {
        ArrayList<EnemyType> availableToSummon = new ArrayList<>();
        availableToSummon.add(EnemyType.PATROLLER);
        availableToSummon.add(EnemyType.SHOOTER);
        availableToSummon.add(EnemyType.FLYING);

        for (IEnemy enemy : enemies) {
            if (enemy.getCharacter() != character) {
                availableToSummon.remove(enemy.getType());
            }
        }

        if (!availableToSummon.isEmpty()) {
            int randomEnemy = random.nextInt(availableToSummon.size());
            pendingEnemyToSummon = availableToSummon.get(randomEnemy);
        } else {
            character.isPerformingSpecialAttack = false; // Cancela el ataque si no hay enemigos para invocar.
        }
    }
}