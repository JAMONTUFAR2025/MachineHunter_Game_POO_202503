package com.machinehunterdev.game.Character;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.machinehunterdev.game.DamageTriggers.Bullet;
import com.machinehunterdev.game.DamageTriggers.WeaponType;
import com.machinehunterdev.game.DamageTriggers.DamageSystem;
import com.machinehunterdev.game.DamageTriggers.DamageType;
import com.badlogic.gdx.graphics.g2d.Sprite;
import java.util.ArrayList;

/**
 * Clase base para todos los personajes del juego (jugador, enemigos, NPCs).
 * Gestiona la lógica de movimiento, física, animaciones, daño e invulnerabilidad.
 * 
 * @author MachineHunterDev
 */
public class Character 
{
    // === ATRIBUTOS DE ESTADO ===
    
    /** Salud actual del personaje */
    public int health;
    
    /** Dimensiones por defecto del personaje */
    public int width = 32;
    public int height = 32;

    // === SISTEMA DE ANIMACIÓN ===
    
    /** Sistema avanzado de animación con múltiples estados */
    public CharacterAnimator characterAnimator;
    
    /** Textura de respaldo para personajes estáticos (compatibilidad) */
    public Texture fallbackTexture;

    // === FÍSICA Y MOVIMIENTO ===
    
    /** Posición actual del personaje (x, y) */
    public Vector2 position;
    
    /** Velocidad actual del personaje (vx, vy) */
    public Vector2 velocity;

    /** Parámetros de movimiento */
    public float speed = 150.0f;        // Velocidad horizontal normal
    public float jumpForce = 400.0f;    // Fuerza de salto
    public float gravity = -1200.0f;    // Gravedad aplicada

    // === SISTEMA DE EMPUJE POR DAÑO ===
    
    /** Indica si el personaje está en estado de empuje */
    public boolean isKnockedBack = false;
    
    /** Velocidad de empuje cuando recibe daño */
    public float knockbackSpeed = 250f;
    
    /** Distancia al suelo o plataforma más cercana */
    public float distanceToGround = Float.MAX_VALUE;

    // === ESTADOS DEL PERSONAJE ===
    
    public boolean isMoving;                    // Está en movimiento
    public boolean isSeeingRight;               // Dirección de mirada (true = derecha)
    public boolean isAttacking;                 // Está atacando
    public boolean isAlive;                     // Está vivo
    public boolean onGround;                    // Está en contacto con el suelo
    public boolean isCrouching;                 // Está agachado
    public boolean isFallingThroughPlatform = false; // Estado para ignorar plataformas temporalmente
    public boolean readyForGameOverTransition = false; // Indica si el personaje está listo para la pantalla de Game Over
    public boolean isPlayer = false;

    // === SISTEMA DE INVULNERABILIDAD ===
    
    /** Indica si el personaje es invulnerable */
    private boolean invulnerable = false;
    
    /** Temporizador de invulnerabilidad */
    private float invulnerabilityTimer = 0f;

    /** Temporizador para el estado de caída */
    private float fallThroughTimer = 0f;
    
    /** Duración de la invulnerabilidad en segundos */
    private static final float INVULNERABILITY_DURATION = 3.0F;

    // === EFECTOS VISUALES ===
    
    /** Indica si debe mostrar parpadeo transparente al recibir daño */
    private boolean flashTransparent = false;

    /** Temporizador del parpadeo transparente */
    private float transparentFlashTimer = 0f;

    // === SISTEMA DE ANIMACIÓN DE DAÑO ===
    
    /** Indica si el personaje está en estado de daño (animación HURT) */
    private boolean isHurt = false;
    private float hurtTimer = 0f;
    private boolean bulletInvocationPending = false; // Indica si hay una invocación de bala pendiente
    private WeaponType pendingWeaponType; // Tipo de arma pendiente para la invocación de bala
    private ArrayList<Bullet> pendingBulletsList; // Lista de balas pendiente para la invocación
    //private static final float HURT_DURATION = 0.3f;

