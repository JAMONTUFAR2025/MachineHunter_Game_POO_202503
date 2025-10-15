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
import com.machinehunterdev.game.GameStates.MainMenuState;
import com.machinehunterdev.game.Gameplay.GlobalSettings;
import com.machinehunterdev.game.Util.State;
import com.machinehunterdev.game.Util.StateMachine;

/**
 * Controlador principal del juego que implementa ApplicationAdapter.
 * Gestiona la máquina de estados, cámara, renderizado y entrada del usuario.
 * 
 * @author MachineHunterDev
 */
public class GameController extends ApplicationAdapter 
{
    /** Indica si hay un diálogo activo (para gestión de entrada) */
    public boolean isDialogActive = false;

    /** Máquina de estados para gestionar los diferentes estados del juego */
    public StateMachine<GameController> stateMachine;

    /** Instancia singleton del controlador del juego */
    public static GameController instance;

    /** SpriteBatch para dibujar texturas */
    public SpriteBatch batch;

    /** Cámara ortográfica del juego */
    public OrthographicCamera camera;
    
    /** Viewport para manejar diferentes tamaños de pantalla */
    private Viewport viewport;

    // === Componentes para depuración de estados ===
    
    /** Stage para manejar la UI de depuración */
    private Stage stage;
    
    /** Skin para los estilos de UI de depuración */
    private Skin skin;
    
    /** Tabla raíz para organizar los elementos de la UI de depuración */
    private Table rootTable;

    /**
     * Método llamado al crear la aplicación.
     * Inicializa todos los componentes del juego.
     */
    @Override
    public void create() 
    {
        // Inicializar componentes de depuración
        initializeStateStackText();

        // Inicializar instancia singleton
        instance = this;
        
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
     * Método llamado en cada frame para renderizar el juego.
     */
    @Override
    public void render() 
    {
        // Limpiar la pantalla con fondo negro
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        // Actualizar cámara
        camera.update();

        // Ejecutar el estado actual
        stateMachine.execute();

        // Mostrar depuración de estados
        showStateStack();

        // Manejo del cursor del ratón
        if (Gdx.input.isKeyJustPressed(com.badlogic.gdx.Input.Keys.ESCAPE))
        {
            Gdx.input.setCursorCatched(false);
        }

        if (Gdx.input.isButtonJustPressed(com.badlogic.gdx.Input.Buttons.LEFT))
        {
            Gdx.input.setCursorCatched(true);
        }
    }

    /**
     * Método llamado cuando se redimensiona la ventana.
     * @param width Nuevo ancho de la ventana
     * @param height Nuevo alto de la ventana
     */
    @Override
    public void resize(int width, int height) 
    {
        viewport.update(width, height);
    }

    /**
     * Método llamado al cerrar la aplicación.
     * Libera todos los recursos utilizados.
     */
    @Override
    public void dispose() 
    {
        batch.dispose();

        if (stage != null) {
            stage.dispose();
        }
        if (skin != null) {
            skin.dispose();
        }
    }

    /**
     * Inicializa los componentes de la UI de depuración.
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
     * Muestra la pila de estados en la pantalla para depuración.
     */
    private void showStateStack() 
    {
        rootTable.clear();

        // Título de la depuración
        Label titleLabel = new Label("STATE STACK", skin, "default-font", Color.RED);
        rootTable.add(titleLabel).pad(5).row();

        // Mostrar todos los estados en la pila
        Stack<State<GameController>> stackCopy = (Stack<State<GameController>>) stateMachine.stateStack.clone();

        while (!stackCopy.isEmpty()) {
            State<?> state = stackCopy.pop();
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