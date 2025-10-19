package com.machinehunterdev.game.FX;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.machinehunterdev.game.DamageTriggers.WeaponType;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

public class ImpactEffectManager {
    private List<ImpactEffect> activeEffects;
    private Map<WeaponType, List<Sprite>> impactFrames;
    private float frameDuration;

    public ImpactEffectManager(float frameDuration) {
        this.activeEffects = new ArrayList<>();
        this.frameDuration = frameDuration;
        this.impactFrames = new EnumMap<>(WeaponType.class);

        // Load frames for each weapon type
        impactFrames.put(WeaponType.LASER, loadSpriteFrames("FX/LaserImpact", 4));
        impactFrames.put(WeaponType.ION, loadSpriteFrames("FX/IonImpact", 4));
        impactFrames.put(WeaponType.RAILGUN, loadSpriteFrames("FX/RailgunImpact", 4));
    }

    private List<Sprite> loadSpriteFrames(String basePath, int frameCount) {
        List<Sprite> frames = new ArrayList<>();
        for (int i = 1; i <= frameCount; i++) {
            frames.add(new Sprite(new Texture(basePath + i + ".png")));
        }
        return frames;
    }

    public void createImpact(float x, float y, WeaponType weaponType) {
        List<Sprite> frames = impactFrames.get(weaponType);
        if (frames != null && !frames.isEmpty()) {
            activeEffects.add(new ImpactEffect(x, y, frames, frameDuration));
        }
    }

    public void update(float delta) {
        for (int i = activeEffects.size() - 1; i >= 0; i--) {
            ImpactEffect effect = activeEffects.get(i);
            effect.update(delta);
            if (effect.isFinished()) {
                activeEffects.remove(i);
            }
        }
    }

    public void draw(SpriteBatch batch) {
        for (ImpactEffect effect : activeEffects) {
            effect.draw(batch);
        }
    }

    public void dispose() {
        // Dispose the shared frames
        for (List<Sprite> frames : impactFrames.values()) {
            for (Sprite frame : frames) {
                frame.getTexture().dispose();
            }
        }
    }
}