    // === SISTEMA DE ARMAS ===
    private WeaponType currentWeapon = WeaponType.LASER;
    private float rifleCooldown = 0f;
    private float shotgunCooldown = 0f;
    private float sniperCooldown = 0f;
    private float thunderCooldown = 0f;
    private static final float LASER_COOLDOWN_TIME = 0.3f;    // 0.3 segundos
    private static final float ION_COOLDOWN_TIME = 0.3f;  // 0.3 segundos
    private static final float RAILGUN_COOLDOWN_TIME = 0.3f;   // 0.3 segundos
    private static final float THUNDER_COOLDOWN_TIME = 0.3f;   // 0.3 segundos
    private static final Vector2 PLAYER_BULLET_SPAWN_OFFSET = new Vector2(-10f, 22f); // Offset para que las balas del jugador no salgan del centro exacto
    private static final Vector2 ENEMY_BULLET_SPAWN_OFFSET = new Vector2(-10f, 10f); // Offset para que las balas de los enemigos no salgan del centro exacto

    // === CONSTRUCTORES ===

    /**
     * Constructor principal del personaje.
     * @param health Salud inicial del personaje
     * @param characterAnimator Animador del personaje
     * @param fallbackTexture Textura de respaldo del personaje
     * @param x Posición inicial en X
     * @param y Posición inicial en Y
     * @param isPlayer true si es el personaje jugador, false si es NPC/enemigo
     */
    public Character(int health, CharacterAnimator animator, Texture fallbackTexture, float x, float y, boolean isPlayer) {
        this.health = health;
        this.characterAnimator = animator;
        this.fallbackTexture = fallbackTexture;
        this.position = new Vector2(x, y);
        this.velocity = new Vector2(0, 0);
        this.isPlayer = isPlayer;
        initDefaults();
    }

    /**
     * Constructor simplificado del personaje.
     * @param health Salud inicial del personaje
     * @param fallbackTexture Textura de respaldo del personaje
     * @param x Posición inicial en X
     * @param y Posición inicial en Y
     */
    public Character(int health, Texture fallbackTexture, float x, float y) {
        this(health, null, fallbackTexture, x, y, false);
    }

    /**
     * Constructor simplificado del personaje.
     * @param health Salud inicial del personaje
     * @param animator Animador del personaje
     * @param x Posición inicial en X
     * @param y Posición inicial en Y
     */
    public Character(int health, CharacterAnimator animator, float x, float y) {
        this(health, animator, null, x, y, false);
    }

    /**
     * Constructor simplificado del personaje jugador.
     * @param health Salud inicial del personaje
     * @param animator Animador del personaje
     * @param x Posición inicial en X
     * @param y Posición inicial en Y
     * @param isPlayer true si es el personaje jugador, false si es NPC/enemigo
     */
    public Character(int health, CharacterAnimator animator, float x, float y, boolean isPlayer) {
        this(health, animator, null, x, y, isPlayer);
    }

    /**
     * Inicializa los valores por defecto de los estados del personaje.
     */
    private void initDefaults() {
        this.isMoving = false;
        this.isSeeingRight = true;
        this.isAttacking = false;
        this.isAlive = true;
        this.onGround = false;
    }

    // === MÉTODO DE ACTUALIZACIÓN PRINCIPAL ===
    
