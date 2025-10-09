package com.machinehunterdev.game.Audio;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.utils.Timer;

// Enums (sin 'public' para estar en el mismo archivo)
enum AudioId {
    UISelect, NotVeryEffective, Effective, SuperEffective,
    Faint, ExpGain, ItemObtained, PokemonObtained
}

enum AudioAttackId {
    Leaf, Punch, Water, Fire, Thunder, Poison, Bug, BulkUp,
    statFell, statRose, Tackle, Psychic, Confusion, Bite, Fly,
    FlyFall, Cut, Metal, PlayRough, FireHit, Ice, Dig, Shadow,
    Spark, Falling, RockSmash, Snore
}

class AudioData {
    public final AudioId id;
    public final String path;
    public AudioData(AudioId id, String path) {
        this.id = id;
        this.path = path;
    }
}

class AudioAttackData {
    public final AudioAttackId id;
    public final String path;
    public AudioAttackData(AudioAttackId id, String path) {
        this.id = id;
        this.path = path;
    }
}

public class AudioManager {
    private static AudioManager instance;

    private float fadeDuration = 1.0f;
    private float originalMusicVolume = 1.0f;
    private Music currentMusic;
    private boolean musicPausedForSfx = false;

    private final ObjectMap<AudioId, Sound> sfxMap = new ObjectMap<>();
    private final ObjectMap<AudioAttackId, Sound> attackSfxMap = new ObjectMap<>();

    public static AudioManager getInstance() {
        if (instance == null) {
            instance = new AudioManager();
        }
        return instance;
    }

    private AudioManager() {}

    public void loadAssets(Array<AudioData> sfxList, Array<AudioAttackData> attackList) {
        for (AudioData data : sfxList) {
            sfxMap.put(data.id, Gdx.audio.newSound(Gdx.files.internal(data.path)));
        }
        for (AudioAttackData data : attackList) {
            attackSfxMap.put(data.id, Gdx.audio.newSound(Gdx.files.internal(data.path)));
        }
    }

    public void dispose() {
        for (Sound s : sfxMap.values()) s.dispose();
        for (Sound s : attackSfxMap.values()) s.dispose();
        if (currentMusic != null) currentMusic.dispose();
        sfxMap.clear();
        attackSfxMap.clear();
        currentMusic = null;
    }

    // ✅ playSfx SIN delay automático (recomendado)
    public void playSfx(AudioId id) {
        Sound sound = sfxMap.get(id);
        if (sound != null) sound.play();
    }

    // ✅ Si necesitas pausar música, hazlo MANUALMENTE desde tu juego
    // Pero si quieres automatizarlo, usa este método con CUIDADO
    public void playSfxWithMusicPause(AudioId id, float durationSeconds) {
        Sound sound = sfxMap.get(id);
        if (sound == null) return;

        if (currentMusic != null && currentMusic.isPlaying()) {
            currentMusic.pause();
            musicPausedForSfx = true;

            // ✅ Usar Timer correctamente
            Timer.schedule(new Timer.Task() {
                @Override
                public void run() {
                    if (musicPausedForSfx && currentMusic != null) {
                        currentMusic.setVolume(0f);
                        currentMusic.play();
                        fadeInMusic();
                        musicPausedForSfx = false;
                    }
                }
            }, durationSeconds); // en segundos
        }

        sound.play();
    }

    public void playAttackSfx(AudioAttackId id) {
        Sound sound = attackSfxMap.get(id);
        if (sound != null) sound.play();
    }

    public void playMusic(String path, boolean loop, boolean fade) {
        if (currentMusic != null) {
            if (fade) {
                fadeOutMusic(() -> {
                    currentMusic.dispose();
                    loadAndPlayNewMusic(path, loop, fade);
                });
            } else {
                currentMusic.stop();
                currentMusic.dispose();
                loadAndPlayNewMusic(path, loop, fade);
            }
        } else {
            loadAndPlayNewMusic(path, loop, fade);
        }
    }

    private void loadAndPlayNewMusic(String path, boolean loop, boolean fade) {
        currentMusic = Gdx.audio.newMusic(Gdx.files.internal(path));
        currentMusic.setLooping(loop);
        originalMusicVolume = 1.0f;
        if (fade) {
            currentMusic.setVolume(0f);
            currentMusic.play();
            fadeInMusic();
        } else {
            currentMusic.setVolume(originalMusicVolume);
            currentMusic.play();
        }
    }

    public void pauseMusic(boolean fade) {
        if (currentMusic == null || !currentMusic.isPlaying()) return;
        if (fade) {
            fadeOutMusic(currentMusic::pause);
        } else {
            currentMusic.pause();
        }
    }

    public void resumeMusic() {
        if (currentMusic != null && !currentMusic.isPlaying()) {
            currentMusic.play();
        }
    }

    // --- Fade usando postRunnable SIN delay (solo para bucle de fade) ---
    private void fadeInMusic() {
        if (currentMusic == null) return;
        float startVol = currentMusic.getVolume();
        long startTime = System.currentTimeMillis();

        Runnable fadeTask = new Runnable() {
            @Override
            public void run() {
                if (currentMusic == null) return;
                float elapsed = (System.currentTimeMillis() - startTime) / 1000f;
                float t = Math.min(elapsed / fadeDuration, 1.0f);
                float vol = startVol + (originalMusicVolume - startVol) * t;
                currentMusic.setVolume(vol);
                if (t < 1.0f) {
                    Gdx.app.postRunnable(this); // ✅ Sin delay: se llama en el próximo frame
                }
            }
        };
        Gdx.app.postRunnable(fadeTask);
    }

    private void fadeOutMusic(Runnable onComplete) {
        if (currentMusic == null) {
            if (onComplete != null) onComplete.run();
            return;
        }
        float startVol = currentMusic.getVolume();
        long startTime = System.currentTimeMillis();

        Runnable fadeTask = new Runnable() {
            @Override
            public void run() {
                if (currentMusic == null) {
                    if (onComplete != null) onComplete.run();
                    return;
                }
                float elapsed = (System.currentTimeMillis() - startTime) / 1000f;
                float t = Math.min(elapsed / fadeDuration, 1.0f);
                float vol = startVol * (1 - t);
                currentMusic.setVolume(vol);
                if (t < 1.0f) {
                    Gdx.app.postRunnable(this);
                } else {
                    if (onComplete != null) onComplete.run();
                }
            }
        };
        Gdx.app.postRunnable(fadeTask);
    }

    // Getters/setters
    public void setFadeDuration(float seconds) { this.fadeDuration = seconds; }
    public void setMusicVolume(float volume) {
        this.originalMusicVolume = volume;
        if (currentMusic != null) currentMusic.setVolume(volume);
    }
}