package com.machinehunterdev.game.UI;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.machinehunterdev.game.GameController;
import com.badlogic.gdx.utils.Align;
import com.machinehunterdev.game.GameStates.MainMenuState;

public class CreditUI implements InputProcessor {

    // Objeto para dibujar texturas y sprites
    private SpriteBatch batch;
    // Referencia al controlador principal del juego
    private GameController gameController;
    // Textura de fondo para la pantalla de creditos
    private Texture backgroundTexture;
    // Fuente para renderizar el texto de los creditos
    private BitmapFont font;
    // Array de cadenas que contiene el texto de los creditos
    private String[] credits = {
            "MACHINE HUNTER",
            "Créditos",
            "",
            "Clase",
            "Programación Orientada a Objetos",
            "",
            "Período 2025-3",
            "",
            "Catedrático",
            "Luis Fernando Teruel Umanzor",
            "",
            "Guión",
            "Raúl Fernando Ramos Lara",
            "",
            "Arte",
            "Mariely Nicol Hiraeta Henrriquez",
            "Ken Kato Castellanos",
            "",
            "Texturas",
            "Explosión: A quien corresponda",
            "Rayo: Pokémon Ruby & Sapphire",
            "",
            "Programación",
            "Josué Alejandro Montúfar Zúniga",
            "Anner Alessandro Teruel Pineda",
            "Ken Kato Castellanos",
            "",
            "Música y sonidos",
            "Mario & Luigi: Superstar Saga",
            "Pokémon Ruby & Sapphire",
            "",
            "",
            "",
            "¡Gracias por Jugar!",
            "Ningun individuo sea guionista, artista o programador",
            "fue sobreexplotado en la elaboración de este proyecto.",
            "*guino* *guino*"
    };

    // Posicion de desplazamiento vertical de los creditos
    private float scrollY;
    // Velocidad de desplazamiento de los creditos
    private float scrollSpeed = 100f;
    // Indica si los creditos han terminado de desplazarse
    private boolean creditsFinished = false;
    // Temporizador para la transicion despues de que los creditos terminan
    private float finishedTimer = 0f;
    // Indica si la opcion de omitir ya fue usada
    private boolean skipUsed = false;

    // Temporizador para la funcion de omitir creditos
    private float skipTimer = 0f;
    // Indica si el usuario esta intentando omitir los creditos
    private boolean isSkipping = false;
    // Tiempo necesario para mantener ENTER y omitir los creditos
    private static final float TIME_TO_SKIP = 5f;
    // Textura para el indicador visual de omitir
    private Texture skipIndicatorTexture;
    // Indica si el avance rapido esta activado
    private boolean fastForward = false;

    /**
     * Constructor de CreditUI.
     * Inicializa los recursos graficos y de audio necesarios para la pantalla de creditos.
     * @param batch El SpriteBatch para dibujar.
     * @param gameController El controlador principal del juego.
     */
    public CreditUI(SpriteBatch batch, GameController gameController) {
        this.batch = batch;
        this.gameController = gameController;
        this.backgroundTexture = new Texture(Gdx.files.internal("Fondos/NameInputBackgroundShadowless.png"));
        this.font = new BitmapFont(Gdx.files.internal("fonts/OrangeKid64.fnt"));
        this.scrollY = -200; // Start credits off-screen

        Pixmap skipPixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        skipPixmap.setColor(Color.WHITE);
        skipPixmap.fill();
        skipIndicatorTexture = new Texture(skipPixmap);
        skipPixmap.dispose();
    }

    /**
     * Actualiza la logica de la pantalla de creditos, incluyendo el temporizador de omitir.
     * @param dt Delta time, el tiempo transcurrido desde el ultimo frame.
     */
    public void update(float dt) {
        if (isSkipping) {
            skipTimer += dt;
            if (skipTimer >= TIME_TO_SKIP) {
                gameController.stateMachine.changeState(MainMenuState.instance);
            }
        }
    }

