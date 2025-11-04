package com.machinehunterdev.game.GameStates;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Preferences;
import com.machinehunterdev.game.Audio.AudioId;
import com.machinehunterdev.game.Audio.AudioManager;
import com.machinehunterdev.game.GameController;
import com.machinehunterdev.game.UI.OptionUI;
import com.machinehunterdev.game.Util.IState;

public class OptionState implements IState<GameController> {
    public static OptionState instance = new OptionState();

    private GameController gameController;
    private OptionUI optionUI;
    private Preferences prefs;

    private int currentSelection = 0; // 0 for music, 1 for sound
    private int musicVolume;
    private int soundVolume;

    private OptionState() {
        instance = this;
    }

    @Override
    public void enter(GameController owner) {
        this.gameController = owner;
        this.optionUI = new OptionUI(gameController);

        prefs = Gdx.app.getPreferences("GameOptions");
        musicVolume = (int) (gameController.getAudioManager().getMusicVolume() * 10);
        soundVolume = (int) (gameController.getAudioManager().getSoundVolume() * 10);

        Gdx.input.setInputProcessor(null);
    }

    @Override
    public void execute() {
        handleInput();
        optionUI.update(Gdx.graphics.getDeltaTime(), currentSelection, musicVolume, soundVolume);
        optionUI.render();
    }

    @Override
    public void exit() {
        savePreferences();
    }

    @Override
    public void resume() {
    }

    private void handleInput() {
        if (Gdx.input.isKeyJustPressed(Input.Keys.W) || Gdx.input.isKeyJustPressed(Input.Keys.S)) {
            currentSelection = (currentSelection + 1) % 2;
            AudioManager.getInstance().playSfx(AudioId.UIChange, null);
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.A)) {
            if (currentSelection == 0) {
                if (musicVolume > 0) {
                    musicVolume = Math.max(0, musicVolume - 1);
                    AudioManager.getInstance().playSfx(AudioId.UIChange, null);
                } else {
                    AudioManager.getInstance().playSfx(AudioId.NotAvailable, null);
                }
            } else {
                if (soundVolume > 0) {
                    soundVolume = Math.max(0, soundVolume - 1);
                    AudioManager.getInstance().playSfx(AudioId.UIChange, null);
                } else {
                    AudioManager.getInstance().playSfx(AudioId.NotAvailable, null);
                }
            }
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.D)) {
            if (currentSelection == 0) {
                if (musicVolume < 10) {
                    musicVolume = Math.min(10, musicVolume + 1);
                    AudioManager.getInstance().playSfx(AudioId.UIChange, null);
                } else {
                    AudioManager.getInstance().playSfx(AudioId.NotAvailable, null);
                }
            } else {
                if (soundVolume < 10) {
                    soundVolume = Math.min(10, soundVolume + 1);
                    AudioManager.getInstance().playSfx(AudioId.UIChange, null);
                } else {
                    AudioManager.getInstance().playSfx(AudioId.NotAvailable, null);
                }
            }
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.Q)) {
            gameController.stateMachine.pop();
            AudioManager.getInstance().playSfx(AudioId.UICancel, null);
        }

        // Apply volume changes
        gameController.getAudioManager().setMusicVolume(musicVolume / 10f);
        gameController.getAudioManager().setSoundVolume(soundVolume / 10f);
    }

    private void savePreferences() {
        prefs.putInteger("musicVolume", musicVolume);
        prefs.putInteger("soundVolume", soundVolume);
        prefs.flush();
    }
}