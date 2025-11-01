package com.machinehunterdev.game.Dialog;

import java.util.ArrayList;
import java.util.List;

import com.machinehunterdev.game.GameController;
import com.badlogic.gdx.Gdx;
import com.machinehunterdev.game.GameStates.MainMenuState;
import com.machinehunterdev.game.Gameplay.GlobalSettings; // Importar GlobalSettings
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

/**
 * Gestor del sistema de diálogos del juego.
 * Maneja la visualización, animación de texto y paginación de diálogos.
 * 
 * @author MachineHunterDev
 */
public class DialogManager {
    /** SpriteBatch para renderizar los diálogos */
    private SpriteBatch batch;
    
    /** Fuente para el texto de los diálogos */
    private BitmapFont font;
    
    /** Diálogo actual que se está mostrando */
    private Dialog currentDialog;
    
    /** Índice de la línea actual del diálogo */
    private int currentLineIndex;
    
    /** Indica si hay un diálogo activo */
    private boolean dialogActive;

    /** Dimensiones y posición del cuadro de diálogo */
    private float dialogBoxWidth;
    private float dialogBoxHeight = 200;
    private float dialogBoxX;
    private float dialogBoxY;

    /** Viewport para la interfaz de usuario */
    private ScreenViewport uiViewport;

    /** Texto actualmente visible (para efecto de escritura) */
    private String currentVisibleText = "";
    
    /** Temporizadores para la animación de texto */
    private float textTimer = 0f;
    private float textSpeed = 0.05f;
    private boolean textFullyVisible = false;

    /** Sistema de paginación para textos largos */
    private List<String> pages;
    private int currentPage;

    /** Layout para medir y formatear texto */
    private GlyphLayout glyphLayout;

    /** Texturas para el fondo y borde del cuadro de diálogo */
    private Texture backgroundTexture;
    private Texture borderTexture;
    private Texture skipIndicatorTexture;
    private Texture flashbackBackground;

    private float skipTimer = 0f;
    private boolean isSkipping = false;
    private static final float TIME_TO_SKIP = 5f;
    private float originalTextSpeed;

    private boolean isFlashback = false;

    private GameController owner;

    /**
     * Constructor del gestor de diálogos.
     * @param batch SpriteBatch para renderizado
     */
    public DialogManager(GameController owner, SpriteBatch batch) {
        this.owner = owner;
        this.batch = batch;
        font = new BitmapFont(Gdx.files.internal("fonts/OrangeKid64.fnt"));
        font.setColor(Color.WHITE);
        glyphLayout = new GlyphLayout();
        pages = new ArrayList<>();
        originalTextSpeed = textSpeed;

        // Crear textura de fondo semi-transparente
        Pixmap bgPixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        bgPixmap.setColor(0, 0, 0, 0.8f);
        bgPixmap.fill();
        backgroundTexture = new Texture(bgPixmap);
        bgPixmap.dispose();

        // Crear textura de borde blanco
        Pixmap borderPixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        borderPixmap.setColor(Color.WHITE);
        borderPixmap.fill();
        borderTexture = new Texture(borderPixmap);
        borderPixmap.dispose();

        Pixmap skipPixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        skipPixmap.setColor(Color.WHITE);
        skipPixmap.fill();
        skipIndicatorTexture = new Texture(skipPixmap);
        skipPixmap.dispose();

        flashbackBackground = new Texture(Gdx.files.internal("Fondos/NameInputBackgroundShadowless.png"));

        uiViewport = new ScreenViewport();
        uiViewport.update(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), true);

