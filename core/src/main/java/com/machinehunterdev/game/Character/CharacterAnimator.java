package com.machinehunterdev.game.Character;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.machinehunterdev.game.Util.SpriteAnimator;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Sistema de animación para personajes con los siguientes estados:
 * - Obligatorios: IDLE, RUN, DEAD
 * - Opcionales: JUMP, FALL, ATTACK
 */
public class CharacterAnimator {
    // Enum para los estados de animación
    public enum AnimationState {
        IDLE, RUN, DEAD, JUMP, FALL, ATTACK
    }

    // Mapa de animaciones
    private Map<AnimationState, SpriteAnimator> animators;
    
    // Estado actual
    private AnimationState currentState = AnimationState.IDLE;
    private AnimationState previousState = AnimationState.IDLE;
    
    // SpriteBatch para dibujar
    private SpriteBatch spriteBatch;
    
    // Dirección de mirada
    private boolean facingRight = true;

    /**
     * Constructor principal
     * @param spriteBatch SpriteBatch para renderizar
     * @param idleFrames Frames para animación idle (obligatorio)
     * @param runFrames Frames para animación run (obligatorio)
     * @param deadFrames Frames para animación dead (obligatorio)
     * @param jumpFrames Frames para animación jump (opcional, puede ser null)
     * @param fallFrames Frames para animación fall (opcional, puede ser null)
     * @param attackFrames Frames para animación attack (opcional, puede ser null)
     */
    public CharacterAnimator(
        SpriteBatch spriteBatch,
        List<Sprite> idleFrames,
        List<Sprite> runFrames,
        List<Sprite> deadFrames,
        List<Sprite> jumpFrames,
        List<Sprite> fallFrames,
        List<Sprite> attackFrames
    ) {
        this.spriteBatch = spriteBatch;
        this.animators = new HashMap<>();
        
        // Animaciones obligatorias
        this.animators.put(AnimationState.IDLE, new SpriteAnimator(idleFrames, spriteBatch));
        this.animators.put(AnimationState.RUN, new SpriteAnimator(runFrames, spriteBatch));
        
        // Animaciones opcionales
        if (deadFrames != null && !deadFrames.isEmpty()) {
            this.animators.put(AnimationState.DEAD, new SpriteAnimator(deadFrames, spriteBatch));
        }
        if (jumpFrames != null && !jumpFrames.isEmpty()) {
            this.animators.put(AnimationState.JUMP, new SpriteAnimator(jumpFrames, spriteBatch));
        }
        if (fallFrames != null && !fallFrames.isEmpty()) {
            this.animators.put(AnimationState.FALL, new SpriteAnimator(fallFrames, spriteBatch));
        }
        if (attackFrames != null && !attackFrames.isEmpty()) {
            this.animators.put(AnimationState.ATTACK, new SpriteAnimator(attackFrames, spriteBatch));
        }
        
        // Iniciar animación por defecto
        setCurrentAnimation(AnimationState.IDLE);
    }

    /**
     * Actualiza la animación actual.
     * @param deltaTime Tiempo transcurrido desde el último frame
     */
    public void update(float deltaTime) {
        SpriteAnimator currentAnimator = animators.get(currentState);
        if (currentAnimator != null) {
            currentAnimator.handleUpdate(deltaTime);
        }
    }

    /**
     * Dibuja el sprite actual en la posición especificada.
     * @param x Posición X
     * @param y Posición Y
     */
    public void draw(float x, float y) {
        SpriteAnimator currentAnimator = animators.get(currentState);
        if (currentAnimator != null) {
            Sprite currentSprite = currentAnimator.getCurrentSprite();
            if (currentSprite != null) {
                currentSprite.setPosition(x, y);
                
                // Aplicar volteo si es necesario
                if (!facingRight) {
                    currentSprite.setScale(-1, 1);
                    currentSprite.setPosition(x, y);
                } else {
                    currentSprite.setScale(1, 1);
                    currentSprite.setPosition(x, y);
                }
                
                currentSprite.draw(spriteBatch);
            }
        }
    }

    /**
     * Cambia la animación actual.
     * @param newState Nuevo estado de animación
     */
    public void setCurrentAnimation(AnimationState newState) {
        // Solo cambiar si el estado es diferente
        if (this.currentState != newState && animators.containsKey(newState)) {
            this.previousState = this.currentState;
            this.currentState = newState;
            animators.get(newState).start();
        }
    }

    /**
     * Verifica si una animación específica está disponible.
     * @param state Estado a verificar
     * @return true si la animación existe
     */
    public boolean hasAnimation(AnimationState state) {
        return animators.containsKey(state);
    }

    /**
     * Obtiene el estado de animación actual.
     */
    public AnimationState getCurrentState() {
        return currentState;
    }

    /**
     * Establece la dirección de mirada.
     * @param facingRight true si mira a la derecha, false si mira a la izquierda
     */
    public void setFacingRight(boolean facingRight) {
        this.facingRight = facingRight;
    }

    public boolean isFacingRight() {
        return facingRight;
    }

    /**
     * Obtiene el sprite actual para operaciones adicionales.
     */
    public Sprite getCurrentSprite() {
        SpriteAnimator currentAnimator = animators.get(currentState);
        return currentAnimator != null ? currentAnimator.getCurrentSprite() : null;
    }

    // Obtener SpriteBash
    public SpriteBatch getSpriteBatch() {
        return spriteBatch;
    }
}