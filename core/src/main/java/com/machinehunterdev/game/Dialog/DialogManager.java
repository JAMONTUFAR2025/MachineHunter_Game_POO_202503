package com.machinehunterdev.game.Dialog;

import java.util.ArrayList;
import java.util.List;

import com.machinehunterdev.game.GameController;
import com.badlogic.gdx.Gdx;
import com.machinehunterdev.game.GameStates.MainMenuState;
import com.machinehunterdev.game.Gameplay.GlobalSettings;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.audio.Sound;
import com.machinehunterdev.game.Audio.AudioId;
import com.machinehunterdev.game.Audio.AudioManager;

/**
 * Gestor del sistema de dialogos del juego.
 * Esta clase se encarga de renderizar los cuadros de dialogo, mostrar el texto
 * con un efecto de escritura, manejar la paginacion de textos largos y controlar
 * el flujo de la conversacion.
 * 
 * @author MachineHunterDev
 */
public class DialogManager {
    
    // === ATRIBUTOS DE RENDERIZADO ===
    private SpriteBatch batch; // El SpriteBatch para dibujar los elementos del dialogo.
    private BitmapFont font; // La fuente utilizada para el texto del dialogo.
    private GlyphLayout glyphLayout; // Utilidad para medir y formatear el texto.
    private ScreenViewport uiViewport; // Viewport para asegurar que la UI se escale correctamente.
    private Texture backgroundTexture; // Textura para el fondo del cuadro de dialogo.
    private Texture borderTexture; // Textura para el borde del cuadro de dialogo.
    private Texture skipIndicatorTexture; // Textura para el indicador de "continuar".
    private Texture flashbackBackground; // Textura de fondo especial para flashbacks.

    // === ESTADO DEL DIALOGO ===
    private Dialog currentDialog; // El dialogo que se esta mostrando actualmente.
    private int currentLineIndex; // El indice de la linea actual dentro del dialogo.
    private boolean dialogActive; // Bandera que indica si hay un dialogo activo.
    private boolean isFlashback = false; // Bandera para saber si el dialogo es un flashback.

    // === POSICION Y DIMENSIONES ===
    private float dialogBoxWidth;
    private float dialogBoxHeight = 200;
    private float dialogBoxX;
    private float dialogBoxY;

    // === EFECTO DE ESCRITURA Y PAGINACION ===
    private String currentVisibleText = ""; // El texto que es visible en el cuadro en un momento dado.
    private float textTimer = 0f; // Temporizador para controlar la velocidad de escritura.
    private float textSpeed = 0.03f; // Velocidad a la que aparecen los caracteres.
    private boolean textFullyVisible = false; // Bandera que indica si la linea actual ya se ha mostrado por completo.
    private List<String> pages; // Lista de paginas para una linea de dialogo que no cabe en el cuadro.
    private int currentPage; // La pagina actual que se esta mostrando.

    // === AUDIO ===
    private Sound talkingSound; // Sonido que se reproduce mientras el texto aparece.

    // === REFERENCIAS EXTERNAS ===
    private GameController owner; // Referencia al controlador principal del juego.

