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
 * Estado del menú principal del juego.
 * Implementa la interfaz State con GameController como tipo propietario.
 * 
 * @author MachineHunterDev
 */
public class MainMenuState implements IState<GameController>{
    /** Interfaz de usuario del menú principal */
    private MainMenuUI menuUI;
    
    /** Instancia singleton del estado */
    public static MainMenuState instance = new MainMenuState();

    /**
     * Constructor privado para implementar el patrón Singleton.
     */
    private MainMenuState() 
    {
        instance = this;
    }

    /** Controlador del juego propietario */
    //private GameController owner;

    /**
     * Inicializa el estado al entrar.
     * @param owner Controlador del juego propietario
     */
    @Override
    public void enter(GameController owner) 
    {
        //this.owner = owner;
        //BitmapFont font = new BitmapFont();
        SpriteBatch batch = new SpriteBatch();
        this.menuUI = new MainMenuUI(batch, owner);
        Gdx.input.setInputProcessor(this.menuUI);
        AudioManager.getInstance().playMusic("Audio/Soundtrack/MainMenu.mp3", true, false);
    }

    /**
     * Ejecuta la lógica del estado cada frame.
     */
    @Override
    public void execute() 
    {
        if (menuUI != null) {
            menuUI.drawMenu();
        }
    }

    /**
     * Limpia los recursos al salir del estado.
     */
    @Override
    public void exit() 
    {
        Gdx.input.setInputProcessor(null);
    }

    @Override
    public void resume() {
        Gdx.input.setInputProcessor(this.menuUI);
    }
}