    /**
     * Dibuja la pantalla de creditos, incluyendo el fondo, el texto de los creditos y el indicador de omitir.
     */
    public void draw() {
        update(Gdx.graphics.getDeltaTime());

        batch.begin();
        batch.draw(backgroundTexture, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

        GlyphLayout layout = new GlyphLayout();
        float y = scrollY;

        float currentScrollSpeed = fastForward ? scrollSpeed * 2 : scrollSpeed;

        if (!creditsFinished) {
            scrollY += currentScrollSpeed * Gdx.graphics.getDeltaTime();
        }

        float totalHeight = 0;
        for (int i = 0; i < credits.length; i++) {
            String line = credits[i];
            if (line.equals("MACHINE HUNTER")) {
                font.setColor(Color.RED);
            } else if (line.equals("Clase") ||
                        line.equals("Catedrático") ||
                        line.equals("Guión") ||
                        line.equals("Arte") ||
                        line.equals("Texturas") ||
                        line.equals("Programación") ||
                        line.equals("Música y sonidos") ||
                        line.equals("¡Gracias por Jugar!")) {
                font.setColor(Color.CYAN);
            } else {
                font.setColor(Color.WHITE);
            }
            layout.setText(font, line);
            float x = (Gdx.graphics.getWidth() - layout.width) / 2;
            font.draw(batch, line, x, y - totalHeight);
            totalHeight += 80;
        }

        if (y - totalHeight > Gdx.graphics.getHeight()) {
            creditsFinished = true;
        }

        if (creditsFinished) {
            finishedTimer += Gdx.graphics.getDeltaTime();
            if (finishedTimer > 1) {
                gameController.stateMachine.changeState(MainMenuState.instance);
            }
        }

        float barWidth = 200;
        float barHeight = 30; // Adjusted position
        float barX = Gdx.graphics.getWidth() - barWidth - 50; // Adjusted position
        float barY = 80; // Adjusted position

        batch.setColor(0.2f, 0.2f, 0.2f, 1);
        batch.draw(skipIndicatorTexture, barX - 4, barY - 4, barWidth + 8, barHeight + 8);

        batch.setColor(0.5f, 0.5f, 0.5f, 1);
        batch.draw(skipIndicatorTexture, barX, barY, barWidth, barHeight);
        batch.setColor(1, 1, 1, 1); // Reset color

        if (isSkipping) {
            float progress = skipTimer / TIME_TO_SKIP;
            batch.setColor(1, 1, 1, 1);
            batch.draw(skipIndicatorTexture, barX + 4, barY + 4, (barWidth - 8) * progress, barHeight - 8);
        }

        font.getData().setScale(0.5f);
        font.draw(batch, "E - Avance rápido | Mantén Enter - Omitir", barX, barY + barHeight + 30, barWidth, Align.center, false);
        font.getData().setScale(1.0f);

        batch.end();
    }

    /**
     * Libera los recursos utilizados por la pantalla de creditos.
     */
    public void dispose() {
        if (font != null) {
            font.dispose();
        }
        if (backgroundTexture != null) {
            backgroundTexture.dispose();
        }
        if (skipIndicatorTexture != null) {
            skipIndicatorTexture.dispose();
        }
    }

    /**
     * Maneja los eventos de teclado cuando una tecla es presionada.
     * @param keycode El codigo de la tecla presionada.
     * @return true si el evento fue manejado, false en caso contrario.
     */
    @Override
    public boolean keyDown(int keycode) {
        if (keycode == Input.Keys.ESCAPE) {
            gameController.stateMachine.changeState(MainMenuState.instance);
        }
        if (keycode == Input.Keys.ENTER) {
            isSkipping = true;
        }
        if (keycode == Input.Keys.E) {
            fastForward = true;
        }
        return true;
    }

    /**
     * Maneja los eventos de teclado cuando una tecla es liberada.
     * @param keycode El codigo de la tecla liberada.
     * @return true si el evento fue manejado, false en caso contrario.
     */
    @Override
    public boolean keyUp(int keycode) {
        if (keycode == Input.Keys.ENTER) {
            isSkipping = false;
            skipTimer = 0;
        }
        if (keycode == Input.Keys.E) {
            fastForward = false;
        }
        return false;
    }

    /**
     * Maneja los eventos de teclado cuando un caracter es tipeado.
     * @param character El caracter tipeado.
     * @return true si el evento fue manejado, false en caso contrario.
     */
    @Override
    public boolean keyTyped(char character) {
        return false;
    }

    /**
     * Maneja los eventos de toque/clic cuando la pantalla es tocada o un boton del raton es presionado.
     * @param screenX La coordenada X de la pantalla.
     * @param screenY La coordenada Y de la pantalla.
     * @param pointer El puntero para eventos multitouch.
     * @param button El boton del raton presionado.
     * @return true si el evento fue manejado, false en caso contrario.
     */
    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    /**
     * Maneja los eventos de toque/clic cuando la pantalla es liberada o un boton del raton es soltado.
     * @param screenX La coordenada X de la pantalla.
     * @param screenY La coordenada Y de la pantalla.
     * @param pointer El puntero para eventos multitouch.
     * @param button El boton del raton soltado.
     * @return true si el evento fue manejado, false en caso contrario.
     */
    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    /**
     * Maneja los eventos de toque/clic cuando se arrastra el dedo o el raton.
     * @param screenX La coordenada X de la pantalla.
     * @param screenY La coordenada Y de la pantalla.
     * @param pointer El puntero para eventos multitouch.
     * @return true si el evento fue manejado, false en caso contrario.
     */
    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        return false;
    }

    /**
     * Maneja los eventos de movimiento del raton sin presionar ningun boton.
     * @param screenX La coordenada X de la pantalla.
     * @param screenY La coordenada Y de la pantalla.
     * @return true si el evento fue manejado, false en caso contrario.
     */
    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        return false;
    }

    /**
     * Maneja los eventos de la rueda del raton.
     * @param amountX La cantidad de desplazamiento horizontal.
     * @param amountY La cantidad de desplazamiento vertical.
     * @return true si el evento fue manejado, false en caso contrario.
     */
    @Override
    public boolean scrolled(float amountX, float amountY) {
        return false;
    }
    /**
     * Maneja los eventos de toque/clic cuando un evento de toque es cancelado.
     * @param screenX La coordenada X de la pantalla.
     * @param screenY La coordenada Y de la pantalla.
     * @param pointer El puntero para eventos multitouch.
     * @param button El boton del raton.
     * @return true si el evento fue manejado, false en caso contrario.
     */
     @Override
    public boolean touchCancelled(int screenX, int screenY, int pointer, int button) {
        return false;
    }
}
