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
 */
public class LeaderboardState implements State<GameController> {

    // Instancia singleton del estado
    public static LeaderboardState instance = new LeaderboardState();

    // Componentes del estado
    private LeaderboardUI leaderboardUI;           // Interfaz de usuario
    private GameController owner;                  // Controlador del juego propietario
    private LeaderboardManager leaderboardManager; // Gestor de clasificaciones (singleton)

    /**
     * Constructor privado para implementar el patrón Singleton.
     */
    private LeaderboardState() {}

    /**
     * Método llamado al entrar en este estado.
     * Inicializa la interfaz y configura el procesador de entrada.
     * @param owner Controlador del juego que posee este estado
     */
    @Override
    public void enter(GameController owner) {
        this.owner = owner;
        // ✅ Usa la instancia singleton del gestor de clasificaciones
        this.leaderboardManager = LeaderboardManager.getInstance();
        this.leaderboardUI = new LeaderboardUI(leaderboardManager, this);
        Gdx.input.setInputProcessor(leaderboardUI.getStage());
    }

    /**
     * Método llamado cada frame mientras se está en este estado.
     * Renderiza la interfaz y maneja la entrada del usuario.
     */
    @Override
    public void execute() {
        leaderboardUI.render(Gdx.graphics.getDeltaTime());
        
        // Permitir salir con teclas Q o ESC (como respaldo)
        if (Gdx.input.isKeyJustPressed(Input.Keys.Q) || Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            exitToMainMenu();
        }
    }

    /**
     * Método llamado al salir de este estado.
     * Libera los recursos utilizados.
     */
    @Override
    public void exit() {
        if (leaderboardUI != null) {
            leaderboardUI.dispose();
        }
    }

    /**
     * Maneja el redimensionamiento de la ventana.
     * @param width Nuevo ancho de la ventana
     * @param height Nuevo alto de la ventana
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