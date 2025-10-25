package com.machinehunterdev.game.GameStates;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.machinehunterdev.game.Character.CharacterAnimator;
import com.machinehunterdev.game.GameController;
import com.machinehunterdev.game.UI.GameOverUI;
import com.machinehunterdev.game.Util.IState;

/**
 * Estado del juego que se muestra cuando el jugador muere.
 * Gestiona la secuencia de animación de fin de juego.
 * 
 * @author MachineHunterDev
 */
public class GameOverState implements IState<GameController> {

    /** Instancia singleton del estado */
    public static GameOverState instance = new GameOverState();
    
    /** Componentes del estado */
    //private GameController owner;
    private SpriteBatch batch;
    private GameOverUI gameOverUI;
    public static CharacterAnimator playerAnimator;
    private Texture backgroundTexture;

    private float gameOverTextTimer;
    private float dialogueTimer;

    private boolean isGameOverTextFinished;
    private boolean isDialogueTypingFinished;

    /**
     * Constructor privado para implementar el patrón Singleton.
     */
    private GameOverState() {
        instance = this;
    }

    public static void setPlayerAnimator(CharacterAnimator animator) {
        playerAnimator = animator;
        if (playerAnimator != null) {
            playerAnimator.setCurrentAnimation(CharacterAnimator.AnimationState.DEAD);
        }
    }

    /**
     * Inicializa el estado al entrar.
     * @param owner Controlador del juego propietario
     */
    @Override
    public void enter(GameController owner) {
        //this.owner = owner;
        this.batch = owner.batch;
        this.gameOverUI = new GameOverUI(batch, owner, playerAnimator);
        Gdx.input.setInputProcessor(gameOverUI);
        backgroundTexture = new Texture("Fondos/GameOverBackground.png");

        // Inicializar temporizadores y estados
        gameOverTextTimer = 0f;
        dialogueTimer = 0f;

        isGameOverTextFinished = false;
        isDialogueTypingFinished = false;

        gameOverUI.setShowDeathMessage(true);
    }

    /**
     * Ejecuta la lógica del estado cada frame.
     */
    @Override
    public void execute() {
        batch.begin();
        batch.draw(backgroundTexture, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        batch.end();

        float deltaTime = Gdx.graphics.getDeltaTime();

        if (playerAnimator != null) {
            playerAnimator.update(deltaTime);
        }

        boolean deathAnimationFinished = playerAnimator == null || playerAnimator.isAnimationFinished(CharacterAnimator.AnimationState.DEAD);
        gameOverUI.setShowContent(deathAnimationFinished);

        // Secuencia de fin de juego
        if (deathAnimationFinished) {
            updateGameOverSequence(deltaTime);
        }

        gameOverUI.draw();
    }

    /**
     * Actualiza la secuencia de texto y diálogo de fin de juego.
     * @param deltaTime Tiempo transcurrido desde el último frame
     */
    private void updateGameOverSequence(float deltaTime) {
        if (!isGameOverTextFinished) {
            gameOverTextTimer += deltaTime;
            gameOverUI.setGameOverTextTimer(gameOverTextTimer);
            if (gameOverTextTimer > 1.5f) {
                isGameOverTextFinished = true;
            }
        } else if (!isDialogueTypingFinished) {
            dialogueTimer += deltaTime;
            gameOverUI.setDialogueTimer(dialogueTimer);
            if (gameOverUI.isDeathMessageFinished()) {
                isDialogueTypingFinished = true;
            }
        }
    }

    /**
     * Limpia los recursos al salir del estado.
     */
    @Override
    public void exit() {
        if (gameOverUI != null) {
            gameOverUI.dispose();
        }
        if (backgroundTexture != null) {
            backgroundTexture.dispose();
        }
        if (playerAnimator != null) {
            playerAnimator.dispose();
            playerAnimator = null;
        }
        Gdx.input.setInputProcessor(null);
    }
}