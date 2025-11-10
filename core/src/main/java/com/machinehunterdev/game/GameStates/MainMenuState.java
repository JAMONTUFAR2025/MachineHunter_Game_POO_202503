package com.machinehunterdev.game.GameStates;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.machinehunterdev.game.GameController;
import com.machinehunterdev.game.UI.MainMenuUI;
import com.machinehunterdev.game.GameStates.OptionState;
import com.machinehunterdev.game.Util.IState;
import com.machinehunterdev.game.Audio.AudioManager;

/**
 * Representa el estado del menu principal del juego.
 * Este es generalmente el primer estado que el jugador ve al iniciar el juego.
 * Se encarga de mostrar las opciones principales como "Jugar", "Opciones" y "Salir".
 * 
 * @author MachineHunterDev
 */
public class MainMenuState implements IState<GameController>{
    
    /** La interfaz de usuario (UI) que contiene los botones y elementos visuales del menu. */
    private MainMenuUI menuUI;
    
    /** Instancia unica de este estado, siguiendo el patron Singleton para evitar multiples instancias. */
    public static MainMenuState instance = new MainMenuState();

    /**
     * Constructor privado para implementar el patron Singleton.
     * Esto asegura que solo exista una instancia de MainMenuState.
     */
    private MainMenuState() 
    {
        instance = this;
    }

    /**
     * Se llama una vez cuando se entra en este estado.
     * Se encarga de inicializar la UI del menu y la musica.
     * @param owner El GameController que gestiona la maquina de estados.
     */
    @Override
    public void enter(GameController owner) 
    {
        // Crea una nueva instancia de la UI del menu principal.
        SpriteBatch batch = new SpriteBatch();
        this.menuUI = new MainMenuUI(batch, owner);
        
        // Establece la UI como el procesador de entrada para que pueda recibir clics de boton.
        Gdx.input.setInputProcessor(this.menuUI);
        
        // Inicia la reproduccion de la musica del menu principal.
        AudioManager.getInstance().playMusic("Audio/Soundtrack/MainMenu.mp3", true, false);
    }

    /**
     * Se llama en cada fotograma mientras este estado este activo.
     * Su unica responsabilidad es dibujar la interfaz de usuario del menu.
     */
    @Override
    public void execute() 
    {
        if (menuUI != null) {
            menuUI.drawMenu();
        }
    }

    /**
     * Se llama una vez cuando se sale de este estado.
     * Limpia el procesador de entrada para que el siguiente estado pueda establecer el suyo.
     */
    @Override
    public void exit() 
    {
        Gdx.input.setInputProcessor(null);
    }

    /**
     * Se llama cuando se vuelve a este estado desde otro (ej. desde el menu de opciones).
     * Restablece el procesador de entrada a la UI de este menu.
     */
    @Override
    public void resume() {
        Gdx.input.setInputProcessor(this.menuUI);
    }
}