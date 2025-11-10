package com.machinehunterdev.game.GameStates;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Preferences;
import com.machinehunterdev.game.Audio.AudioId;
import com.machinehunterdev.game.Audio.AudioManager;
import com.machinehunterdev.game.GameController;
import com.machinehunterdev.game.UI.OptionUI;
import com.machinehunterdev.game.Util.IState;

/**
 * Representa el estado del menu de opciones del juego.
 * En este estado, el jugador puede ajustar configuraciones como el volumen
 * de la musica y de los efectos de sonido.
 * 
 * @author MachineHunterDev
 */
public class OptionState implements IState<GameController> {
    
    /** Instancia unica de este estado (patron Singleton). */
    public static OptionState instance = new OptionState();

    private GameController gameController;
    private OptionUI optionUI; // La interfaz de usuario para la pantalla de opciones.
    private Preferences prefs; // Objeto para guardar y cargar las preferencias del jugador.

    private int currentSelection = 0; // 0 para musica, 1 para sonido.
    private int musicVolume; // Volumen de la musica (0-10).
    private int soundVolume; // Volumen de los efectos de sonido (0-10).

    /**
     * Constructor privado para implementar el patron Singleton.
     */
    private OptionState() {
        instance = this;
    }

    /**
     * Se llama una vez al entrar en este estado.
     * Carga las preferencias de volumen guardadas e inicializa la UI.
     * @param owner El GameController que gestiona la maquina de estados.
     */
    @Override
    public void enter(GameController owner) {
        this.gameController = owner;
        this.optionUI = new OptionUI(gameController);

        // Carga las preferencias guardadas del archivo "GameOptions".
        prefs = Gdx.app.getPreferences("GameOptions");
        musicVolume = (int) (gameController.getAudioManager().getMusicVolume() * 10);
        soundVolume = (int) (gameController.getAudioManager().getSoundVolume() * 10);

        // No se establece un InputProcessor aqui, ya que el manejo de entrada se hace directamente en execute().
        Gdx.input.setInputProcessor(null);
    }

    /**
     * Se llama en cada fotograma.
     * Maneja la entrada del teclado para ajustar las opciones y actualiza y renderiza la UI.
     */
    @Override
    public void execute() {
        handleInput();
        optionUI.update(Gdx.graphics.getDeltaTime(), currentSelection, musicVolume, soundVolume);
        optionUI.render();
    }

    /**
     * Se llama una vez al salir de este estado.
     * Guarda las preferencias de volumen actuales.
     */
    @Override
    public void exit() {
        savePreferences();
    }

    /**
     * Se llama al reanudar este estado (no se usa actualmente).
     */
    @Override
    public void resume() {
        // No se necesita logica de reanudacion especifica.
    }

    /**
     * Procesa la entrada del teclado para navegar y modificar las opciones.
     */
    private void handleInput() {
        // Cambia entre la seleccion de volumen de musica y de sonido.
        if (Gdx.input.isKeyJustPressed(Input.Keys.W) || Gdx.input.isKeyJustPressed(Input.Keys.S)) {
            currentSelection = (currentSelection + 1) % 2;
            AudioManager.getInstance().playSfx(AudioId.UIChange, null);
        }

        // Disminuye el volumen del elemento seleccionado.
        if (Gdx.input.isKeyJustPressed(Input.Keys.A)) {
            if (currentSelection == 0) { // Musica
                if (musicVolume > 0) {
                    musicVolume = Math.max(0, musicVolume - 1);
                    AudioManager.getInstance().playSfx(AudioId.UIChange, null);
                } else {
                    AudioManager.getInstance().playSfx(AudioId.NotAvailable, null);
                }
            } else { // Sonido
                if (soundVolume > 0) {
                    soundVolume = Math.max(0, soundVolume - 1);
                    AudioManager.getInstance().playSfx(AudioId.UIChange, null);
                } else {
                    AudioManager.getInstance().playSfx(AudioId.NotAvailable, null);
                }
            }
        }

        // Aumenta el volumen del elemento seleccionado.
        if (Gdx.input.isKeyJustPressed(Input.Keys.D)) {
            if (currentSelection == 0) { // Musica
                if (musicVolume < 10) {
                    musicVolume = Math.min(10, musicVolume + 1);
                    AudioManager.getInstance().playSfx(AudioId.UIChange, null);
                } else {
                    AudioManager.getInstance().playSfx(AudioId.NotAvailable, null);
                }
            } else { // Sonido
                if (soundVolume < 10) {
                    soundVolume = Math.min(10, soundVolume + 1);
                    AudioManager.getInstance().playSfx(AudioId.UIChange, null);
                } else {
                    AudioManager.getInstance().playSfx(AudioId.NotAvailable, null);
                }
            }
        }

        // Sale del menu de opciones y vuelve al estado anterior.
        if (Gdx.input.isKeyJustPressed(Input.Keys.Q)) {
            gameController.stateMachine.pop();
            AudioManager.getInstance().playSfx(AudioId.UICancel, null);
        }

        // Aplica los cambios de volumen al AudioManager en tiempo real.
        gameController.getAudioManager().setMusicVolume(musicVolume / 10f);
        gameController.getAudioManager().setSoundVolume(soundVolume / 10f);
    }

    /**
     * Guarda los valores de volumen actuales en el archivo de preferencias.
     */
    private void savePreferences() {
        prefs.putInteger("musicVolume", musicVolume);
        prefs.putInteger("soundVolume", soundVolume);
        prefs.flush(); // Asegura que los datos se escriban en el disco.
    }
}