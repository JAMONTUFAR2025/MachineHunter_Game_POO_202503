package com.machinehunterdev.game.UI;

import com.machinehunterdev.game.GameController;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.machinehunterdev.game.Audio.AudioId;
import com.machinehunterdev.game.Audio.AudioManager;
import com.machinehunterdev.game.GameStates.PauseState;
import com.machinehunterdev.game.GameStates.OptionState;
import com.machinehunterdev.game.Gameplay.GlobalSettings;

public class PauseUI implements InputProcessor {

    // Referencia al estado de pausa que gestiona esta UI
    private PauseState pauseState;
    // Referencia al controlador principal del juego
    private GameController gameController;
    // SpriteBatch para dibujar elementos
    private SpriteBatch batch;
    // Fuente para renderizar el texto
    private BitmapFont font;
    // Textura de fondo para la interfaz de pausa
    private Texture backgroundTexture;

    // Enumeracion para los diferentes estados del menu de pausa
    private enum MenuState { MAIN, CONFIRM_EXIT, CONFIRM_RETRY }
    // Estado actual del menu de pausa
    private MenuState currentState = MenuState.MAIN;

    // Opciones del menu principal de pausa
    private String[] mainOptions = {"Reanudar", "Opciones", "Reintentar", "Salir"};
    // Opciones para los menus de confirmacion
    private String[] confirmOptions = {"Si", "No"};
    
    // Indice de la opcion actualmente seleccionada
    private int selectedOption = 0;

    /**
     * Constructor de la interfaz de pausa.
     * Inicializa el estado de pausa, el controlador del juego, el SpriteBatch y carga la fuente personalizada.
     * @param pauseState El estado de pausa que gestiona esta UI.
     * @param gameController El controlador principal del juego.
     * @param batch El SpriteBatch para dibujar elementos.
     */
    public PauseUI(PauseState pauseState, GameController gameController, SpriteBatch batch) {
        this.pauseState = pauseState;
        this.gameController = gameController;
        this.batch = batch;

        Pixmap pixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        pixmap.setColor(Color.WHITE);
        pixmap.fill();
        this.backgroundTexture = new Texture(pixmap);
        pixmap.dispose();

        loadCustomBitmapFont();
    }

    /**
     * Carga la fuente personalizada para la interfaz.
     * Si falla la carga, se utiliza una fuente por defecto.
     */
    private void loadCustomBitmapFont() {
        try {
            this.font = new BitmapFont(Gdx.files.internal("fonts/OrangeKid64.fnt"));
        } catch (Exception e) {
            this.font = new BitmapFont();
        }
    }

