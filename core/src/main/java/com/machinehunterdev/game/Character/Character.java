package com.machinehunterdev.game.Character;

import java.util.ArrayList;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.machinehunterdev.game.Audio.AudioId;
import com.machinehunterdev.game.Audio.AudioManager;
import com.machinehunterdev.game.DamageTriggers.Bullet;
import com.machinehunterdev.game.DamageTriggers.DamageSystem;
import com.machinehunterdev.game.DamageTriggers.DamageType;
import com.machinehunterdev.game.DamageTriggers.WeaponType;
import com.machinehunterdev.game.Gameplay.GlobalSettings;
import com.machinehunterdev.game.Levels.LevelData;

// Clase base para todos los personajes del juego: jugador, enemigos y NPCs.
// Gestiona fisica, animacion, daño, armas, invulnerabilidad y efectos visuales.
public class Character 
{
    // === ATRIBUTOS DE ESTADO ===
    
    // Salud actual y maxima del personaje.
    public int health;
    public int maxHealth;
    
    // Dimensiones por defecto del personaje (usadas si no hay textura o animacion).
    public int width = 32;
    public int height = 32;

    // === SISTEMA DE ANIMACION ===
    
    // Animador avanzado que maneja multiples estados (correr, saltar, atacar, etc).
    public CharacterAnimator characterAnimator;
    
    // Textura de respaldo para personajes sin animacion (compatibilidad con NPCs simples).
    public Texture fallbackTexture;

    // === FISICA Y MOVIMIENTO ===
    
    // Posicion y velocidad del personaje en el mundo.
    public Vector2 position;
    public Vector2 velocity;

    // Hitbox de colision (puede ser desplazada respecto a la posicion).
    private Rectangle hitbox = new Rectangle();
    private float hitboxOffsetX = 0;
    private float hitboxOffsetY = 0;
    private float originalHitboxHeight = 0;

    // Parametros fisicos basicos: velocidad, salto y gravedad.
    public float speed = 150.0f;        // Velocidad horizontal
    public float jumpForce = 400.0f;    // Fuerza de salto
    public float gravity = -1200.0f;    // Gravedad (negativa porque Y crece hacia arriba)

    // === SISTEMA DE EMPUJE POR DANO ===
    
    // Indica si el personaje esta siendo empujado por recibir daño.
    public boolean isKnockedBack = false;
    // Velocidad del empuje.
    public float knockbackSpeed = 250f;
    // Distancia al suelo mas cercano (usada para animaciones de caida).
    public float distanceToGround = Float.MAX_VALUE;

    // === ESTADOS DEL PERSONAJE ===
    
    // Banderas de estado para controlar comportamiento y animacion.
    public boolean isMoving;                    // Se esta moviendo horizontalmente
    public boolean isSeeingRight;               // Direccion en la que mira (true = derecha)
    public boolean isAttacking;                 // Esta realizando un ataque
    public boolean isAlive;                     // Esta vivo
    public boolean onGround;                    // Esta tocando el suelo
    public boolean onPlatform;                  // Esta sobre una plataforma movil
    public boolean isCrouching;                 // Esta agachado
    public boolean isPerformingSpecialAttack = false; // Esta ejecutando un ataque especial (jefe)
    public boolean isFallingThroughPlatform = false; // Puede atravesar plataformas temporalmente
    public boolean readyForGameOverTransition = false; // Listo para transicion de Game Over (jugador)
    public boolean isPlayer = false;            // Indica si es el jugador
    public boolean isPaused = false;            // Indica si el personaje esta en pausa
    private EnemyType enemyType;                // Tipo de enemigo (null si no aplica)

    // Establece el tipo de enemigo.
    public void setEnemyType(EnemyType enemyType) {
        this.enemyType = enemyType;
    }

    // === SISTEMA DE INVULNERABILIDAD ===
    
    // Indica si el personaje no puede recibir dano.
    private boolean invulnerable = false;
    // Temporizador que cuenta hacia atras el tiempo de invulnerabilidad.
    private float invulnerabilityTimer = 0f;
    // Temporizador para ignorar plataformas al caer (accion de jugador).
    private float fallThroughTimer = 0f;
    // Duracion fija de la invulnerabilidad tras recibir dano.
    private static final float INVULNERABILITY_DURATION = 3.0F;

