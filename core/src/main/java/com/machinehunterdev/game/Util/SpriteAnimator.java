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

    private int currentFrame;        // Frame actual
    private float timer;             // Temporizador

    // Constructor
    public SpriteAnimator(List<Sprite> frames, SpriteBatch spriteBatch, float frameRate) {
        this.frames = frames;
        this.spriteBatch = spriteBatch;
        this.frameRate = frameRate;
    }

    // Constructor con valor por defecto para frameRate
    public SpriteAnimator(List<Sprite> frames, SpriteBatch spriteBatch) {
        this(frames, spriteBatch, 0.16f);
    }

    // Inicializa las variables al iniciar
    public void start() {
        currentFrame = 0; // En Java los índices empiezan en 0
        timer = 0f;
        // Nota: En libGDX no asignamos el sprite directamente al SpriteBatch
        // El sprite actual se dibujará en el método render
    }

    // Función que actualiza (llamada en el método render principal)
    public void handleUpdate(float deltaTime) {
        // Muestra los fotogramas en un bucle que se reinicia
        timer += deltaTime;
        if (timer > frameRate) {
            currentFrame = (currentFrame + 1) % frames.size();
            timer -= frameRate;
        }
    }

    // Método para dibujar el sprite actual
    public void draw() {
        if (!frames.isEmpty()) {
            frames.get(currentFrame).draw(spriteBatch);
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
}