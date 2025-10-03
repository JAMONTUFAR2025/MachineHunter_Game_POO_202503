package com.machinehunterdev.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

/** {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms. */
public class GameController extends ApplicationAdapter 
{

    private ShapeRenderer shapeRenderer;

    private OrthographicCamera camera;
    private Viewport viewport;
    
    // Define la resolución virtual/interna del juego
    public static final int VIRTUAL_WIDTH = 700;
    public static final int VIRTUAL_HEIGHT = 400;

    @Override
    public void create() 
    {
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
}
