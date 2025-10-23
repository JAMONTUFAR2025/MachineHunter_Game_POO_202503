package com.machinehunterdev.game.FX;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.machinehunterdev.game.Util.SpriteAnimator;

import java.util.List;

/**
 * Efecto visual de impacto en el juego.
 * Utiliza una animación de sprites para representar el efecto.
 * 
 * @author MachineHunterDev
 */
public class ImpactEffect {
    /* Posición en dos dimensiones */
    private Vector2 position;
    /* Animador de sprites */
    private SpriteAnimator animator;
    /* Banderín para indicar si la animación ha termiando, para hacer dispose */
    private boolean isFinished;

    /**
     * Constructor del efecto de impacto.
     * @param x Posición X.
     * @param y Posición Y.
     * @param frames Lista de sprites para la animación.
     * @param frameDuration Duración de cada frame en segundos.
     */
    public ImpactEffect(float x, float y, List<Sprite> frames, float frameDuration) {
        this.position = new Vector2(x, y);
        this.animator = new SpriteAnimator(frames, frameDuration, false); // No está en bucle
        this.animator.start();
        this.isFinished = false;
    }

    /**
     * Actualiza el efecto de impacto.
     * @param delta Tiempo transcurrido desde la última actualización.
     */
    public void update(float delta) {
        animator.handleUpdate(delta);
        if (animator.isFinished()) {
            isFinished = true;
        }
    }

    /**
     * Dibuja el efecto de impacto.
     * @param batch El SpriteBatch utilizado para el dibujo.
     */
    public void draw(SpriteBatch batch) {
        if (!isFinished) {
            Sprite currentSprite = animator.getCurrentSprite();
            if (currentSprite != null) {
                currentSprite.setPosition(position.x, position.y);
                animator.draw(batch);
            }
        }
    }
    /**
     * Verifica si la animación ha terminado.
     * @return true si la animación ha terminado.
     */
    public boolean isFinished() {
        return isFinished;
    }
}
