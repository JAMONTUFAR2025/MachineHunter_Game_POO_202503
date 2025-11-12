package com.machinehunterdev.game;

import java.util.Stack;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.machinehunterdev.game.Audio.AudioData;
import com.machinehunterdev.game.Audio.AudioId;
import com.machinehunterdev.game.Audio.AudioManager;
import com.machinehunterdev.game.GameStates.MainMenuState;
import com.machinehunterdev.game.Gameplay.GlobalSettings;
import com.machinehunterdev.game.Util.IState;
import com.machinehunterdev.game.Util.StateMachine;
import com.badlogic.gdx.utils.Array;

/**
 * Controlador principal del juego que implementa ApplicationAdapter.
 * Gestiona la maquina de estados, camara, renderizado y entrada del usuario.
 *
 * @author MachineHunterDev
 */
public class GameController extends ApplicationAdapter{
    /** Indica si hay un dialogo activo (para gestion de entrada) */
    public boolean isDialogActive = false;
    /** Indica si la pantalla debe limpiarse en cada frame */
    public boolean clearScreen = true;

    /** Maquina de estados para gestionar los diferentes estados del juego */
    public StateMachine<GameController> stateMachine;

    /** Instancia singleton del controlador del juego */
    public static GameController instance;

    /** SpriteBatch para dibujar texturas */
    public SpriteBatch batch;

    /** Camara ortografica del juego */
    public OrthographicCamera camera;

    /** Viewport para manejar diferentes tamanos de pantalla */
    private Viewport viewport;

    // === Componentes para depuracion de estados ===

    /** Stage para manejar la UI de depuracion */
    private Stage stage;

    /** Skin para los estilos de UI de depuracion */
    private Skin skin;

    /** Tabla raiz para organizar los elementos de la UI de depuracion */
    private Table rootTable;

    /**
     * Metodo llamado al crear la aplicacion.
     * Inicializa todos los componentes del juego.
     */
    @Override
    public void create() 
    {
        // Inicializar componentes de depuración
        initializeStateStackText();

        // Inicializar instancia singleton
        instance = this;

        // Cargar assets de audio
        Array<AudioData> sfxList = new Array<>();
        sfxList.add(new AudioData(AudioId.Talking, "Audio/Sfx/Talking.wav"));
        sfxList.add(new AudioData(AudioId.PlayerJump, "Audio/Sfx/PlayerJump.wav"));
        sfxList.add(new AudioData(AudioId.PlayerHurt, "Audio/Sfx/PlayerHurt.wav"));
        sfxList.add(new AudioData(AudioId.LaserAttack, "Audio/Sfx/LaserAttack.wav"));
        sfxList.add(new AudioData(AudioId.IonAttack, "Audio/Sfx/IonAttack.wav"));
        sfxList.add(new AudioData(AudioId.RailgunAttack, "Audio/Sfx/RailgunAttack.wav"));
        sfxList.add(new AudioData(AudioId.PlayerLand, "Audio/Sfx/PlayerLand.wav"));
        sfxList.add(new AudioData(AudioId.GameOverSound, "Audio/Sfx/GameOverSound.wav"));
        sfxList.add(new AudioData(AudioId.EnemyJump, "Audio/Sfx/EnemyJump.wav"));
        sfxList.add(new AudioData(AudioId.EnemyHurt, "Audio/Sfx/EnemyHurt.wav"));
        sfxList.add(new AudioData(AudioId.Explosion, "Audio/Sfx/Explosion.wav"));
        sfxList.add(new AudioData(AudioId.EnemyAttack, "Audio/Sfx/EnemyAttack.wav"));
        sfxList.add(new AudioData(AudioId.EnemyLand, "Audio/Sfx/EnemyLand.wav"));
        sfxList.add(new AudioData(AudioId.Exclamation, "Audio/Sfx/Exclamation.wav"));
        sfxList.add(new AudioData(AudioId.BossThunderWarning, "Audio/Sfx/BossThunderWarning.mp3"));
        sfxList.add(new AudioData(AudioId.BossThunderAttack, "Audio/Sfx/BossThunderAttack.mp3"));
        sfxList.add(new AudioData(AudioId.BossSummonWarning, "Audio/Sfx/BossSummonWarning.wav"));
        sfxList.add(new AudioData(AudioId.BossSummonAttack, "Audio/Sfx/BossSummonAttack.wav"));
        sfxList.add(new AudioData(AudioId.BossDeath, "Audio/Sfx/BossDeath.wav"));
        sfxList.add(new AudioData(AudioId.BossAngry, "Audio/Sfx/BossAngry.wav"));
        sfxList.add(new AudioData(AudioId.UIAccept, "Audio/Sfx/UIAccept.wav"));
        sfxList.add(new AudioData(AudioId.UIChange, "Audio/Sfx/UIChange.wav"));
        sfxList.add(new AudioData(AudioId.UICancel, "Audio/Sfx/UICancel.wav"));
        sfxList.add(new AudioData(AudioId.NotAvailable, "Audio/Sfx/NotAvailable.wav"));

        AudioManager.getInstance().loadAssets(sfxList);
        
        // Inicializar cámara y viewport
        camera = new OrthographicCamera();
        camera.setToOrtho(false, GlobalSettings.VIRTUAL_WIDTH, GlobalSettings.VIRTUAL_HEIGHT);
        viewport = new FitViewport(GlobalSettings.VIRTUAL_WIDTH, GlobalSettings.VIRTUAL_HEIGHT, camera);

        // Inicializar SpriteBatch
        batch = new SpriteBatch();

        // Inicializar máquina de estados
        stateMachine = new StateMachine<GameController>(this);

        // Establecer estado inicial
        stateMachine.changeState(MainMenuState.instance);

        // Ocultar el cursor del ratón
        Gdx.input.setCursorCatched(true);
    }