    // === EFECTOS VISUALES ===
    
    // Indica si debe mostrarse parpadeo transparente al recibir dano.
    private boolean flashTransparent = false;
    // Temporizador para el efecto de parpadeo.
    private float transparentFlashTimer = 0f;

    // === SISTEMA DE ANIMACION DE DANO ===
    
    // Indica si esta en animacion de dano (HURT).
    private boolean isHurt = false;
    private float hurtTimer = 0f;
    // Flags para gestionar el disparo sincronizado con la animacion.
    private boolean bulletInvocationPending = false;
    private WeaponType pendingWeaponType;
    private ArrayList<Bullet> pendingBulletsList;

    // === SISTEMA DE ARMAS ===
    private WeaponType currentWeapon = WeaponType.LASER;
    private float rifleCooldown = 0f;
    private float shotgunCooldown = 0f;
    private float sniperCooldown = 0f;
    private float thunderCooldown = 0f;
    private static final float LASER_COOLDOWN_TIME = 0.3f;
    private static final float ION_COOLDOWN_TIME = 0.3f;
    private static final float RAILGUN_COOLDOWN_TIME = 0.3f;
    private static final float THUNDER_COOLDOWN_TIME = 0.3f;
    // Offset para que las balas no salgan del centro exacto del personaje.
    private static final Vector2 PLAYER_BULLET_SPAWN_OFFSET = new Vector2(-10f, 22f);
    private static final Vector2 ENEMY_BULLET_SPAWN_OFFSET = new Vector2(-10f, 10f);

    // === CONSTRUCTORES ===

    // Constructor principal que permite personalizar todos los aspectos.
    public Character(int health, CharacterAnimator animator, Texture fallbackTexture, float x, float y, boolean isPlayer) {
        this.health = health;
        this.maxHealth = health;
        this.characterAnimator = animator;
        this.fallbackTexture = fallbackTexture;
        this.position = new Vector2(x, y);
        this.velocity = new Vector2(0, 0);
        this.isPlayer = isPlayer;
        setHitbox(null); // Inicializa hitbox con valores por defecto
        initDefaults();
    }

    // Constructores simplificados para casos comunes.
    public Character(int health, Texture fallbackTexture, float x, float y) {
        this(health, null, fallbackTexture, x, y, false);
    }

    public Character(int health, CharacterAnimator animator, float x, float y) {
        this(health, animator, null, x, y, false);
    }

    public Character(int health, CharacterAnimator animator, float x, float y, boolean isPlayer) {
        this(health, animator, null, x, y, isPlayer);
    }

    // Inicializa los estados basicos del personaje.
    private void initDefaults() {
        this.isMoving = false;
        this.isSeeingRight = true;
        this.isAttacking = false;
        this.isAlive = true;
        this.onGround = false;
        this.onPlatform = false;
    }

    // === METODO DE ACTUALIZACION PRINCIPAL ===
    
