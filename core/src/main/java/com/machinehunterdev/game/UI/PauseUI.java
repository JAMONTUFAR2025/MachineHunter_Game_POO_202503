package com.machinehunterdev.game.UI;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.machinehunterdev.game.GameStates.GameplayState;

/**
 * Interfaz de usuario para el menú de pausa con sistema de confirmación.
 * Implementa un sistema de dos estados: menú principal y confirmación de salida.
 * 
 * @author MachineHunterDev
 */
public class PauseUI implements InputProcessor {

    /** Referencia al estado de gameplay para reanudar o salir */
    private GameplayState gameplayState;
    
    /** SpriteBatch para renderizado */
    private SpriteBatch batch;
    
    /** Fuente para el texto del menú */
    private BitmapFont font;
    
    /** Textura blanca para el fondo semi-transparente */
    private Texture backgroundTexture;

    /** Estados del menú de pausa */
    private enum MenuState { MAIN, CONFIRM_EXIT }
    private MenuState currentState = MenuState.MAIN;

    /** Opciones para cada estado del menú */
    private String[] mainOptions = {"Reanudar", "Salir"};
    private String[] confirmOptions = {"Si", "No"};
    
    /** Índice de la opción seleccionada actualmente */
    private int selectedOption = 0;

    /**
     * Constructor del menú de pausa.
     * @param gameplayState Referencia al estado de gameplay
     * @param batch SpriteBatch compartido para renderizado
     */
    public PauseUI(GameplayState gameplayState, SpriteBatch batch) {
        this.gameplayState = gameplayState;
        this.batch = batch;

        // Crear textura blanca programáticamente para el fondo
        Pixmap pixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        pixmap.setColor(Color.WHITE);
        pixmap.fill();
        this.backgroundTexture = new Texture(pixmap);
        pixmap.dispose();

        loadCustomBitmapFont();
    }

    /**
     * Carga la fuente personalizada para la interfaz.
     */
    private void loadCustomBitmapFont() {
        try {
            this.font = new BitmapFont(Gdx.files.internal("fonts/OrangeKid64.fnt"));
        } catch (Exception e) {
            this.font = new BitmapFont();
        }
    }

