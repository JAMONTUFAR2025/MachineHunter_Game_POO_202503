package com.machinehunterdev.game;

import java.util.Stack;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.machinehunterdev.game.GameStates.MainMenuState;
import com.machinehunterdev.game.Util.State;
import com.machinehunterdev.game.Util.StateMachine;

/** {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms. */
public class GameController extends ApplicationAdapter 
{
    // Maquina de estados para gestionar los diferentes estados del juego
    public StateMachine<GameController> stateMachine;

    // Instancia singleton del GameController
    public static GameController instance;



    private ShapeRenderer shapeRenderer;

    private OrthographicCamera camera;
    private Viewport viewport;

    // PRUEBAS DE MAQUINA DE ESTADOS
    // Stage para manejar la UI
    private Stage stage;
    // Skin para los estilos de UI
    private Skin skin;
    // Tabla raíz para organizar los elementos de la UI
    private Table rootTable;
    
    // Define la resolución virtual/interna del juego
    public static final int VIRTUAL_WIDTH = 700;
    public static final int VIRTUAL_HEIGHT = 400;

    @Override
    public void create() 
    {
        // PRUEBAS DE MAQUINA DE ESTADOS
        initializeStateStackText();

        // Inicializa la instancia singleton
        instance = this;

        // Inicializa la máquina de estados con el GameController como propietario
        stateMachine = new StateMachine<GameController>(this);

        // Coloca el estado inicial al menú principal
        stateMachine.changeState(MainMenuState.instance);

        // Configura la cámara y el viewport para manejar diferentes tamaños de pantalla
        camera = new OrthographicCamera();
        // Configura la cámara para que el eje Y apunte hacia arriba
        camera.setToOrtho(false, VIRTUAL_WIDTH, VIRTUAL_HEIGHT);
        // Utiliza FitViewport para mantener la relación de aspecto
        viewport = new FitViewport(VIRTUAL_WIDTH, VIRTUAL_HEIGHT, camera);

        // Ocultar el cursor del ratón
        Gdx.input.setCursorCatched(true);

        // Dibujar formas básicas (opcional, puede ser útil para depuración)
        shapeRenderer = new ShapeRenderer();
    }

    @Override
    public void render() 
    {
        // Fondo negro
        ScreenUtils.clear(0f, 0f, 0f, 1f);

        // Actualiza la cámara y establece la matriz de proyección para el SpriteBatch
        camera.update();

        // Dibujar cuadrado blanco de 680px 480px centrado
        // Establece la matriz de proyección para el ShapeRenderer
        shapeRenderer.setProjectionMatrix(camera.combined);
        // Iniciar el ShapeRenderer para dibujar formas, FILLED para un rectángulo sólido
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        // Establecemos color blanco
        shapeRenderer.setColor(1, 1, 1, 1);
        // Dibujamos el rectángulo centrado
        shapeRenderer.rect((VIRTUAL_WIDTH - 680) / 2, (VIRTUAL_HEIGHT - 380) / 2, 680, 380);
        // Finaliza el ShapeRenderer
        shapeRenderer.end();

        // Ejecuta el estado actual de la máquina de estados
        stateMachine.execute();

        // PRUEBAS DE MAQUINA DE ESTADOS
        showStateStack();
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
        shapeRenderer.dispose();
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
        // ----------------------------------------------------
        // 1. Limpiar el contenedor (Tabla) y prepararla para la esquina superior izquierda
        rootTable.clear(); // Elimina todas las etiquetas del frame anterior
        // ----------------------------------------------------

        // CAP-101 En la parte superior se mostrará el texto STATE STACK
        // Usamos una etiqueta para el título.
        Label titleLabel = new Label("STATE STACK", skin, "default-font", Color.RED); // Reemplaza "default-font" con el nombre de tu estilo
        rootTable.add(titleLabel).pad(5).row(); // Añade el título y pasa a la siguiente fila

        // ----------------------------------------------------
        // CAP-101 Recorre cada uno de los estados de la pila de estados
        // ----------------------------------------------------

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
