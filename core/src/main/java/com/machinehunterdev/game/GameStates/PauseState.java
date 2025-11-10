package com.machinehunterdev.game.GameStates;

import com.badlogic.gdx.Gdx;
import com.machinehunterdev.game.GameController;
import com.machinehunterdev.game.UI.PauseUI;
import com.machinehunterdev.game.GameStates.OptionState;
import com.machinehunterdev.game.Util.IState;
import com.machinehunterdev.game.Audio.AudioManager;

/**
 * Representa el estado de pausa del juego.
 * Este estado se superpone al estado de juego (GameplayState) sin detener
 * completamente su renderizado, creando un efecto de "juego congelado" en el fondo.
 * 
 * @author MachineHunterDev
 */
public class PauseState implements IState<GameController> {

    private GameController owner;
    private GameplayState gameplayState; // Referencia al estado de juego que fue pausado.
    private PauseUI pauseUI; // La interfaz de usuario para el menu de pausa.

    /**
     * Constructor para el estado de pausa.
     * @param gameplayState La instancia del estado de juego que se esta pausando.
     */
    public PauseState(GameplayState gameplayState) {
        this.gameplayState = gameplayState;
    }

    /**
     * Se llama una vez al entrar en este estado.
     * Inicializa la UI de pausa y pausa la musica del juego.
     * @param owner El GameController que gestiona la maquina de estados.
     */
    @Override
    public void enter(GameController owner) {
        this.owner = owner;
        this.owner.clearScreen = false; // Evita que la pantalla se limpie para mantener el fondo del juego.
        this.pauseUI = new PauseUI(this, owner, owner.batch);
        Gdx.input.setInputProcessor(pauseUI); // La UI de pausa maneja la entrada.
        AudioManager.getInstance().pauseMusic(false); // Pausa la musica del nivel.
        draw(); // Dibuja el primer fotograma inmediatamente.
    }

    /**
     * Se llama en cada fotograma.
     * Actualiza el audio y redibuja la pantalla de pausa.
     */
    @Override
    public void execute() {
        AudioManager.getInstance().update(Gdx.graphics.getDeltaTime());
        draw();
    }

    /**
     * Dibuja el estado de pausa.
     * Primero dibuja el estado de juego en el fondo y luego la UI de pausa encima.
     */
    private void draw() {
        // Renderiza el estado de juego en el fondo para crear el efecto de superposicion.
        gameplayState.drawGameWorld();

        // Renderiza la UI de pausa encima del juego congelado.
        pauseUI.draw();
    }

    /**
     * Se llama una vez al salir de este estado.
     * Reanuda la musica y limpia los recursos.
     */
    @Override
    public void exit() {
        owner.clearScreen = true; // Restaura el comportamiento normal de limpieza de pantalla.
        AudioManager.getInstance().resumeMusic(false); // Reanuda la musica del nivel.
        Gdx.input.setInputProcessor(null);
        if (pauseUI != null) {
            pauseUI.dispose();
        }
    }

    /**
     * Se llama al reanudar este estado (ej. al volver del menu de opciones).
     */
    @Override
    public void resume() {
        Gdx.input.setInputProcessor(pauseUI);
    }

    // === METODOS DE ACCION LLAMADOS DESDE LA UI ===

    /**
     * Reanuda el juego, sacando este estado de la pila.
     */
    public void resumeGame() {
        owner.stateMachine.pop();
    }

    /**
     * Reinicia el nivel actual.
     */
    public void restartLevel() {
        gameplayState.restartLevel();
    }

    /**
     * Sale al menu principal.
     */
    public void exitToMainMenu() {
        gameplayState.exitToMainMenu();
    }
}