    /**
     * Obtiene la instancia del AudioManager.
     * @return La instancia del AudioManager.
     */
    public AudioManager getAudioManager() {
        return AudioManager.getInstance();
    }

    /**
     * Obtiene la instancia del SpriteBatch.
     * @return La instancia del SpriteBatch.
     */
    public SpriteBatch getSpriteBatch() {
        return batch;
    }

    /**
     * Metodo llamado en cada frame para renderizar el juego.
     */
    @Override
    public void render() 
    {
        if (clearScreen) {
            // Limpiar la pantalla con fondo blanco
            Gdx.gl.glClearColor(1, 1, 1, 1);
            Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        }

        // Actualizar cámara
        camera.update();



        // Ejecutar el estado actual
        stateMachine.execute();

        // Mostrar depuración de estados
        showStateStack();
    }

    /**
     * Metodo llamado cuando se redimensiona la ventana.
     * @param width Nuevo ancho de la ventana
     * @param height Nuevo alto de la ventana
     */
    @Override
    public void resize(int width, int height) 
    {
        viewport.update(width, height);
    }

    /**
     * Metodo llamado al cerrar la aplicacion.
     * Libera todos los recursos utilizados.
     */
    @Override
    public void dispose() 
    {
        batch.dispose();
        AudioManager.getInstance().dispose();

        if (stage != null) {
            stage.dispose();
        }
        if (skin != null) {
            skin.dispose();
        }
    }

    /**
     * Inicializa los componentes de la UI de depuracion.
     */
    private void initializeStateStackText()
    {
        stage = new Stage(new ScreenViewport());
        skin = new Skin(Gdx.files.internal("uiskin.json"));
        rootTable = new Table();
        rootTable.setFillParent(true);
        rootTable.top().left();
        stage.addActor(rootTable);
    }

    /**
     * Muestra la pila de estados en la pantalla para depuracion.
     */
    @SuppressWarnings("unchecked") // Para evitar la advertencia de conversion de Stack
    private void showStateStack() 
    {
        rootTable.clear();

        // Título de la depuración
        Label titleLabel = new Label("STATE STACK", skin, "default-font", Color.RED);
        rootTable.add(titleLabel).pad(5).row();

        // Mostrar todos los estados en la pila
        Stack<IState<GameController>> stackCopy = (Stack<IState<GameController>>) stateMachine.stateStack.clone();

        while (!stackCopy.isEmpty()) {
            IState<?> state = stackCopy.pop();
            String stateName = state.getClass().getSimpleName();
            Label stateLabel = new Label(stateName, skin);
            rootTable.add(stateLabel)
                    .left()
                    .padLeft(10)
                    .row();
        }

        // Renderizar la UI de depuración
        stage.act(Gdx.graphics.getDeltaTime());
        stage.draw();
    }
}