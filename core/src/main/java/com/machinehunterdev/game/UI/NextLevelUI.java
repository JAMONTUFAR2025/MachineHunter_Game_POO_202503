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
import com.machinehunterdev.game.Gameplay.GlobalSettings;
import com.machinehunterdev.game.Audio.AudioId;
import com.machinehunterdev.game.Audio.AudioManager;
import com.machinehunterdev.game.GameStates.DialogState;

/**
 * Interfaz de usuario que se muestra al completar un nivel.
 * Permite al jugador avanzar al siguiente nivel o regresar al menú principal.
 * 
 * @author MachineHunterDev
 */
public class NextLevelUI implements InputProcessor {

    /** Estado del juego */
    private GameplayState gameplayState;
    /** SpriteBatch para renderizado */
    private SpriteBatch batch;
    /** Fuente para el texto */
    private BitmapFont font;
    /** Textura de fondo */
    private Texture backgroundTexture;

    /* Opciones del menu */
    private String[] options = {"Continuar"};
    /* Indice de la opcion seleccionada */
    private int selectedOption = 0;

    /**
     * Constructor de la interfaz de siguiente nivel.
     * Inicializa el estado de juego, el SpriteBatch y carga la fuente personalizada.
     * @param gameplayState Estado de juego actual.
     * @param batch SpriteBatch para renderizado.
     */
    public NextLevelUI(GameplayState gameplayState, SpriteBatch batch) {
        this.gameplayState = gameplayState;
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
     * Renderiza la interfaz de siguiente nivel.
     * Dibuja el fondo, el titulo "¡Nivel completado!", las opciones del menu y los controles.
     */
    public void draw() {
        batch.getProjectionMatrix().setToOrtho2D(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        
        batch.begin();
        
        batch.setColor(0, 0, 0, 0.7f);
        batch.draw(backgroundTexture, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        batch.setColor(Color.WHITE);

        font.getData().setScale(1.0f);

        drawMenu(options, Gdx.graphics.getHeight() / 2f + 80);
        
        drawControls();
        batch.end();
    }

    /**
     * Dibuja las instrucciones de control en pantalla.
     * Muestra la tecla de interaccion para seleccionar opciones.
     */
    private void drawControls() {
        String controlsText = Input.Keys.toString(GlobalSettings.CONTROL_INTERACT) + " - Seleccionar";
        GlyphLayout layout = new GlyphLayout(font, controlsText);

        font.setColor(Color.WHITE);
        float textX = (Gdx.graphics.getWidth() - layout.width) / 2f;
        float textY = 10 + layout.height + 20;
        font.draw(batch, controlsText, textX, textY);
    }

    /**
     * Dibuja el menu de opciones.
     * Incluye el titulo "¡Nivel completado!" y las opciones seleccionables.
     * @param options Opciones del menu.
     * @param startY Posicion Y inicial para dibujar las opciones.
     */
    private void drawMenu(String[] options, float startY) {
        switch (GlobalSettings.currentLevelFile) {
            case "Levels/Level 0.json":
                drawText("¡Completaste el tutorial!", Gdx.graphics.getHeight() / 2f + 160, false);
                break;
            case "Levels/Level 3.json":
                drawText("¡Derrotaste a GEMINI.EXE!", Gdx.graphics.getHeight() / 2f + 160, false);
                break;
            case "Levels/Level 5.json":
                drawText("¡Derrotaste a CHATGPT.EXE!", Gdx.graphics.getHeight() / 2f + 160, false);
                break;
            default:
                drawText("¡Nivel completado!", Gdx.graphics.getHeight() / 2f + 160, false);
                break;
        }

        for (int i = 0; i < options.length; i++) {
            drawText(options[i], startY - (i * 80), i == selectedOption);
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

    // === Manejo de entrada ===
    /**
     * Maneja la entrada del teclado para la seleccion de opciones.
     * Al presionar la tecla de interaccion, avanza al siguiente nivel o a la pantalla de creditos.
     * @param keycode Codigo de la tecla presionada.
     * @return true si la entrada fue procesada.
     */
    @Override
    public boolean keyDown(int keycode) {
        if (keycode == GlobalSettings.CONTROL_INTERACT) {
            AudioManager.getInstance().playSfx(AudioId.UIAccept, null);
            if (selectedOption == 0) { // Siguiente Nivel
                com.machinehunterdev.game.Levels.LevelData currentLevel = gameplayState.getCurrentLevel();
                if (currentLevel != null && currentLevel.nextLevel != null && !currentLevel.nextLevel.isEmpty()) {
                    com.machinehunterdev.game.Levels.LevelData nextLevelData = com.machinehunterdev.game.Levels.LevelLoader.loadLevel(currentLevel.nextLevel);
                    if (nextLevelData != null && nextLevelData.flashbackDialogueSection != null && !nextLevelData.flashbackDialogueSection.isEmpty()) {
                        gameplayState.getOwner().stateMachine.changeState(new com.machinehunterdev.game.GameStates.DialogState(nextLevelData.flashbackDialogueSection, currentLevel.nextLevel));
                    } else {
                        GameplayState nextLevelState = GameplayState.createForLevel(currentLevel.nextLevel);
                        gameplayState.getOwner().stateMachine.changeState(nextLevelState);
                    }
                } else {
                    gameplayState.getOwner().stateMachine.changeState(new DialogState("Final", "credits"));
                }
            }
        }
        return true;
    }

    /**
     * Libera los recursos utilizados por la interfaz.
     * Incluye la fuente y la textura de fondo.
     */
    public void dispose() {
        font.dispose();
        backgroundTexture.dispose();
    }

    // === Metodos de InputProcessor no utilizados ===
    @Override public boolean keyUp(int keycode) { return false; }
    @Override public boolean keyTyped(char character) { return false; }
    @Override public boolean touchDown(int screenX, int screenY, int pointer, int button) { return false; }
    @Override public boolean touchUp(int screenX, int screenY, int pointer, int button) { return false; }
    @Override public boolean touchDragged(int screenX, int screenY, int pointer) { return false; }
    @Override public boolean mouseMoved(int screenX, int screenY) { return false; }
    @Override public boolean scrolled(float amountX, float amountY) { return false; }
    @Override public boolean touchCancelled(int screenX, int screenY, int pointer, int button) { return false; }
}