    // Actualiza logica de fisica, animacion, dano y efectos cada frame.
    public void update(float delta) {
        if (isPaused) {
            if (characterAnimator != null) {
                characterAnimator.setCurrentAnimation(CharacterAnimator.AnimationState.IDLE);
                characterAnimator.update(delta);
            }
            return;
        }

        // Maneja el empuje por dano: fija velocidad horizontal y desactiva gravedad temporal.
        if (isKnockedBack) {
            velocity.x = isSeeingRight ? -knockbackSpeed : knockbackSpeed;
            isMoving = true;
            onGround = false;
        }

        // Determina el estado de animacion apropiado segun el estado actual del personaje.
        if (characterAnimator != null) {
            characterAnimator.setFacingRight(isSeeingRight);
            
            CharacterAnimator.AnimationState newState;

            if (isKnockedBack && characterAnimator.hasAnimation(CharacterAnimator.AnimationState.HURT)) {
                newState = CharacterAnimator.AnimationState.HURT;
            } else if (isBossInPhaseTwo() && isHurt && characterAnimator.hasAnimation(CharacterAnimator.AnimationState.ANGRY_HURT) 
                        && !(isPerformingSpecialAttack || (isAttacking && !isPlayer))) {
                newState = CharacterAnimator.AnimationState.ANGRY_HURT;
            } else if (!isAlive) {
                newState = CharacterAnimator.AnimationState.DEAD;
                if (!isPlayer) {
                    velocity.x = 0;
                    velocity.y = 0;
                }
            } else {
                if (isHurt && characterAnimator.hasAnimation(CharacterAnimator.AnimationState.HURT) 
                            && !(isPerformingSpecialAttack || (isAttacking && !isPlayer))) {
                    newState = CharacterAnimator.AnimationState.HURT;
                } else if (isCrouching && characterAnimator.hasAnimation(CharacterAnimator.AnimationState.CROUCH)) {
                    newState = CharacterAnimator.AnimationState.CROUCH;
                } else if (isAttacking) {
                    // Selecciona animacion de ataque segun el arma actual.
                    if (currentWeapon == WeaponType.LASER && characterAnimator.hasAnimation(CharacterAnimator.AnimationState.LASER_ATTACK)) {
                        newState = CharacterAnimator.AnimationState.LASER_ATTACK;
                    } else if (currentWeapon == WeaponType.ION && characterAnimator.hasAnimation(CharacterAnimator.AnimationState.ION_ATTACK)) {
                        newState = CharacterAnimator.AnimationState.ION_ATTACK;
                    } else if (currentWeapon == WeaponType.RAILGUN && characterAnimator.hasAnimation(CharacterAnimator.AnimationState.RAILGUN_ATTACK)) {
                        newState = CharacterAnimator.AnimationState.RAILGUN_ATTACK;
                    } else {
                        newState = CharacterAnimator.AnimationState.ATTACK;
                    }

                    // Invoca la bala en el primer frame de la animacion de ataque.
                    if (bulletInvocationPending && characterAnimator.getCurrentFrameIndex() == 0) {
                        switch (pendingWeaponType) {
                            case LASER:
                                shootRifle(pendingBulletsList);
                                break;
                            case ION:
                                shootShotgun(pendingBulletsList);
                                break;
                            case RAILGUN:
                                shootSniper(pendingBulletsList);
                                break;
                            default:
                        }
                        bulletInvocationPending = false;
                    }
                } else if (isPerformingSpecialAttack) {
                    newState = characterAnimator.getCurrentState();
                } else if (!onGround) {
                    // Usa animacion de salto o caida segun la velocidad vertical.
                    if (velocity.y > 0) {
                        newState = CharacterAnimator.AnimationState.JUMP;
                    } else if (velocity.y < 0) {
                        newState = CharacterAnimator.AnimationState.FALL;
                        // Si esta cerca del suelo, fuerza el ultimo frame de caida.
                        if (distanceToGround <= 5f) {
                            com.machinehunterdev.game.Util.SpriteAnimator fallAnimator = characterAnimator.getAnimator(CharacterAnimator.AnimationState.FALL);
                            if (fallAnimator != null) {
                                fallAnimator.setCurrentFrame(fallAnimator.getFrames().size() - 1);
                            }
                        }
                    } else {
                        newState = CharacterAnimator.AnimationState.IDLE;
                    }
                } else {
                    // En el suelo: caminar si se mueve, idle si esta quieto.
                    if (isMoving) {
                        newState = CharacterAnimator.AnimationState.RUN;
                    } else {
                        if (characterAnimator.getCurrentState() != CharacterAnimator.AnimationState.IDLE_RAGE) {
                            newState = CharacterAnimator.AnimationState.IDLE;
                        } else {
                            newState = CharacterAnimator.AnimationState.IDLE_RAGE;
                        }
                    }
                }
            }

            // Fallback a IDLE si la animacion deseada no existe.
            if (!characterAnimator.hasAnimation(newState)) {
                if ((newState == CharacterAnimator.AnimationState.JUMP || newState == CharacterAnimator.AnimationState.FALL || newState == CharacterAnimator.AnimationState.RUN) && characterAnimator.hasAnimation(CharacterAnimator.AnimationState.IDLE)) {
                    newState = CharacterAnimator.AnimationState.IDLE;
                } else {
                    newState = CharacterAnimator.AnimationState.IDLE;
                }
            }
            
            characterAnimator.setCurrentAnimation(newState);
            characterAnimator.update(delta);
        }

        // Actualiza efectos visuales.
        if (flashTransparent) {
            transparentFlashTimer -= delta;
            if (transparentFlashTimer <= 0) {
                flashTransparent = false;
            }
        }

        // Actualiza invulnerabilidad.
        if (invulnerable) {
            invulnerabilityTimer -= delta;
            if (invulnerabilityTimer <= 0) {
                invulnerable = false;
                invulnerabilityTimer = 0;
            }
        }

        // Maneja el estado de dano (detiene movimiento horizontal en enemigos no voladores).
        if (isHurt) {
            hurtTimer -= delta;

            if (this.enemyType != EnemyType.FLYING) {
                velocity.x = 0;
            }

            if (hurtTimer <= 0) {
                isHurt = false;
                hurtTimer = 0;
            }
        }

        // Maneja la caida a traves de plataformas.
        if (isFallingThroughPlatform) {
            fallThroughTimer -= delta;
            if (fallThroughTimer <= 0) {
                isFallingThroughPlatform = false;
                fallThroughTimer = 0;
            }
        }

        // Aplica gravedad si no esta en el suelo.
        if (!onGround) {
            velocity.y += gravity * delta;
        }

        // Actualiza posicion.
        position.x += velocity.x * delta;
        position.y += velocity.y * delta;

        // Ajusta la hitbox si es el jugador y esta agachado.
        if (isPlayer) {
            if (isCrouching) {
                hitbox.height = originalHitboxHeight / 2;
            } else {
                hitbox.height = originalHitboxHeight;
            }
        }

        // Actualiza la posicion de la hitbox segun la direccion de mirada.
        if (isSeeingRight) {
            hitbox.setPosition(position.x + hitboxOffsetX, position.y + hitboxOffsetY);
        } else {
            float flippedX = position.x + getWidth() - (hitboxOffsetX + hitbox.width);
            hitbox.setPosition(flippedX, position.y + hitboxOffsetY);
        }

        // Reinicia velocidad horizontal si no se mueve y no esta en estados especiales.
        if (!isKnockedBack && !isHurt && isAlive) {
            if (!isMoving) {
                velocity.x = 0;
            }

            if (velocity.x > 0) {
                isSeeingRight = true;
            } else if (velocity.x < 0) {
                isSeeingRight = false;
            }
        }

        // Actualiza los temporizadores de cooldown de las armas.
        updateWeaponCooldowns(delta);
    }

