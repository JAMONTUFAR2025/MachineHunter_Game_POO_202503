package com.machinehunterdev.game.GameStates;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.machinehunterdev.game.GameController;
import com.machinehunterdev.game.UI.GameOverUI;
import com.machinehunterdev.game.Util.State;

/**
 * Estado del juego que se muestra cuando el jugador muere.
 * Gestiona la secuencia de animación de fin de juego.
 * 
 * @author MachineHunterDev
 */
public class GameOverState implements State<GameController> {

    /** Instancia singleton del estado */
    public static GameOverState instance = new GameOverState();
    
    /** Componentes del estado */
    private GameController owner;
    private SpriteBatch batch;
    private GameOverUI gameOverUI;

    /** Temporizadores para la secuencia de fin de juego */
    private float deathAnimationTimer;
    private float gameOverTextTimer;
    private float dialogueTimer;

    /** Estados de la secuencia */
    private boolean isDeathAnimationFinished;
    private boolean isGameOverTextFinished;
    private boolean isDialogueTypingFinished;

    /**
     * Constructor privado para implementar el patrón Singleton.
     */
    private GameOverState() {
        instance = this;
    }

    /**
     * Inicializa el estado al entrar.
     * @param owner Controlador del juego propietario
     */
    @Override
    public void enter(GameController owner) {
        this.owner = owner;
        this.batch = owner.batch;
        this.gameOverUI = new GameOverUI(batch, owner);
        Gdx.input.setInputProcessor(gameOverUI);

        // Inicializar temporizadores y estados
        deathAnimationTimer = 1.5f;
        gameOverTextTimer = 0f;
        dialogueTimer = 0f;

        isDeathAnimationFinished = false;
        isGameOverTextFinished = false;
        isDialogueTypingFinished = false;

        gameOverUI.setShowDeathMessage(true);
    }

    /**
     * Ejecuta la lógica del estado cada frame.
     */
    @Override
    public void execute() {
        float deltaTime = Gdx.graphics.getDeltaTime();

        // Temporizador de animación de muerte
        if (deathAnimationTimer > 0) {
            deathAnimationTimer -= deltaTime;
        } else {
            isDeathAnimationFinished = true;
        }

        gameOverUI.setShowContent(isDeathAnimationFinished);

        // Secuencia de fin de juego
        if (isDeathAnimationFinished) {
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
            gameOverUI.equals(this); // Nota: Esto parece un error, debería ser dispose()
        }
        Gdx.input.setInputProcessor(null);
    }
}