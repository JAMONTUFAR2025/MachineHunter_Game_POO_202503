package com.machinehunterdev.game.Character;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.machinehunterdev.game.Util.SpriteAnimator;

/**
 * Sistema de animación para personajes con soporte para múltiples estados.
 * Gestiona animaciones de idle, run, dead, jump, fall, attack, hurt y crouch.
 * 
 * @author MachineHunterDev
 */
public class CharacterAnimator {
    /**
     * Enumeración de estados de animación soportados.
     */
    public enum AnimationState {
        IDLE, RUN, DEAD, JUMP, FALL, ATTACK, LASER_ATTACK, ION_ATTACK, RAILGUN_ATTACK, HURT, CROUCH, IDLE_RAGE, ATTACK1, ATTACK2, SUMMON
    }

    /** Mapa que asocia estados de animación con sus respectivos animadores */
    private Map<AnimationState, SpriteAnimator> animators;
    
    /** Estado de animación actual */
    private AnimationState currentState = AnimationState.IDLE;
    
    /** Estado de animación anterior */
    //private AnimationState previousState = AnimationState.IDLE;
    
    /** Dirección de mirada del personaje */
    private boolean facingRight = true;

    /**
     * Constructor principal del sistema de animación.
     * @param idleFrames Lista de sprites para la animación de idle. (obligatorio)
     * @param runFrames Lista de sprites para la animación de run. (opcional)
     * @param deadFrames Lista de sprites para la animación de dead. (opcional)
     * @param jumpFrames Lista de sprites para la animación de jump. (opcional)
     * @param fallFrames Lista de sprites para la animación de fall. (opcional)
     * @param attackFrames Lista de sprites para la animación de attack. (opcional)
     * @param attackLaserFrames Lista de sprites para la animación de laser attack. (opcional)
     * @param attackIonFrames Lista de sprites para la animación de ion attack. (opcional)
     * @param attackRailgunFrames Lista de sprites para la animación de railgun attack. (opcional)
     * @param hurtFrames Lista de sprites para la animación de hurt. (opcional)
     * @param crouchFrames Lista de sprites para la animación de crouch. (opcional)
     */
    public CharacterAnimator(
        List<Sprite> idleFrames,
        List<Sprite> runFrames,
        List<Sprite> deadFrames,
        List<Sprite> jumpFrames,
        List<Sprite> fallFrames,
        List<Sprite> attackFrames,
        List<Sprite> attackLaserFrames,
        List<Sprite> attackIonFrames,
        List<Sprite> attackRailgunFrames,
        List<Sprite> hurtFrames,
        List<Sprite> crouchFrames,
        List<Sprite> idleRageFrames,
        List<Sprite> attack1Frames,
        List<Sprite> attack2Frames,
        List<Sprite> summonFrames
    ) {
        this.animators = new HashMap<>();
        
        // Inicializar animaciones obligatorias
        if (idleFrames != null && !idleFrames.isEmpty()) {
            this.animators.put(AnimationState.IDLE, new SpriteAnimator(idleFrames));
        }
        
        // Inicializar animaciones opcionales con configuraciones específicas
        if (runFrames != null && !runFrames.isEmpty()) {
            this.animators.put(AnimationState.RUN, new SpriteAnimator(runFrames));
        }
        if (deadFrames != null && !deadFrames.isEmpty()) {
            this.animators.put(AnimationState.DEAD, new SpriteAnimator(deadFrames, 0.15f, false));
        }
        if (jumpFrames != null && !jumpFrames.isEmpty()) {
            this.animators.put(AnimationState.JUMP, new SpriteAnimator(jumpFrames, 0.16f, false));
        }
        if (fallFrames != null && !fallFrames.isEmpty()) {
            this.animators.put(AnimationState.FALL, new SpriteAnimator(fallFrames, 0.16f, false));
        }
        if (attackFrames != null && !attackFrames.isEmpty()) {
            this.animators.put(AnimationState.ATTACK, new SpriteAnimator(attackFrames, 0.15f));
        }
        if (attackLaserFrames != null && !attackLaserFrames.isEmpty()) {
            this.animators.put(AnimationState.LASER_ATTACK, new SpriteAnimator(attackLaserFrames, 0.15f));
        }
        if (attackIonFrames != null && !attackIonFrames.isEmpty()) {
            this.animators.put(AnimationState.ION_ATTACK, new SpriteAnimator(attackIonFrames, 0.15f));
        }
        if (attackRailgunFrames != null && !attackRailgunFrames.isEmpty()) {
            this.animators.put(AnimationState.RAILGUN_ATTACK, new SpriteAnimator(attackRailgunFrames, 0.15f));
        }
        if (hurtFrames != null && !hurtFrames.isEmpty()) {
            this.animators.put(AnimationState.HURT, new SpriteAnimator(hurtFrames, 0.1f, false));
        }
        if (crouchFrames != null && !crouchFrames.isEmpty()) {
            this.animators.put(AnimationState.CROUCH, new SpriteAnimator(crouchFrames));
        }
        if (idleRageFrames != null && !idleRageFrames.isEmpty()) {
            this.animators.put(AnimationState.IDLE_RAGE, new SpriteAnimator(idleRageFrames));
        }
        if (attack1Frames != null && !attack1Frames.isEmpty()) {
            this.animators.put(AnimationState.ATTACK1, new SpriteAnimator(attack1Frames, 0.15f));
        }
        if (attack2Frames != null && !attack2Frames.isEmpty()) {
            this.animators.put(AnimationState.ATTACK2, new SpriteAnimator(attack2Frames, 0.15f));
        }
        if (summonFrames != null && !summonFrames.isEmpty()) {
            this.animators.put(AnimationState.SUMMON, new SpriteAnimator(summonFrames, 0.15f));
        }  

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
     * @param spriteBatch SpriteBatch para renderizar
     */
    public void draw(float x, float y, SpriteBatch spriteBatch) {
        SpriteAnimator currentAnimator = animators.get(currentState);
        if (currentAnimator != null) {
            Sprite currentSprite = currentAnimator.getCurrentSprite();
            if (currentSprite != null) {
                currentSprite.setPosition(x, y);
                
                if (!facingRight) {
                    currentSprite.setScale(-1, 1);
                    currentSprite.setPosition(x, y);
                } else {
                    currentSprite.setScale(1, 1);
                    currentSprite.setPosition(x, y);
                }
                
                currentAnimator.draw(spriteBatch);
            }
        }
    }

    /**
     * Cambia la animación actual.
     * @param newState Nuevo estado de animación
     */
    public void setCurrentAnimation(AnimationState newState) {
        if (this.currentState != newState && animators.containsKey(newState)) {
            //this.previousState = this.currentState;
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
     * Obtiene el SpriteAnimator para un estado específico.
     * @param state Estado de animación
     * @return SpriteAnimator correspondiente o null si no existe
     */
    public SpriteAnimator getAnimator(AnimationState state) {
        return animators.get(state);
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

    /**
     * Verifica si el personaje está mirando a la derecha.
     * @return true si mira a la derecha
     */
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

    /**
     * Obtiene el índice del frame actual de la animación.
     * @return Índice del frame actual o 0 si no hay animador.
     */
    public int getCurrentFrameIndex() {
        SpriteAnimator currentAnimator = animators.get(currentState);
        return currentAnimator != null ? currentAnimator.getCurrentFrameIndex() : 0;
    }

    /**
     * Verifica si una animación específica ha terminado de reproducirse (solo para animaciones no cíclicas).
     * @param state Estado de animación a verificar.
     * @return true si la animación ha terminado, false en caso contrario o si es cíclica.
     */
    public boolean isAnimationFinished(AnimationState state) {
        SpriteAnimator animator = animators.get(state);
        return animator != null && animator.isFinished();
    }

    /**
     * Libera todos los recursos de los animadores.
     */
    public void dispose() {
        for (SpriteAnimator animator : animators.values()) {
            animator.dispose();
        }
    }

}