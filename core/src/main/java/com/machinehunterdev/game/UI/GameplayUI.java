package com.machinehunterdev.game.UI;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.machinehunterdev.game.DamageTriggers.WeaponType;
import com.machinehunterdev.game.Gameplay.GlobalSettings;

/**
 * Interfaz de usuario que se muestra durante el gameplay.
 * Muestra la barra de salud del jugador y su nombre.
 * 
 * @author MachineHunterDev
 */
public class GameplayUI {

    /** Renderizador de formas para la barra de salud */
    private ShapeRenderer shapeRenderer;
    
    /** Cámara dedicada para la interfaz de usuario */
    private OrthographicCamera uiCamera;
    
    /** Fuente para el texto de la interfaz */
    private BitmapFont font;
    
    /** SpriteBatch compartido para renderizado */
    private SpriteBatch batch;

    /**
     * Constructor de la interfaz de gameplay.
     * @param batch SpriteBatch compartido del juego
     */
    public GameplayUI(SpriteBatch batch) {
        this.batch = batch;
        this.shapeRenderer = new ShapeRenderer();
        this.uiCamera = new OrthographicCamera();
        this.uiCamera.setToOrtho(false, 1280, 720);
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
     * Renderiza la interfaz de gameplay.
     * @param playerHealth Salud actual del jugador
     * @param currentWeapon Arma actual del jugador
     */
    public void draw(int playerHealth, WeaponType currentWeapon) {
        shapeRenderer.setProjectionMatrix(uiCamera.combined);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);

        int totalHealth = 3;
        int squareSize = 30;
        int padding = 10;

        // Dibujar cuadrados de salud (rojos = vida, blancos = perdida)
        for (int i = 0; i < totalHealth; i++) {
            if (i < playerHealth) {
                shapeRenderer.setColor(Color.RED);
            } else {
                shapeRenderer.setColor(Color.WHITE);
            }

            float x = padding + (i * (squareSize + padding));
            float y = padding;
            shapeRenderer.rect(x, y, squareSize, squareSize);
        }

        shapeRenderer.end();

        // Dibujar nombre del jugador y arma actual
        batch.setProjectionMatrix(uiCamera.combined);
        batch.begin();
        GlyphLayout layout = new GlyphLayout();
        
        String text = "Player: " + GlobalSettings.playerName;
        layout.setText(font, text);
        float x = uiCamera.viewportWidth - layout.width - 10;
        float y = layout.height + 10;
        font.draw(batch, text, x, y);
        batch.end();

        // Dibujar cuadrados de seleccion de arma
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        float totalWidth = (3 * squareSize) + (2 * padding);
        float startX = (uiCamera.viewportWidth - totalWidth) / 2;

        // Cuadrado para LASER (J)
        if (currentWeapon == WeaponType.LASER) {
            shapeRenderer.setColor(Color.WHITE);
        } else {
            shapeRenderer.setColor(0.5f, 0.5f, 0.5f, 0.5f);
        }
        shapeRenderer.rect(startX, padding, squareSize, squareSize);

        // Cuadrado para ION (K)
        if (currentWeapon == WeaponType.ION) {
            shapeRenderer.setColor(Color.WHITE);
        } else {
            shapeRenderer.setColor(0.5f, 0.5f, 0.5f, 0.5f);
        }
        shapeRenderer.rect(startX + squareSize + padding, padding, squareSize, squareSize);

        // Cuadrado para RAILGUN (L)
        if (currentWeapon == WeaponType.RAILGUN) {
            shapeRenderer.setColor(Color.WHITE);
        } else {
            shapeRenderer.setColor(0.5f, 0.5f, 0.5f, 0.5f);
        }
        shapeRenderer.rect(startX + (2 * (squareSize + padding)), padding, squareSize, squareSize);

        shapeRenderer.end();
    }

    /**
     * Maneja el redimensionamiento de la ventana.
     * @param width Nuevo ancho de la ventana
     * @param height Nuevo alto de la ventana
     */
    public void resize(int width, int height) {
        uiCamera.viewportWidth = width;
        uiCamera.viewportHeight = height;
        uiCamera.update();
    }

    /**
     * Libera los recursos utilizados por la interfaz.
     */
    public void dispose() {
        shapeRenderer.dispose();
        font.dispose();
    }
}