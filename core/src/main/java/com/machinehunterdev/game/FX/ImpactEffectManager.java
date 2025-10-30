package com.machinehunterdev.game.FX;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.machinehunterdev.game.DamageTriggers.WeaponType;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

/**
 * Gestor para efectos de impacto en el juego.
 * Administra la creación, actualización y dibujo de múltiples efectos de impacto.
 * 
 * @author MachineHunterDev
 */
public class ImpactEffectManager {
    /* Lista de efectos activos */
    private List<ImpactEffect> activeEffects;
    /* Diccionario que contiene los frames de impacto */
    private Map<WeaponType, List<Sprite>> impactFrames;
    /* Duración entre cada frame */
    private float frameDuration;

    /**
     * Constructor del gestor de efectos de impacto.
     * @param frameDuration Duración entre cada frame de la animación.
     */
    public ImpactEffectManager(float frameDuration) {
        this.activeEffects = new ArrayList<>();
        this.frameDuration = frameDuration;
        this.impactFrames = new EnumMap<>(WeaponType.class);

        // Carga los frames de impacto para cada tipo de arma
        impactFrames.put(WeaponType.LASER, loadSpriteFrames("FX/LaserImpact", 4));
        impactFrames.put(WeaponType.ION, loadSpriteFrames("FX/IonImpact", 4));
        impactFrames.put(WeaponType.RAILGUN, loadSpriteFrames("FX/RailgunImpact", 4));
        impactFrames.put(WeaponType.SHOOTER, loadSpriteFrames("FX/ShooterImpact", 4));
        impactFrames.put(WeaponType.PATROLLER, loadSpriteFrames("FX/PatrollerImpact", 4));
        impactFrames.put(WeaponType.FLYING, loadSpriteFrames("FX/FlyingImpact", 4));
    }

    /**
     * Carga los frames de sprites desde los archivos.
     * @param basePath Ruta base de los archivos de sprites.
     * @param frameCount Número de frames a cargar.
     * @return Lista de sprites cargados.
     */
    private List<Sprite> loadSpriteFrames(String basePath, int frameCount) {
        List<Sprite> frames = new ArrayList<>();
        for (int i = 1; i <= frameCount; i++) {
            frames.add(new Sprite(new Texture(basePath + i + ".png")));
        }
        return frames;
    }

    /**
     * Crea un nuevo efecto de impacto en la posición dada.
     * @param x Posición X.
     * @param y Posición Y.
     * @param weaponType Tipo de arma que causó el impacto.
     */
    public void createImpact(float x, float y, WeaponType weaponType) {
        List<Sprite> frames = impactFrames.get(weaponType);
        if (frames != null && !frames.isEmpty()) {
            activeEffects.add(new ImpactEffect(x, y, frames, frameDuration));
        }
    }

    /**
     * Actualiza todos los efectos de impacto activos.
     * @param delta Tiempo transcurrido desde la última actualización.
     */
    public void update(float delta) {
        for (int i = activeEffects.size() - 1; i >= 0; i--) {
            ImpactEffect effect = activeEffects.get(i);
            effect.update(delta);
            if (effect.isFinished()) {
                activeEffects.remove(i);
            }
        }
    }

    /**
     * Dibuja todos los efectos de impacto activos.
     * @param batch El SpriteBatch utilizado para el dibujo.
     */
    public void draw(SpriteBatch batch) {
        for (ImpactEffect effect : activeEffects) {
            effect.draw(batch);
        }
    }

    /**
     * Libera los recursos utilizados por los efectos de impacto.
     */
    public void dispose() {
        // Dispose the shared frames
        for (List<Sprite> frames : impactFrames.values()) {
            for (Sprite frame : frames) {
                frame.getTexture().dispose();
            }
        }
    }
}
