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
import com.machinehunterdev.game.GameController;
import com.machinehunterdev.game.Gameplay.GlobalSettings;
import com.machinehunterdev.game.GameStates.CreditState;

/**
 * Interfaz de usuario para el menú principal del juego.
 * Implementa InputProcessor para manejar la navegación con teclado.
 * 
 * @author MachineHunterDev
 */
public class MainMenuUI implements InputProcessor {
    
    /** Opciones del menú principal */
    private String[] options = {"Iniciar partida", "Créditos", "Salir"};
    
    /** Índice de la opción seleccionada actualmente */
    private int selected = 0;
    
    /** Fuente para el texto del menú */
    private BitmapFont font;
    private BitmapFont titleFont;
    
    /** SpriteBatch para renderizado */
    private SpriteBatch batch;
    
    /** Controlador del juego para gestión de estados */
    private GameController gameController;
    
    /** Textura del fondo del menú */
    private Texture texture;
    
    /** Textura blanca para elementos de interfaz */
    private Texture backgroundTexture;

    /**
     * Constructor de la interfaz del menú principal.
     * @param batch SpriteBatch para dibujar elementos
     * @param gameController Controlador del juego para manejar estados
     */
    public MainMenuUI(SpriteBatch batch, GameController gameController) {
        this.batch = batch;
        this.gameController = gameController;
        this.texture = new Texture(Gdx.files.internal("Fondos/NameInputBackgroundShadowless.png"));
        loadCustomBitmapFont();

        // Crear textura blanca programáticamente para elementos de interfaz
        Pixmap pixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        pixmap.setColor(Color.WHITE);
        pixmap.fill();
        this.backgroundTexture = new Texture(pixmap);
        pixmap.dispose();
    }

    /**
     * Carga la fuente bitmap personalizada.
     */
    private void loadCustomBitmapFont() {
        try {
            this.font = new BitmapFont(Gdx.files.internal("fonts/OrangeKid64.fnt"));
            this.font.setColor(Color.WHITE);
            this.titleFont = new BitmapFont(Gdx.files.internal("fonts/OrangeKid128.fnt"));
            this.titleFont.setColor(Color.RED);
        } catch (Exception e) {
            this.font = new BitmapFont();
            this.titleFont = new BitmapFont();
        }
    }

    /**
     * Dibuja las opciones del menú en pantalla.
     */
    public void drawMenu() {
        batch.begin();
        batch.draw(texture, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        GlyphLayout layout = new GlyphLayout();

        int screenWidth = Gdx.graphics.getWidth();
        int screenHeight = Gdx.graphics.getHeight();

        // Draw title
        layout.setText(titleFont, "MACHINE HUNTER");
        float titleX = (screenWidth - layout.width) / 2f;
        float titleY = screenHeight * 0.9f;
        titleFont.draw(batch, layout, titleX, titleY);

        float startY = screenHeight * 0.6f;
        float lineHeight = 110f;

        // Dibujar opciones con resaltado de selección
        for (int i = 0; i < options.length; i++) {
            String text = (i == selected ? "> " : "  ") + options[i];
            layout.setText(font, text);
            float x = (screenWidth - layout.width) / 2f;
            float y = startY - i * lineHeight;

            font.setColor(i == selected ? Color.RED : Color.WHITE);
            font.draw(batch, text, x, y);
        }

        drawControls();
        batch.end();
    }

    /**
     * Dibuja las instrucciones de controles en la parte inferior.
     */
    private void drawControls() {
        String controlsText = Input.Keys.toString(GlobalSettings.CONTROL_INTERACT) + "-Seleccionar | " + Input.Keys.toString(GlobalSettings.CONTROL_JUMP) + "/" + Input.Keys.toString(GlobalSettings.CONTROL_CROUCH) + "-Moverse";
        GlyphLayout layout = new GlyphLayout(font, controlsText);

        float boxWidth = layout.width + 40;
        float boxHeight = layout.height + 40;
        float boxX = (Gdx.graphics.getWidth() - boxWidth) / 2f;
        float boxY = 10;

        batch.setColor(0, 0, 0, 0.7f);
        batch.draw(backgroundTexture, boxX, boxY, boxWidth, boxHeight);
        batch.setColor(Color.WHITE);

        float textX = (Gdx.graphics.getWidth() - layout.width) / 2f;
        float textY = boxY + boxHeight / 2 + layout.height / 2;
        font.draw(batch, controlsText, textX, textY);
    }

    // === Manejo de entrada ===
    
    @Override
    public boolean keyDown(int keycode) {
        manejarEntrada(keycode);
        return true;
    }

    /**
     * Maneja la entrada del teclado para navegación y selección.
     * @param keycode Código de la tecla presionada
     */
    public void manejarEntrada(int keycode) {
        if (keycode == GlobalSettings.CONTROL_JUMP) {
            selected = (selected - 1 + options.length) % options.length;
        } else if (keycode == GlobalSettings.CONTROL_CROUCH) {
            selected = (selected + 1) % options.length;
        } else if (keycode == GlobalSettings.CONTROL_INTERACT) {
            if (selected == 0) {
                starGame();
            }else if (selected == 1) {
                gameController.stateMachine.changeState(CreditState.instance);
            } else if (selected == 2) {
                exitGame();
            }
        }
    }

    /**
     * Inicia una nueva partida.
     */
    private void starGame() {
        Gdx.input.setInputProcessor(null);
        gameController.stateMachine.changeState(com.machinehunterdev.game.GameStates.NameInputState.instance);
    }

    /**
     * Sale del juego.
     */
    private void exitGame() {
        Gdx.app.exit();
    }

    /**
     * Libera los recursos utilizados por la interfaz.
     */
    public void dispose() {
        if (font != null) {
            font.dispose();
        }
        // Nota: No se dispone 'texture' ni 'batch' aquí si son gestionados por otra clase
    }

    // === Métodos de InputProcessor no utilizados ===
    
    @Override public boolean keyTyped(char character) { return false; }
    @Override public boolean keyUp(int keycode) { return false; }
    @Override public boolean touchDown(int screenX, int screenY, int pointer, int button) { return false; }
    @Override public boolean touchUp(int screenX, int screenY, int pointer, int button) { return false; }
    @Override public boolean touchDragged(int screenX, int screenY, int pointer) { return false; }
    @Override public boolean mouseMoved(int screenX, int screenY) { return false; }
    @Override public boolean scrolled(float amountX, float amountY) { return false; }
    @Override public boolean touchCancelled(int screenX, int screenY, int pointer, int button) { return false; }
}