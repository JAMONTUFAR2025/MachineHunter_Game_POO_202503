package com.machinehunterdev.game.Audio;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.utils.Timer;
import com.machinehunterdev.game.Character.Character;
import com.badlogic.gdx.graphics.OrthographicCamera;

public class AudioManager {
    private static AudioManager instance;

    private float fadeDuration = 1.0f;
    private float targetMusicVolume = 1.0f;
    private Music currentMusic;
    private String currentMusicPath;
    private boolean musicPausedForSfx = false;

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

    public void setPlayer(Character player) {
        this.player = player;
    }

    public void setCamera(OrthographicCamera camera) {
        this.camera = camera;
    }

    public void loadAssets(Array<AudioData> sfxList) {
        for (AudioData data : sfxList) {
            sfxMap.put(data.id, Gdx.audio.newSound(Gdx.files.internal(data.path)));
        }
    }

    public Sound getSound(AudioId id) {
        return sfxMap.get(id);
    }

    public void dispose() {
        for (Sound s : sfxMap.values()) s.dispose();
        if (currentMusic != null) currentMusic.dispose();
        sfxMap.clear();
        currentMusic = null;
    }

    // ✅ playSfx SIN delay automático (recomendado)
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
            float characterX = source.getX();

            if (characterX >= cameraLeft && characterX <= cameraRight) {
                Sound sound = sfxMap.get(id);
                if (sound != null) sound.play(volume);
            }
        } else {
            Sound sound = sfxMap.get(id);
            if (sound != null) sound.play(volume);
        }
    }

    // ✅ Si necesitas pausar música, hazlo MANUALMENTE desde tu juego
    // Pero si quieres automatizarlo, usa este método con CUIDADO
    public void playSfxWithMusicPause(AudioId id, float durationSeconds, Character source) {
        playSfxWithMusicPause(id, durationSeconds, source, 1.0f);
    }

    public void playSfxWithMusicPause(AudioId id, float durationSeconds, Character source, float volume) {
        if (source != null && source.isPlayer) {
            Sound sound = sfxMap.get(id);
            if (sound != null) sound.play(volume);
            return;
        }

        if (camera != null && source != null) {
            float tolerance = 40f;
            float cameraLeft = camera.position.x - camera.viewportWidth / 2 - tolerance;
            float cameraRight = camera.position.x + camera.viewportWidth / 2 + tolerance;
            float characterX = source.getX();

            if (characterX >= cameraLeft && characterX <= cameraRight) {
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

                sound.play(volume);
            }
        } else {
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

            sound.play(volume);
        }
    }

    public String getCurrentMusicPath() {
        return currentMusicPath;
    }

    public void playMusic(String path, boolean loop, boolean fade) {
        Gdx.app.log("AudioManager", "playMusic: " + path + ", loop: " + loop + ", fade: " + fade);
        if (currentMusic != null && currentMusicPath != null && currentMusicPath.equals(path)) {
            Gdx.app.log("AudioManager", "playMusic: Same music already playing, returning.");
            return; // No reiniciar la misma música
        }

        if (currentMusic != null) {
            Gdx.app.log("AudioManager", "playMusic: Stopping current music.");
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
            Gdx.app.log("AudioManager", "playMusic: No current music, loading new.");
            loadAndPlayNewMusic(path, loop, fade);
        }
    }

    private void loadAndPlayNewMusic(String path, boolean loop, boolean fade) {
        Gdx.app.log("AudioManager", "loadAndPlayNewMusic: " + path);
        currentMusic = Gdx.audio.newMusic(Gdx.files.internal(path));
        currentMusicPath = path;
        currentMusic.setLooping(loop);
        targetMusicVolume = 1.0f; // Default to full volume
        if (fade) {
            currentMusic.setVolume(0f);
            currentMusic.play();
            fadeInMusic();
        } else {
            currentMusic.setVolume(targetMusicVolume);
            currentMusic.play();
        }
        Gdx.app.log("AudioManager", "loadAndPlayNewMusic: Music started at volume: " + currentMusic.getVolume());
    }

    public void pauseMusic(boolean fade) {
        Gdx.app.log("AudioManager", "pauseMusic: fade: " + fade);
        if (currentMusic == null || !currentMusic.isPlaying()) return;
        if (fade) {
            fadeOutMusic(currentMusic::pause);
        }
        else {
            currentMusic.pause();
        }
        Gdx.app.log("AudioManager", "pauseMusic: Music paused.");
    }

    public void resumeMusic() {
        Gdx.app.log("AudioManager", "resumeMusic.");
        if (currentMusic != null && !currentMusic.isPlaying()) {
            currentMusic.play();
            restoreMusicVolume();
        }
    }

    // --- Fade usando postRunnable SIN delay (solo para bucle de fade) ---
    private void fadeInMusic() {
        Gdx.app.log("AudioManager", "fadeInMusic: Starting fade in.");
        if (currentMusic == null) return;
        float startVol = currentMusic.getVolume();
        long startTime = System.currentTimeMillis();

        Runnable fadeTask = new Runnable() {
            @Override
            public void run() {
                if (currentMusic == null) return;
                float elapsed = (System.currentTimeMillis() - startTime) / 1000f;
                float t = Math.min(elapsed / fadeDuration, 1.0f);
                float vol = startVol + (targetMusicVolume - startVol) * t;
                currentMusic.setVolume(vol);
                if (t < 1.0f) {
                    Gdx.app.postRunnable(this); // ✅ Sin delay: se llama en el próximo frame
                } else {
                    Gdx.app.log("AudioManager", "fadeInMusic: Fade in complete. Final volume: " + currentMusic.getVolume());
                }
            }
        };
        Gdx.app.postRunnable(fadeTask);
    }

    private void fadeOutMusic(Runnable onComplete) {
        Gdx.app.log("AudioManager", "fadeOutMusic: Starting fade out.");
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
                    Gdx.app.log("AudioManager", "fadeOutMusic: Fade out complete. Final volume: " + currentMusic.getVolume());
                    if (onComplete != null) onComplete.run();
                }
            }
        };
        Gdx.app.postRunnable(fadeTask);
    }

    // Getters/setters
    public void setFadeDuration(float seconds) { this.fadeDuration = seconds; }

    public void restoreMusicVolume() {
        Gdx.app.log("AudioManager", "restoreMusicVolume: Restoring to targetMusicVolume: " + targetMusicVolume);
        if (currentMusic != null) {
            currentMusic.setVolume(targetMusicVolume);
            Gdx.app.log("AudioManager", "restoreMusicVolume: Current music volume set to: " + currentMusic.getVolume());
        }
    }
    public void setMusicVolume(float volume) {
        if (currentMusic != null) currentMusic.setVolume(volume);
    }
}