    // === METODO DE RENDERIZADO ===
    
    // Dibuja el personaje aplicando efectos visuales segun su estado.
    public void draw(SpriteBatch spriteBatch) {
        if (!isAlive && (characterAnimator == null || characterAnimator.isAnimationFinished(CharacterAnimator.AnimationState.DEAD))) {
            return; // No dibujar muertos con animacion terminada
        }

        if (characterAnimator != null) {
            Sprite currentSprite = characterAnimator.getCurrentSprite();
            if (currentSprite != null) {
                Color originalColor = new Color(currentSprite.getColor());

                // Aplica transparencia segun el estado: dano instantaneo > invulnerabilidad parpadeante > normal
                if (flashTransparent) {
                    currentSprite.setColor(1,1,1,0.3f);
                } else if (invulnerable) {
                    float blinkTime = invulnerabilityTimer % 1.0f;
                    float alpha = (blinkTime < 0.5f) ? 0.7f : 0.3f;
                    currentSprite.setColor(1, 1, 1, alpha);
                } else {
                    currentSprite.setColor(1, 1, 1, 1);
                }

                characterAnimator.draw(position.x, position.y, spriteBatch);
                currentSprite.setColor(originalColor);
            }
        }
    }

    // === METODOS DE ACCION ===
    
    // Inicia la animacion de ataque.
    public void attack() { 
        isAttacking = true; 
    }

