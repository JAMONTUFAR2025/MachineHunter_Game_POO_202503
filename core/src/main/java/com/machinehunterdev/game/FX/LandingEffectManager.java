package com.machinehunterdev.game.FX;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

/**
 * Gestor para los efectos de aterrizaje en el juego.
 * Maneja la creacion, actualizacion y renderizado de efectos visuales
 * 
 * @author MachineHunterDev
 */
public class LandingEffectManager {

    /** Tipos de efectos de aterrizaje disponibles */
    public enum EffectType {
        SMOKE,
        SPARK
    }

    // Lista de efectos activos en el juego
    private List<LandingEffect> activeEffects;
    // Mapa de listas de sprites para cada tipo de efecto
    private Map<EffectType, List<Sprite>> effectFrames;
    // Duracion de cada frame en la animacion
    private float frameDuration;

    /**
     * Constructor del gestor de efectos de aterrizaje.
     * @param frameDuration Duracion de cada frame en la animacion
     */
    public LandingEffectManager(float frameDuration) {
        this.activeEffects = new ArrayList<>();
        this.frameDuration = frameDuration;
        this.effectFrames = new EnumMap<>(EffectType.class);

        effectFrames.put(EffectType.SMOKE, loadSpriteFrames("FX/Cloud", 4));
        effectFrames.put(EffectType.SPARK, loadSpriteFrames("FX/Spark", 4));
    }

    /**
     * Carga los frames de sprites desde los archivos.
     * @param basePath Ruta base de los archivos de sprite
     * @param frameCount Cantidad de frames a cargar
     * @return Lista de sprites cargados
     */
    private List<Sprite> loadSpriteFrames(String basePath, int frameCount) {
        List<Sprite> frames = new ArrayList<>();
        for (int i = 1; i <= frameCount; i++) {
            frames.add(new Sprite(new Texture(basePath + i + ".png")));
        }
        return frames;
    }

    /**
     * Crea un nuevo efecto de aterrizaje en la posicion dada.
     * @param x Coordenada X de la posicion de aterrizaje
     * @param y Coordenada Y de la posicion de aterrizaje
     * @param type Tipo de efecto de aterrizaje
     */
    public void createEffect(float x, float y, EffectType type) {
        List<Sprite> frames = effectFrames.get(type);
        if (frames != null && !frames.isEmpty()) {
            activeEffects.add(new LandingEffect(x, y, frames, frameDuration));
        }
    }

    /**
     * Actualiza todos los efectos activos.
     * @param delta Tiempo transcurrido desde la ultima actualizacion
     */
    public void update(float delta) {
        for (int i = activeEffects.size() - 1; i >= 0; i--) {
            LandingEffect effect = activeEffects.get(i);
            effect.update(delta);
            if (effect.isFinished()) {
                activeEffects.remove(i);
            }
        }
    }

    /**
     * Dibuja todos los efectos activos.
     * @param batch El SpriteBatch utilizado para el renderizado
     */
    public void draw(SpriteBatch batch) {
        for (LandingEffect effect : activeEffects) {
            effect.draw(batch);
        }
    }

    /**
     * Libera los recursos utilizados por los efectos.
     */
    public void dispose() {
        for (List<Sprite> frames : effectFrames.values()) {
            for (Sprite frame : frames) {
                frame.getTexture().dispose();
            }
        }
    }
}
