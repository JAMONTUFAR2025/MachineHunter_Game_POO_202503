package com.machinehunterdev.game.Audio;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;
import com.machinehunterdev.game.Character.Character;

public class AudioManager {
    private static AudioManager instance;

    private Music currentMusic;
    private String currentMusicPath;

    // Sistema de Fade
    private float normalVolume = 0.0f;
    private float volumeBeforeFade;
    private float targetVolume;
    private float fadeTimer;
    private float fadeDuration;
    private boolean isFading;
    private Runnable onFadeComplete;

    private final ObjectMap<AudioId, Sound> sfxMap = new ObjectMap<>();
    private Character player;
    private OrthographicCamera camera;

    public static AudioManager getInstance() {
        if (instance == null) {
            instance = new AudioManager();
        }
        return instance;
    }

    private AudioManager() {}

    public void update(float delta) {
        if (isFading && currentMusic != null) {
            fadeTimer += delta;
            float progress = Math.min(fadeTimer / fadeDuration, 1.0f);
            float newVolume = volumeBeforeFade + (targetVolume - volumeBeforeFade) * progress;
            currentMusic.setVolume(newVolume);

            if (progress >= 1.0f) {
                isFading = false;
                if (onFadeComplete != null) {
                    onFadeComplete.run();
                    onFadeComplete = null;
                }
            }
        }
    }

    private void startFade(float duration, float targetVol, Runnable onComplete) {
        if (currentMusic == null) {
            if (onComplete != null) onComplete.run();
            return;
        }
        this.fadeDuration = Math.max(0.01f, duration);
        this.targetVolume = targetVol;
        this.volumeBeforeFade = currentMusic.getVolume();
        this.fadeTimer = 0;
        this.isFading = true;
        this.onFadeComplete = onComplete;
    }

    public void playMusic(String path, boolean loop, boolean fade) {
        if (currentMusic != null) {
            if (currentMusicPath != null && currentMusicPath.equals(path)) {
                return; // No reiniciar la misma música
            }
            stopMusic(fade);
        }

        currentMusic = Gdx.audio.newMusic(Gdx.files.internal(path));
        currentMusicPath = path;
        currentMusic.setLooping(loop);
        
        if (fade) {
            currentMusic.setVolume(0);
            startFade(1.0f, normalVolume, null);
        } else {
            currentMusic.setVolume(normalVolume);
        }
        currentMusic.play();
    }

    public void pauseMusic(boolean fade) {
        if (currentMusic == null) return;
        if (fade) {
            startFade(0.5f, 0.2f, null);
        } else {
            currentMusic.setVolume(0.2f);
        }
    }

    public void resumeMusic(boolean fade) {
        if (currentMusic == null) return;
        if (fade) {
            startFade(0.5f, normalVolume, null);
        } else {
            currentMusic.setVolume(normalVolume);
        }
    }

    public void stopMusic(boolean fade) {
        if (currentMusic == null) return;
        if (fade) {
            startFade(1.0f, 0.0f, () -> {
                currentMusic.stop();
                currentMusic.dispose();
                currentMusic = null;
                currentMusicPath = null;
            });
        } else {
            currentMusic.stop();
            currentMusic.dispose();
            currentMusic = null;
            currentMusicPath = null;
        }
    }

    // --- SFX Methods ---
    public void setPlayer(Character player) { this.player = player; }
    public void setCamera(OrthographicCamera camera) { this.camera = camera; }

    public void loadAssets(Array<AudioData> sfxList) {
        for (AudioData data : sfxList) {
            sfxMap.put(data.id, Gdx.audio.newSound(Gdx.files.internal(data.path)));
        }
    }

    public void playSfx(AudioId id, Character source) {
        playSfx(id, source, 1.0f);
    }

    public void playSfx(AudioId id, Character source, float volume) {
        if (source != null && source.isPlayer) {
            Sound sound = sfxMap.get(id);
            if (sound != null) sound.play(volume);
            return;
        }

        if (camera != null && source != null) {
            float tolerance = 40f;
            float cameraLeft = camera.position.x - camera.viewportWidth / 2 - tolerance;
            float cameraRight = camera.position.x + camera.viewportWidth / 2 + tolerance;
            if (source.getX() >= cameraLeft && source.getX() <= cameraRight) {
                Sound sound = sfxMap.get(id);
                if (sound != null) sound.play(volume);
            }
        } else {
            Sound sound = sfxMap.get(id);
            if (sound != null) sound.play(volume);
        }
    }

    public Sound getSound(AudioId id) {
        return sfxMap.get(id);
    }

    public void dispose() {
        for (Sound s : sfxMap.values()) s.dispose();
        if (currentMusic != null) {
            currentMusic.dispose();
        }
        sfxMap.clear();
        currentMusic = null;
    }
}