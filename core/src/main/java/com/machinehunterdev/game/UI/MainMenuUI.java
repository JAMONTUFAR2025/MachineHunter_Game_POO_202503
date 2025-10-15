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
import com.machinehunterdev.game.GameStates.GameplayState;
import com.machinehunterdev.game.GameStates.LeaderboardState;

// ** CREADO POR ANNER ALESSANDRO TERUEL 2025-10-03 **
public class MainMenuUI implements InputProcessor {
    
    private String[] options = {"Iniciar partida", "Clasificacion", "Salir"};
    private int selected = 0;
    private BitmapFont font;
    private SpriteBatch batch;
    private GameController gameController;
    private Texture texture;
    private Texture backgroundTexture;


    /**
     * Constructor de la clase MainMenuUI
     * @param batch Lote de sprites para dibujar elementos
     * @param gameController Controlador del juego para manejar estados
     * @param texture Textura de fondo del menu
     */
    public MainMenuUI(SpriteBatch batch, GameController gameController, Texture texture) {
        this.batch = batch;
        this.gameController = gameController;
        this.texture = texture;
        loadCustomBitmapFont();

        // Create a 1x1 white texture programmatically
        Pixmap pixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        pixmap.setColor(Color.WHITE);
        pixmap.fill();
        this.backgroundTexture = new Texture(pixmap);
        pixmap.dispose();
    }

    /**
     * Carga una fuente bitmap pregenerada (ej. myfont.fnt + myfont.png)
     */
    private void loadCustomBitmapFont() {
        try {
            // Asegúrate de que estos archivos existen en tu carpeta assets/fonts/
            this.font = new BitmapFont(Gdx.files.internal("fonts/OrangeKid64.fnt"));
            this.font.setColor(Color.WHITE);
        } catch (Exception e) {
            System.err.println("Error al cargar la fuente personalizada. Usando fuente por defecto.");
            this.font = new BitmapFont();
        }
    }

    /**
     * Dibuja las opciones del menu en pantalla
     */
    public void drawMenu() {
        batch.begin();
        batch.draw(texture, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        GlyphLayout layout = new GlyphLayout();

        // Menu de opciones
        int screenWidth = Gdx.graphics.getWidth();
        int screenHeight = Gdx.graphics.getHeight();

        float startY = screenHeight * 0.6f;
        float lineHeight = 110f;

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

    private void drawControls() {
        String controlsText = "E-Seleccionar | W/S-Moverse";
        GlyphLayout layout = new GlyphLayout(font, controlsText);

        float boxWidth = layout.width + 40;
        float boxHeight = layout.height + 40;
        float boxX = (Gdx.graphics.getWidth() - boxWidth) / 2f;
        float boxY = 10;

        batch.setColor(0, 0, 0, 0.7f);
        batch.draw(backgroundTexture, boxX, boxY, boxWidth, boxHeight);
        batch.setColor(Color.WHITE);

        // Set the font scale for the controls text
        font.setColor(Color.WHITE);

        // Calculate the position of the text
        float textX = (Gdx.graphics.getWidth() - layout.width) / 2f;
        float textY = boxY + boxHeight / 2 + layout.height / 2;

        // Draw the text
        font.draw(batch, controlsText, textX, textY);
    }

    @Override
    public boolean keyDown(int keycode) {
        manejarEntrada(keycode);
        return true;
    }

    public void manejarEntrada(int keycode) {
        if (keycode == Input.Keys.W || keycode == Input.Keys.UP) {
            selected = (selected - 1 + options.length) % options.length;
        } else if (keycode == Input.Keys.S || keycode == Input.Keys.DOWN) {
            selected = (selected + 1) % options.length;
        } else if (keycode == Input.Keys.E) {
            if (selected == 0) {
                starGame();
            } else if (selected == 1) {
                gameController.stateMachine.changeState(LeaderboardState.instance);
            } else if (selected == 2) {
                exitGame();
            }
        }
    }

    private void starGame() {
        System.out.println("Juego iniciado");
        Gdx.input.setInputProcessor(null);
        gameController.stateMachine.changeState(com.machinehunterdev.game.GameStates.NameInputState.instance);
    }

    private void exitGame() {
        System.out.println("Saliendo del juego");
        Gdx.app.exit();
    }

    public void dispose() {
        if (font != null) {
            font.dispose();
        }
        // Nota: No dispenses 'texture' ni 'batch' aquí si son gestionados por otra clase
    }

    // Métodos de InputProcessor no usados
    @Override public boolean keyTyped(char character) { return false; }
    @Override public boolean keyUp(int keycode) { return false; }
    @Override public boolean touchDown(int screenX, int screenY, int pointer, int button) { return false; }
    @Override public boolean touchUp(int screenX, int screenY, int pointer, int button) { return false; }
    @Override public boolean touchDragged(int screenX, int screenY, int pointer) { return false; }
    @Override public boolean mouseMoved(int screenX, int screenY) { return false; }
    @Override public boolean scrolled(float amountX, float amountY) { return false; }
    @Override public boolean touchCancelled(int screenX, int screenY, int pointer, int button) { return false; }
}