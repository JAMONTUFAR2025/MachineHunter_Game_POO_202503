package com.machinehunterdev.game.GameStates;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.machinehunterdev.game.GameController;
import com.machinehunterdev.game.UI.GameOverUI;
import com.machinehunterdev.game.Util.State;

public class GameOverState implements State<GameController> {

    public static GameOverState instance = new GameOverState();
    private GameController owner;
    private SpriteBatch batch;
    private GameOverUI gameOverUI;

    private float deathAnimationTimer;
    private float gameOverTextTimer;
    private float dialogueTimer;

    private boolean isDeathAnimationFinished;
    private boolean isGameOverTextFinished;
    private boolean isDialogueTypingFinished;
    private boolean isWaitingForInput;

    private GameOverState() {
        instance = this;
    }

    @Override
    public void enter(GameController owner) {
        this.owner = owner;
        this.batch = owner.batch;
        this.gameOverUI = new GameOverUI(batch, owner);
        Gdx.input.setInputProcessor(gameOverUI);

        deathAnimationTimer = 3f;
        gameOverTextTimer = 0f;
        dialogueTimer = 0f;

        isDeathAnimationFinished = false;
        isGameOverTextFinished = false;
        isDialogueTypingFinished = false;
        isWaitingForInput = false;

        gameOverUI.setShowDeathMessage(true); // Show message container from the start
    }

    @Override
    public void execute() {
        float deltaTime = Gdx.graphics.getDeltaTime();

        if (deathAnimationTimer > 0) {
            deathAnimationTimer -= deltaTime;
        } else {
            isDeathAnimationFinished = true;
        }

        gameOverUI.setShowContent(isDeathAnimationFinished);

        if (isDeathAnimationFinished) {
            updateGameOverSequence(deltaTime);
        }

        gameOverUI.draw();
    }

    private void updateGameOverSequence(float deltaTime) {
        if (!isGameOverTextFinished) {
            gameOverTextTimer += deltaTime;
            gameOverUI.setGameOverTextTimer(gameOverTextTimer);
            if (gameOverTextTimer > 1.5f) { // 1.5 seconds for the animation
                isGameOverTextFinished = true;
            }
        } else if (!isDialogueTypingFinished) {
            dialogueTimer += deltaTime;
            gameOverUI.setDialogueTimer(dialogueTimer);
            if (dialogueTimer > 2.5f) { // 2.5 seconds for dialogue typing
                isDialogueTypingFinished = true;
                isWaitingForInput = true;
                gameOverUI.setWaitingForInput(true);
            }
        }
    }

    @Override
    public void exit() {
        if (gameOverUI != null) {
            gameOverUI.equals(this);
        }
        Gdx.input.setInputProcessor(null);
    }
}
