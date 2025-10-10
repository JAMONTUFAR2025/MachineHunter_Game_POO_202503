package com.machinehunterdev.game.Character;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.machinehunterdev.game.Util.SpriteAnimator;
import java.util.List;

// Clase base para todos los personajes del juego
public class Character 
{
    // Atributos comunes para todos los personajes
    public int health;
    public int width = 32;  // Ancho por defecto
    public int height = 32; // Alto por defecto

    // --- SISTEMA DE ANIMACIÓN ---
    public SpriteAnimator animator; // Reemplaza a 'texture'
    public Texture fallbackTexture; // Solo por compatibilidad (opcional)

    // ** ATRIBUTOS DE MOVIMIENTO/FISICA **
    public Vector2 position;
    public Vector2 velocity;

    public float speed = 150.0f;
    public float jumpForce = 400.0f;
    public float gravity = -1200.0f;

    // --- Nuevos atributos para empuje por daño ---
    public boolean isKnockedBack = false;
    public float knockbackSpeed = 250f;

    // Estados del personaje
    public boolean isMoving;
    public boolean isSeeingRight;
    public boolean isAttacking;
    public boolean isAlive;
    public boolean onGround;

    // Atributos de invulnerabilidad
    private boolean invulnerable = false;
    private float invulnerabilityTimer = 0f;
    private static final float INVULNERABILITY_DURATION = 5.0F; // Duración en 5 segundos

    // === Constructores ===

    // Constructor ANTIGUO (mantenido para compatibilidad con enemigos estáticos)
    public Character(int health, Texture texture, float x, float y) {
        this.health = health;
        this.fallbackTexture = texture; // Guardamos la textura
        this.position = new Vector2(x, y);
        this.velocity = new Vector2(0, 0);
        initDefaults();
    }

    // Constructor NUEVO para personajes animados
    public Character(int health, List<Sprite> idleFrames, com.badlogic.gdx.graphics.g2d.SpriteBatch batch, float x, float y) {
        this.health = health;
        this.animator = new SpriteAnimator(idleFrames, batch);
        this.animator.start();
        this.position = new Vector2(x, y);
        this.velocity = new Vector2(0, 0);
        initDefaults();
    }

    // Inicializa los valores por defecto
    private void initDefaults() {
        this.isMoving = false;
        this.isSeeingRight = true;
        this.isAttacking = false;
        this.isAlive = true;
        this.onGround = false;
    }

    // === MÉTODO DE ACTUALIZACIÓN ===
    public void update(float delta) {
        // --- 1.1. Actualizar animación ---
        if (animator != null) {
            animator.handleUpdate(delta);
        }

        // --- 1.2. Manejar invulnerabilidad ---
        if (invulnerable) {
            invulnerabilityTimer -= delta;
            if (invulnerabilityTimer <= 0) {
                invulnerable = false;
                invulnerabilityTimer = 0;
            }
        }

        // --- 2. MANEJO DE EMPUJE POR DAÑO ---
        if (isKnockedBack) {
            velocity.x = isSeeingRight ? -knockbackSpeed : knockbackSpeed;
            isMoving = true;
            onGround = false;
        }

        // --- 3. APLICAR GRAVEDAD ---
        if (!onGround) {
            velocity.y += gravity * delta;
        }

        // --- 4. ACTUALIZAR POSICIÓN ---
        position.x += velocity.x * delta;
        position.y += velocity.y * delta;

        // --- 5. LÓGICA DE MOVIMIENTO NORMAL ---
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
    }

    // === MÉTODO PARA DIBUJAR ===
    public void draw() {
        if (animator != null) {
            Sprite current = animator.getCurrentSprite();
            if (current != null) {
                // Guardar el color original
                Color originalColor = new Color(current.getColor());
                
                // Calcular alpha
                float alpha = 1.0f;
                if (invulnerable) {
                    // Parpadeo: 0.5 segundos visible, 0.5 segundos invisible
                    float blinkTime = invulnerabilityTimer % 1.0f;
                    alpha = (blinkTime < 0.5f) ? 0.7f : 0.3f;
                }

                // Aplicar posición y dirección (esto debe hacerse SIEMPRE, no solo cuando es invulnerable)
                current.setPosition(position.x, position.y);
                if (!isSeeingRight) {
                    current.setScale(-1, 1);
                    current.setPosition(position.x, position.y);
                } else {
                    current.setScale(1, 1);
                    current.setPosition(position.x, position.y);
                }

                // Aplicar transparencia y dibujar (esto debe hacerse SIEMPRE)
                current.setColor(1, 1, 1, alpha);
                current.draw(animator.getSpriteBatch());

                // Restaurar color original
                current.setColor(originalColor);
            }
        } else if (fallbackTexture != null) {
            // Dibujo de respaldo para personajes estáticos
            // Esto se manejará en GameplayState por ahora
        }
    }

    // === MÉTODOS DE ACCIÓN ===
    public void attack() { isAttacking = true; }
    
    public void takeDamage(int damage) {
        // No recibir daño si está invulnerable
        if (invulnerable) return;

        health -= damage;
        if (health <= 0) {
            isAlive = false;
            health = 0;
        }

        // Activar invulnerabilidad temporal y empuje
        invulnerable = true;
        invulnerabilityTimer = INVULNERABILITY_DURATION;
    }
    
    public void landOn(float groundY) {
        position.y = groundY;
        velocity.y = 0;
        onGround = true;
        isKnockedBack = false;
    }

    public void moveLeft() {
        if (!isKnockedBack) {
            velocity.x = -speed;
            isMoving = true;
        }
    }

    public void moveRight() {
        if (!isKnockedBack) {
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
        if (onGround) {
            velocity.y = jumpForce;
            onGround = false;
        }
    }

    public void forceJump(float forceMultiplier) {
        velocity.y = jumpForce * forceMultiplier;
        onGround = false;
    }

    // === GETTERS ===
    public float getX() { return position.x; }
    public float getY() { return position.y; }
    
    // Devuelve la textura actual (útil para enemigos estáticos o depuración)
    public Texture getTexture() {
        if (animator != null && animator.getCurrentSprite() != null) {
            return animator.getCurrentSprite().getTexture();
        }
        return fallbackTexture;
    }

    public int getHealth() { return health; }
    public boolean isMoving() { return isMoving; }
    public boolean isSeeingRight() { return isSeeingRight; }
    public boolean isAttacking() { return isAttacking; }
    public boolean isAlive() { return isAlive; }
    public boolean isKnockedBack() { return isKnockedBack; }

    // Devuelve el ancho basándose en la animación actual o en la textura de respaldo
    public float getWidth() { 
        if (animator != null && animator.getCurrentSprite() != null) {
            return animator.getCurrentSprite().getWidth();
        } else if (fallbackTexture != null) {
            return fallbackTexture.getWidth();
        }
        return width; 
    }

    // Devuelve el alto basándose en la animación actual o en la textura de respaldo
    public float getHeight() { 
        if (animator != null && animator.getCurrentSprite() != null) {
            return animator.getCurrentSprite().getHeight();
        } else if (fallbackTexture != null) {
            return fallbackTexture.getHeight();
        }
        return height; 
    }

    // Devuelve los límites del personaje para colisiones
    public Rectangle getBounds() {
        return new Rectangle(position.x, position.y, getWidth(), getHeight());
    }

    // Devuelve si el personaje es invulnerable
    public boolean isInvulnerable() {
        return invulnerable;
    }
}