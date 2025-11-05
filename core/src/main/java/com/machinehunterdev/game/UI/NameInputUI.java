package com.machinehunterdev.game.UI;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.machinehunterdev.game.Audio.AudioId;
import com.machinehunterdev.game.Audio.AudioManager;
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

    private enum State {
        NAME_INPUT,
        TUTORIAL_CONFIRM
    }

    private State currentState = State.NAME_INPUT;
    private int tutorialConfirmSelection = 0;
    private boolean ignoreInput = false;

    /** Fuente para el texto de la interfaz */
    private BitmapFont font;
    
    /** SpriteBatch para renderizado */
    private SpriteBatch batch;
    

    
    /** Controlador del juego para gestión de estados */
    private GameController gameController;
    
    /** Nombre del jugador en construcción */
    private StringBuilder playerName;
    private com.badlogic.gdx.graphics.Texture tutorialBackground;

    /**
     * Constructor de la interfaz de entrada de nombre.
     * @param batch SpriteBatch para renderizado
     * @param gameController Controlador del juego para gestión de estados
     */
    public NameInputUI(SpriteBatch batch, GameController gameController) {
        this.batch = batch;
        this.gameController = gameController;
        this.playerName = new StringBuilder();
        this.tutorialBackground = new com.badlogic.gdx.graphics.Texture("Fondos/NameInputBackgroundShadowless.png");

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

        switch (currentState) {
            case NAME_INPUT:
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

                // Adjust to center the text and cursor together
                float totalInputWidth = layout.width + 2; // Width of text + cursor
                float centeredStartX = (Gdx.graphics.getWidth() - totalInputWidth) / 2f;

                float nameY = Gdx.graphics.getHeight() / 4f;
                font.draw(batch, nameText, centeredStartX, nameY);

                String controls = "Teclado: Escribir nombre | Enter: Aceptar";
                font.getData().setScale(1);
                layout.setText(font, controls);
                float controlsX = (Gdx.graphics.getWidth() - layout.width) / 2f;
                font.draw(batch, controls, controlsX, 50);

                batch.end();


                break;

            case TUTORIAL_CONFIRM:
                batch.draw(tutorialBackground, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
                drawDialog("¿Quieres jugar el tutorial?", new String[]{"Sí", "No"}, tutorialConfirmSelection);
                break;
        }

        if (currentState != State.NAME_INPUT) {
            String dialogControls = "E: Aceptar | Q: Retroceder | W/S: Cambiar selección.";
            GlyphLayout layout = new GlyphLayout();
            font.getData().setScale(1);
            layout.setText(font, dialogControls);
            float controlsX = (Gdx.graphics.getWidth() - layout.width) / 2f;
            font.draw(batch, dialogControls, controlsX, 50);
            batch.end();
        }
    }

    private void drawDialog(String title, String[] options, int selection) {
        GlyphLayout layout = new GlyphLayout();
        font.getData().setScale(1.5f);
        layout.setText(font, title);
        float titleX = (Gdx.graphics.getWidth() - layout.width) / 2f;
        float titleY = Gdx.graphics.getHeight() / 2f + 100;
        font.draw(batch, title, titleX, titleY);

        float optionY = Gdx.graphics.getHeight() / 2f;
        for (int i = 0; i < options.length; i++) {
            if (i == selection) {
                font.setColor(Color.RED);
            } else {
                font.setColor(Color.WHITE);
            }
            layout.setText(font, options[i]);
            float optionX = (Gdx.graphics.getWidth() - layout.width) / 2f;
            font.draw(batch, options[i], optionX, optionY);
            optionY -= 80;
        }
        font.setColor(Color.WHITE);
    }

    // === Manejo de entrada ===
    /**
     * Maneja la entrada del teclado para construir el nombre del jugador.
     * @param keycode Código de la tecla presionada.
     * @return true si la entrada fue procesada
     */
    @Override
    public boolean keyDown(int keycode) {
        switch (currentState) {
            case NAME_INPUT:
                if (keycode == GlobalSettings.CONTROL_CONFIRM) {
                    String trimmedName = playerName.toString().trim();
                    if (!trimmedName.isEmpty()) {
                        AudioManager.getInstance().playSfx(AudioId.UIAccept, null);
                        GlobalSettings.playerName = trimmedName;
                        currentState = State.TUTORIAL_CONFIRM;
                    }else{
                        AudioManager.getInstance().playSfx(AudioId.NotAvailable, null);
                    }
                } else if (keycode == GlobalSettings.CONTROL_BACKSPACE) {
                    if (playerName.length() > 0) {
                        AudioManager.getInstance().playSfx(AudioId.UICancel, null);
                        playerName.setLength(playerName.length() - 1);
                    } else {
                        AudioManager.getInstance().playSfx(AudioId.NotAvailable, null);
                    }
                }
                break;
            case TUTORIAL_CONFIRM:
                if (keycode == GlobalSettings.CONTROL_JUMP || keycode == GlobalSettings.CONTROL_CROUCH) {
                    AudioManager.getInstance().playSfx(AudioId.UIChange, null);
                    tutorialConfirmSelection = 1 - tutorialConfirmSelection;
                } else if (keycode == GlobalSettings.CONTROL_INTERACT) {
                    AudioManager.getInstance().playSfx(AudioId.UIAccept, null);
                    if (tutorialConfirmSelection == 0) { // Sí
                        // Cargar nivel 0 (tutorial)
                        GameplayState tutorial = GameplayState.createForLevel("Levels/Level 3.json");
                        gameController.stateMachine.changeState(tutorial);
                    } else { // No
                        // Cargar nivel 1
                        com.machinehunterdev.game.Levels.LevelData level1Data = com.machinehunterdev.game.Levels.LevelLoader.loadLevel("Levels/Level 1.json");
                        if (level1Data != null && level1Data.flashbackDialogueSection != null && !level1Data.flashbackDialogueSection.isEmpty()) {
                            gameController.stateMachine.changeState(new com.machinehunterdev.game.GameStates.DialogState(level1Data.flashbackDialogueSection, "Levels/Level 1.json"));
                        } else {
                            GameplayState level1 = GameplayState.createForLevel("Levels/Level 1.json");
                            gameController.stateMachine.changeState(level1);
                        }
                    }
                } else if (keycode == GlobalSettings.CONTROL_CANCEL) {
                    AudioManager.getInstance().playSfx(AudioId.UICancel, null);
                    currentState = State.NAME_INPUT;
                    ignoreInput = true;
                }
                break;
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
        if (ignoreInput) {
            ignoreInput = false;
            return true;
        }
        if (currentState == State.NAME_INPUT) {
            if (playerName.length() < 15 && (java.lang.Character.isLetterOrDigit(character) || character == ' ')) {
                playerName.append(character);
                AudioManager.getInstance().playSfx(AudioId.UIChange, null);
            } else if (playerName.length() >= 15) {
                AudioManager.getInstance().playSfx(AudioId.NotAvailable, null);
            }
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
        tutorialBackground.dispose();

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