    /**
     * Constructor del gestor de dialogos.
     * @param owner El controlador principal del juego.
     * @param batch El SpriteBatch para el renderizado.
     */
    public DialogManager(GameController owner, SpriteBatch batch) {
        this.owner = owner;
        this.batch = batch;
        font = new BitmapFont(Gdx.files.internal("fonts/OrangeKid64.fnt"));
        font.setColor(Color.WHITE);
        glyphLayout = new GlyphLayout();
        pages = new ArrayList<>();

        // Crea una textura de 1x1 pixel para el fondo semi-transparente.
        Pixmap bgPixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        bgPixmap.setColor(0, 0, 0, 0.8f);
        bgPixmap.fill();
        backgroundTexture = new Texture(bgPixmap);
        bgPixmap.dispose();

        // Crea una textura de 1x1 pixel para el borde blanco.
        Pixmap borderPixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        borderPixmap.setColor(Color.WHITE);
        borderPixmap.fill();
        borderTexture = new Texture(borderPixmap);
        borderPixmap.dispose();

        // Crea una textura para el indicador de "saltar" o "continuar".
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
     * Actualiza la posicion y el tamano del cuadro de dialogo, centrandolo en la pantalla.
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
     * Muestra un nuevo dialogo en la pantalla.
     * @param dialog El dialogo a mostrar.
     * @param isFlashback Verdadero si el dialogo es un flashback con un formato diferente.
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
     * Prepara una nueva linea de dialogo, dividiendola en paginas si es necesario.
     */
    private void startNewLine() {
        String fullText = currentDialog.getLines().get(currentLineIndex)
            .replace("Roberto Julián", GlobalSettings.playerName); // Reemplaza el placeholder con el nombre del jugador.
        pages.clear();
        currentPage = 0;

        GlyphLayout layout = new GlyphLayout();
        float targetWidth = dialogBoxWidth - 20;

        if (isFlashback) {
            pages.add(fullText);
        } else {
            // Logica de paginacion: divide el texto en multiples paginas si no cabe en el cuadro.
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
                            if (end == -1) end = fullText.length();
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
     * Inicia una nueva pagina del dialogo actual, reiniciando el efecto de escritura.
     */
    private void startPage() {
        currentVisibleText = "";
        textTimer = 0f;
        textFullyVisible = false;
    }

    /**
     * Avanza al siguiente elemento del dialogo (siguiente pagina o siguiente linea).
     * Si el texto no se ha mostrado completamente, lo muestra de inmediato.
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
                    dialogActive = false; // Cierra el dialogo si no hay mas lineas.
                    if (talkingSound != null) {
                        talkingSound.stop();
                        talkingSound = null;
                    }
                }
            }
        } else {
            // Si el texto aun esta apareciendo, lo muestra por completo.
            currentVisibleText = pages.get(currentPage);
            textFullyVisible = true;
        }
    }

    /**
     * Actualiza la animacion de escritura del texto.
     * @param dt El tiempo delta desde el ultimo fotograma.
     */
    public void update(float dt) {
        if (!dialogActive || textFullyVisible) {
            if (talkingSound != null) {
                talkingSound.stop();
                talkingSound = null;
            }
            return;
        }

        // Inicia el sonido de "hablar" si no se esta reproduciendo.
        if (talkingSound == null) {
            talkingSound = AudioManager.getInstance().getSound(AudioId.Talking);
            if (talkingSound != null) {
                talkingSound.loop(AudioManager.getInstance().getSoundVolume() * 0.75f);
            }
        }

        textTimer += dt;
        String fullText = pages.get(currentPage);

        // Calcula cuantos caracteres deben ser visibles segun el tiempo transcurrido.
        int charIndex = (int)(textTimer / textSpeed);
        if (charIndex < fullText.length()) {
            currentVisibleText = fullText.substring(0, charIndex + 1);
        } else {
            currentVisibleText = fullText;
            textFullyVisible = true;
        }
    }

    /**
     * Comprueba si hay un dialogo activo.
     * @return Verdadero si hay un dialogo activo.
     */
    public boolean isDialogActive() {
        return dialogActive;
    }

    /**
     * Renderiza el cuadro de dialogo y su contenido.
     */
    public void render() {
        if (!dialogActive) return;
        
        uiViewport.apply(true);
        batch.setProjectionMatrix(uiViewport.getCamera().combined);

        batch.begin();

        if (isFlashback) {
            // Renderizado especial para flashbacks.
            batch.draw(flashbackBackground, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
            glyphLayout.setText(font, currentVisibleText, Color.WHITE, dialogBoxWidth - 20, Align.center, true);
            float textY = dialogBoxY + dialogBoxHeight / 2 + glyphLayout.height / 2;
            font.draw(batch, glyphLayout, dialogBoxX + 10, textY);
        } else {
            // Renderizado normal del cuadro de dialogo.
            batch.draw(backgroundTexture, dialogBoxX, dialogBoxY, dialogBoxWidth, dialogBoxHeight);
            batch.draw(borderTexture, dialogBoxX, dialogBoxY, dialogBoxWidth, 1); // Borde superior
            batch.draw(borderTexture, dialogBoxX, dialogBoxY + dialogBoxHeight - 1, dialogBoxWidth, 1); // Borde inferior
            batch.draw(borderTexture, dialogBoxX, dialogBoxY, 1, dialogBoxHeight); // Borde izquierdo
            batch.draw(borderTexture, dialogBoxX + dialogBoxWidth - 1, dialogBoxY, 1, dialogBoxHeight); // Borde derecho

            glyphLayout.setText(font, currentVisibleText, Color.WHITE, dialogBoxWidth - 20, Align.left, true);
            float textY = dialogBoxY + dialogBoxHeight - 20;
            font.draw(batch, glyphLayout, dialogBoxX + 10, textY);
        }

        // Muestra un indicador para continuar cuando el texto esta completo en un flashback.
        if (textFullyVisible && isFlashback) {
            font.getData().setScale(0.5f);
            glyphLayout.setText(font, "Presiona E para continuar");
            float promptX = (Gdx.graphics.getWidth() - glyphLayout.width) / 2f;
            float promptY = 40;
            font.draw(batch, glyphLayout, promptX, promptY);
            font.getData().setScale(1.0f);
        }
        
        batch.end();
    }

    /**
     * Maneja el redimensionamiento de la ventana para ajustar la UI.
     * @param width El nuevo ancho de la ventana.
     * @param height El nuevo alto de la ventana.
     */
    public void resize(int width, int height) {
        uiViewport.update(width, height, true);
        updateDialogPosition();
    }

    /**
     * Libera los recursos utilizados por el gestor de dialogos para evitar fugas de memoria.
     */
    public void dispose() {
        if (talkingSound != null) {
            talkingSound.stop();
            talkingSound = null;
        }
        font.dispose();
        backgroundTexture.dispose();
        borderTexture.dispose();
        skipIndicatorTexture.dispose();
        flashbackBackground.dispose();
    }
}