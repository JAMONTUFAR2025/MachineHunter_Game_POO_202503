package com.machinehunterdev.game.Character;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.graphics.g2d.Sprite;

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

    public float distanceToGround = Float.MAX_VALUE; // Distancia al suelo

    // Estados del personaje
    public boolean isMoving;
    public boolean isSeeingRight;
    public boolean isAttacking;
    public boolean isAlive;
    public boolean onGround;
    public boolean isOverlappingEnemy = false; // Para evitar daño continuo

    // Atributos de invulnerabilidad
    private boolean invulnerable = false;
    private float invulnerabilityTimer = 0f;
    private static final float INVULNERABILITY_DURATION = 5.0F; // Duración en 5 segundos

    // Atributos para el parpadeo rojo al recibir daño
    private boolean flashRed = false;
    private float redFlashTimer = 0f;

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
            CharacterAnimator.AnimationState currentState = characterAnimator.getCurrentState();
            CharacterAnimator.AnimationState newState = currentState;
            com.machinehunterdev.game.Util.SpriteAnimator currentAnimator = characterAnimator.getAnimator(currentState);

            if (!isAlive) {
                newState = CharacterAnimator.AnimationState.DEAD;
            } else if (isKnockedBack && characterAnimator.hasAnimation(CharacterAnimator.AnimationState.HURT)) {
                newState = CharacterAnimator.AnimationState.HURT;
            } else if (isAttacking && characterAnimator.hasAnimation(CharacterAnimator.AnimationState.ATTACK)) {
                newState = CharacterAnimator.AnimationState.ATTACK;
            } else if (!onGround) {
                // En el aire: JUMP si sube, FALL si baja
                if (velocity.y > 0) {
                    newState = CharacterAnimator.AnimationState.JUMP;
                } else if (velocity.y < 0) {
                    newState = CharacterAnimator.AnimationState.FALL;

                    // Si está a punto de aterrizar, forzar el último frame de la animación de caída
                    if (distanceToGround <= 5f) {
                        com.machinehunterdev.game.Util.SpriteAnimator fallAnimator = characterAnimator.getAnimator(CharacterAnimator.AnimationState.FALL);
                        if (fallAnimator != null) {
                            fallAnimator.setCurrentFrame(fallAnimator.getFrames().size() - 1);
                        }
                    }
                }
                // Si velocity.y es 0, mantenemos el estado actual (JUMP o FALL)
                
            } else {
                // En el suelo: RUN si se mueve, IDLE si está quieto
                if (isMoving) {
                    newState = CharacterAnimator.AnimationState.RUN;
                } else {
                    newState = CharacterAnimator.AnimationState.IDLE;
                }
            }

            // Fallback para animaciones que no existen
            if (!characterAnimator.hasAnimation(newState)) {
                if (newState == CharacterAnimator.AnimationState.JUMP || newState == CharacterAnimator.AnimationState.FALL) {
                    newState = CharacterAnimator.AnimationState.RUN; // Usar RUN como alternativa
                } else {
                    newState = CharacterAnimator.AnimationState.IDLE; // Default a IDLE
                }
            }
            
            characterAnimator.setCurrentAnimation(newState);
            characterAnimator.update(delta);
        }

        // --- Manejar temporizador de parpadeo rojo ---
        if (flashRed) {
            redFlashTimer -= delta;
            if (redFlashTimer <= 0) {
                flashRed = false;
            }
        }

        // --- 2. Manejar invulnerabilidad ---
        if (invulnerable) {
            invulnerabilityTimer -= delta;
            if (invulnerabilityTimer <= 0) {
                // Solo desactivar invulnerabilidad si no está superpuesto con un enemigo
                if (!isOverlappingEnemy) {
                    invulnerable = false;
                    invulnerabilityTimer = 0;
                }
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
    public void draw(SpriteBatch spriteBatch) {
        if (characterAnimator != null) {
            Sprite currentSprite = characterAnimator.getCurrentSprite();
            if (currentSprite != null) {
                // Guardar color original para restaurarlo después
                Color originalColor = new Color(currentSprite.getColor());

                // Lógica de color: parpadeo rojo o parpadeo de invulnerabilidad
                if (flashRed) {
                    // 1. Prioridad: Parpadeo rojo al recibir daño
                    currentSprite.setColor(Color.RED);
                } else if (invulnerable) {
                    // 2. Si no hay parpadeo rojo, aplicar parpadeo de invulnerabilidad
                    float blinkTime = invulnerabilityTimer % 1.0f;
                    float alpha = (blinkTime < 0.5f) ? 0.7f : 0.3f;
                    currentSprite.setColor(1, 1, 1, alpha);
                } else {
                    // 3. Sin efectos, color normal
                    currentSprite.setColor(1, 1, 1, 1);
                }

                // Dibujar el sprite con la posición y escala correctas
                characterAnimator.draw(position.x, position.y, spriteBatch);

                // Restaurar el color original del sprite
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

        // Activar parpadeo rojo en lugar de invulnerabilidad inmediata
        flashRed = true;
        redFlashTimer = 0.1f;
    }
    
    public void landOn(float groundY) {
        position.y = groundY;
        velocity.y = 0;
        onGround = true;

        // Si el personaje estaba en retroceso (knockback), ahora se vuelve invulnerable
        if (isKnockedBack) {
            invulnerable = true;
            invulnerabilityTimer = INVULNERABILITY_DURATION;
        }

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

    public void setDistanceToGround(float distance) {
        this.distanceToGround = distance;
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