    /**
     * Renderiza la interfaz de pausa en pantalla.
     * Dibuja el fondo, el menu principal o los menus de confirmacion, y los controles.
     */
    public void draw() {
        batch.getProjectionMatrix().setToOrtho2D(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        
        batch.begin();
        
        batch.setColor(0, 0, 0, 0.7f);
        batch.draw(backgroundTexture, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        batch.setColor(Color.WHITE);

        font.getData().setScale(1.0f);

        if (currentState == MenuState.MAIN) {
            drawMenu(mainOptions, Gdx.graphics.getHeight() / 2f + 80);
        } else if (currentState == MenuState.CONFIRM_EXIT) {
            drawConfirmationMenu();
        } else if (currentState == MenuState.CONFIRM_RETRY) {
            drawRetryConfirmationMenu();
        }
        drawControls();
        batch.end();
    }

    /**
     * Dibuja las instrucciones de control en la parte inferior de la pantalla.
     */
    private void drawControls() {
        String controlsText = Input.Keys.toString(GlobalSettings.CONTROL_CANCEL) + " - Retroceder | " + Input.Keys.toString(GlobalSettings.CONTROL_INTERACT) + " - Seleccionar | " + Input.Keys.toString(GlobalSettings.CONTROL_JUMP) + " / " + Input.Keys.toString(GlobalSettings.CONTROL_CROUCH) + " - Moverse";
        GlyphLayout layout = new GlyphLayout(font, controlsText);

        font.setColor(Color.WHITE);
        float textX = (Gdx.graphics.getWidth() - layout.width) / 2f;
        float textY = 10 + layout.height + 20;
        font.draw(batch, controlsText, textX, textY);
    }

    /**
     * Dibuja un menu generico con las opciones dadas.
     * @param options Las opciones a dibujar.
     * @param startY La posicion Y inicial para dibujar las opciones.
     */
    private void drawMenu(String[] options, float startY) {
        for (int i = 0; i < options.length; i++) {
            drawText(options[i], startY - (i * 80), i == selectedOption);
        }
    }

    /**
     * Dibuja el menu de confirmacion para salir del juego.
     */
    private void drawConfirmationMenu() {
        float startY = Gdx.graphics.getHeight() / 2f + 80;
        drawText("¿Estás seguro que quieres salir?", startY, false);
        for (int i = 0; i < confirmOptions.length; i++) {
            float optionY = startY - ((i + 1) * 80);
            drawText(confirmOptions[i], optionY, i == selectedOption);
        }
    }

    /**
     * Dibuja el menu de confirmacion para reintentar el nivel.
     */
    private void drawRetryConfirmationMenu() {
        float startY = Gdx.graphics.getHeight() / 2f + 80;
        drawText("¿Estás seguro que quieres reintentar?", startY, false);
        for (int i = 0; i < confirmOptions.length; i++) {
            float optionY = startY - ((i + 1) * 80);
            drawText(confirmOptions[i], optionY, i == selectedOption);
        }
    }

    /**
     * Dibuja una linea de texto en pantalla.
     * El color del texto cambia si la opcion esta seleccionada.
     * @param text Texto a dibujar.
     * @param y Posicion Y para dibujar el texto.
     * @param isSelected Indica si la opcion esta seleccionada.
     */
    private void drawText(String text, float y, boolean isSelected) {
        GlyphLayout layout = new GlyphLayout(font, text);
        float x = (Gdx.graphics.getWidth() - layout.width) / 2f;
        font.setColor(isSelected ? new Color(0.7f, 0, 0, 1) : Color.WHITE);
        font.draw(batch, text, x, y);
    }

    @Override
    public boolean keyDown(int keycode) {
        if (currentState == MenuState.MAIN) {
            handleMainMenuInput(keycode);
        } else if (currentState == MenuState.CONFIRM_EXIT) {
            handleConfirmExitInput(keycode);
        } else if (currentState == MenuState.CONFIRM_RETRY) {
            handleConfirmRetryInput(keycode);
        }
        return true;
    }

    /**
     * Maneja la entrada del teclado para el menu principal de pausa.
     * @param keycode Codigo de la tecla presionada.
     */
    private void handleMainMenuInput(int keycode) {
        if (keycode == GlobalSettings.CONTROL_JUMP) {
            AudioManager.getInstance().playSfx(AudioId.UIChange, null);
            selectedOption = (selectedOption - 1 + mainOptions.length) % mainOptions.length;
        } else if (keycode == GlobalSettings.CONTROL_CROUCH) {
            AudioManager.getInstance().playSfx(AudioId.UIChange, null);
            selectedOption = (selectedOption + 1) % mainOptions.length;
        } else if (keycode == GlobalSettings.CONTROL_INTERACT) {
            if (selectedOption == 0) { // Reanudar
                AudioManager.getInstance().playSfx(AudioId.UICancel, null);
                pauseState.resumeGame();
            } else if (selectedOption == 1) { // Opciones
                gameController.stateMachine.push(com.machinehunterdev.game.GameStates.OptionState.instance);
            } else if (selectedOption == 2) { // Reintentar
                AudioManager.getInstance().playSfx(AudioId.UIAccept, null);
                currentState = MenuState.CONFIRM_RETRY;
                selectedOption = 0;
            } else if (selectedOption == 3) { // Salir
                AudioManager.getInstance().playSfx(AudioId.UIAccept, null);
                currentState = MenuState.CONFIRM_EXIT;
                selectedOption = 0;
            }
        } else if (keycode == GlobalSettings.CONTROL_CANCEL) {
            AudioManager.getInstance().playSfx(AudioId.UICancel, null);
            pauseState.resumeGame();
        }
    }

    /**
     * Maneja la entrada del teclado para el menu de confirmacion de salida.
     * @param keycode Codigo de la tecla presionada.
     */
    private void handleConfirmExitInput(int keycode) {
        if (keycode == GlobalSettings.CONTROL_JUMP) {
            AudioManager.getInstance().playSfx(AudioId.UIChange, null);
            selectedOption = (selectedOption - 1 + confirmOptions.length) % confirmOptions.length;
        } else if (keycode == GlobalSettings.CONTROL_CROUCH) {
            AudioManager.getInstance().playSfx(AudioId.UIChange, null);
            selectedOption = (selectedOption + 1) % confirmOptions.length;
        } else if (keycode == GlobalSettings.CONTROL_INTERACT) {
            if (selectedOption == 0) { // Si
                AudioManager.getInstance().playSfx(AudioId.UIAccept, null);
                pauseState.exitToMainMenu();
            } else if (selectedOption == 1) { // No
                AudioManager.getInstance().playSfx(AudioId.UICancel, null);
                currentState = MenuState.MAIN;
                selectedOption = 0;
            }
        } else if (keycode == GlobalSettings.CONTROL_CANCEL) {
            AudioManager.getInstance().playSfx(AudioId.UICancel, null);
            currentState = MenuState.MAIN;
            selectedOption = 0;
        }
    }

    /**
     * Maneja la entrada del teclado para el menu de confirmacion de reintento.
     * @param keycode Codigo de la tecla presionada.
     */
    private void handleConfirmRetryInput(int keycode) {
        if (keycode == GlobalSettings.CONTROL_JUMP) {
            AudioManager.getInstance().playSfx(AudioId.UIChange, null);
            selectedOption = (selectedOption - 1 + confirmOptions.length) % confirmOptions.length;
        } else if (keycode == GlobalSettings.CONTROL_CROUCH) {
            AudioManager.getInstance().playSfx(AudioId.UIChange, null);
            selectedOption = (selectedOption + 1) % confirmOptions.length;
        } else if (keycode == GlobalSettings.CONTROL_INTERACT) {
            if (selectedOption == 0) { // Si
                AudioManager.getInstance().playSfx(AudioId.UIAccept, null);
                pauseState.restartLevel();
            } else if (selectedOption == 1) { // No
                AudioManager.getInstance().playSfx(AudioId.UICancel, null);
                currentState = MenuState.MAIN;
                selectedOption = 0;
            }
        } else if (keycode == GlobalSettings.CONTROL_CANCEL) {
            AudioManager.getInstance().playSfx(AudioId.UICancel, null);
            currentState = MenuState.MAIN;
            selectedOption = 0;
        }
    }

    /**
     * Libera los recursos utilizados por la interfaz de pausa.
     * Incluye la fuente y la textura de fondo.
     */
    public void dispose() {
        font.dispose();
        backgroundTexture.dispose();
    }

    // Metodo no utilizado: se llama cuando se suelta una tecla.
    @Override public boolean keyUp(int keycode) { return false; }
    // Metodo no utilizado: se llama cuando se escribe un caracter.
    @Override public boolean keyTyped(char character) { return false; }
    // Metodo no utilizado: se llama cuando se presiona la pantalla o un boton del raton.
    @Override public boolean touchDown(int screenX, int screenY, int pointer, int button) { return false; }
    // Metodo no utilizado: se llama cuando se suelta la pantalla o un boton del raton.
    @Override public boolean touchUp(int screenX, int screenY, int pointer, int button) { return false; }
    // Metodo no utilizado: se llama cuando se arrastra el dedo o el raton.
    @Override public boolean touchDragged(int screenX, int screenY, int pointer) { return false; }
    // Metodo no utilizado: se llama cuando el raton se mueve sin botones presionados.
    @Override public boolean mouseMoved(int screenX, int screenY) { return false; }
    // Metodo no utilizado: se llama cuando se usa la rueda del raton.
    @Override public boolean scrolled(float amountX, float amountY) { return false; }
    // Metodo no utilizado: se llama cuando un evento tactil es cancelado (ej. por una llamada telefonica).
    @Override public boolean touchCancelled(int screenX, int screenY, int pointer, int button) { return false; }
}
