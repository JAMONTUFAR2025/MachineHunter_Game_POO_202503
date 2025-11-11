package com.machinehunterdev.game.GameStates;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.machinehunterdev.game.Character.CharacterAnimator;
import com.machinehunterdev.game.GameController;
import com.machinehunterdev.game.Audio.AudioId;
import com.machinehunterdev.game.Audio.AudioManager;
import com.machinehunterdev.game.UI.GameOverUI;
import com.machinehunterdev.game.Util.IState;

/**
 * Estado del juego que se muestra cuando el jugador muere.
 * Gestiona la secuencia de "Game Over", incluyendo la animacion de muerte del jugador,
 * la aparicion de texto y la presentacion de opciones como "Reintentar" o "Salir".
 * 
 * @author MachineHunterDev
 */
public class GameOverState implements IState<GameController> {

    /** Instancia unica de este estado (patron Singleton). */
    public static GameOverState instance = new GameOverState();
    
    // === COMPONENTES DEL ESTADO ===
    private SpriteBatch batch;
    private GameOverUI gameOverUI; // La interfaz de usuario para la pantalla de Game Over.
    public static CharacterAnimator playerAnimator; // El animador del jugador para mostrar la animacion de muerte.
    private Texture backgroundTexture; // La textura de fondo de la pantalla.

    // === TEMPORIZADORES PARA LA SECUENCIA ===
    private float gameOverTextTimer; // Temporizador para la aparicion del texto "Game Over".
    private float dialogueTimer; // Temporizador para el mensaje de muerte.

    private boolean isGameOverTextFinished; // Indica si el texto "Game Over" ha terminado de aparecer.
    private boolean isDialogueTypingFinished; // Indica si el mensaje de muerte ha terminado de escribirse.

    /**
     * Constructor privado para implementar el patron Singleton.
     */
    private GameOverState() {
        instance = this;
    }

    /**
     * Establece el animador del jugador para que este estado pueda reproducir la animacion de muerte.
     * @param animator El animador del personaje del jugador.
     */
    public static void setPlayerAnimator(CharacterAnimator animator) {
        playerAnimator = animator;
        if (playerAnimator != null) {
            playerAnimator.setCurrentAnimation(CharacterAnimator.AnimationState.DEAD);
        }
    }

    /**
     * Se llama una vez al entrar en este estado.
     * Inicializa la UI, la musica y los temporizadores de la secuencia.
     * @param owner El GameController que gestiona la maquina de estados.
     */
    @Override
    public void enter(GameController owner) {
        this.batch = owner.batch;
        this.gameOverUI = new GameOverUI(batch, owner, playerAnimator);
        Gdx.input.setInputProcessor(gameOverUI);
        backgroundTexture = new Texture("Fondos/GameOverBackground.png");

        // Detiene cualquier musica que se este reproduciendo.
        AudioManager.getInstance().stopMusic(false);

        // Reinicia los temporizadores y las banderas de la secuencia.
        gameOverTextTimer = 0f;
        dialogueTimer = 0f;
        isGameOverTextFinished = false;
        isDialogueTypingFinished = false;

        // Reproduce el sonido de Game Over.
        AudioManager.getInstance().playSfx(AudioId.GameOverSound, null);
    }

    /**
     * Se llama en cada fotograma.
     * Dibuja el fondo y actualiza la secuencia de Game Over.
     */
    @Override
    public void execute() {
        batch.begin();
        batch.draw(backgroundTexture, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        batch.end();

        float deltaTime = Gdx.graphics.getDeltaTime();

        // Actualiza la animacion de muerte del jugador si existe.
        if (playerAnimator != null) {
            playerAnimator.update(deltaTime);
        }

        // Comprueba si la animacion de muerte ha terminado.
        boolean deathAnimationFinished = playerAnimator == null || playerAnimator.isAnimationFinished(CharacterAnimator.AnimationState.DEAD);
        gameOverUI.setShowContent(deathAnimationFinished);

        // Si la animacion de muerte ha terminado, comienza la secuencia de texto.
        if (deathAnimationFinished) {
            updateGameOverSequence(deltaTime);
        }

        gameOverUI.draw();
    }

    /**
     * Actualiza la secuencia de aparicion de texto y dialogos en la pantalla de Game Over.
     * @param deltaTime El tiempo transcurrido desde el ultimo fotograma.
     */
    private void updateGameOverSequence(float deltaTime) {
        if (!isGameOverTextFinished) {
            gameOverTextTimer += deltaTime;
            gameOverUI.setGameOverTextTimer(gameOverTextTimer);
            if (gameOverTextTimer > 1.5f) {
                isGameOverTextFinished = true;
                gameOverUI.setShowDeathMessage(true);
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
     * Se llama una vez al salir de este estado.
     * Libera todos los recursos utilizados.
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

    /**
     * Se llama al reanudar este estado.
     */
    @Override
    public void resume() {
        Gdx.input.setInputProcessor(gameOverUI);
    }
}