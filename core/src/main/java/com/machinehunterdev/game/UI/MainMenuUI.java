package com.machinehunterdev.game.UI;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.machinehunterdev.game.GameController;

public class MainMenuUI implements InputProcessor {
    
    // Opciones del menu
    private String[] options = {"Iniciar partida", "Salir"};
    // Indice de la opcion seleccionada actualmente
    private int selected = 0;
    // Fuente para dibujar texto
    private BitmapFont font;
    // Lote de sprites para dibujar en la pantalla
    private SpriteBatch batch;

    private GameController gameController;

    /**
     * Constructor de la clase MainMenuUI
     * @param font Fuente de texto para dibujar el menu
     * @param batch Lote de sprites para dibujar elementos
     */
    public MainMenuUI(BitmapFont font, SpriteBatch batch, GameController gameController) {
        this.font = font;
        this.batch = batch;
        this.gameController = gameController;
        // Establece esta clase como el procesador de entrada del teclado
        Gdx.input.setInputProcessor(this);
    }

    /**
     * Dibuja las opciones del menu en pantalla
     * Cambia de color en rojo la opcion seleccionada
     */
    public void drawMenu() {
        // Comienza a dibujar
        batch.begin();
        for (int i = 0; i < options.length; i++) {
            if (i == selected) {
                // Si la opcion es seleccionada, usa color rojo
                font.setColor(Color.RED);
                font.draw(batch, "> " + options[i], 100, 400 - i * 30);
            } else {
                // Si no, usar color blanco
                font.setColor(Color.WHITE);
                font.draw(batch, "> " + options[i], 100, 400 - i * 30);
            }
        }
        // Finaliza el dibujo
        batch.end();
    }

    /**
     * Metodo llamado cada vez que se presiona una tecla
     * @param keycode Codigo de la tecla presionada
     * @return true si se maneja la entrada
     */
    @Override
    public boolean keyDown(int keycode) {
        // Llama al metodo que maneja la logica de las teclas
        manejarEntrada(keycode);
        return true;
    }
    
    /**
     * Procesa las teclas W, S, E, Q para navegar y seleccionar
     * @param keycode Codigo de la tecla presionada
     */
    public void manejarEntrada(int keycode) {
        if (keycode == Input.Keys.W || keycode == Input.Keys.UP) {
            // Mover hacia arriba: retrocede la seleccion
            // Usa modulo para que el menu sea ciclico
            selected = (selected - 1 + options.length) % options.length;
        } else if (keycode == Input.Keys.S || keycode == Input.Keys.DOWN) {
            // Mover hacia abajo: avanza la seleccion
            selected = (selected + 1) % options.length;
        } else if (keycode == Input.Keys.E) {
            // Seleccionar opcion actual
            if (selected == 0) {
                starGame();
            } else if (selected == 1) {
                exitGame();
            }
        } else if (keycode == Input.Keys.Q) {
            // Salir del menu actual (pero no del juego)
            // Salir del estado actual (menú)
            gameController.stateMachine.pop();
            exitGame();
        }
    }

    /**
     * Accion al seleccionar "Iniciar Juego"
     * Se puede llamar la logica de juego desde aqui
     */
    private void starGame() {
        System.out.println("Juego iniciado");
        // Aqui puedes cambiar a otra pantalla o estado
    }

    /**
     * Accion al seleccionar "Salir"
     * Cierra la aplicacion
     */
    private void exitGame() {
        System.out.println("Saliendo del juego");
        Gdx.app.exit();
    }

    // Metodos requeridos por InputProcessor (no usados aqui, se dejan vacios)
    @Override
    public boolean keyTyped(char character) { return false; }
    @Override
    public boolean keyUp(int keycode) { return false; }
    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) { return false; }
    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) { return false; }
    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) { return false; }
    @Override
    public boolean mouseMoved(int screenX, int screenY) { return false; }
    @Override
    public boolean scrolled(float amountX, float amountY) { return false; }
    @Override
    public boolean touchCancelled(int screenX, int screenY, int pointer, int button) { return false; }
}