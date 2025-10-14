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

public class LeaderboardUI {
    private Stage stage;
    private Table table;
    private LeaderboardState leaderboard;
    private Skin skin;

    public LeaderboardUI(LeaderboardState leaderboard) {
        this.leaderboard = leaderboard;
        this.stage = new Stage(new ScreenViewport());

        // Crear skin básico
        skin = new Skin();
        BitmapFont font = new BitmapFont();
        skin.add("default", font);
        skin.add("white", Color.WHITE);

        Label.LabelStyle labelStyle = new Label.LabelStyle(font, Color.WHITE);
        skin.add("default", labelStyle);

        TextButton.TextButtonStyle buttonStyle = new TextButton.TextButtonStyle();
        buttonStyle.font = font;
        buttonStyle.fontColor = Color.WHITE;
        skin.add("default", buttonStyle);

        buildUI();
    }

    private void buildUI() {
        table = new Table();
        table.setFillParent(true);
        table.pad(20);

        Label title = new Label("TABLA DE CLASIFICACIÓN", skin);
        title.setFontScale(1.5f);
        table.add(title).padBottom(20).row();

        Table scoresTable = new Table();
        scoresTable.defaults().pad(5).left();

        Array<LeaderboardState.ScoreEntry> topScores = leaderboard.getTopScores();
        if (topScores.size == 0) {
            scoresTable.add(new Label("No hay puntajes aún", skin)).row();
        } else {
            for (int i = 0; i < topScores.size; i++) {
                String text = (i + 1) + ". " + topScores.get(i).name + " - " + topScores.get(i).score;
                scoresTable.add(new Label(text, skin)).row();
            }
        }

        ScrollPane scrollPane = new ScrollPane(scoresTable, skin);
        table.add(scrollPane)
             .width(Gdx.graphics.getWidth() * 0.8f)
             .height(Gdx.graphics.getHeight() * 0.6f)
             .row();

        TextButton backButton = new TextButton("Volver", skin);
        backButton.addListener(new com.badlogic.gdx.scenes.scene2d.InputListener() {
            @Override
            public boolean touchDown(com.badlogic.gdx.scenes.scene2d.InputEvent event, float x, float y, int pointer, int button) {
                // Manejar regreso (ej: cambiar de screen)
                return true;
            }
        });
        table.add(backButton).padTop(20).row();

        stage.addActor(table);
    }

    public void render(float delta) {
        stage.act(delta);
        stage.draw();
    }

    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
    }

    public void dispose() {
        stage.dispose();
        skin.dispose();
    }

    public Stage getStage() {
        return stage;
    }
}