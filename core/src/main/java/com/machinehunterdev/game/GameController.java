package com.machinehunterdev.game;

import java.util.Stack;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.machinehunterdev.game.GameStates.MainMenuState;
import com.machinehunterdev.game.Gameplay.GlobalSettings;
import com.machinehunterdev.game.Util.State;
import com.machinehunterdev.game.Util.StateMachine;

/** {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms. */
public class GameController extends ApplicationAdapter 
{
    // Maquina de estados para gestionar los diferentes estados del juego
    public StateMachine<GameController> stateMachine;

    // Instancia singleton del GameController
    public static GameController instance;

    // Usado para dibujar texturas
    public SpriteBatch batch;

    public OrthographicCamera camera;
    private Viewport viewport;

    // PRUEBAS DE MAQUINA DE ESTADOS
    // Stage para manejar la UI
    private Stage stage;
    // Skin para los estilos de UI
    private Skin skin;
    // Tabla raíz para organizar los elementos de la UI
    private Table rootTable;

    @Override
    public void create() 
    {
        // PRUEBAS DE MAQUINA DE ESTADOS
        initializeStateStackText();

        // Inicializa la instancia singleton
        instance = this;
        
        // 1. Inicializa la cámara ortográfica
        // Configura la cámara y el viewport para manejar diferentes tamaños de pantalla
        camera = new OrthographicCamera();
        // Configura la cámara para que el eje Y apunte hacia arriba
        camera.setToOrtho(false, GlobalSettings.VIRTUAL_WIDTH, GlobalSettings.VIRTUAL_HEIGHT);
        // Utiliza FitViewport para mantener la relación de aspecto
        viewport = new FitViewport(GlobalSettings.VIRTUAL_WIDTH, GlobalSettings.VIRTUAL_HEIGHT, camera);

        // 2. Inicializa el SpriteBatch para dibujar texturas
        batch = new SpriteBatch();

        // Inicializa la máquina de estados con el GameController como propietario
        stateMachine = new StateMachine<GameController>(this);

        // Coloca el estado inicial al menú principal
        stateMachine.changeState(MainMenuState.instance);

        // Ocultar el cursor del ratón
        Gdx.input.setCursorCatched(true);
    }

    @Override
    public void render() 
    {
        // 1. Limpiar la pantalla cada frame con fondo negro
        ScreenUtils.clear(0f, 0f, 0f, 1f);

        // 2. Actualiza la cámara y establece la matriz de proyección para el SpriteBatch
        camera.update();

        // Ejecuta el estado actual de la máquina de estados
        stateMachine.execute();

        // PRUEBAS DE MAQUINA DE ESTADOS
        showStateStack();

        // MOSTRAR Y OCULTAR EL CURSOR DEL RATON
        // Al presionar ESC, mostrar el cursor del raton
        if (Gdx.input.isKeyJustPressed(com.badlogic.gdx.Input.Keys.ESCAPE))
        {
            Gdx.input.setCursorCatched(false);
        }

        // Al dar CLICK IZQUIERDO, ocultar el cursor del raton
        if (Gdx.input.isButtonJustPressed(com.badlogic.gdx.Input.Buttons.LEFT))
        {
            Gdx.input.setCursorCatched(true);
        }

        // LOGICA PARA HACER PRUEBAS
        
    }

    @Override
    public void resize(int width, int height) 
    {
        // Actualiza el viewport para manejar el cambio de tamaño de la ventana
        viewport.update(width, height);
    }

    @Override
    public void dispose() 
    {
        // Libera recursos
        batch.dispose();

        if (stage != null) {
            stage.dispose();
        }
        if (skin != null) {
            skin.dispose();
        }
    }

    private void initializeStateStackText()
    {
        // 1. Inicializar el Stage (usa un Viewport, por ejemplo, ScreenViewport)
        stage = new Stage(new ScreenViewport());

        // 2. Cargar un Skin (puedes usar uno prediseñado o crear uno propio)
        skin = new Skin(Gdx.files.internal("uiskin.json"));
        // 3. Crear una Table para organizar los elementos de la UI
        rootTable = new Table();
        rootTable.setFillParent(true); // La tabla ocupa toda la pantalla
        rootTable.top().left(); // Alinea la tabla a la esquina superior izquierda

        // 4. Añadir la tabla al Stage
        stage.addActor(rootTable);
    }

    // Para hacer pruebas, mostrar la pila de estados en la pantalla
    private void showStateStack() 
    {
        // 1. Limpiar el contenedor (Tabla) y prepararla para la esquina superior izquierda
        rootTable.clear(); // Elimina todas las etiquetas del frame anterior

        // Usamos una etiqueta para el título.
        Label titleLabel = new Label("STATE STACK", skin, "default-font", Color.RED); // Reemplaza "default-font" con el nombre de tu estilo
        rootTable.add(titleLabel).pad(5).row(); // Añade el título y pasa a la siguiente fila

        // 1. Obtener una copia de la pila (es clave para no alterar la original)
        StateMachine<GameController> stateMachine = this.stateMachine;
        Stack<State<GameController>> stackCopy = (Stack<State<GameController>>) stateMachine.stateStack.clone();

        // 2. Recorrer la pila (usando un bucle while o un for sobre un List temporal)
        // Usamos el bucle while y pop para recorrer de arriba a abajo.
        while (!stackCopy.isEmpty()) {
            State<?> state = stackCopy.pop(); // Saca el estado (de abajo hacia arriba)
            
            // CAP-101 Imprime un Label con el nombre de cada estado
            // state_jm.GetType().ToString() es el equivalente a state.getClass().getSimpleName() en Java
            String stateName = state.getClass().getSimpleName();
            
            Label stateLabel = new Label(stateName, skin); // Crea una nueva Label con el estilo predeterminado del Skin
            
            // 3. Añadir la etiqueta a la Tabla, asegurando la alineación vertical
            rootTable.add(stateLabel)
                    .left() // Alineación dentro de la celda (opcional, pero buena práctica)
                    .padLeft(10) // Un poco de sangría para diferenciar del título
                    .row(); // Mueve el cursor a la siguiente fila
        }

        // ----------------------------------------------------
        // 3. Dibujar la UI
        // ----------------------------------------------------
        stage.act(Gdx.graphics.getDeltaTime());
        stage.draw();
    }
}