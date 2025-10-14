package com.machinehunterdev.game.GameStates;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.machinehunterdev.game.GameController;
import com.machinehunterdev.game.Leaderboard.LeaderboardManager;
import com.machinehunterdev.game.UI.LeaderboardUI;
import com.machinehunterdev.game.Util.State;

public class LeaderboardState implements State<GameController> {

    public static LeaderboardState instance = new LeaderboardState();

    private LeaderboardUI leaderboardUI;
    private GameController owner;
    private LeaderboardManager leaderboardManager;

    private LeaderboardState() {}

    @Override
    public void enter(GameController owner) {
        this.owner = owner;
        this.leaderboardManager = new LeaderboardManager(); // Create a new manager each time
        this.leaderboardUI = new LeaderboardUI(leaderboardManager, this);
        Gdx.input.setInputProcessor(leaderboardUI.getStage());
    }

    @Override
    public void execute() {
        leaderboardUI.render(Gdx.graphics.getDeltaTime());
        if (Gdx.input.isKeyJustPressed(Input.Keys.Q) || Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            exitToMainMenu();
        }
    }

    @Override
    public void exit() {
        if (leaderboardUI != null) {
            leaderboardUI.dispose();
        }
    }

    public void resize(int width, int height) {
        if (leaderboardUI != null) {
            leaderboardUI.resize(width, height);
        }
    }

    public void exitToMainMenu() {
        owner.stateMachine.changeState(MainMenuState.instance);
    }
}
