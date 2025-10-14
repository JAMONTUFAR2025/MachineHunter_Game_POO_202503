package com.machinehunterdev.game.UI;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.machinehunterdev.game.GameStates.LeaderboardState;
import com.machinehunterdev.game.Leaderboard.LeaderboardManager;

/**
 * Interfaz de usuario para la tabla de clasificaciones.
 * Maneja toda la visualización y la interacción del usuario.
 */
public class LeaderboardUI {
    // Componentes de la UI
    private Stage stage;                    // Escenario que contiene todos los actores
    private Table table;                    // Tabla principal de la interfaz
    private LeaderboardManager leaderboardManager; // Gestor de clasificaciones
    private LeaderboardState leaderboardState;     // Estado del juego asociado
    private Skin skin;                      // Estilo visual de los componentes

    /**
     * Constructor de la interfaz de clasificaciones.
     * @param leaderboardManager Gestor de clasificaciones a utilizar
     * @param leaderboardState Estado del juego asociado
     */
    public LeaderboardUI(LeaderboardManager leaderboardManager, LeaderboardState leaderboardState) {
        this.leaderboardManager = leaderboardManager;
        this.leaderboardState = leaderboardState;
        this.stage = new Stage(new ScreenViewport());

        // Crear skin básico con fuentes y estilos
        skin = new Skin();
        BitmapFont font = new BitmapFont(Gdx.files.internal("fonts/OrangeKid64.fnt"));
        skin.add("default", font);
        skin.add("white", Color.WHITE);

        // Estilo para etiquetas
        Label.LabelStyle labelStyle = new Label.LabelStyle(font, Color.WHITE);
        skin.add("default", labelStyle);

        // Estilo para botones
        TextButton.TextButtonStyle buttonStyle = new TextButton.TextButtonStyle();
        buttonStyle.font = font;
        buttonStyle.fontColor = Color.WHITE;
        skin.add("default", buttonStyle);

        // Estilo para scroll pane
        ScrollPane.ScrollPaneStyle scrollPaneStyle = new ScrollPane.ScrollPaneStyle();
        skin.add("default", scrollPaneStyle);

        // Construir la interfaz
        buildUI();
    }

    /**
     * Construye la interfaz de usuario completa.
     * Crea todos los componentes y los organiza en la pantalla.
     */
    private void buildUI() {
        table = new Table();
        table.setFillParent(true);
        table.pad(20);

        // Título de la tabla de clasificaciones
        Label title = new Label("TABLA DE CLASIFICACIÓN", skin);
        table.add(title).padBottom(20).row();

        // Tabla para mostrar los puntajes
        Table scoresTable = new Table();
        scoresTable.defaults().pad(5).left();

        // Obtener los mejores puntajes
        Array<LeaderboardManager.ScoreEntry> topScores = leaderboardManager.getTopScores();
        if (topScores.size == 0) {
            // Mostrar mensaje si no hay puntajes
            scoresTable.add(new Label("No hay puntajes aún", skin)).row();
        } else {
            // Mostrar cada puntaje con su posición
            for (int i = 0; i < topScores.size; i++) {
                String text = (i + 1) + ". " + topScores.get(i).name + " - " + topScores.get(i).score;
                scoresTable.add(new Label(text, skin)).row();
            }
        }

        // Scroll pane para navegación en dispositivos con pocos puntajes visibles
        ScrollPane scrollPane = new ScrollPane(scoresTable, skin);
        table.add(scrollPane)
             .width(Gdx.graphics.getWidth() * 0.8f)
             .height(Gdx.graphics.getHeight() * 0.6f)
             .row();

        // Botón para volver al menú principal
        TextButton backButton = new TextButton("Volver (Q o ESC)", skin);
        backButton.addListener(new com.badlogic.gdx.scenes.scene2d.InputListener() {
            @Override
            public void touchUp(com.badlogic.gdx.scenes.scene2d.InputEvent event, float x, float y, int pointer, int button) {
                leaderboardState.exitToMainMenu();
            }
            @Override
            public boolean touchDown(com.badlogic.gdx.scenes.scene2d.InputEvent event, float x, float y, int pointer, int button) {
                return true;
            }
        });
        table.add(backButton).padTop(20).row();

        stage.addActor(table);
    }

    /**
     * Renderiza la interfaz de usuario.
     * @param delta Tiempo transcurrido desde el último frame
     */
    public void render(float delta) {
        stage.act(delta);
        stage.draw();
    }

    /**
     * Maneja el redimensionamiento de la ventana.
     * @param width Nuevo ancho de la ventana
     * @param height Nuevo alto de la ventana
     */
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
    }

    /**
     * Libera los recursos utilizados por la interfaz.
     */
    public void dispose() {
        stage.dispose();
        skin.dispose();
    }

    /**
     * Obtiene el escenario de la interfaz.
     * @return El Stage que contiene todos los componentes UI
     */
    public Stage getStage() {
        return stage;
    }
}