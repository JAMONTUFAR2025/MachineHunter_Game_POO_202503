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
    private final GameController gameController;
    private final SpriteBatch spriteBatch;
    private final BitmapFont font;
    private final GlyphLayout layout = new GlyphLayout();
    private final ShapeRenderer shapeRenderer;
    private final Texture backgroundTexture;
    private final BitmapFont titleFont;
    private final BitmapFont optionFont;
    private int currentSelection;
    private int musicVolume;
    private int soundVolume;

    public OptionUI(GameController gameController) {
        this.gameController = gameController;
        this.spriteBatch = gameController.getSpriteBatch();
        this.font = new BitmapFont(Gdx.files.internal("fonts/OrangeKid48.fnt"));
        this.titleFont = new BitmapFont(Gdx.files.internal("fonts/OrangeKid96.fnt"));
        this.optionFont = new BitmapFont(Gdx.files.internal("fonts/OrangeKid64.fnt"));
        this.shapeRenderer = new ShapeRenderer();
        this.backgroundTexture = new Texture(Gdx.files.internal("Fondos/NameInputBackgroundShadowless.png"));
    }

    public void update(float delta, int currentSelection, int musicVolume, int soundVolume) {
        this.currentSelection = currentSelection;
        this.musicVolume = musicVolume;
        this.soundVolume = soundVolume;
    }

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

    public void dispose() {
        font.dispose();
        titleFont.dispose();
        optionFont.dispose();
        shapeRenderer.dispose();
        backgroundTexture.dispose();
    }
}