    /**
     * Actualiza la lógica del personaje cada frame.
     * Gestiona animaciones, física, invulnerabilidad y empuje.
     * @param delta Tiempo transcurrido desde el último frame
     */
    public void update(float delta) {
        // --- MANEJO DE EMPUJE POR DAÑO ---
        if (isKnockedBack) {
            velocity.x = isSeeingRight ? -knockbackSpeed : knockbackSpeed;
            isMoving = true;
            onGround = false;
        }

        // --- ACTUALIZACIÓN DE ANIMACIONES ---
        if (characterAnimator != null) {
            characterAnimator.setFacingRight(isSeeingRight);
            
            // Determinar el nuevo estado de animación
            CharacterAnimator.AnimationState newState;

            if (isKnockedBack && characterAnimator.hasAnimation(CharacterAnimator.AnimationState.HURT)) {
                // Prioridad máxima: Animación de empuje
                newState = CharacterAnimator.AnimationState.HURT;
            } else if (!isAlive) {
                newState = CharacterAnimator.AnimationState.DEAD;
                if (!isPlayer) {
                    velocity.x = 0;
                    velocity.y = 0;
                }
            } else { // Only determine other states if character is alive
                if (isHurt && characterAnimator.hasAnimation(CharacterAnimator.AnimationState.HURT)) {
                    // Prioridad: Animación de daño
                    newState = CharacterAnimator.AnimationState.HURT;
                } else if (isCrouching && characterAnimator.hasAnimation(CharacterAnimator.AnimationState.CROUCH)) {
                    newState = CharacterAnimator.AnimationState.CROUCH;
                } else if (isAttacking) {

                    // Gestionar animaciones de ataque del jugador
                    if (currentWeapon == WeaponType.LASER && characterAnimator.hasAnimation(CharacterAnimator.AnimationState.LASER_ATTACK)) {
                        newState = CharacterAnimator.AnimationState.LASER_ATTACK;
                    } else if (currentWeapon == WeaponType.ION && characterAnimator.hasAnimation(CharacterAnimator.AnimationState.ION_ATTACK)) {
                        newState = CharacterAnimator.AnimationState.ION_ATTACK;
                    } else if (currentWeapon == WeaponType.RAILGUN && characterAnimator.hasAnimation(CharacterAnimator.AnimationState.RAILGUN_ATTACK)) {
                        newState = CharacterAnimator.AnimationState.RAILGUN_ATTACK;
                    } else {
                        newState = CharacterAnimator.AnimationState.ATTACK;
                    }

                    // Invocar bala en el frame 1 de la animación de ataque
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
                            case THUNDER:
                                shootThunder(pendingBulletsList);
                                break;
                        }
                        bulletInvocationPending = false;
                                        }
                } else if (!onGround) {
                    // En el aire: JUMP si sube, FALL si baja
                    if (velocity.y > 0) {
                        newState = CharacterAnimator.AnimationState.JUMP;
                    } else if (velocity.y < 0) {
                        newState = CharacterAnimator.AnimationState.FALL;
                        // Forzar último frame de caída si está a punto de aterrizar
                        if (distanceToGround <= 5f) {
                            com.machinehunterdev.game.Util.SpriteAnimator fallAnimator = characterAnimator.getAnimator(CharacterAnimator.AnimationState.FALL);
                            if (fallAnimator != null) {
                                fallAnimator.setCurrentFrame(fallAnimator.getFrames().size() - 1);
                            }
                        }
                    } else { // Should not happen if !onGround, but for completeness
                        newState = CharacterAnimator.AnimationState.IDLE;
                    }
                } else {
                    // En el suelo: RUN si se mueve, IDLE si está quieto
                    if (isMoving) {
                        newState = CharacterAnimator.AnimationState.RUN;
                    } else {
                        newState = CharacterAnimator.AnimationState.IDLE;
                    }
                }
            }

            // Fallback para animaciones no disponibles
            if (!characterAnimator.hasAnimation(newState)) {
                if ((newState == CharacterAnimator.AnimationState.JUMP || newState == CharacterAnimator.AnimationState.FALL || newState == CharacterAnimator.AnimationState.RUN) && characterAnimator.hasAnimation(CharacterAnimator.AnimationState.IDLE)) {
                    newState = CharacterAnimator.AnimationState.IDLE; // Si no hay animaciones de movimiento/salto/caída, usar IDLE
                } else {
                    newState = CharacterAnimator.AnimationState.IDLE; // Fallback general a IDLE
                }
            }
            
