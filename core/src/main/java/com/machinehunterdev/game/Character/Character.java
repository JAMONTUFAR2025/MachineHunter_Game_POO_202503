package com.machinehunterdev.game.Character;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.machinehunterdev.game.DamageTriggers.Bullet;
import com.machinehunterdev.game.DamageTriggers.WeaponType;
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
    
    public boolean isMoving;            // Está en movimiento
    public boolean isSeeingRight;       // Dirección de mirada (true = derecha)
    public boolean isAttacking;         // Está atacando
    public boolean isAlive;             // Está vivo
    public boolean onGround;            // Está en contacto con el suelo
    public boolean isOverlappingEnemy;  // Está superpuesto con un enemigo (para evitar daño continuo)
    public boolean isCrouching;         // Está agachado

    // === SISTEMA DE INVULNERABILIDAD ===
    
    /** Indica si el personaje es invulnerable */
    private boolean invulnerable = false;
    
    /** Temporizador de invulnerabilidad */
    private float invulnerabilityTimer = 0f;
    
    /** Duración de la invulnerabilidad en segundos */
    private static final float INVULNERABILITY_DURATION = 5.0F;

    // === EFECTOS VISUALES ===
    
    /** Indica si debe mostrar parpadeo rojo al recibir daño */
    private boolean flashRed = false;
    
    /** Temporizador del parpadeo rojo */
    private float redFlashTimer = 0f;

    // === SISTEMA DE ARMAS ===
    private WeaponType currentWeapon = WeaponType.LASER;
    private float laserCooldown = 0f;
    private float ionCooldown = 0f;
    private float railgunCooldown = 0f;
    private static final float RIFLE_COOLDOWN_TIME = 0.3f;    // 0.3 segundos
    private static final float SHOTGUN_COOLDOWN_TIME = 1.0f;  // 1 segundo
    private static final float SNIPER_COOLDOWN_TIME = 2.0f;   // 2 segundos

    // === CONSTRUCTORES ===

    /**
     * Constructor para personajes estáticos (sin animaciones).
     * @param health Salud inicial del personaje
     * @param texture Textura del personaje
     * @param x Posición inicial en X
     * @param y Posición inicial en Y
     */
    public Character(int health, Texture texture, float x, float y) {
        this.health = health;
        this.fallbackTexture = texture;
        this.position = new Vector2(x, y);
        this.velocity = new Vector2(0, 0);
        initDefaults();
    }

    /**
     * Constructor para personajes animados.
     * @param health Salud inicial del personaje
     * @param animator Sistema de animación
     * @param x Posición inicial en X
     * @param y Posición inicial en Y
     */
    public Character(int health, CharacterAnimator animator, float x, float y) {
        this.health = health;
        this.characterAnimator = animator;
        this.position = new Vector2(x, y);
        this.velocity = new Vector2(0, 0);
        initDefaults();
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
        // --- ACTUALIZACIÓN DE ANIMACIONES ---
        if (characterAnimator != null) {
            characterAnimator.setFacingRight(isSeeingRight);
            
            // Determinar el nuevo estado de animación
            CharacterAnimator.AnimationState currentState = characterAnimator.getCurrentState();
            CharacterAnimator.AnimationState newState = currentState;

            if (!isAlive) {
                newState = CharacterAnimator.AnimationState.DEAD;
            } else if (isKnockedBack && characterAnimator.hasAnimation(CharacterAnimator.AnimationState.HURT)) {
                newState = CharacterAnimator.AnimationState.HURT;
            } else if (isCrouching && characterAnimator.hasAnimation(CharacterAnimator.AnimationState.CROUCH)) {
                newState = CharacterAnimator.AnimationState.CROUCH;
            } else if (isAttacking && characterAnimator.hasAnimation(CharacterAnimator.AnimationState.ATTACK)) {
                newState = CharacterAnimator.AnimationState.ATTACK;
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
                }
            } else {
                // En el suelo: RUN si se mueve, IDLE si está quieto
                if (isMoving) {
                    newState = CharacterAnimator.AnimationState.RUN;
                } else {
                    newState = CharacterAnimator.AnimationState.IDLE;
                }
            }

            // Fallback para animaciones no disponibles
            if (!characterAnimator.hasAnimation(newState)) {
                if (newState == CharacterAnimator.AnimationState.JUMP || newState == CharacterAnimator.AnimationState.FALL) {
                    newState = CharacterAnimator.AnimationState.RUN;
                } else {
                    newState = CharacterAnimator.AnimationState.IDLE;
                }
            }
            
            characterAnimator.setCurrentAnimation(newState);
            characterAnimator.update(delta);
        }

        // --- MANEJO DE EFECTOS VISUALES ---
        if (flashRed) {
            redFlashTimer -= delta;
            if (redFlashTimer <= 0) {
                flashRed = false;
            }
        }

        // --- MANEJO DE INVULNERABILIDAD ---
        if (invulnerable) {
            invulnerabilityTimer -= delta;
            if (invulnerabilityTimer <= 0) {
                if (!isOverlappingEnemy) {
                    invulnerable = false;
                    invulnerabilityTimer = 0;
                }
            }
        }

        // --- MANEJO DE EMPUJE POR DAÑO ---
        if (isKnockedBack) {
            velocity.x = isSeeingRight ? -knockbackSpeed : knockbackSpeed;
            isMoving = true;
            onGround = false;
        }

        // --- APLICACIÓN DE GRAVEDAD ---
        if (!onGround) {
            velocity.y += gravity * delta;
        }

        // --- ACTUALIZACIÓN DE POSICIÓN ---
        position.x += velocity.x * delta;
        position.y += velocity.y * delta;

        // --- LÓGICA DE MOVIMIENTO NORMAL ---
        if (!isKnockedBack) {
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
        if (characterAnimator != null) {
            Sprite currentSprite = characterAnimator.getCurrentSprite();
            if (currentSprite != null) {
                Color originalColor = new Color(currentSprite.getColor());

                // Aplicar efectos visuales: parpadeo rojo > invulnerabilidad > normal
                if (flashRed) {
                    currentSprite.setColor(Color.RED);
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
                return laserCooldown <= 0f;
            case ION:
                return ionCooldown <= 0f;
            case RAILGUN:
                return railgunCooldown <= 0f;
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
        
        switch (currentWeapon) {
            case LASER:
                shootRifle(bullets);
                laserCooldown = RIFLE_COOLDOWN_TIME;
                break;
            case ION:
                shootShotgun(bullets);
                ionCooldown = SHOTGUN_COOLDOWN_TIME;
                break;
            case RAILGUN:
                shootSniper(bullets);
                railgunCooldown = SNIPER_COOLDOWN_TIME;
                break;
        }
    }

    /**
     * Dispara con rifle (bala única a larga distancia).
     * @param bullets Lista de balas activas
     */
    private void shootRifle(ArrayList<Bullet> bullets) {
        float bulletX = position.x + (isSeeingRight ? getWidth() : 0);
        float bulletY = position.y + getHeight() / 2;
        bullets.add(new Bullet(bulletX, bulletY, isSeeingRight, WeaponType.LASER));
    }

    /**
     * Dispara con escopeta (múltiples balas a corta distancia).
     * @param bullets Lista de balas activas
     */
    private void shootShotgun(ArrayList<Bullet> bullets) {
        float bulletX = position.x + (isSeeingRight ? getWidth() : 0);
        float bulletY = position.y + getHeight() / 2;
        
        // Disparar 3 balas con ligera variación en ángulo
        for (int i = -1; i <= 1; i++) {
            // Crear bala con posición ligeramente desplazada
            float spreadY = bulletY + (i * 8f); // 8px de separación vertical
            bullets.add(new Bullet(bulletX, spreadY, isSeeingRight, WeaponType.ION));
        }
    }

    /**
     * Dispara con rifle de francotirador (bala perforante).
     * @param bullets Lista de balas activas
     */
    private void shootSniper(ArrayList<Bullet> bullets) {
        float bulletX = position.x + (isSeeingRight ? getWidth() : 0);
        float bulletY = position.y + getHeight() / 2;
        bullets.add(new Bullet(bulletX, bulletY, isSeeingRight, WeaponType.RAILGUN));
    }

    /**
     * Actualiza los cooldowns de las armas.
     * @param delta Tiempo transcurrido desde el último frame
     */
    public void updateWeaponCooldowns(float delta) {
        if (laserCooldown > 0f) laserCooldown -= delta;
        if (ionCooldown > 0f) ionCooldown -= delta;
        if (railgunCooldown > 0f) railgunCooldown -= delta;
        
        // Asegurar que no sean negativos
        if (laserCooldown < 0f) laserCooldown = 0f;
        if (ionCooldown < 0f) ionCooldown = 0f;
        if (railgunCooldown < 0f) railgunCooldown = 0f;
    }

    /**
     * Aplica daño al personaje.
     * @param damage Cantidad de daño a aplicar
     */
    public void takeDamage(int damage) {
        if (invulnerable) return;

        health -= damage;
        if (health <= 0) {
            isAlive = false;
            health = 0;
        }

        // Activar parpadeo rojo visual
        flashRed = true;
        redFlashTimer = 0.1f;
    }
    
    /**
     * Hace que el personaje aterrice en una superficie.
     * @param groundY Posición Y de la superficie
     */
    public void landOn(float groundY) {
        position.y = groundY;
        velocity.y = 0;
        onGround = true;

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
        if (!isKnockedBack && !isCrouching && !isAttacking) {
            velocity.x = -speed;
            isMoving = true;
        }
    }

    public void moveRight() {
        if (!isKnockedBack && !isCrouching && !isAttacking) {
            velocity.x = speed;
            isMoving = true;
        }
    }

    public void stopMoving() {
        if (!isKnockedBack) {
            isMoving = false;
        }
    }

    public void jump() {
        if (onGround && !isCrouching && !isAttacking) {
            velocity.y = jumpForce;
            onGround = false;
        }
    }

    public void forceJump(float forceMultiplier) {
        velocity.y = jumpForce * forceMultiplier;
        onGround = false;
    }

    public void stopAttacking() {
        isAttacking = false;
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
}