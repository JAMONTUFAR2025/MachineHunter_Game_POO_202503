package com.machinehunterdev.game.GameStates;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.machinehunterdev.game.GameController;
import com.machinehunterdev.game.UI.MainMenuUI;
import com.machinehunterdev.game.Util.State;
// Estado del menu principal, implementa la interfaz State con GameController como tipo propietario
public class MainMenuState implements State<GameController>{
    // Instancia del MainMenuUI
    private MainMenuUI menuUI;
    // Instancia singleton del MainMenuState
    public static MainMenuState instance = new MainMenuState();

    // Constructor privado para evitar instanciación externa
    private MainMenuState() 
    {
        instance = this;
    }

    // Poseedor es el GameController
    private GameController owner;

    @Override
    public void enter(GameController owner) 
    {
        // Codigo para inicializar el estado del menu principal
        this.owner = owner;
        // Crear los recursos para la UI del menu
        BitmapFont font = new BitmapFont();
        SpriteBatch batch = new SpriteBatch();

        // Crear la UI del menu (y asignar InputProcessor automaticamente)
        this.menuUI = new MainMenuUI(batch, owner, new Texture("FondoMenuPrincipal.png"));

        // Establece esta clase como el procesador de entrada del teclado
        Gdx.input.setInputProcessor(this.menuUI);
    }

    @Override
    public void execute() 
    {
        // Codigo que se ejecuta en cada frame mientras estamos en el estado del menu principal
        
        //Prueba menu UI
        if (menuUI != null) {
            menuUI.drawMenu();
        }
        
        // // PRUEBAS DE MAQUINA DE ESTADOS
        // // Si se presiona la tecla ESPACIO, salir del juego
        // if (com.badlogic.gdx.Gdx.input.isKeyJustPressed(com.badlogic.gdx.Input.Keys.SPACE)) 
        // {
        //     com.badlogic.gdx.Gdx.app.exit();
        // }

        // // Si se presiona la tecla Q, quitar un estado de la pila
        // if (com.badlogic.gdx.Gdx.input.isKeyJustPressed(com.badlogic.gdx.Input.Keys.Q)) 
        // {
        //     owner.stateMachine.pop();
        // }

        // // Si se presiona la tecla E, coloca un estado nuevo en la pila
        // if (com.badlogic.gdx.Gdx.input.isKeyJustPressed(com.badlogic.gdx.Input.Keys.E)) 
        // {
        //     owner.stateMachine.push(MainMenuState.instance);
        // }
    }

    @Override
    public void exit() 
    {
        // Codigo para limpiar el estado del menu principal al salir de el
        // Desasignar el procesador de entrada del menu UI
        Gdx.input.setInputProcessor(null);
    }
}