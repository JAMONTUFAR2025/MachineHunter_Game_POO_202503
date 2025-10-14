package com.machinehunterdev.game.UI;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.machinehunterdev.game.GameController;
import com.machinehunterdev.game.GameStates.GameplayState;

// ** CREADO POR ANNER ALESSANDRO TERUEL 2025-10-03 **
public class MainMenuUI implements InputProcessor {
    
    private String[] options = {"Iniciar partida", "Estadisticas", "Salir"};
    private int selected = 0;
    private BitmapFont font;
    private SpriteBatch batch;
    private GameController gameController;
    private Texture texture;
    private boolean showingStats = false;

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

        if (showingStats) {
            // Menu de estadisticass
            int screenWidth = Gdx.graphics.getWidth();
            int screenHeight = Gdx.graphics.getHeight();

            String[] statsTexts = {
                "=== ESTADISTICAS ===",
                "Partidas jugadas: 5",
                "Puntuacion maxima: 10000",
                "Tiempo total jugado: 3h 45m",
                "Niveles completados: 3/5",
                "Monedas recolectadas: 150",
                "Presiona Q o E para volver"
            };

            float startY = screenHeight * 0.7f;
            float lineHeight = 90f; // Separador de filas, Ajusta según el tamaño de tu fuente

            for (int i = 0; i < statsTexts.length; i++) {
                String text = statsTexts[i];
                layout.setText(font, text);
                float x = (screenWidth - layout.width) / 2f;
                float y = startY - i * lineHeight;

                if (i == 0) {
                    font.setColor(Color.YELLOW);
                } else {
                    font.setColor(Color.WHITE);
                }
                font.draw(batch, text, x, y);
            }
        } else {
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
        }
        batch.end();
    }

    @Override
    public boolean keyDown(int keycode) {
        manejarEntrada(keycode);
        return true;
    }

    public void manejarEntrada(int keycode) {
        if (showingStats) {
            if (keycode == Input.Keys.Q || keycode == Input.Keys.E) {
                showingStats = false;
            }
            return;
        }

        if (keycode == Input.Keys.W || keycode == Input.Keys.UP) {
            selected = (selected - 1 + options.length) % options.length;
        } else if (keycode == Input.Keys.S || keycode == Input.Keys.DOWN) {
            selected = (selected + 1) % options.length;
        } else if (keycode == Input.Keys.E) {
            if (selected == 0) {
                starGame();
            } else if (selected == 1) {
                showingStats = true;
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