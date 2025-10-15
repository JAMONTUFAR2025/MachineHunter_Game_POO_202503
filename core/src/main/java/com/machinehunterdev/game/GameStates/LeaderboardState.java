package com.machinehunterdev.game.GameStates;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.machinehunterdev.game.GameController;
import com.machinehunterdev.game.UI.LeaderboardUI;
import com.machinehunterdev.game.Util.State;
import com.machinehunterdev.game.Leaderboard.LeaderboardManager;

/**
 * Estado del juego que muestra la tabla de clasificaciones.
 * Implementa el patrón Singleton para asegurar una única instancia.
 * 
 * @author MachineHunterDev
 */
public class LeaderboardState implements State<GameController> {

    /** Instancia singleton del estado */
    public static LeaderboardState instance = new LeaderboardState();

    /** Componentes del estado */
    private LeaderboardUI leaderboardUI;
    private GameController owner;
    private LeaderboardManager leaderboardManager;

    /**
     * Constructor privado para implementar el patrón Singleton.
     */
    private LeaderboardState() {}

    /**
     * Inicializa el estado al entrar.
     * @param owner Controlador del juego propietario
     */
    @Override
    public void enter(GameController owner) {
        this.owner = owner;
        this.leaderboardManager = LeaderboardManager.getInstance();
        this.leaderboardUI = new LeaderboardUI(leaderboardManager, this);
        Gdx.input.setInputProcessor(leaderboardUI.getStage());
    }

    /**
     * Ejecuta la lógica del estado cada frame.
     */
    @Override
    public void execute() {
        leaderboardUI.render(Gdx.graphics.getDeltaTime());
        
        if (Gdx.input.isKeyJustPressed(Input.Keys.Q) || Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            exitToMainMenu();
        }
    }

    /**
     * Limpia los recursos al salir del estado.
     */
    @Override
    public void exit() {
        if (leaderboardUI != null) {
            leaderboardUI.dispose();
        }
    }

    /**
     * Maneja el redimensionamiento de la ventana.
     */
    public void resize(int width, int height) {
        if (leaderboardUI != null) {
            leaderboardUI.resize(width, height);
        }
    }

    /**
     * Cambia al estado del menú principal.
     */
    public void exitToMainMenu() {
        owner.stateMachine.changeState(MainMenuState.instance);
    }
}