    // Cambia el arma actual del jugador.
    public void switchWeapon(WeaponType weaponType) {
        this.currentWeapon = weaponType;
    }

    // Devuelve el arma actual.
    public WeaponType getCurrentWeapon() {
        return currentWeapon;
    }

    // Verifica si puede disparar segun el cooldown del arma actual.
    public boolean canShoot() {
        switch (currentWeapon) {
            case LASER:
                return rifleCooldown <= 0f;
            case ION:
                return shotgunCooldown <= 0f;
            case RAILGUN:
                return sniperCooldown <= 0f;
            case SHOOTER:
                return thunderCooldown <= 0f;
            default:
                return true;
        }
    }

    // Dispara con el arma actual (solo si esta en el suelo y no en cooldown).
    public void shoot(ArrayList<Bullet> bullets) {
        if (!canShoot() || !onGround) return;
        
        isAttacking = true;
        bulletInvocationPending = true;
        pendingWeaponType = currentWeapon;
        pendingBulletsList = bullets;

        // Aplica cooldown inmediato.
        switch (currentWeapon) {
            case LASER:
                rifleCooldown = LASER_COOLDOWN_TIME;
                break;
            case ION:
                shotgunCooldown = ION_COOLDOWN_TIME;
                break;
            case RAILGUN:
                sniperCooldown = RAILGUN_COOLDOWN_TIME;
                break;
            case SHOOTER:
                thunderCooldown = THUNDER_COOLDOWN_TIME;
                break;
            default:
        }
    }

    // Disparo especifico para rifle laser.
    private void shootRifle(ArrayList<Bullet> bullets) {
        AudioManager.getInstance().playSfx(AudioId.LaserAttack, this, GlobalSettings.ANNOYING_VOLUME);
        float bulletX = position.x + (isSeeingRight ? getWidth() + PLAYER_BULLET_SPAWN_OFFSET.x - 8: 0 - PLAYER_BULLET_SPAWN_OFFSET.x);
        float bulletY = position.y + getHeight() - PLAYER_BULLET_SPAWN_OFFSET.y;
        bullets.add(new Bullet(bulletX, bulletY, isSeeingRight, WeaponType.LASER, this));
    }

    // Disparo especifico para escopeta (ion).
    private void shootShotgun(ArrayList<Bullet> bullets) {
        AudioManager.getInstance().playSfx(AudioId.IonAttack, this, GlobalSettings.ANNOYING_VOLUME);
        float bulletX = position.x + (isSeeingRight ? getWidth() + PLAYER_BULLET_SPAWN_OFFSET.x - 8: 0 - PLAYER_BULLET_SPAWN_OFFSET.x);
        float bulletY = position.y + getHeight() - PLAYER_BULLET_SPAWN_OFFSET.y;
        
        // Dispara tres balas con ligera dispersion vertical.
        for (int i = -1; i <= 1; i++) {
            float spreadY = bulletY + (i * 8f);
            bullets.add(new Bullet(bulletX, spreadY, isSeeingRight, WeaponType.ION, this));
        }
    }

    // Disparo especifico para rifle de francotirador (railgun).
    private void shootSniper(ArrayList<Bullet> bullets) {
        AudioManager.getInstance().playSfx(AudioId.RailgunAttack, this, GlobalSettings.ANNOYING_VOLUME);
        float bulletX = position.x + (isSeeingRight ? getWidth() + PLAYER_BULLET_SPAWN_OFFSET.x - 8: 0 - PLAYER_BULLET_SPAWN_OFFSET.x);
        float bulletY = position.y + getHeight() - PLAYER_BULLET_SPAWN_OFFSET.y;
        bullets.add(new Bullet(bulletX, bulletY, isSeeingRight, WeaponType.RAILGUN, this));
    }

