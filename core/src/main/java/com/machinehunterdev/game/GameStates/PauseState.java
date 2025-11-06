package com.machinehunterdev.game.GameStates;

import com.badlogic.gdx.Gdx;
import com.machinehunterdev.game.GameController;
import com.machinehunterdev.game.UI.PauseUI;
import com.machinehunterdev.game.GameStates.OptionState;
import com.machinehunterdev.game.Util.IState;
import com.machinehunterdev.game.Audio.AudioManager;

public class PauseState implements IState<GameController> {

    private GameController owner;
    private GameplayState gameplayState;
    private PauseUI pauseUI;

    public PauseState(GameplayState gameplayState) {
        this.gameplayState = gameplayState;
    }

    @Override
    public void enter(GameController owner) {
        this.owner = owner;
        this.owner.clearScreen = false;
        this.pauseUI = new PauseUI(this, owner, owner.batch);
        Gdx.input.setInputProcessor(pauseUI);
        AudioManager.getInstance().pauseMusic(false);
        draw();
    }

    @Override
    public void execute() {
        AudioManager.getInstance().update(Gdx.graphics.getDeltaTime());
        draw();
    }

    private void draw() {
        // Render the gameplay state in the background
        gameplayState.drawGameWorld();

        // Render the pause UI on top
        pauseUI.draw();
    }

    @Override
    public void exit() {
        owner.clearScreen = true;
        AudioManager.getInstance().resumeMusic(false);
        Gdx.input.setInputProcessor(null);
        if (pauseUI != null) {
            pauseUI.dispose();
        }
    }

    @Override
    public void resume() {
        Gdx.input.setInputProcessor(pauseUI);
    }

    public void resumeGame() {
        owner.stateMachine.pop();
    }

    public void restartLevel() {
        gameplayState.restartLevel();
    }

    public void exitToMainMenu() {
        gameplayState.exitToMainMenu();
    }
}
