package com.machinehunterdev.game.Character;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.machinehunterdev.game.Util.SpriteAnimator;

/**
 * Sistema de animacion para personajes que soporta multiples estados.
 * Esta clase actua como una maquina de estados para las animaciones de un personaje,
 * permitiendo transiciones suaves y gestionando diferentes clips de animacion
 * como reposo, correr, saltar, atacar, etc.
 * 
 * @author MachineHunterDev
 */
public class CharacterAnimator {
    /**
     * Enumeracion de todos los posibles estados de animacion que un personaje puede tener.
     * Cada estado corresponde a una secuencia de sprites.
     */
    public enum AnimationState {
        IDLE,           // Reposo
        RUN,            // Correr
        DEAD,           // Muerte
        JUMP,           // Salto
        FALL,           // Caida
        ATTACK,         // Ataque generico
        LASER_ATTACK,   // Ataque con laser
        ION_ATTACK,     // Ataque con ion
        RAILGUN_ATTACK, // Ataque con railgun
        HURT,           // Recibiendo dano
        ANGRY_HURT,     // Recibiendo dano en modo furia (para jefes)
        CROUCH,         // Agachado
        IDLE_RAGE,      // Reposo en modo furia (para jefes)
        ATTACK1,        // Ataque especial 1 (para jefes)
        ATTACK2,        // Ataque especial 2 (para jefes)
        SUMMON          // Invocacion (para jefes o enemigos especiales)
    }

    // Un mapa que asocia cada estado de animacion con su respectivo objeto SpriteAnimator.
    private Map<AnimationState, SpriteAnimator> animators;
    
    // El estado de animacion actual que se esta reproduciendo.
    private AnimationState currentState = AnimationState.IDLE;
    
    // Almacena el estado de animacion anterior, util para reanudar animaciones interrumpidas.
    private AnimationState previousState = AnimationState.IDLE;
    
    // Guarda el indice del fotograma en el que una animacion fue interrumpida.
    private int interruptedFrame = 0;
    
    // La direccion en la que el personaje esta mirando (derecha o izquierda).
    private boolean facingRight = true;

