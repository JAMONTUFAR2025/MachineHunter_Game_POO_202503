package com.machinehunterdev.game.GameStates;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.machinehunterdev.game.GameController;
import com.machinehunterdev.game.UI.CreditUI;
import com.machinehunterdev.game.Util.IState;
import com.machinehunterdev.game.Audio.AudioManager;

/**
 * Representa el estado de la pantalla de creditos del juego.
 * Este estado se muestra al finalizar el juego y se encarga de
 * mostrar los creditos con su musica correspondiente.
 * 
 * @author MachineHunterDev
 */
public class CreditState implements IState<GameController> {

    /** Instancia unica de este estado (patron Singleton). */
    public static CreditState instance = new CreditState();
    
    /** La interfaz de usuario que renderiza y gestiona la logica de los creditos. */
    private CreditUI creditUI;

    /**
     * Constructor privado para implementar el patron Singleton.
     */
    private CreditState() {
        instance = this;
    }

    /**
     * Se llama una vez al entrar en este estado.
     * Inicializa la UI de los creditos y la musica.
     * @param owner El GameController que gestiona la maquina de estados.
     */
    @Override
    public void enter(GameController owner) {
        SpriteBatch batch = new SpriteBatch();
        this.creditUI = new CreditUI(batch, owner);
        
        // Establece la UI como el procesador de entrada para manejar interacciones.
        Gdx.input.setInputProcessor(this.creditUI);
        
        // Inicia la musica de los creditos.
        AudioManager.getInstance().playMusic("Audio/Soundtrack/Credits.mp3", true, false);
    }

    /**
     * Se llama en cada fotograma.
     * Dibuja la interfaz de usuario de los creditos.
     */
    @Override
    public void execute() {
        if (creditUI != null) {
            creditUI.draw();
        }
    }

    /**
     * Se llama una vez al salir de este estado.
     * Libera los recursos de la UI y el procesador de entrada.
     */
    @Override
    public void exit() {
        Gdx.input.setInputProcessor(null);
        if (creditUI != null) {
            creditUI.dispose();
        }
    }

    /**
     * Se llama al reanudar este estado (no se usa actualmente).
     */
    @Override
    public void resume() {
        // No se necesita logica de reanudacion especifica por ahora.
    }
}
