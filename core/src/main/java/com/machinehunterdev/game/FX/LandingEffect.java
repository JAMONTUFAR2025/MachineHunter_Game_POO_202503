package com.machinehunterdev.game.FX;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.machinehunterdev.game.Util.SpriteAnimator;

import java.util.List;

/**
 * Efecto visual que se muestra cuando un personaje aterriza en el suelo.
 * Crea una animacion de destellos en la posicion de aterrizaje.
 * 
 * @author MachineHunterDev
 */
public class LandingEffect {
    // Posicion del efecto de aterrizaje
    private Vector2 position;
    // Animador para manejar la animacion del efecto
    private SpriteAnimator animator;
    // Indica si la animacion ha terminado
    private boolean isFinished;

    /**
     * Constructor del efecto de aterrizaje.
     * @param x Coordenada X de la posicion de aterrizaje
     * @param y Coordenada Y de la posicion de aterrizaje
     * @param frames Lista de frames de la animacion
     * @param frameDuration Duracion de cada frame en la animacion
     */
    public LandingEffect(float x, float y, List<Sprite> frames, float frameDuration) {
        this.position = new Vector2(x, y);
        this.animator = new SpriteAnimator(frames, frameDuration, false);
        this.animator.start();
        this.isFinished = false;
    }

    /**
     * Actualiza la animacion del efecto.
     * @param delta Tiempo transcurrido desde la ultima actualizacion
     */
    public void update(float delta) {
        animator.handleUpdate(delta);
        if (animator.isFinished()) {
            isFinished = true;
        }
    }

    /**
     * Dibuja el efecto de aterrizaje.
     * @param batch El SpriteBatch utilizado para el renderizado
     */
    public void draw(SpriteBatch batch) {
        if (!isFinished) {
            Sprite currentSprite = animator.getCurrentSprite();
            if (currentSprite != null) {
                float drawX = position.x - currentSprite.getWidth() / 2;
                float drawY = position.y;
                currentSprite.setPosition(drawX, drawY);

                // Guarda el color original del sprite
                Color originalSpriteColor = currentSprite.getColor();
                // Establece el alpha del sprite a 50%
                currentSprite.setColor(originalSpriteColor.r, originalSpriteColor.g, originalSpriteColor.b, 0.5f);

                animator.draw(batch);

                // Restaura el color original del sprite
                currentSprite.setColor(originalSpriteColor);
            }
        }
    }
    
    /**
     * Indica si la animacion ha terminado.
     * @return true si la animacion ha terminado, false en caso contrario
     */
    public boolean isFinished() {
        return isFinished;
    }
}