    /**
     * Constructor principal del sistema de animacion.
     * Recibe listas de sprites para cada estado de animacion posible.
     * @param idleFrames Lista de sprites para la animacion de reposo (obligatorio).
     * @param runFrames Lista de sprites para la animacion de correr.
     * @param deadFrames Lista de sprites para la animacion de muerte.
     * @param jumpFrames Lista de sprites para la animacion de salto.
     * @param fallFrames Lista de sprites para la animacion de caida.
     * @param attackFrames Lista de sprites para la animacion de ataque generico.
     * @param attackLaserFrames Lista de sprites para la animacion de ataque con laser.
     * @param attackIonFrames Lista de sprites para la animacion de ataque con ion.
     * @param attackRailgunFrames Lista de sprites para la animacion de ataque con railgun.
     * @param hurtFrames Lista de sprites para la animacion de recibir dano.
     * @param angryHurtFrames Lista de sprites para la animacion de recibir dano en modo furia.
     * @param crouchFrames Lista de sprites para la animacion de agacharse.
     * @param idleRageFrames Lista de sprites para la animacion de reposo en modo furia.
     * @param attack1Frames Lista de sprites para la animacion de ataque especial 1.
     * @param attack2Frames Lista de sprites para la animacion de ataque especial 2.
     * @param summonFrames Lista de sprites para la animacion de invocacion.
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
        List<Sprite> angryHurtFrames,
        List<Sprite> crouchFrames,
        List<Sprite> idleRageFrames,
        List<Sprite> attack1Frames,
        List<Sprite> attack2Frames,
        List<Sprite> summonFrames
    ) {
        this.animators = new HashMap<>();
        
        // Inicializa la animacion de reposo, que es obligatoria.
        if (idleFrames != null && !idleFrames.isEmpty()) {
            this.animators.put(AnimationState.IDLE, new SpriteAnimator(idleFrames));
        }
        
        // Inicializa las animaciones opcionales, configurando su velocidad y si se repiten.
        if (runFrames != null && !runFrames.isEmpty()) {
            this.animators.put(AnimationState.RUN, new SpriteAnimator(runFrames));
        }
        if (deadFrames != null && !deadFrames.isEmpty()) {
            this.animators.put(AnimationState.DEAD, new SpriteAnimator(deadFrames, 0.15f, false)); // No se repite
        }
        if (jumpFrames != null && !jumpFrames.isEmpty()) {
            this.animators.put(AnimationState.JUMP, new SpriteAnimator(jumpFrames, 0.16f, false)); // No se repite
        }
        if (fallFrames != null && !fallFrames.isEmpty()) {
            this.animators.put(AnimationState.FALL, new SpriteAnimator(fallFrames, 0.16f, false)); // No se repite
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
            this.animators.put(AnimationState.HURT, new SpriteAnimator(hurtFrames, 0.1f, false)); // No se repite
        }
        if (angryHurtFrames != null && !angryHurtFrames.isEmpty()) {
            this.animators.put(AnimationState.ANGRY_HURT, new SpriteAnimator(angryHurtFrames, 0.1f, false)); // No se repite
        }
        if (crouchFrames != null && !crouchFrames.isEmpty()) {
            this.animators.put(AnimationState.CROUCH, new SpriteAnimator(crouchFrames));
        }
        if (idleRageFrames != null && !idleRageFrames.isEmpty()) {
            this.animators.put(AnimationState.IDLE_RAGE, new SpriteAnimator(idleRageFrames));
        }
        if (attack1Frames != null && !attack1Frames.isEmpty()) {
            this.animators.put(AnimationState.ATTACK1, new SpriteAnimator(attack1Frames, 0.15f, false)); // No se repite
        }
        if (attack2Frames != null && !attack2Frames.isEmpty()) {
            this.animators.put(AnimationState.ATTACK2, new SpriteAnimator(attack2Frames, 0.15f, false)); // No se repite
        }
        if (summonFrames != null && !summonFrames.isEmpty()) {
            this.animators.put(AnimationState.SUMMON, new SpriteAnimator(summonFrames, 0.15f, false)); // No se repite
        }  

        // Establece la animacion inicial.
        setCurrentAnimation(AnimationState.IDLE);
    }

    /**
     * Actualiza la logica de la animacion actual.
     * @param deltaTime El tiempo transcurrido desde el ultimo fotograma.
     */
    public void update(float deltaTime) {
        SpriteAnimator currentAnimator = animators.get(currentState);
        if (currentAnimator != null) {
            currentAnimator.handleUpdate(deltaTime);
        }
    }

    /**
     * Dibuja el sprite actual de la animacion en la posicion especificada.
     * @param x La posicion en el eje X donde se dibujara el sprite.
     * @param y La posicion en el eje Y donde se dibujara el sprite.
     * @param spriteBatch El SpriteBatch utilizado para el renderizado.
     */
    public void draw(float x, float y, SpriteBatch spriteBatch) {
        SpriteAnimator currentAnimator = animators.get(currentState);
        if (currentAnimator != null) {
            Sprite currentSprite = currentAnimator.getCurrentSprite();
            if (currentSprite != null) {
                float drawX = x;
                if (currentState == AnimationState.DEAD) {
                    drawX -= 10f; // Ajuste para centrar la animación de muerte (100px) en el cuerpo de 80px
                }
                currentSprite.setPosition(drawX, y);
                
                // Voltea el sprite horizontalmente si el personaje no mira a la derecha.
                if (!facingRight) {
                    currentSprite.setScale(-1, 1);
                    currentSprite.setPosition(drawX, y);
                } else {
                    currentSprite.setScale(1, 1);
                    currentSprite.setPosition(drawX, y);
                }
                
                currentAnimator.draw(spriteBatch);
            }
        }
    }