    /**
     * Renderiza el menú de pausa con fondo semi-transparente.
     */
    public void draw() {
        batch.getProjectionMatrix().setToOrtho2D(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        
        batch.begin();
        
        // Dibujar fondo semi-transparente sobre toda la pantalla
        batch.setColor(0, 0, 0, 0.7f);
        batch.draw(backgroundTexture, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        batch.setColor(Color.WHITE);

        // Dibujar el menú apropiado según el estado actual
        font.getData().setScale(1.0f);

        if (currentState == MenuState.MAIN) {
            drawMenu(mainOptions, Gdx.graphics.getHeight() / 2f + 80);
        } else if (currentState == MenuState.CONFIRM_EXIT) {
            drawConfirmationMenu();
        }
        drawControls();
        batch.end();
    }

    /**
     * Dibuja las instrucciones de controles en la parte inferior.
     */
    private void drawControls() {
        String controlsText = "Q-Retroceder | E-Seleccionar | W/S-Moverse";
        GlyphLayout layout = new GlyphLayout(font, controlsText);

        font.setColor(Color.WHITE);
        float textX = (Gdx.graphics.getWidth() - layout.width) / 2f;
        float textY = 10 + layout.height + 20;
        font.draw(batch, controlsText, textX, textY);
    }

    /**
     * Dibuja un menú con las opciones proporcionadas.
     * @param options Opciones a mostrar
     * @param startY Posición Y inicial para el primer elemento
     */
    private void drawMenu(String[] options, float startY) {
        for (int i = 0; i < options.length; i++) {
            drawText(options[i], startY - (i * 80), i == selectedOption);
        }
    }

    /**
     * Dibuja el menú de confirmación de salida.
     */
    private void drawConfirmationMenu() {
        float startY = Gdx.graphics.getHeight() / 2f + 80;

        drawText("¿Esta seguro?", startY, false);
        
        for (int i = 0; i < confirmOptions.length; i++) {
            float optionY = startY - ((i + 1) * 80);
            drawText(confirmOptions[i], optionY, i == selectedOption);
        }
    }

    /**
     * Dibuja un texto individual con resaltado de selección.
     * @param text Texto a dibujar
     * @param y Posición Y del texto
     * @param isSelected Indica si el texto está seleccionado
     */
    private void drawText(String text, float y, boolean isSelected) {
        GlyphLayout layout = new GlyphLayout(font, text);
        float x = (Gdx.graphics.getWidth() - layout.width) / 2f;
        font.setColor(isSelected ? new Color(0.7f, 0, 0, 1) : Color.WHITE);
        font.draw(batch, text, x, y);
    }

    // === Manejo de entrada ===
    
    @Override
    public boolean keyDown(int keycode) {
        if (currentState == MenuState.MAIN) {
            handleMainMenuInput(keycode);
        } else if (currentState == MenuState.CONFIRM_EXIT) {
            handleConfirmExitInput(keycode);
        }
        return true;
    }

    /**
     * Maneja la entrada en el menú principal de pausa.
     * @param keycode Código de la tecla presionada
     */
    private void handleMainMenuInput(int keycode) {
        if (keycode == Input.Keys.UP || keycode == Input.Keys.W) {
            selectedOption = (selectedOption - 1 + mainOptions.length) % mainOptions.length;
        } else if (keycode == Input.Keys.DOWN || keycode == Input.Keys.S) {
            selectedOption = (selectedOption + 1) % mainOptions.length;
        } else if (keycode == Input.Keys.ENTER || keycode == Input.Keys.E) {
            if (selectedOption == 0) { // Reanudar
                gameplayState.resumeGame();
            } else if (selectedOption == 1) { // Salir
                currentState = MenuState.CONFIRM_EXIT;
                selectedOption = 0;
            }
        } else if (keycode == Input.Keys.Q) {
            gameplayState.resumeGame();
        }
    }

    /**
     * Maneja la entrada en el menú de confirmación de salida.
     * @param keycode Código de la tecla presionada
     */
    private void handleConfirmExitInput(int keycode) {
        if (keycode == Input.Keys.UP || keycode == Input.Keys.W) {
            selectedOption = (selectedOption - 1 + confirmOptions.length) % confirmOptions.length;
        } else if (keycode == Input.Keys.DOWN || keycode == Input.Keys.S) {
            selectedOption = (selectedOption + 1) % confirmOptions.length;
        } else if (keycode == Input.Keys.ENTER || keycode == Input.Keys.E) {
            if (selectedOption == 0) { // Si
                gameplayState.exitToMainMenu();
            } else if (selectedOption == 1) { // No
                currentState = MenuState.MAIN;
                selectedOption = 0;
            }
        } else if (keycode == Input.Keys.Q) {
            currentState = MenuState.MAIN;
            selectedOption = 0;
        }
    }

    /**
     * Libera los recursos utilizados por la interfaz.
     */
    public void dispose() {
        font.dispose();
        backgroundTexture.dispose();
    }

    // === Métodos de InputProcessor no utilizados ===
    
    @Override public boolean keyUp(int keycode) { return false; }
    @Override public boolean keyTyped(char character) { return false; }
    @Override public boolean touchDown(int screenX, int screenY, int pointer, int button) { return false; }
    @Override public boolean touchUp(int screenX, int screenY, int pointer, int button) { return false; }
    @Override public boolean touchDragged(int screenX, int screenY, int pointer) { return false; }
    @Override public boolean mouseMoved(int screenX, int screenY) { return false; }
    @Override public boolean scrolled(float amountX, float amountY) { return false; }
    @Override public boolean touchCancelled(int screenX, int screenY, int pointer, int button) { return false; }
}