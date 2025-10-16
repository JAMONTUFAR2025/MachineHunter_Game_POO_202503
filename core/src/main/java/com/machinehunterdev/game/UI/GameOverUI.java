package com.machinehunterdev.game.UI;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;
import com.machinehunterdev.game.GameController;
import com.machinehunterdev.game.GameStates.GameplayState;
import com.machinehunterdev.game.GameStates.MainMenuState;

/**
 * Interfaz de usuario para la pantalla de fin de juego.
 * Muestra animaciones de texto, mensajes de muerte aleatorios y opciones de reinicio.
 * 
 * @author MachineHunterDev
 */
public class GameOverUI implements InputProcessor {

    /** Opciones disponibles en la pantalla de fin de juego */
    private String[] options = {"Reintentar", "Salir"};
    
    /** Índice de la opción seleccionada actualmente */
    private int selected = 0;
    
    /** Fuente para el texto de la interfaz */
    private BitmapFont font;
    
    /** SpriteBatch para renderizado */
    private SpriteBatch batch;
    
    /** Controlador del juego para cambiar estados */
    private GameController gameController;
    
    /** Textura del personaje para mostrar en la pantalla de fin de juego */
    private Texture placeholderTexture;

    // === Temporizadores y estados ===
    
    private float gameOverTextTimer = 0f;
    private float dialogueTimer = 0f;
    private float optionsTimer = 0f;
    private boolean deathMessageFinished = false;
    private boolean showDeathMessage = false;
    private boolean showOptions = false;

    private boolean showContent = false;

    // === Mensajes de muerte ===
    
    private List<String> deathMessages;
    private String randomDeathMessage;

    /**
     * Constructor de la interfaz de fin de juego.
     * @param batch SpriteBatch para renderizado
     * @param gameController Controlador del juego para gestión de estados
     */
    public GameOverUI(SpriteBatch batch, GameController gameController) {
        this.batch = batch;
        this.gameController = gameController;
        this.placeholderTexture = new Texture("Player/PlayerIdle1.png");
        loadCustomBitmapFont();
        loadDeathMessages();
        
        // Seleccionar mensaje de muerte aleatorio al crear la interfaz
        if (deathMessages != null && !deathMessages.isEmpty()) {
            randomDeathMessage = deathMessages.get(new Random().nextInt(deathMessages.size()));
        }
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
     * Carga los mensajes de muerte desde un archivo JSON.
     */
    private void loadDeathMessages() {
        deathMessages = new ArrayList<>();
        try {
            JsonReader jsonReader = new JsonReader();
            JsonValue base = jsonReader.parse(Gdx.files.internal("Dialogos/Dialogos_muerte.json"));
            JsonValue dialogos = base.get("Dialogos_muerte");
            for (JsonValue dialogo : dialogos) {
                deathMessages.add(dialogo.getString("Texto"));
            }
        } catch (Exception e) {
            System.err.println("Error al cargar los mensajes de muerte. Usando mensajes por defecto.");
            deathMessages.add("Has muerto.");
        }
    }

    // === Métodos setters para control de estados ===
    
    public void setShowContent(boolean show) {
        this.showContent = show;
    }

    public void setGameOverTextTimer(float timer) {
        this.gameOverTextTimer = timer;
    }

    public void setDialogueTimer(float timer) {
        this.dialogueTimer = timer;
    }


    public void setShowDeathMessage(boolean show) {
        this.showDeathMessage = show;
    }

    /**
     * Renderiza la interfaz de fin de juego.
     */
    public void draw() {
        batch.getProjectionMatrix().setToOrtho2D(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        batch.begin();

        // Dibujar imagen del personaje
        if (placeholderTexture != null) {
            float scale = 7.0f;
            float newWidth = placeholderTexture.getWidth() * scale;
            float newHeight = placeholderTexture.getHeight() * scale;
            float imgX = (Gdx.graphics.getWidth() - newWidth) / 2f;
            float imgY = (Gdx.graphics.getHeight() - newHeight) / 2f;
            batch.draw(placeholderTexture, imgX, imgY, newWidth, newHeight);
        }

        if (showContent) {
            drawGameOverText();

            if (deathMessageFinished) {
                optionsTimer += Gdx.graphics.getDeltaTime();
                if (optionsTimer > 2f) {
                    showDeathMessage = false;
                    showOptions = true;
                }
            }

            if (showDeathMessage) {
                drawDeathMessage();
            }

            if (showOptions) {
                drawOptions();
            }
        }

        batch.end();
    }

    /**
     * Libera los recursos utilizados por la interfaz.
     */
    public void dispose() {
        if (font != null) {
            font.dispose();
        }
        if (placeholderTexture != null) {
            placeholderTexture.dispose();
        }
    }

    /**
     * Dibuja el texto "GAME OVER" con animación de escritura.
     */
    private void drawGameOverText() {
        String gameOverText = "GAME OVER";
        float charsToShow = gameOverText.length() * (Math.min(gameOverTextTimer, 1.5f) / 1.5f);
        String visibleText = gameOverText.substring(0, Math.min((int) charsToShow, gameOverText.length()));

        font.getData().setScale(2.0f);

        GlyphLayout layout = new GlyphLayout(font, visibleText);
        float x = (Gdx.graphics.getWidth() - layout.width) / 2f;
        float y = Gdx.graphics.getHeight() * 0.9f;

        font.setColor(Color.RED);
        font.draw(batch, visibleText, x, y);
        font.setColor(Color.WHITE);
        font.getData().setScale(1.0f);
    }

    /**
     * Dibuja el mensaje de muerte con animación de escritura.
     */
    private void drawDeathMessage() {
        if (randomDeathMessage == null) return;

        float charsToShow = randomDeathMessage.length() * (Math.min(dialogueTimer, 2.5f) / 2.5f);
        String visibleText = randomDeathMessage.substring(0, Math.min((int) charsToShow, randomDeathMessage.length()));

        if ((int) charsToShow >= randomDeathMessage.length()) {
            deathMessageFinished = true;
        }

        GlyphLayout layout = new GlyphLayout(font, visibleText);
        float x = (Gdx.graphics.getWidth() - layout.width) / 2f;
        float y = Gdx.graphics.getHeight() * 0.25f;
        font.draw(batch, visibleText, x, y);
    }

    /**
     * Dibuja las opciones de reinicio/salida con resaltado de selección.
     */
    private void drawOptions() {
        float startY = Gdx.graphics.getHeight() * 0.25f;
        float lineHeight = 110f;

        for (int i = 0; i < options.length; i++) {
            String text = (i == selected ? "> " : "  ") + options[i];
            GlyphLayout layout = new GlyphLayout(font, text);
            float x = (Gdx.graphics.getWidth() - layout.width) / 2f;
            float y = startY - i * lineHeight;

            font.setColor(i == selected ? Color.RED : Color.WHITE);
            font.draw(batch, text, x, y);
        }
    }

    // === Manejo de entrada ===
    
    @Override
    public boolean keyDown(int keycode) {
        if (showOptions) {
            if (keycode == Input.Keys.W || keycode == Input.Keys.UP) {
                selected = (selected - 1 + options.length) % options.length;
            } else if (keycode == Input.Keys.S || keycode == Input.Keys.DOWN) {
                selected = (selected + 1) % options.length;
            } else if (keycode == Input.Keys.E || keycode == Input.Keys.ENTER) {
                if (selected == 0) {
                    gameController.stateMachine.changeState(GameplayState.instance);
                } else if (selected == 1) {
                    gameController.stateMachine.changeState(MainMenuState.instance);
                }
            }
        }
        return true;
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