    /**
     * Cambia la animacion actual a un nuevo estado.
     * @param newState El nuevo estado de animacion a establecer.
     */
    public void setCurrentAnimation(AnimationState newState) {
        // Solo cambia la animacion si el nuevo estado existe y es diferente al actual.
        if (animators.containsKey(newState)) {
            if (this.currentState != newState) {
                // Si la nueva animacion es 'HURT', guarda el estado anterior para poder reanudarlo.
                if (newState == AnimationState.HURT) {
                    this.previousState = this.currentState;
                    SpriteAnimator interruptedAnimator = animators.get(this.currentState);
                    if (interruptedAnimator != null) {
                        this.interruptedFrame = interruptedAnimator.getCurrentFrameIndex();
                    }
                }
                this.currentState = newState;
                // Inicia la nueva animacion desde el principio.
                animators.get(newState).start();
            }
        }
    }

    /**
     * Reanuda una animacion que fue interrumpida, comenzando desde el fotograma guardado.
     * @param resumeState El estado de animacion que se debe reanudar.
     */
    public void resumeAnimation(AnimationState resumeState) {
        if (animators.containsKey(resumeState)) {
            this.currentState = resumeState;
            SpriteAnimator animator = animators.get(resumeState);
            if (animator != null) {
                animator.start(); // Reinicia el temporizador y la bandera 'finished'.
                animator.setCurrentFrame(this.interruptedFrame); // Establece el fotograma interrumpido.
            }
        }
    }

    /**
     * Comprueba si una animacion para un estado especifico esta disponible.
     * @param state El estado de animacion a verificar.
     * @return Verdadero si la animacion existe, falso en caso contrario.
     */
    public boolean hasAnimation(AnimationState state) {
        return animators.containsKey(state);
    }

    /**
     * Obtiene el objeto SpriteAnimator para un estado de animacion especifico.
     * @param state El estado de animacion deseado.
     * @return El SpriteAnimator correspondiente, o null si no existe.
     */
    public SpriteAnimator getAnimator(AnimationState state) {
        return animators.get(state);
    }

    /**
     * Obtiene el estado de animacion actual.
     * @return El estado de animacion actual.
     */
    public AnimationState getCurrentState() {
        return currentState;
    }

    /**
     * Obtiene el estado de animacion anterior.
     * @return El estado de animacion previo.
     */
    public AnimationState getPreviousState() {
        return previousState;
    }

    /**
     * Establece la direccion en la que mira el personaje.
     * @param facingRight Verdadero si mira a la derecha, falso si mira a la izquierda.
     */
    public void setFacingRight(boolean facingRight) {
        this.facingRight = facingRight;
    }

    /**
     * Comprueba si el personaje esta mirando a la derecha.
     * @return Verdadero si esta mirando a la derecha.
     */
    public boolean isFacingRight() {
        return facingRight;
    }

    /**
     * Obtiene el sprite actual de la animacion en curso.
     * @return El sprite actual, o null si no hay animacion.
     */
    public Sprite getCurrentSprite() {
        SpriteAnimator currentAnimator = animators.get(currentState);
        return currentAnimator != null ? currentAnimator.getCurrentSprite() : null;
    }

    /**
     * Obtiene el indice del fotograma actual de la animacion en curso.
     * @return El indice del fotograma actual, o 0 si no hay animador.
     */
    public int getCurrentFrameIndex() {
        SpriteAnimator currentAnimator = animators.get(currentState);
        return currentAnimator != null ? currentAnimator.getCurrentFrameIndex() : 0;
    }

    /**
     * Comprueba si una animacion especifica ha terminado de reproducirse.
     * Solo es relevante para animaciones que no se repiten (no ciclicas).
     * @param state El estado de animacion a verificar.
     * @return Verdadero si la animacion ha terminado, falso en caso contrario.
     */
    public boolean isAnimationFinished(AnimationState state) {
        SpriteAnimator animator = animators.get(state);
        return animator != null && animator.isFinished();
    }

    /**
     * Libera todos los recursos (texturas) utilizados por los animadores.
     * Es importante llamar a este metodo para evitar fugas de memoria.
     */
    public void dispose() {
        for (SpriteAnimator animator : animators.values()) {
            animator.dispose();
        }
    }

}