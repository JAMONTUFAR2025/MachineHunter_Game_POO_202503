package com.machinehunterdev.game.GameStates;

import com.machinehunterdev.game.Util.State;
import com.machinehunterdev.game.GameController;

// Estado del menu principal, implementa la interfaz State con GameController como tipo propietario
public class MainMenuState implements State<GameController>
{
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
    }

    @Override
    public void execute() 
    {
        // Codigo que se ejecuta en cada frame mientras estamos en el estado del menu principal
        


        // PRUEBAS DE MAQUINA DE ESTADOS
        // Si se presiona la tecla ESPACIO, salir del juego
        if (com.badlogic.gdx.Gdx.input.isKeyJustPressed(com.badlogic.gdx.Input.Keys.SPACE)) 
        {
            com.badlogic.gdx.Gdx.app.exit();
        }

        // Si se presiona la tecla Q, quitar un estado de la pila
        if (com.badlogic.gdx.Gdx.input.isKeyJustPressed(com.badlogic.gdx.Input.Keys.Q)) 
        {
            owner.stateMachine.pop();
        }

        // Si se presiona la tecla E, coloca un estado nuevo en la pila
        if (com.badlogic.gdx.Gdx.input.isKeyJustPressed(com.badlogic.gdx.Input.Keys.E)) 
        {
            owner.stateMachine.push(MainMenuState.instance);
        }
    }

    @Override
    public void exit() 
    {
        // Codigo para limpiar el estado del menu principal al salir de el
    }
}