package com.machinehunterdev.game.UI;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.machinehunterdev.game.Character.Character;
import com.machinehunterdev.game.GameController;
import com.machinehunterdev.game.GameStates.GameplayState;
import com.machinehunterdev.game.Gameplay.GlobalSettings;

/**
 * Interfaz de usuario para la entrada del nombre del jugador.
 * Muestra un cursor parpadeante y valida la entrada del usuario.
 * 
 * @author MachineHunterDev
 */
public class NameInputUI implements InputProcessor {

    /** Fuente para el texto de la interfaz */
    private BitmapFont font;
    
    /** SpriteBatch para renderizado */
    private SpriteBatch batch;
    
    /** Renderizador de formas para el cursor */
    private ShapeRenderer shapeRenderer;
    
    /** Controlador del juego para gestión de estados */
    private GameController gameController;
    
    /** Nombre del jugador en construcción */
    private StringBuilder playerName;
    
    /** Temporizador para el parpadeo del cursor */
    private float elapsedTime = 0;

    /**
     * Constructor de la interfaz de entrada de nombre.
     * @param batch SpriteBatch para renderizado
     * @param gameController Controlador del juego para gestión de estados
     */
    public NameInputUI(SpriteBatch batch, GameController gameController) {
        this.batch = batch;
        this.gameController = gameController;
        this.playerName = new StringBuilder();
        this.shapeRenderer = new ShapeRenderer();
        loadCustomBitmapFont();
    }

    /**
     * Carga la fuente personalizada para la interfaz.
     */
    private void loadCustomBitmapFont() {
        try {
            this.font = new BitmapFont(Gdx.files.internal("fonts/OrangeKid64.fnt"));
            this.font.setColor(Color.WHITE);
        } catch (Exception e) {
            System.err.println("Error al cargar la fuente personalizada. Usando fuente por defecto.");
            this.font = new BitmapFont();
        }
    }

    /**
     * Renderiza la interfaz de entrada de nombre.
     * @param playerCharacter Personaje animado para mostrar en la pantalla
     */
    public void draw(Character playerCharacter) {
        batch.begin();

        // Dibujar el personaje animado
        playerCharacter.draw(batch);

        GlyphLayout layout = new GlyphLayout();
        String prompt = "Ingresa tu nombre";
        font.getData().setScale(2);
        layout.setText(font, prompt);
        float promptX = (Gdx.graphics.getWidth() - layout.width) / 2f;
        float promptY = Gdx.graphics.getHeight() - 50;
        font.draw(batch, prompt, promptX, promptY);

        font.getData().setScale(1.5f);
        String nameText = playerName.toString();
        layout.setText(font, nameText);
        float nameX = (Gdx.graphics.getWidth() - layout.width) / 2f;
        float nameY = Gdx.graphics.getHeight() / 4f;
        font.draw(batch, nameText, nameX, nameY);

        batch.end();

        // Dibujar cursor parpadeante
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        elapsedTime += Gdx.graphics.getDeltaTime();
        if ((int)(elapsedTime * 2) % 2 == 0) {
            float cursorX = nameX + layout.width;
            shapeRenderer.rect(cursorX, nameY - font.getCapHeight(), 20, font.getCapHeight() + 10);
        }
        shapeRenderer.end();
    }

    // === Manejo de entrada ===
    /**
     * Maneja la entrada del teclado para construir el nombre del jugador.
     * @param keycode Código de la tecla presionada.
     * @return true si la entrada fue procesada
     */
    @Override
    public boolean keyDown(int keycode) {
        if (keycode == GlobalSettings.CONTROL_CONFIRM) {
            if (playerName.length() > 0) {
                GlobalSettings.playerName = playerName.toString();
                // Cargar nivel 1
                GameplayState level1 = GameplayState.createForLevel("Levels/level1.json");
                gameController.stateMachine.changeState(level1);
            }
        } else if (keycode == GlobalSettings.CONTROL_BACKSPACE) {
            if (playerName.length() > 0) {
                playerName.setLength(playerName.length() - 1);
            }
        }
        return true;
    }

    /**
     * Maneja la entrada de caracteres para construir el nombre del jugador.
     * @param character Carácter ingresado por el usuario.
     * @return true si la entrada fue procesada.
     */
    @Override
    public boolean keyTyped(char character) {
        if (playerName.length() < 15 && (java.lang.Character.isLetterOrDigit(character) || character == ' ')) {
            playerName.append(character);
        }
        return true;
    }

    /**
     * Libera los recursos utilizados por la interfaz.
     */
    public void dispose() {
        if (font != null) {
            font.dispose();
        }
        shapeRenderer.dispose();
    }

    // === Métodos de InputProcessor no utilizados ===
    
    @Override public boolean keyUp(int keycode) { return false; }
    @Override public boolean touchDown(int screenX, int screenY, int pointer, int button) { return false; }
    @Override public boolean touchUp(int screenX, int screenY, int pointer, int button) { return false; }
    @Override public boolean touchDragged(int screenX, int screenY, int pointer) { return false; }
    @Override public boolean mouseMoved(int screenX, int screenY) { return false; }
    @Override public boolean scrolled(float amountX, float amountY) { return false; }
    @Override public boolean touchCancelled(int screenX, int screenY, int pointer, int button) { return false; }
}