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
    
    /** Indica si se debe excluir el último frame en la animación normal */
    private boolean excludeLastFrame;

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
     * @param excludeLastFrame Indica si se excluye el último frame
     */
    public SpriteAnimator(List<Sprite> frames, float frameRate, boolean loop, boolean excludeLastFrame) {
        this.frames = frames;
        this.frameRate = frameRate;
        this.loop = loop;
        this.excludeLastFrame = excludeLastFrame;
    }

    /**
     * Constructor sin parámetro excludeLastFrame (por defecto false).
     * @param frames Lista de fotogramas de la animación
     * @param frameRate Tiempo entre frames en segundos
     * @param loop Indica si la animación debe repetirse
     */
    public SpriteAnimator(List<Sprite> frames, float frameRate, boolean loop) {
        this(frames, frameRate, loop, false);
    }

    /**
     * Constructor con valores por defecto para loop (true) y excludeLastFrame (false).
     * @param frames Lista de fotogramas de la animación
     * @param frameRate Tiempo entre frames en segundos
     */
    public SpriteAnimator(List<Sprite> frames, float frameRate) {
        this(frames, frameRate, true, false);
    }

    /**
     * Constructor con valores por defecto para frameRate (0.16f), loop (true) y excludeLastFrame (false).
     * @param frames Lista de fotogramas de la animación
     */
    public SpriteAnimator(List<Sprite> frames) {
        this(frames, 0.16f, true, false);
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
            int frameLimit = excludeLastFrame ? frames.size() - 1 : frames.size();
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
     * Verifica si una animación sin loop ha terminado.
     * @return true si la animación ha terminado
     */
    public boolean isFinished() {
        return finished;
    }
}