            characterAnimator.setCurrentAnimation(newState);
            characterAnimator.update(delta);
        }

        // --- MANEJO DE EFECTOS VISUALES ---
        if (flashTransparent) {
            transparentFlashTimer -= delta;
            if (transparentFlashTimer <= 0) {
                flashTransparent = false;
            }
        }

        // --- MANEJO DE INVULNERABILIDAD ---
        if (invulnerable) {
            invulnerabilityTimer -= delta;
            if (invulnerabilityTimer <= 0) {
                invulnerable = false;
                invulnerabilityTimer = 0;
            }
        }

        // --- MANEJO DEL ESTADO DE DAÑO ---
        if (isHurt) {
            hurtTimer -= delta;
            velocity.x = 0; // Detener movimiento horizontal
            if (hurtTimer <= 0) {
                isHurt = false;
                hurtTimer = 0;
            }
        }

        // --- MANEJO DE CAÍDA A TRAVÉS DE PLATAFORMAS ---
        if (isFallingThroughPlatform) {
            fallThroughTimer -= delta;
            if (fallThroughTimer <= 0) {
                isFallingThroughPlatform = false;
                fallThroughTimer = 0;
            }
        }


        // --- APLICACIÓN DE GRAVEDAD ---
        if (!onGround) {
            velocity.y += gravity * delta;
        }

        // --- ACTUALIZACIÓN DE POSICIÓN ---
        position.x += velocity.x * delta;
        position.y += velocity.y * delta;

        // --- LÓGICA DE MOVIMIENTO NORMAL ---
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

        // --- ACTUALIZACIÓN DE COOLDOWNS DE ARMAS ---
        updateWeaponCooldowns(delta);
    }

    // === MÉTODO DE RENDERIZADO ===
    
    /**
     * Dibuja el personaje con efectos visuales apropiados.
     * @param spriteBatch SpriteBatch para renderizar
     */
    public void draw(SpriteBatch spriteBatch) {
        if (!isAlive && (characterAnimator == null || characterAnimator.isAnimationFinished(CharacterAnimator.AnimationState.DEAD))) {
            return; // No dibujar si está muerto y la animación ha terminado
        }

        if (characterAnimator != null) {
            Sprite currentSprite = characterAnimator.getCurrentSprite();
            if (currentSprite != null) {
                Color originalColor = new Color(currentSprite.getColor());

                // Aplicar efectos visuales: parpadeo transparente > invulnerabilidad > normal
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

    // === MÉTODOS DE ACCIÓN ===
    
    /**
     * Inicia la animación de ataque.
     */
    public void attack() { 
        isAttacking = true; 
    }

    /**
     * Cambia el arma actual del jugador.
     * @param weaponType Nuevo tipo de arma
     */
    public void switchWeapon(WeaponType weaponType) {
        this.currentWeapon = weaponType;
    }

    /**
     * Obtiene el arma actual del jugador.
     * @return Tipo de arma actual
     */
    public WeaponType getCurrentWeapon() {
        return currentWeapon;
    }

    /**
     * Verifica si el arma actual está lista para disparar.
     * @return true si puede disparar
     */
    public boolean canShoot() {
        switch (currentWeapon) {
            case LASER:
                return rifleCooldown <= 0f;
            case ION:
                return shotgunCooldown <= 0f;
            case RAILGUN:
                return sniperCooldown <= 0f;
            case THUNDER:
                return thunderCooldown <= 0f;
            default:
                return true;
        }
    }

    /**
     * Dispara con el arma actual.
     * @param bullets Lista de balas activas
     */
    public void shoot(ArrayList<Bullet> bullets) {
        if (!canShoot() || !onGround) return;
        
        isAttacking = true;
        bulletInvocationPending = true;
        pendingWeaponType = currentWeapon;
        pendingBulletsList = bullets;

        // Aplicar cooldowns inmediatamente
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
            case THUNDER:
                thunderCooldown = THUNDER_COOLDOWN_TIME;
                break;
        }
    }

    /* Nota: Ajustar la posición de la bala para que se alinee correctamente con el sprite del personaje, -8 es una solucion temporal */

    private void shootThunder(ArrayList<Bullet> bullets) {
        float bulletX = position.x + (isSeeingRight ? getWidth() + ENEMY_BULLET_SPAWN_OFFSET.x - 8: 0 - ENEMY_BULLET_SPAWN_OFFSET.x);
        float bulletY = position.y + getHeight() - ENEMY_BULLET_SPAWN_OFFSET.y;
        bullets.add(new Bullet(bulletX, bulletY, isSeeingRight, WeaponType.THUNDER, this));
    }

    /**
     * Dispara con rifle (bala única a larga distancia).
     * @param bullets Lista de balas activas
     */
    private void shootRifle(ArrayList<Bullet> bullets) {
        float bulletX = position.x + (isSeeingRight ? getWidth() + PLAYER_BULLET_SPAWN_OFFSET.x - 8: 0 - PLAYER_BULLET_SPAWN_OFFSET.x);
        float bulletY = position.y + getHeight() - PLAYER_BULLET_SPAWN_OFFSET.y;
        bullets.add(new Bullet(bulletX, bulletY, isSeeingRight, WeaponType.LASER, this));
    }

    /**
     * Dispara con escopeta (múltiples balas a corta distancia).
     * @param bullets Lista de balas activas
     */
    private void shootShotgun(ArrayList<Bullet> bullets) {
        float bulletX = position.x + (isSeeingRight ? getWidth() + PLAYER_BULLET_SPAWN_OFFSET.x - 8: 0 - PLAYER_BULLET_SPAWN_OFFSET.x);
        float bulletY = position.y + getHeight() - PLAYER_BULLET_SPAWN_OFFSET.y;
        
        // Disparar 3 balas con ligera variación en ángulo
        for (int i = -1; i <= 1; i++) {
            // Crear bala con posición ligeramente desplazada
            float spreadY = bulletY + (i * 8f); // 8px de separación vertical
            bullets.add(new Bullet(bulletX, spreadY, isSeeingRight, WeaponType.ION, this));
        }
    }

    /**
     * Dispara con rifle de francotirador (bala perforante).
     * @param bullets Lista de balas activas
     */
    private void shootSniper(ArrayList<Bullet> bullets) {
        float bulletX = position.x + (isSeeingRight ? getWidth() + PLAYER_BULLET_SPAWN_OFFSET.x - 8: 0 - PLAYER_BULLET_SPAWN_OFFSET.x);
        float bulletY = position.y + getHeight() - PLAYER_BULLET_SPAWN_OFFSET.y;
        bullets.add(new Bullet(bulletX, bulletY, isSeeingRight, WeaponType.RAILGUN, this));
    }

    /**
     * Actualiza los cooldowns de las armas.
     * @param delta Tiempo transcurrido desde el último frame
     */
    public void updateWeaponCooldowns(float delta) {
        if (rifleCooldown > 0f) rifleCooldown -= delta;
        if (shotgunCooldown > 0f) shotgunCooldown -= delta;
        if (sniperCooldown > 0f) sniperCooldown -= delta;
        if (thunderCooldown > 0f) thunderCooldown -= delta;
        
        // Asegurar que no sean negativos
        if (rifleCooldown < 0f) rifleCooldown = 0f;
        if (shotgunCooldown < 0f) shotgunCooldown = 0f;
        if (sniperCooldown < 0f) sniperCooldown = 0f;
        if (thunderCooldown < 0f) thunderCooldown = 0f;
    }

    // === MÉTODOS DE DAÑO (usando el sistema centralizado) ===
    
    /**
     * Método heredado para compatibilidad (usa el nuevo sistema DamageSystem).
     * @param damage Cantidad de daño
     */
    public void takeDamage(int damage) {
        DamageSystem.applyDamageNoKnockback(this, damage, DamageType.CONTACT);
    }

    /**
     * Aplica daño sin empuje usando el sistema centralizado.
     * @param damage Cantidad de daño
     */
    public void takeDamageNoKnockback(int damage) {
        DamageSystem.applyDamageNoKnockback(this, damage, DamageType.PROJECTILE);
    }

    /**
     * Aplica daño por contacto usando el sistema centralizado.
     * @param source Personaje que causa el daño
     * @param damage Cantidad de daño
     */
    public void takeContactDamage(Character source, int damage) {
        DamageSystem.applyContactDamage(this, source, damage);
    }

    /**
     * Aplica daño sin activar invulnerabilidad.
     * @param damage Cantidad de daño
     */
    public void takeDamageWithoutVulnerability(int damage) {
        DamageSystem.applyDamageWithoutInvulnerability(this, damage, false);
    }
    
    /**
     * Hace que el personaje aterrice en una superficie.
     * @param groundY Posición Y de la superficie
     */
    public void landOn(float groundY) {
        position.y = groundY;
        velocity.y = 0;
        onGround = true;

        if (!isAlive) {
            readyForGameOverTransition = true;
        }

        // Activar invulnerabilidad al aterrizar después de empuje
        if (isKnockedBack) {
            invulnerable = true;
            invulnerabilityTimer = INVULNERABILITY_DURATION;
        }

        isKnockedBack = false;
    }

    /**
     * Establece el estado de agachado.
     * @param crouching true para agacharse, false para levantarse
     */
    public void setCrouching(boolean crouching) {
        isCrouching = crouching;
    }

    // === MÉTODOS DE MOVIMIENTO ===
    
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

    public void stopMoving() {
        if (!isKnockedBack && !isHurt) {
            isMoving = false;
        }
    }

    public void jump() {
        if (onGround && !isCrouching && !isAttacking) {
            velocity.y = jumpForce;
            onGround = false;
        }
    }

    public void fallThroughPlatform() {
        if (onGround) {
            isFallingThroughPlatform = true;
            fallThroughTimer = 0.2f; // Ignorar plataformas por 0.2 segundos
            onGround = false;
        }
    }

    public void forceJump(float forceMultiplier) {
        velocity.y = jumpForce * forceMultiplier;
        onGround = false;
    }

    public void stopAttacking() {
        isAttacking = false;
        bulletInvocationPending = false;
    }

    public void setSeeingRight(boolean seeingRight) {
        this.isSeeingRight = seeingRight;
    }

    public void setDistanceToGround(float distance) {
        this.distanceToGround = distance;
    }

    // === GETTERS ===
    
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
    public boolean isMoving() { return isMoving; }
    public boolean isSeeingRight() { return isSeeingRight; }
    public boolean isAttacking() { return isAttacking; }
    public boolean isAlive() { return isAlive; }
    public boolean isKnockedBack() { return isKnockedBack; }
    public boolean isInvulnerable() { return invulnerable; }
    public boolean isFlashingTransparent() { return flashTransparent; }
    public boolean isHurt() { return isHurt; }

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

    public Rectangle getBounds() {
        return new Rectangle(position.x, position.y, getWidth(), getHeight());
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

    // === SISTEMA DE MUERTE ===
    /**
     * Verifica si el personaje ha terminado su animación de muerte y está listo para ser removido.
     * @return true si el personaje está muerto y la animación ha terminado.
     */
    public boolean isReadyForRemoval() {
        return !isAlive && characterAnimator != null && characterAnimator.isAnimationFinished(CharacterAnimator.AnimationState.DEAD);
    }

    /**
     * Libera los recursos del personaje.
     */
    public void dispose() {
        if (characterAnimator != null) {
            // characterAnimator.dispose(); // Asumiendo que CharacterAnimator tiene un método dispose
        }
        if (fallbackTexture != null) {
            fallbackTexture.dispose();
        }
    }
}