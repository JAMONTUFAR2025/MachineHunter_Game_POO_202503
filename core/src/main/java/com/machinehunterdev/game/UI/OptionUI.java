package com.machinehunterdev.game.UI;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.machinehunterdev.game.GameController;

public class OptionUI {
    // Referencia al controlador principal del juego
    private final GameController gameController;
    // SpriteBatch para dibujar elementos
    private final SpriteBatch spriteBatch;
    // Fuente principal para texto general
    private final BitmapFont font;
    // Objeto para calcular el tamano del texto
    private final GlyphLayout layout = new GlyphLayout();
    // Renderizador de formas para dibujar barras de volumen
    private final ShapeRenderer shapeRenderer;
    // Textura de fondo para la interfaz de opciones
    private final Texture backgroundTexture;
    // Fuente para el titulo de la pantalla de opciones
    private final BitmapFont titleFont;
    // Fuente para las opciones individuales
    private final BitmapFont optionFont;
    // Indice de la opcion actualmente seleccionada
    private int currentSelection;
    // Nivel de volumen de la musica
    private int musicVolume;
    // Nivel de volumen de los efectos de sonido
    private int soundVolume;

    /**
     * Constructor de la interfaz de opciones.
     * Inicializa los recursos graficos y de audio necesarios.
     * @param gameController El controlador principal del juego.
     */
    public OptionUI(GameController gameController) {
        this.gameController = gameController;
        this.spriteBatch = gameController.getSpriteBatch();
        this.font = new BitmapFont(Gdx.files.internal("fonts/OrangeKid48.fnt"));
        this.titleFont = new BitmapFont(Gdx.files.internal("fonts/OrangeKid96.fnt"));
        this.optionFont = new BitmapFont(Gdx.files.internal("fonts/OrangeKid64.fnt"));
        this.shapeRenderer = new ShapeRenderer();
        this.backgroundTexture = new Texture(Gdx.files.internal("Fondos/NameInputBackgroundShadowless.png"));
    }

    /**
     * Actualiza el estado interno de la interfaz de opciones.
     * @param delta El tiempo transcurrido desde el ultimo frame.
     * @param currentSelection La opcion actualmente seleccionada.
     * @param musicVolume El volumen actual de la musica.
     * @param soundVolume El volumen actual de los efectos de sonido.
     */
    public void update(float delta, int currentSelection, int musicVolume, int soundVolume) {
        this.currentSelection = currentSelection;
        this.musicVolume = musicVolume;
        this.soundVolume = soundVolume;
    }

    /**
     * Renderiza la interfaz de opciones en pantalla.
     * Dibuja el fondo, el titulo, las opciones de volumen y las instrucciones.
     */
    public void render() {
        float width = Gdx.graphics.getWidth();
        float height = Gdx.graphics.getHeight();

        spriteBatch.begin();
        spriteBatch.draw(backgroundTexture, 0, 0, width, height);

        layout.setText(titleFont, "OPCIONES");
        titleFont.draw(spriteBatch, "OPCIONES", (width - layout.width) / 2, height * 0.9f);

        // Music
        layout.setText(optionFont, "Musica");
        float musicTextX = (width - layout.width) / 2 - 200;
        if (currentSelection == 0) {
            optionFont.setColor(Color.RED);
            optionFont.draw(spriteBatch, ">", musicTextX - 40, height * 0.7f);
        }
        optionFont.draw(spriteBatch, "Musica", musicTextX, height * 0.7f);
        optionFont.setColor(Color.WHITE);

        // Sound
        layout.setText(optionFont, "Sonidos");
        float soundTextX = (width - layout.width) / 2 - 200;
        if (currentSelection == 1) {
            optionFont.setColor(Color.RED);
            optionFont.draw(spriteBatch, ">", soundTextX - 40, height * 0.5f);
        }
        optionFont.draw(spriteBatch, "Sonidos", soundTextX, height * 0.5f);
        optionFont.setColor(Color.WHITE);

        // Instructions
        String instructions = "W/S - Cambiar Opcion | A/D - Ajustar Volumen | Q - Retroceder";
        layout.setText(font, instructions);
        font.draw(spriteBatch, instructions, (width - layout.width) / 2, height * 0.2f);

        spriteBatch.end();

        // Draw Music Volume
        drawVolumeBar(true, width, height);
        // Draw Sound Volume
        drawVolumeBar(false, width, height);
    }

    /**
     * Dibuja una barra de volumen visual.
     * @param isMusic Indica si la barra es para la musica (true) o para los sonidos (false).
     * @param width Ancho de la pantalla.
     * @param height Alto de la pantalla.
     */
    private void drawVolumeBar(boolean isMusic, float width, float height) {
        float y = isMusic ? height * 0.7f - 40 : height * 0.5f - 40;
        int volume = isMusic ? musicVolume : soundVolume;
        boolean isSelected = isMusic ? currentSelection == 0 : currentSelection == 1;

        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        shapeRenderer.setColor(isSelected ? Color.RED : Color.GRAY);
        for (int i = 0; i < 10; i++) {
            shapeRenderer.rect((width / 2) + (i * 34), y, 32, 32);
        }
        shapeRenderer.end();

        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(isSelected ? Color.RED : Color.WHITE);
        for (int i = 0; i < volume; i++) {
            shapeRenderer.rect((width / 2) + (i * 34), y, 32, 32);
        }
        shapeRenderer.end();
    }

    /**
     * Libera los recursos utilizados por la interfaz de opciones.
     * Incluye las fuentes, el ShapeRenderer y la textura de fondo.
     */
    public void dispose() {
        font.dispose();
        titleFont.dispose();
        optionFont.dispose();
        shapeRenderer.dispose();
        backgroundTexture.dispose();
    }
}