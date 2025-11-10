package com.machinehunterdev.game.FX;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.machinehunterdev.game.Util.SpriteAnimator;

import java.util.List;

/**
 * Representa un efecto visual de impacto que se reproduce una vez y luego desaparece.
 * Se utiliza para mostrar explosiones, chispas de impacto de balas, etc.
 * Cada efecto tiene su propia posicion y animacion.
 * 
 * @author MachineHunterDev
 */
public class ImpactEffect {
    
    // La posicion en el mundo donde se muestra el efecto.
    private Vector2 position;
    
    // El animador que gestiona la secuencia de sprites del efecto.
    private SpriteAnimator animator;
    
    // Bandera que indica si la animacion del efecto ha terminado.
    private boolean isFinished;

    /**
     * Constructor para crear un nuevo efecto de impacto.
     * @param x La posicion inicial en el eje X.
     * @param y La posicion inicial en el eje Y.
     * @param frames La lista de sprites que componen la animacion del efecto.
     * @param frameDuration La duracion de cada fotograma en la animacion.
     */
    public ImpactEffect(float x, float y, List<Sprite> frames, float frameDuration) {
        this.position = new Vector2(x, y);
        // Se crea un animador que no se repite (isLooping = false).
        this.animator = new SpriteAnimator(frames, frameDuration, false);
        this.animator.start();
        this.isFinished = false;
    }

    /**
     * Actualiza el estado de la animacion del efecto.
     * @param delta El tiempo transcurrido desde el ultimo fotograma.
     */
    public void update(float delta) {
        animator.handleUpdate(delta);
        // Si el animador ha terminado su secuencia, marca este efecto como finalizado.
        if (animator.isFinished()) {
            isFinished = true;
        }
    }

    /**
     * Dibuja el fotograma actual de la animacion del efecto.
     * @param batch El SpriteBatch utilizado para el renderizado.
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
     * Comprueba si la animacion del efecto ha terminado.
     * @return Verdadero si la animacion ha finalizado.
     */
    public boolean isFinished() {
        return isFinished;
    }
}
