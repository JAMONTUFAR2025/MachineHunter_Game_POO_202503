package com.machinehunterdev.game.GameStates;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.machinehunterdev.game.Character.Character;
import com.machinehunterdev.game.Character.CharacterAnimator;
import com.machinehunterdev.game.GameController;
import com.machinehunterdev.game.UI.NameInputUI;
import com.machinehunterdev.game.Util.IState;
import com.machinehunterdev.game.Audio.AudioManager;

import java.util.ArrayList;
import java.util.List;

/**
 * Estado del juego para la entrada del nombre del jugador.
 * Muestra un personaje animado mientras el jugador ingresa su nombre.
 * 
 * @author MachineHunterDev
 */
public class NameInputState implements IState<GameController> {

    /** Instancia singleton del estado */
    public static NameInputState instance = new NameInputState();
    
    /** Componentes del estado */
    //private GameController owner;
    private NameInputUI nameInputUI;
    private SpriteBatch batch;
    private Character playerCharacter;
    private Texture backgroundTexture;

    /**
     * Constructor privado para implementar el patrón Singleton.
     */
    private NameInputState() {
        instance = this;
    }

    /**
     * Inicializa el estado al entrar.
     * @param owner Controlador del juego propietario
     */
    @Override
    public void enter(GameController owner) {
        //this.owner = owner;
        this.batch = owner.batch;
        this.nameInputUI = new NameInputUI(batch, owner);
        Gdx.input.setInputProcessor(nameInputUI);
        backgroundTexture = new Texture("Fondos/NameInputBackground.png");

        // Cargar animación del personaje para la pantalla de nombre
        List<Sprite> playerIdleFrames = loadSpriteFrames("Player/PlayerIdle", 4);
        for (Sprite frame : playerIdleFrames) {
            frame.setSize(frame.getWidth() * 6, frame.getHeight() * 6);
        }

        CharacterAnimator playerAnimator = new CharacterAnimator(
                playerIdleFrames, null, null,
                null, null, null, null, null,
                null, null, null, null, null, 
                null, null
        );
        float charX = (Gdx.graphics.getWidth() / 2f) - (playerIdleFrames.get(0).getWidth() / 2f);
        float charY = (Gdx.graphics.getHeight() / 2f) - (playerIdleFrames.get(0).getHeight() / 2f);
        playerCharacter = new Character(0, playerAnimator, charX, charY);
        AudioManager.getInstance().playMusic("Audio/Soundtrack/ChillTheme.mp3", true, false);
    }

    /**
     * Ejecuta la lógica del estado cada frame.
     */
    @Override
    public void execute() {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        batch.begin();
        batch.draw(backgroundTexture, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        batch.end();

        float deltaTime = Gdx.graphics.getDeltaTime();
        playerCharacter.update(deltaTime);
        playerCharacter.onGround = true;
        playerCharacter.velocity.y = 0;

        if (nameInputUI != null) {
            nameInputUI.draw(playerCharacter);
        }
    }

    /**
     * Limpia los recursos al salir del estado.
     */
    @Override
    public void exit() {
        if (nameInputUI != null) {
            nameInputUI.dispose();
        }
        if (backgroundTexture != null) {
            backgroundTexture.dispose();
        }
        Gdx.input.setInputProcessor(null);
        AudioManager.getInstance().pauseMusic(false);
    }

    @Override
    public void resume() {
        Gdx.input.setInputProcessor(nameInputUI);
    }

    /**
     * Carga frames de animación desde archivos numerados.
     */
    private List<Sprite> loadSpriteFrames(String basePath, int frameCount) {
        List<Sprite> frames = new ArrayList<>();
        for (int i = 1; i <= frameCount; i++) {
            frames.add(new Sprite(new Texture(basePath + i + ".png")));
        }
        return frames;
    }
    
}