    // Actualiza todos los temporizadores de cooldown.
    public void updateWeaponCooldowns(float delta) {
        if (rifleCooldown > 0f) rifleCooldown -= delta;
        if (shotgunCooldown > 0f) shotgunCooldown -= delta;
        if (sniperCooldown > 0f) sniperCooldown -= delta;
        if (thunderCooldown > 0f) thunderCooldown -= delta;
        
        // Asegura que no queden valores negativos.
        if (rifleCooldown < 0f) rifleCooldown = 0f;
        if (shotgunCooldown < 0f) shotgunCooldown = 0f;
        if (sniperCooldown < 0f) sniperCooldown = 0f;
        if (thunderCooldown < 0f) thunderCooldown = 0f;
    }

    // === METODOS DE DANO ===
    
    // Metodos que delegan al sistema centralizado de dano.
    public void takeDamage(int damage) {
        DamageSystem.applyDamageNoKnockback(this, damage, DamageType.CONTACT);
    }

    public void takeDamageNoKnockback(int damage) {
        DamageSystem.applyDamageNoKnockback(this, damage, DamageType.PROJECTILE);
    }

    public void takeContactDamage(Character source, int damage) {
        DamageSystem.applyContactDamage(this, source, damage);
    }

    public void takeDamageWithoutVulnerability(int damage) {
        DamageSystem.applyDamageWithoutInvulnerability(this, damage, false);
    }
    
    // Hace que el personaje aterrice en una superficie.
    public void landOn(float groundY) {
        position.y = groundY;
        velocity.y = 0;

        // Reproduce sonido de aterrizaje segun si es jugador o enemigo.
        if(onGround) return;
        if(isPlayer) AudioManager.getInstance().playSfx(AudioId.PlayerLand, this);
        else AudioManager.getInstance().playSfx(AudioId.EnemyLand, this);

        onGround = true;
        onPlatform = (groundY != GlobalSettings.GROUND_LEVEL);

        if (!isAlive) {
            readyForGameOverTransition = true;
        }

        // Activa invulnerabilidad breve tras aterrizar despues de empuje.
        if (isKnockedBack) {
            invulnerable = true;
            invulnerabilityTimer = INVULNERABILITY_DURATION;
        }

        isKnockedBack = false;
    }

    // Establece el estado de agachado.
    public void setCrouching(boolean crouching) {
        isCrouching = crouching;
    }

    // === METODOS DE MOVIMIENTO ===
    
    // Movimiento izquierda/derecha (solo si no esta en estados bloqueantes).
    public void moveLeft() {
        if (!isKnockedBack && !isCrouching && !isAttacking && !isHurt) {
            velocity.x = -speed;
            isMoving = true;
        }
    }

    public void moveRight() {
        if (!isKnockedBack && !isCrouching && !isAttacking && !isHurt) {
            velocity.x = speed;
            isMoving = true;
        }
    }

    // Detiene el movimiento horizontal.
    public void stopMoving() {
        if (!isKnockedBack && !isHurt) {
            isMoving = false;
        }
    }

    // Realiza un salto si esta en el suelo.
    public void jump() {
        if (onGround && !isCrouching && !isAttacking) {
            velocity.y = jumpForce;
            onGround = false;
            if(isPlayer) AudioManager.getInstance().playSfx(AudioId.PlayerJump, this);
            else AudioManager.getInstance().playSfx(AudioId.EnemyJump, this);
        }
    }

    // Permite caer a traves de plataformas si esta sobre una.
    public void fallThroughPlatform() {
        if (onGround && onPlatform) {
            isFallingThroughPlatform = true;
            fallThroughTimer = 0.2f;
            onGround = false;
            onPlatform = false;
        }
    }

    // Fuerza un salto con multiplicador (usado por habilidades especiales).
    public void forceJump(float forceMultiplier) {
        velocity.y = jumpForce * forceMultiplier;
        onGround = false;
    }

    // Termina la animacion de ataque.
    public void stopAttacking() {
        isAttacking = false;
        bulletInvocationPending = false;
    }

    // Establece la direccion de mirada.
    public void setSeeingRight(boolean seeingRight) {
        this.isSeeingRight = seeingRight;
    }

    // Establece la distancia al suelo (para logica de animacion).
    public void setDistanceToGround(float distance) {
        this.distanceToGround = distance;
    }

    // === GETTERS ===
    
