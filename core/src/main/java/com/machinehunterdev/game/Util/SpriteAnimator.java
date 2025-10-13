package com.machinehunterdev.game.Util;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import java.util.List;

// -- CLASE PARA ANIMAR SPRITES (FOTOGRAMAS) --
public class SpriteAnimator
{
    private SpriteBatch spriteBatch; // En libGDX usamos SpriteBatch para renderizar
    private List<Sprite> frames;     // Lista de fotogramas
    private float frameRate;         // Tiempo entre frames (en segundos)
    private boolean loop;            // Si la animación debe repetirse
    private boolean excludeLastFrame; // Si se debe excluir el último frame en la animación normal

    private int currentFrame;        // Frame actual
    private float timer;             // Temporizador
    private boolean finished;        // Si la animación ha terminado (para animaciones no loopeadas)

    // Constructor principal
    public SpriteAnimator(List<Sprite> frames, SpriteBatch spriteBatch, float frameRate, boolean loop, boolean excludeLastFrame) {
        this.frames = frames;
        this.spriteBatch = spriteBatch;
        this.frameRate = frameRate;
        this.loop = loop;
        this.excludeLastFrame = excludeLastFrame;
    }

    // Constructor sin excludeLastFrame (por defecto false)
    public SpriteAnimator(List<Sprite> frames, SpriteBatch spriteBatch, float frameRate, boolean loop) {
        this(frames, spriteBatch, frameRate, loop, false);
    }

    // Constructor con valor por defecto para frameRate y loop
    public SpriteAnimator(List<Sprite> frames, SpriteBatch spriteBatch, float frameRate) {
        this(frames, spriteBatch, frameRate, true, false);
    }

    // Constructor con valor por defecto para frameRate
    public SpriteAnimator(List<Sprite> frames, SpriteBatch spriteBatch) {
        this(frames, spriteBatch, 0.16f, true, false);
    }

    // Inicializa las variables al iniciar
    public void start() {
        currentFrame = 0;
        timer = 0f;
        finished = false;
    }

    // Función que actualiza (llamada en el método render principal)
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

    // Método para dibujar el sprite actual
    public void draw() {
        if (!frames.isEmpty()) {
            frames.get(currentFrame).draw(spriteBatch);
        }
    }

    public void setCurrentFrame(int frameIndex) {
        if (frameIndex >= 0 && frameIndex < frames.size()) {
            this.currentFrame = frameIndex;
        }
    }

    // Getter para frames
    public List<Sprite> getFrames() {
        return frames;
    }

    // Getter para obtener el sprite actual
    public Sprite getCurrentSprite() {
        if (frames.isEmpty()) return null;
        return frames.get(currentFrame);
    }

    // Getter para el SpriteBatch (útil si se necesita en otra clase)
    public SpriteBatch getSpriteBatch() {
        return spriteBatch;
    }

    // Para saber si una animación no loopeada ha terminado
    public boolean isFinished() {
        return finished;
    }
}