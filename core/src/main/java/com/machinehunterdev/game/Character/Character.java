package com.machinehunterdev.game.Character;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.machinehunterdev.game.Util.SpriteAnimator;

// Clase base para todos los personajes del juego
public class Character 
{
    // Atributos comunes para todos los personajes
    public int health;
    public int width = 32;  // Ancho por defecto
    public int height = 32; // Alto por defecto

    // --- SISTEMA DE ANIMACIÓN ---
    public CharacterAnimator characterAnimator; // Sistema de animación avanzado
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
    public Character(int health, CharacterAnimator animator, float x, float y) {
        this.health = health;
        this.characterAnimator = animator;
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
        // --- 1. Actualizar animación ---
        if (characterAnimator != null) {
            // Actualizar dirección de mirada
            characterAnimator.setFacingRight(isSeeingRight);
            
            // Determinar estado de animación
            CharacterAnimator.AnimationState newState = CharacterAnimator.AnimationState.IDLE;
            
            if (!isAlive) {
                newState = CharacterAnimator.AnimationState.DEAD;
            } else if (isAttacking && characterAnimator.hasAnimation(CharacterAnimator.AnimationState.ATTACK)) {
                newState = CharacterAnimator.AnimationState.ATTACK;
            } else if (!onGround) {
                // En el aire
                if (velocity.y > 0) {
                    // Subiendo (saltando)
                    if (characterAnimator.hasAnimation(CharacterAnimator.AnimationState.JUMP)) {
                        newState = CharacterAnimator.AnimationState.JUMP;
                    } else {
                        newState = CharacterAnimator.AnimationState.RUN; // fallback
                    }
                } else {
                    // Cayendo
                    if (characterAnimator.hasAnimation(CharacterAnimator.AnimationState.FALL)) {
                        newState = CharacterAnimator.AnimationState.FALL;
                    } else {
                        newState = CharacterAnimator.AnimationState.RUN; // fallback
                    }
                }
            } else if (isMoving) {
                newState = CharacterAnimator.AnimationState.RUN;
            }
            
            characterAnimator.setCurrentAnimation(newState);
            characterAnimator.update(delta);
        }

        // --- 2. Manejar invulnerabilidad ---
        if (invulnerable) {
            invulnerabilityTimer -= delta;
            if (invulnerabilityTimer <= 0) {
                invulnerable = false;
                invulnerabilityTimer = 0;
            }
        }

        // --- 3. MANEJO DE EMPUJE POR DAÑO ---
        if (isKnockedBack) {
            velocity.x = isSeeingRight ? -knockbackSpeed : knockbackSpeed;
            isMoving = true;
            onGround = false;
        }

        // --- 4. APLICAR GRAVEDAD ---
        if (!onGround) {
            velocity.y += gravity * delta;
        }

        // --- 5. ACTUALIZAR POSICIÓN ---
        position.x += velocity.x * delta;
        position.y += velocity.y * delta;

        // --- 6. LÓGICA DE MOVIMIENTO NORMAL ---
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
        if (characterAnimator != null) {
            Sprite currentSprite = characterAnimator.getCurrentSprite();
            if (currentSprite != null) {
                // Aplicar transparencia aquí
                Color originalColor = new Color(currentSprite.getColor());
                float alpha = 1.0f;
                
                if (invulnerable) {
                    float blinkTime = invulnerabilityTimer % 1.0f;
                    alpha = (blinkTime < 0.5f) ? 0.7f : 0.3f;
                }
                
                // Dibujar manualmente con transparencia
                currentSprite.setPosition(position.x, position.y);
                if (!isSeeingRight) {
                    currentSprite.setScale(-1, 1);
                    currentSprite.setPosition(position.x, position.y);
                } else {
                    currentSprite.setScale(1, 1);
                    currentSprite.setPosition(position.x, position.y);
                }
                
                currentSprite.setColor(1, 1, 1, alpha);
                currentSprite.draw(characterAnimator.getSpriteBatch());
                currentSprite.setColor(originalColor);
            }
        } else if (fallbackTexture != null) {
            // Dibujo estático
        }
    }

    // === MÉTODOS DE ACCIÓN ===
    public void attack() { 
        isAttacking = true; 
        // La animación de ataque se manejará en update()
    }
    
    public void takeDamage(int damage) {
        if (invulnerable) return;

        health -= damage;
        if (health <= 0) {
            isAlive = false;
            health = 0;
        }

        // Activar invulnerabilidad temporal
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

    // Devuelve el ancho basándose en la animación actual o en la textura de respaldo
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

    // Devuelve el alto basándose en la animación actual o en la textura de respaldo
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

    // Devuelve los límites del personaje para colisiones
    public Rectangle getBounds() {
        return new Rectangle(position.x, position.y, getWidth(), getHeight());
    }
}