        updateDialogPosition();
        dialogActive = false;
    }

    /**
     * Actualiza la posición del cuadro de diálogo según el tamaño de la pantalla.
     */
    private void updateDialogPosition() {
        if (isFlashback) {
            dialogBoxWidth = Gdx.graphics.getWidth() - 700;
            dialogBoxX = (Gdx.graphics.getWidth() - dialogBoxWidth) / 2f;
            dialogBoxY = (Gdx.graphics.getHeight() - dialogBoxHeight) / 2f;
        } else {
            dialogBoxWidth = Gdx.graphics.getWidth() - 40;
            dialogBoxX = (Gdx.graphics.getWidth() - dialogBoxWidth) / 2f;
            dialogBoxY = Gdx.graphics.getHeight() - dialogBoxHeight - 20;
        }
    }

    /**
     * Muestra un nuevo diálogo.
     * @param dialog Diálogo a mostrar
     */
    public void showDialog(Dialog dialog, boolean isFlashback) {
        this.isFlashback = isFlashback;
        currentDialog = dialog;
        currentLineIndex = 0;
        dialogActive = true;
        updateDialogPosition();
        startNewLine();
    }

    /**
     * Inicia una nueva línea de diálogo con paginación automática.
     */
    private void startNewLine() {
        String fullText = currentDialog.getLines().get(currentLineIndex)
            .replace("Roberto Julián", GlobalSettings.playerName);
        pages.clear();
        currentPage = 0;

        GlyphLayout layout = new GlyphLayout();
        float targetWidth = dialogBoxWidth - 20;

        if (isFlashback) {
            pages.add(fullText);
        } else {
            float targetHeight = dialogBoxHeight - 20;
            int start = 0;
            while (start < fullText.length()) {
                int end = start;
                while (end < fullText.length()) {
                    end++;
                    layout.setText(font, fullText.substring(start, end), Color.WHITE, targetWidth, Align.left, true);
                    if (layout.height > targetHeight) {
                        end = fullText.lastIndexOf(' ', end - 1);
                        if (end == -1 || end <= start) {
                            end = fullText.indexOf(' ', start);
                            if (end == -1) {
                                end = fullText.length();
                            }
                        }
                        break;
                    }
                }
                pages.add(fullText.substring(start, end));
                start = end;
                if (start < fullText.length() && fullText.charAt(start) == ' ') {
                    start++;
                }
            }
        }

        startPage();
    }

    /**
     * Inicia una nueva página del diálogo actual.
     */
    private void startPage() {
        currentVisibleText = "";
        textTimer = 0f;
        textFullyVisible = false;
    }

    /**
     * Avanza al siguiente elemento del diálogo (página, línea o cierra diálogo).
     */
    public void nextLine() {
        if (textFullyVisible) {
            if (currentPage < pages.size() - 1) {
                currentPage++;
                startPage();
            } else {
                if (currentLineIndex < currentDialog.getLines().size() - 1) {
                    currentLineIndex++;
                    startNewLine();
                } else {
                    dialogActive = false;
                }
            }
        }

        else {
            currentVisibleText = pages.get(currentPage);
            textFullyVisible = true;
        }
    }

    /**
     * Actualiza la animación de escritura del texto.
     * @param dt Delta time desde el último frame
     */
    public void update(float dt) {
        if (!dialogActive) return;

        // Fast-forward
        if (Gdx.input.isKeyPressed(Input.Keys.E)) {
            textSpeed = originalTextSpeed / 2;
        } else {
            textSpeed = originalTextSpeed;
        }

        // Skip dialog
        if (Gdx.input.isKeyPressed(Input.Keys.ENTER)) {
            isSkipping = true;
            skipTimer += dt;
            if (skipTimer >= TIME_TO_SKIP) {
                skipDialog();
            }
        } else {
            isSkipping = false;
            skipTimer = 0;
        }



        if (textFullyVisible) {
            return;
        }

        textTimer += dt;
        String fullText = pages.get(currentPage);

        int charIndex = (int)(textTimer / textSpeed);
        if (charIndex < fullText.length()) {
            currentVisibleText = fullText.substring(0, charIndex + 1);
        } else {
            currentVisibleText = fullText;
            textFullyVisible = true;
        }
    }

    /**
     * Skips the current dialog.
     */
    public void skipDialog() {
        dialogActive = false;
        isSkipping = false;
        skipTimer = 0;
        owner.stateMachine.changeState(MainMenuState.instance);
    }

    /**
     * Verifica si hay un diálogo activo.
     * @return true si hay diálogo activo
     */
    public boolean isDialogActive() {
        return dialogActive;
    }

    /**
     * Renderiza el cuadro de diálogo actual.
     */
    public void render() {
        if (!dialogActive) return;
        
        uiViewport.apply(true);
        batch.setProjectionMatrix(uiViewport.getCamera().combined);

        batch.begin();

        if (isFlashback) {
            batch.draw(flashbackBackground, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
            glyphLayout.setText(font, currentVisibleText, Color.WHITE, dialogBoxWidth - 20, Align.center, true);
            float textY = dialogBoxY + dialogBoxHeight / 2 + glyphLayout.height / 2;
            font.draw(batch, glyphLayout, dialogBoxX + 10, textY);
        } else {
            // Dibujar fondo del cuadro de diálogo
            batch.draw(backgroundTexture, dialogBoxX, dialogBoxY, dialogBoxWidth, dialogBoxHeight);

            // Dibujar borde del cuadro de diálogo
            batch.draw(borderTexture, dialogBoxX, dialogBoxY, dialogBoxWidth, 1); // Superior
            batch.draw(borderTexture, dialogBoxX, dialogBoxY + dialogBoxHeight - 1, dialogBoxWidth, 1); // Inferior
            batch.draw(borderTexture, dialogBoxX, dialogBoxY, 1, dialogBoxHeight); // Izquierdo
            batch.draw(borderTexture, dialogBoxX + dialogBoxWidth - 1, dialogBoxY, 1, dialogBoxHeight); // Derecho

            glyphLayout.setText(font, currentVisibleText, Color.WHITE, dialogBoxWidth - 20, Align.left, true);
            float textY = dialogBoxY + dialogBoxHeight - 20; // From the top
            font.draw(batch, glyphLayout, dialogBoxX + 10, textY);
        }

        if (textFullyVisible && isFlashback) {
            font.getData().setScale(0.5f);
            font.draw(batch, "Presiona E para continuar...", 0, glyphLayout.height + 20, Gdx.graphics.getWidth(), Align.center, false);
            font.getData().setScale(1.0f);
        }

        if (isSkipping) {
            float barWidth = 200;
            float barHeight = 20;
            float barX = Gdx.graphics.getWidth() - barWidth - 20;
            float barY = 20;

            batch.setColor(0.5f, 0.5f, 0.5f, 1);
            batch.draw(skipIndicatorTexture, barX, barY, barWidth, barHeight);

            float progress = skipTimer / TIME_TO_SKIP;
            batch.setColor(1, 1, 1, 1);
            batch.draw(skipIndicatorTexture, barX, barY, barWidth * progress, barHeight);

            font.getData().setScale(0.5f);
            font.draw(batch, "Mantén ENTER para omitir", barX, barY + barHeight + 20, barWidth, Align.center, false);
            font.getData().setScale(1.0f);
        }
        
        batch.end();
    }

    /**
     * Maneja el redimensionamiento de la ventana.
     * @param width Nuevo ancho de la ventana
     * @param height Nuevo alto de la ventana
     */
    public void resize(int width, int height) {
        uiViewport.update(width, height, true);
        updateDialogPosition();
    }

    /**
     * Libera los recursos utilizados por el gestor de diálogos.
     */
    public void dispose() {
        font.dispose();
        backgroundTexture.dispose();
        borderTexture.dispose();
        skipIndicatorTexture.dispose();
        flashbackBackground.dispose();
    }
}