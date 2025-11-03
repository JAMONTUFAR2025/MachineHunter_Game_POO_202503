package com.machinehunterdev.game.UI;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;
import com.badlogic.gdx.audio.Sound;
import com.machinehunterdev.game.Character.CharacterAnimator;
import com.machinehunterdev.game.GameController;
import com.machinehunterdev.game.Audio.AudioId;
import com.machinehunterdev.game.Audio.AudioManager;
import com.machinehunterdev.game.GameStates.GameplayState;
import com.machinehunterdev.game.GameStates.MainMenuState;
import com.machinehunterdev.game.Gameplay.GlobalSettings;

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

    /** Animador del personaje para mostrar en la pantalla de fin de juego */
    private CharacterAnimator playerAnimator;
    private Random random = new Random();

    // === Temporizadores y estados ===

    private float gameOverTextTimer = 0f;
    private float dialogueTimer = 0f;
    private float optionsTimer = 0f;
    private boolean deathMessageFinished = false;
    private boolean showDeathMessage = false;
    private boolean showOptions = false;
    private boolean musicStarted = false;

    private boolean showContent = false;

    // === Mensajes de muerte ===

    private List<String> deathMessages;
    private String randomDeathMessage;
    private String[] confirmationOptions = {"Sí", "No"};
    private int confirmationSelected = 0;
    private boolean isExitConfirmationVisible = false;
    private int eliminatedSoundPlayed = 0;
    private Sound talkingSound;

    
    /**
     * Constructor de la interfaz de fin de juego.
     * @param batch SpriteBatch para renderizado
     * @param gameController Controlador del juego para gestión de estados
     * @param animator Animador del personaje para mostrar la animación de muerte
     */
    public GameOverUI(SpriteBatch batch, GameController gameController, CharacterAnimator animator) {
        this.batch = batch;
        this.gameController = gameController;
        this.playerAnimator = animator;
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

    /**
     * Renderiza la interfaz de fin de juego.
     */
    public void draw() {
        batch.getProjectionMatrix().setToOrtho2D(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        batch.begin();

        // Dibujar animación del personaje
        if (playerAnimator != null) {
            Sprite currentSprite = playerAnimator.getCurrentSprite();
            if (currentSprite != null) {
                float scale = 7.0f;
                // Usar getRegionWidth/Height para obtener el tamaño original y evitar escalado doble
                float newWidth = currentSprite.getRegionWidth() * scale;
                float newHeight = currentSprite.getRegionHeight() * scale;
                float imgX = (Gdx.graphics.getWidth() - newWidth) / 2f;
                float imgY = (Gdx.graphics.getHeight() - newHeight) / 2f;
                
                // Dibujar el sprite manualmente en lugar de usar el método draw del animador
                currentSprite.setSize(newWidth, newHeight);
                currentSprite.setPosition(imgX, imgY);
                currentSprite.draw(batch);
            }
        }

        if (showContent) {
            drawGameOverText();

            if (deathMessageFinished) {
                optionsTimer += Gdx.graphics.getDeltaTime();
                if (optionsTimer > 1.5f) { // Changed delay to 1.5 seconds
                    showDeathMessage = false;
                    showOptions = true;
                    if (!musicStarted) {
                        AudioManager.getInstance().playMusic("Audio/Soundtrack/ChillTheme.mp3", true, false);
                        musicStarted = true;
                    }
                }
            }

            if (showDeathMessage) {
                drawDeathMessage();
            }

            if (showOptions) {
                if (isExitConfirmationVisible) {
                    drawExitConfirmation();
                } else {
                    drawOptions();
                }
            }
        }

        batch.end();
    }

    /**
     * Dibuja el texto "ELIMINADO" con animación de escritura.
     */
    private void drawGameOverText() {
        String gameOverText = "ELIMINADO";
        float animationPerCharDuration = 0.1f; // Increased speed
        float normalScale = 4.0f; // Reduced the size
        float startScale = 20.0f; // Adjusted for a more dramatic effect
        float shakeAmount = 4.0f;
        float letterSpacing = 30.0f; // Set to 30 pixels

        font.getData().setScale(normalScale);
        float totalWidth = 0;
        for (char c : gameOverText.toCharArray()) {
            GlyphLayout charLayout = new GlyphLayout(font, String.valueOf(c));
            totalWidth += charLayout.width + letterSpacing;
        }
        totalWidth -= letterSpacing;

        float startX = (Gdx.graphics.getWidth() - totalWidth) / 2f;
        float y = Gdx.graphics.getHeight() * 0.9f;

        float currentX = startX;

        for (int i = 0; i < gameOverText.length(); i++) {
            char c = gameOverText.charAt(i);
            GlyphLayout charLayout = new GlyphLayout(font, String.valueOf(c));
            float charWidth = charLayout.width;

            float startTime = i * animationPerCharDuration;

            float drawX = currentX;
            float drawY = y;
            float scale = normalScale;
            float alpha = 1.0f;

            float shakeX = 0;
            float shakeY = 0;

            if (gameOverTextTimer >= startTime) {
                if (i >= eliminatedSoundPlayed) {
                    AudioManager.getInstance().playSfx(AudioId.EnemyHurt, null);
                    eliminatedSoundPlayed++;
                }

                float progress = Math.min(1f, (gameOverTextTimer - startTime) / animationPerCharDuration);

                if (progress < 1f) { // Animating
                    scale = Interpolation.pow2Out.apply(startScale, normalScale, progress);
                    alpha = Interpolation.fade.apply(0, 1, progress);

                    drawX = currentX;
                    drawY = y;

                    shakeX = (random.nextFloat() - 0.5f) * shakeAmount * (1 - progress);
                    shakeY = (random.nextFloat() - 0.5f) * shakeAmount * (1 - progress);
                } else { // Animation finished, just shake
                    shakeX = (random.nextFloat() - 0.5f) * 8f;
                    shakeY = (random.nextFloat() - 0.5f) * 8f;
                }

                font.getData().setScale(scale);
                font.setColor(1, 0, 0, alpha); // Red color with alpha
                font.draw(batch, String.valueOf(c), drawX + shakeX, drawY + shakeY);
                font.getData().setScale(normalScale); // Reset scale for next char calculation
            }

            currentX += charWidth + letterSpacing;
        }

        font.setColor(Color.WHITE);
        font.getData().setScale(1.0f);
    }

    /**
     * Dibuja el mensaje de muerte con animación de escritura.
     */
    private void drawDeathMessage() {
        if (randomDeathMessage == null) return;

        if (talkingSound == null) {
            talkingSound = AudioManager.getInstance().getSound(AudioId.Talking);
            if (talkingSound != null) {
                talkingSound.loop();
            }
        }

        float charsPerSecond = 20f;
        float duration = randomDeathMessage.length() / charsPerSecond;
        float charsToShow = randomDeathMessage.length() * (Math.min(dialogueTimer, duration) / duration);
        String visibleText = randomDeathMessage.substring(0, Math.min((int) charsToShow, randomDeathMessage.length()));

        if ((int) charsToShow >= randomDeathMessage.length()) {
            deathMessageFinished = true;
            if (talkingSound != null) {
                talkingSound.stop();
                talkingSound = null;
            }
        }

        GlyphLayout layout = new GlyphLayout(font, visibleText);
        float x = (Gdx.graphics.getWidth() - layout.width) / 2f;
        float y = Gdx.graphics.getHeight() * 0.25f;
        font.draw(batch, visibleText, x, y);
    }

    /**
     * Dibuja las opciones de reinicio/salida con resaltado de selección.
     */
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



    private void drawExitConfirmation() {
        String confirmationText = "¿Seguro que quieres salir?";
        GlyphLayout confirmationLayout = new GlyphLayout(font, confirmationText);
        float confirmationX = (Gdx.graphics.getWidth() - confirmationLayout.width) / 2f;
        float confirmationY = Gdx.graphics.getHeight() * 0.30f;
        font.setColor(Color.WHITE);
        font.draw(batch, confirmationText, confirmationX, confirmationY);

        float startY = Gdx.graphics.getHeight() * 0.20f;
        float lineHeight = 110f;

        for (int i = 0; i < confirmationOptions.length; i++) {
            String text = (i == confirmationSelected ? "> " : "  ") + confirmationOptions[i];
            GlyphLayout layout = new GlyphLayout(font, text);
            float x = (Gdx.graphics.getWidth() - layout.width) / 2f;
            float y = startY - i * lineHeight;

            font.setColor(i == confirmationSelected ? Color.RED : Color.WHITE);
            font.draw(batch, text, x, y);
        }
    }
    /**
     * Maneja la entrada del teclado para navegar y seleccionar opciones.
     * @param keycode Código de la tecla presionada.
     */
    @Override
    public boolean keyDown(int keycode) {
        if (showOptions) {
            if (isExitConfirmationVisible) {
                if (keycode == GlobalSettings.CONTROL_JUMP) {
                    confirmationSelected = (confirmationSelected - 1 + confirmationOptions.length) % confirmationOptions.length;
                    AudioManager.getInstance().playSfx(AudioId.UIChange, null);
                } else if (keycode == GlobalSettings.CONTROL_CROUCH) {
                    confirmationSelected = (confirmationSelected + 1) % confirmationOptions.length;
                    AudioManager.getInstance().playSfx(AudioId.UIChange, null);
                } else if (keycode == GlobalSettings.CONTROL_INTERACT) {
                    if (confirmationSelected == 0) { // Sí
                        AudioManager.getInstance().playSfx(AudioId.UIAccept, null);
                        gameController.stateMachine.changeState(MainMenuState.instance);
                    } else { // No
                        AudioManager.getInstance().playSfx(AudioId.UICancel, null);
                        isExitConfirmationVisible = false;
                    }
                }
            } else {
                if (keycode == GlobalSettings.CONTROL_JUMP) {
                    selected = (selected - 1 + options.length) % options.length;
                    AudioManager.getInstance().playSfx(AudioId.UIChange, null);
                } else if (keycode == GlobalSettings.CONTROL_CROUCH) {
                    selected = (selected + 1) % options.length;
                    AudioManager.getInstance().playSfx(AudioId.UIChange, null);
                } else if (keycode == GlobalSettings.CONTROL_INTERACT) {
                    if (selected == 0) { // Reintentar
                        AudioManager.getInstance().playSfx(AudioId.UIAccept, null);
                        AudioManager.getInstance().pauseMusic(false);
                        GameplayState currentLevel = GameplayState.createForLevel(com.machinehunterdev.game.Gameplay.GlobalSettings.currentLevelFile);
                        gameController.stateMachine.changeState(currentLevel);
                    } else if (selected == 1) { // Salir
                        AudioManager.getInstance().playSfx(AudioId.UICancel, null);
                        isExitConfirmationVisible = true;
                        confirmationSelected = 0;
                    }
                }
            }
        }
        return true;
    }

    // === Métodos setters para control de estados ===
    /**
     * Muestra u oculta el contenido de la pantalla de Game Over.
     * @param show Indica si se debe mostrar el contenido.
     */
    public void setShowContent(boolean show) {
        this.showContent = show;
    }

    /**
     * Establece el temporizador para la animación del texto de fin de juego.
     * @param timer Nuevo valor del temporizador.
     */
    public void setGameOverTextTimer(float timer) {
        this.gameOverTextTimer = timer;
    }

    /**
     * Establece el temporizador para la animación del mensaje de muerte.
     * @param timer Nuevo valor del temporizador.
     */
    public void setDialogueTimer(float timer) {
        this.dialogueTimer = timer;
    }

    /**
     * Establece si se debe mostrar el mensaje de muerte.
     * @param show Indica si se debe mostrar el mensaje de muerte.
     */
    public void setShowDeathMessage(boolean show) {
        this.showDeathMessage = show;
    }

    // === Métodos getters para control de estados ===
    /**
     * Verifica si el mensaje de muerte ha terminado de mostrarse.
     * @return true si el mensaje ha terminado.
     */
    public boolean isDeathMessageFinished() {
        return deathMessageFinished;
    }

    /**
     * Libera los recursos utilizados por la interfaz.
     */
    public void dispose() {
        if (talkingSound != null) {
            talkingSound.stop();
            talkingSound = null;
        }
        if (font != null) {
            font.dispose();
        }
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