    // Metodos de acceso a propiedades comunes.
    public float getX() { return position.x; }
    public float getY() { return position.y; }
    public Texture getTexture() {
        if (characterAnimator != null) {
            Sprite currentSprite = characterAnimator.getCurrentSprite();
            return currentSprite != null ? currentSprite.getTexture() : null;
        }
        return fallbackTexture;
    }

    public int getHealth() { return health; }
    public int getMaxHealth() { return maxHealth; }
    public boolean isMoving() { return isMoving; }
    public boolean isSeeingRight() { return isSeeingRight; }
    public boolean isAttacking() { return isAttacking; }
    public boolean isAlive() { return isAlive; }
    public boolean isKnockedBack() { return isKnockedBack; }
    public boolean isInvulnerable() { return invulnerable; }
    public boolean isFlashingTransparent() { return flashTransparent; }
    public boolean isHurt() { return isHurt; }

    // Verifica si es un jefe en fase 2 (salud <= 50%).
    public boolean isBossInPhaseTwo() {
        if (enemyType == null) return false;

        boolean isBoss = enemyType == EnemyType.BOSS_GEMINI || enemyType == EnemyType.BOSS_CHATGPT;
        if (!isBoss) return false;

        return (float) health / maxHealth <= 0.5f;
    }

    // Devuelve ancho y alto real del sprite actual o de la textura de respaldo.
    public float getWidth() { 
        if (characterAnimator != null) {
            Sprite currentSprite = characterAnimator.getCurrentSprite();
            if (currentSprite != null) {
                return currentSprite.getWidth();
            }
        } else if (fallbackTexture != null) {
            return fallbackTexture.getWidth();
        }
        return width; 
    }

    public float getHeight() { 
        if (characterAnimator != null) {
            Sprite currentSprite = characterAnimator.getCurrentSprite();
            if (currentSprite != null) {
                return currentSprite.getHeight();
            }
        } else if (fallbackTexture != null) {
            return fallbackTexture.getHeight();
        }
        return height; 
    }

    // Devuelve la hitbox actualizada.
    public Rectangle getBounds() {
        return hitbox;
    }

    public WeaponType getWeaponType() {
        return currentWeapon;
    }

    // === SETTERS ===
    public void setInvulnerable(boolean invulnerable) {
        this.invulnerable = invulnerable;
    }

    public void setInvulnerabilityTimer(float timer) {
        this.invulnerabilityTimer = timer;
    }

    public void setFlashTransparent(boolean flashTransparent) {
        this.flashTransparent = flashTransparent;
    }

    public void setTransparentFlashTimer(float redFlashTimer) {
        this.transparentFlashTimer = redFlashTimer;
    }

    public void setHurt(boolean isHurt) {
        this.isHurt = isHurt;
    }

    public void setHurtTimer(float hurtTimer) {
        this.hurtTimer = hurtTimer;
    }

    // Configura la hitbox usando datos externos o valores por defecto.
    public void setHitbox(LevelData.HitboxData hitboxData) {
        if (hitboxData != null) {
            this.hitbox.width = hitboxData.width;
            this.hitbox.height = hitboxData.height;
            this.hitboxOffsetX = hitboxData.offsetX;
            this.hitboxOffsetY = hitboxData.offsetY;
        } else {
            this.hitbox.width = getWidth();
            this.hitbox.height = getHeight();
            this.hitboxOffsetX = 0;
            this.hitboxOffsetY = 0;
        }
        this.originalHitboxHeight = this.hitbox.height;
        this.hitbox.setPosition(this.position.x + this.hitboxOffsetX, this.position.y + this.hitboxOffsetY);
    }

    // === SISTEMA DE MUERTE ===
    
    // Indica si el personaje puede ser eliminado (esta muerto y la animacion termino).
    public boolean isReadyForRemoval() {
        return !isAlive && characterAnimator != null && characterAnimator.isAnimationFinished(CharacterAnimator.AnimationState.DEAD);
    }

    // Libera recursos graficos.
    public void dispose() {
        if (characterAnimator != null) {
            // characterAnimator.dispose();
        }
        if (fallbackTexture != null) {
            fallbackTexture.dispose();
        }
    }
}