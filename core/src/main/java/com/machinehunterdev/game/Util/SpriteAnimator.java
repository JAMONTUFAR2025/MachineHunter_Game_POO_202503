package com.machinehunterdev.game.Util;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import java.util.List;

/**
 * Clase para animar secuencias de sprites (fotogramas).
 * Soporta animaciones con loop, sin loop, y exclusión del último frame.
 * 
 * @author MachineHunterDev
 */
public class SpriteAnimator
{
    /** Lista de fotogramas que componen la animación */
    private List<Sprite> frames;
    
    /** Tiempo entre frames en segundos */
    private float frameRate;
    
    /** Indica si la animación debe repetirse indefinidamente */
    private boolean loop;

    /** Frame actual que se está mostrando */
    private int currentFrame;
    
    /** Temporizador para controlar la velocidad de la animación */
    private float timer;
    
    /** Indica si una animación sin loop ha terminado */
    private boolean finished;

    /**
     * Constructor principal con todos los parámetros.
     * @param frames Lista de fotogramas de la animación
     * @param frameRate Tiempo entre frames en segundos
     * @param loop Indica si la animación debe repetirse
     */
    public SpriteAnimator(List<Sprite> frames, float frameRate, boolean loop) {
        this.frames = frames;
        this.frameRate = frameRate;
        this.loop = loop;
    }

    /**
     * Constructor con valores por defecto para loop (true)
     * @param frames Lista de fotogramas de la animación
     * @param frameRate Tiempo entre frames en segundos
     */
    public SpriteAnimator(List<Sprite> frames, float frameRate) {
        this(frames, frameRate, true);
    }

    /**
     * Constructor con valores por defecto para frameRate (0.16f) y loop (true).
     * @param frames Lista de fotogramas de la animación
     */
    public SpriteAnimator(List<Sprite> frames) {
        this(frames, 0.16f, true);
    }

    /**
     * Inicializa la animación al estado inicial.
     */
    public void start() {
        currentFrame = 0;
        timer = 0f;
        finished = false;
    }

    /**
     * Actualiza la animación cada frame.
     * @param deltaTime Tiempo transcurrido desde el último frame
     */
    public void handleUpdate(float deltaTime) {
        if (finished) return; // No actualizar si ya terminó

        timer += deltaTime;
        if (timer > frameRate) {
            // Determinar el límite de frames a animar
            int frameLimit = frames.size();
            if (frameLimit <= 0) frameLimit = 1; // Evitar división por cero o negativo

            if (loop) {
                currentFrame = (currentFrame + 1) % frameLimit;
            } else {
                if (currentFrame < frameLimit - 1) {
                    currentFrame++;
                } else {
                    finished = true;
                }
            }
            timer -= frameRate;
        }
    }

    /**
     * Dibuja el sprite actual de la animación.
     * @param spriteBatch SpriteBatch para renderizado
     */
    public void draw(SpriteBatch spriteBatch) {
        if (!frames.isEmpty()) {
            frames.get(currentFrame).draw(spriteBatch);
        }
    }

    /**
     * Establece el frame actual de la animación.
     * @param frameIndex Índice del frame a establecer
     */
    public void setCurrentFrame(int frameIndex) {
        if (frameIndex >= 0 && frameIndex < frames.size()) {
            this.currentFrame = frameIndex;
        }
    }

    /**
     * Obtiene la lista de frames de la animación.
     * @return Lista de frames
     */
    public List<Sprite> getFrames() {
        return frames;
    }

    /**
     * Obtiene el sprite actual de la animación.
     * @return Sprite actual o null si no hay frames
     */
    public Sprite getCurrentSprite() {
        if (frames.isEmpty()) return null;
        return frames.get(currentFrame);
    }
    
    /**
     * Obtiene el índice del frame actual.
     * @return Índice del frame actual
     */
    public int getCurrentFrameIndex() {
        return currentFrame;
    }

    /**
     * Verifica si una animación sin loop ha terminado.
     * @return true si la animación ha terminado
     */
    public boolean isFinished() {
        return finished;
    }

    /**
     * Libera los recursos de las texturas de los sprites.
     */
    public void dispose() {
        for (Sprite sprite : frames) {
            if (sprite.getTexture() != null) {
                sprite.getTexture().dispose();
            }
        }
    }
}