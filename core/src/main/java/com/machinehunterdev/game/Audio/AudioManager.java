package com.machinehunterdev.game.Audio;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;
import com.machinehunterdev.game.Character.Character;

// Clase singleton que gestiona la reproduccion de musica y efectos de sonido.
// Maneja volumenes, fundidos (fade), y reproduce sonidos solo si estan cerca de la camara.
public class AudioManager {
    // Instancia unica del gestor de audio (patron singleton).
    private static AudioManager instance;

    // Musica actualmente en reproduccion.
    private Music currentMusic;
    // Ruta del archivo de la musica actual.
    private String currentMusicPath;
    // Indica si la musica esta en pausa.
    private boolean isMusicPaused = false;

    // Sistema de fundido (fade) para la musica.
    private float musicVolume = 0.2f;        // Volumen base de la musica.
    private float soundVolume = 0.5f;        // Volumen base de los efectos de sonido.
    private float volumeBeforeFade;          // Volumen antes de iniciar un fundido.
    private float targetVolume;              // Volumen objetivo del fundido.
    private float fadeTimer;                 // Temporizador del fundido.
    private float fadeDuration;              // Duracion total del fundido.
    private boolean isFading;                // Indica si se esta realizando un fundido.
    private Runnable onFadeComplete;         // Accion a ejecutar al terminar el fundido.

    // Mapa que asocia cada AudioId con su respectivo efecto de sonido cargado.
    private final ObjectMap<AudioId, Sound> sfxMap = new ObjectMap<>();
    // Referencia al personaje jugador (para audio espacial).
    private Character player;
    // Camara del juego (para determinar visibilidad de sonidos).
    private OrthographicCamera camera;

    // Devuelve la unica instancia del gestor de audio.
    public static AudioManager getInstance() {
        if (instance == null) {
            instance = new AudioManager();
        }
        return instance;
    }

    // Constructor privado para asegurar el patron singleton.
    private AudioManager() {}

    // Establece el volumen de la musica.
    public void setMusicVolume(float volume) {
        this.musicVolume = volume;
        if (currentMusic != null) {
            currentMusic.setVolume(musicVolume);
        }
        isFading = false;
    }

    // Establece el volumen de los efectos de sonido.
    public void setSoundVolume(float volume) {
        this.soundVolume = volume;
    }

    // Devuelve el volumen actual de la musica.
    public float getMusicVolume() {
        return musicVolume;
    }

    // Devuelve el volumen actual de los efectos de sonido.
    public float getSoundVolume() {
        return soundVolume;
    }

    // Actualiza el estado del fundido cada frame.
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

    // Inicia un fundido de volumen en la musica actual.
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

    // Reproduce una pista de musica.
    // path: ruta del archivo.
    // loop: indica si debe repetirse.
    // fade: indica si debe iniciar con fundido.
    public void playMusic(String path, boolean loop, boolean fade) {
        if (currentMusic != null) {
            if (currentMusicPath != null && currentMusicPath.equals(path)) {
                return; // No reiniciar la misma musica.
            }
            stopMusic(fade);
        }

        currentMusic = Gdx.audio.newMusic(Gdx.files.internal(path));
        currentMusicPath = path;
        currentMusic.setLooping(loop);
        isMusicPaused = false;
        
        if (fade) {
            currentMusic.setVolume(0);
            startFade(1.0f, musicVolume, null);
        } else {
            currentMusic.setVolume(musicVolume);
        }
        currentMusic.play();
    }

    // Pausa la musica actual.
    public void pauseMusic(boolean fade) {
        if (currentMusic == null) return;
        isMusicPaused = true;
        if (fade) {
            startFade(0.5f, 0.2f * musicVolume, null);
        } else {
            currentMusic.setVolume(0.2f * musicVolume);
        }
    }

    // Reanuda la musica pausada.
    public void resumeMusic(boolean fade) {
        if (currentMusic == null) return;
        isMusicPaused = false;
        if (fade) {
            startFade(0.5f, musicVolume, null);
        } else {
            currentMusic.setVolume(musicVolume);
        }
    }

    // Detiene la musica actual.
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

    // --- Metodos para efectos de sonido (SFX) ---

    // Establece la referencia al jugador.
    public void setPlayer(Character player) { this.player = player; }
    // Establece la camara del juego.
    public void setCamera(OrthographicCamera camera) { this.camera = camera; }

    // Carga una lista de efectos de sonido en memoria.
    public void loadAssets(Array<AudioData> sfxList) {
        for (AudioData data : sfxList) {
            sfxMap.put(data.id, Gdx.audio.newSound(Gdx.files.internal(data.path)));
        }
    }

    // Reproduce un efecto de sonido asociado a un personaje.
    public void playSfx(AudioId id, Character source) {
        playSfx(id, source, soundVolume);
    }

    // Version sobrecargada que permite especificar un volumen adicional.
    public void playSfx(AudioId id, Character source, float volume) {
        // Si el sonido proviene del jugador, se reproduce siempre.
        if (source != null && source.isPlayer) {
            Sound sound = sfxMap.get(id);
            if (sound != null) sound.play(soundVolume * volume);
            return;
        }

        // Si hay camara y el sonido proviene de otro personaje,
        // se reproduce solo si esta dentro del area visible (con tolerancia).
        if (camera != null && source != null) {
            float tolerance = 40f;
            float cameraLeft = camera.position.x - camera.viewportWidth / 2 - tolerance;
            float cameraRight = camera.position.x + camera.viewportWidth / 2 + tolerance;
            if (source.getX() >= cameraLeft && source.getX() <= cameraRight) {
                Sound sound = sfxMap.get(id);
                if (sound != null) sound.play(soundVolume * volume);
            }
        } else {
            // Si no hay camara o fuente, se reproduce sin restricciones.
            Sound sound = sfxMap.get(id);
            if (sound != null) sound.play(soundVolume * volume);
        }
    }

    // Devuelve el objeto Sound asociado a un AudioId (para uso avanzado).
    public Sound getSound(AudioId id) {
        return sfxMap.get(id);
    }

    // Libera todos los recursos de audio al cerrar el juego o cambiar de pantalla.
    public void dispose() {
        for (Sound s : sfxMap.values()) s.dispose();
        if (currentMusic != null) {
            currentMusic.dispose();
        }
        sfxMap.clear();
        currentMusic = null;
    }
}