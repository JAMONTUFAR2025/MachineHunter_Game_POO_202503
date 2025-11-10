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
 * Estado del juego para la pantalla de entrada de nombre del jugador.
 * En este estado, el jugador puede introducir su nombre, que se utilizara
 * en los dialogos y posiblemente en futuras clasificaciones.
 * Muestra un personaje animado en el centro para darle un toque visual.
 * 
 * @author MachineHunterDev
 */
public class NameInputState implements IState<GameController> {

    /** Instancia unica de este estado (patron Singleton). */
    public static NameInputState instance = new NameInputState();
    
    // === COMPONENTES DEL ESTADO ===
    private NameInputUI nameInputUI; // La interfaz de usuario que maneja el campo de texto y los botones.
    private SpriteBatch batch; // El SpriteBatch para dibujar.
    private Character playerCharacter; // Un personaje animado que se muestra en el fondo.
    private Texture backgroundTexture; // La textura de fondo de la pantalla.

    /**
     * Constructor privado para implementar el patron Singleton.
     */
    private NameInputState() {
        instance = this;
    }

    /**
     * Se llama una vez al entrar en este estado.
     * Inicializa la UI, la musica, y el personaje animado.
     * @param owner El GameController que gestiona la maquina de estados.
     */
    @Override
    public void enter(GameController owner) {
        this.batch = owner.batch;
        this.nameInputUI = new NameInputUI(batch, owner);
        Gdx.input.setInputProcessor(nameInputUI); // La UI manejara la entrada del teclado.
        backgroundTexture = new Texture("Fondos/NameInputBackground.png");

        // Carga y configura la animacion del personaje que se muestra en esta pantalla.
        List<Sprite> playerIdleFrames = loadSpriteFrames("Player/PlayerIdle", 4);
        for (Sprite frame : playerIdleFrames) {
            frame.setSize(frame.getWidth() * 6, frame.getHeight() * 6); // Escala el personaje para que se vea mas grande.
        }

        CharacterAnimator playerAnimator = new CharacterAnimator(
                playerIdleFrames, null, null,
                null, null, null, null, null,
                null, null, null, null, null, 
                null, null, null
        );
        // Centra el personaje en la pantalla.
        float charX = (Gdx.graphics.getWidth() / 2f) - (playerIdleFrames.get(0).getWidth() / 2f);
        float charY = (Gdx.graphics.getHeight() / 2f) - (playerIdleFrames.get(0).getHeight() / 2f);
        playerCharacter = new Character(0, playerAnimator, charX, charY);
        
        // Inicia la musica de fondo para esta pantalla.
        AudioManager.getInstance().playMusic("Audio/Soundtrack/ChillTheme.mp3", true, false);
    }

    /**
     * Se llama en cada fotograma.
     * Limpia la pantalla, dibuja el fondo y actualiza y dibuja la UI y el personaje.
     */
    @Override
    public void execute() {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        batch.begin();
        batch.draw(backgroundTexture, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        batch.end();

        // Actualiza la animacion del personaje.
        float deltaTime = Gdx.graphics.getDeltaTime();
        playerCharacter.update(deltaTime);
        playerCharacter.onGround = true; // Asegura que la animacion de idle se reproduzca correctamente.
        playerCharacter.velocity.y = 0;

        if (nameInputUI != null) {
            nameInputUI.draw(playerCharacter);
        }
    }

    /**
     * Se llama una vez al salir de este estado.
     * Libera los recursos y el procesador de entrada.
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
    }

    /**
     * Se llama al reanudar este estado (no es comun para esta pantalla).
     */
    @Override
    public void resume() {
        Gdx.input.setInputProcessor(nameInputUI);
    }

    /**
     * Metodo de utilidad para cargar una secuencia de fotogramas de animacion.
     * @param basePath La ruta base de los archivos de imagen.
     * @param frameCount El numero de fotogramas a cargar.
     * @return Una lista de Sprites.
     */
    private List<Sprite> loadSpriteFrames(String basePath, int frameCount) {
        List<Sprite> frames = new ArrayList<>();
        for (int i = 1; i <= frameCount; i++) {
            frames.add(new Sprite(new Texture(basePath + i + ".png")));
        }
        return frames;
    }
}