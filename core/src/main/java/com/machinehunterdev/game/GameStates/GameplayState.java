package com.machinehunterdev.game.GameStates;

import java.util.ArrayList;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.Input;
import com.machinehunterdev.game.GameController;
import com.machinehunterdev.game.Character.PlayerController;
import com.machinehunterdev.game.Character.Character;
import com.machinehunterdev.game.Character.EnemyController;
import com.machinehunterdev.game.Environment.SolidObject;
import com.machinehunterdev.game.Gameplay.GlobalSettings;
import com.machinehunterdev.game.Util.State;

public class GameplayState implements State<GameController> {
    // SUELO SÓLIDO
    private ArrayList<SolidObject> solidObjects;

    // ** ATRIBUTOS DEL JUGADOR **
    private Character playerCharacter;   // La instancia del personaje (ahora animado)
    private PlayerController playerController; // El controlador de entrada

    // -- ENEMIGO DE PRUEBA --
    private Texture enemyTexture;       // La textura del enemigo (estático por ahora)
    private Character enemyCharacter;   // La instancia del enemigo
    private EnemyController enemyController; // El controlador del enemigo

    // - - SPRITEBATCH DEL GAMECONTROLLER - -
    private SpriteBatch gameBatch;

    // CAMARA
    private OrthographicCamera camera;

    // Instancia singleton del GameplayState
    public static GameplayState instance = new GameplayState();

    // Constructor privado para evitar instanciación externa
    private GameplayState() {
        instance = this;
    }

    // Poseedor es el GameController
    private GameController owner;

    @Override
    public void enter(GameController owner) {
        this.owner = owner;
        this.gameBatch = owner.batch;
        this.camera = owner.camera;

        // - - INICIALIZAR SUELO SOLIDO - -
        solidObjects = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            solidObjects.add(new SolidObject(i * 480, 0, 480, 32, "suelo.png", true));
            solidObjects.add(new SolidObject(64 + i * 480, 64, 200, 16, "suelo.png", true));
        }

        // --- INICIALIZAR JUGADOR ANIMADO ---
        ArrayList<Sprite> playerFrames = new ArrayList<>();
        for (int i = 1; i <= 4; i++) {
            playerFrames.add(new Sprite(new Texture("Idle/roberto" + i + ".png")));
        }
        playerCharacter = new Character(GlobalSettings.PLAYER_HEALTH, playerFrames, gameBatch, 50, 100);
        playerController = new PlayerController(playerCharacter);

        // --- INICIALIZAR ENEMIGO DE PRUEBA (estático) ---
        enemyTexture = new Texture("enemy.png");
        enemyCharacter = new Character(50, enemyTexture, 300, 100);
        enemyController = new EnemyController(enemyCharacter);
    }

    @Override
    public void execute() {
        // Actualización de lógica
        float deltaTime = Gdx.graphics.getDeltaTime();

        // Actualizar logica
        playerController.update(deltaTime, solidObjects);
        enemyController.update(deltaTime, solidObjects);

        // ✅ Detectar colisión jugador-enemigo
        checkPlayerEnemyCollision();

        playerController.centerCameraOnPlayer(camera);

        // Cambio de estado para regresar o cuando el jugador muere
        if (Gdx.input.isKeyJustPressed(Input.Keys.Q) || !playerCharacter.isAlive()) {
            owner.stateMachine.changeState(MainMenuState.instance);
        }

        // --- DIBUJAR ---
        gameBatch.setProjectionMatrix(camera.combined);
        gameBatch.begin();

        // Fondo (⚠️ Mejora futura: cargar textura fuera del bucle)
        for (int i = 0; i < 3; i++) {
            gameBatch.draw(new Texture("FondoJuego.png"), i * GlobalSettings.VIRTUAL_WIDTH, 0);
        }

        // Suelos sólidos
        for (SolidObject floor : solidObjects) {
            floor.render(gameBatch);
        }

        // ✅ Dibuja el JUGADOR ANIMADO (incluye volteo y posición)
        playerCharacter.draw();

        // Dibuja el ENEMIGO (estático por ahora)
        Texture enemyTex = enemyCharacter.getTexture();
        float ex = enemyCharacter.getX();
        float ey = enemyCharacter.getY();
        float ew = enemyTex.getWidth();
        float eh = enemyTex.getHeight();

        if (enemyCharacter.isSeeingRight()) {
            gameBatch.draw(enemyTex, ex, ey);
        } else {
            gameBatch.draw(enemyTex, ex + ew, ey, -ew, eh);
        }

        gameBatch.end();
    }

    @Override
    public void exit() {
        // Liberar recursos del suelo
        for (SolidObject object : solidObjects) {
            object.dispose();
        }

        // Liberar textura del enemigo (el jugador ya gestiona sus texturas internamente)
        if (enemyTexture != null) {
            enemyTexture.dispose();
        }
    }

    // Metodos para el GameplayState
    // Verifica colisiones entre el jugador y el enemigo
    private void checkPlayerEnemyCollision() {
        // Obtenemos los bounds de ambos personajes
        Rectangle playerBounds = playerCharacter.getBounds();
        Rectangle enemyBounds = enemyCharacter.getBounds();

        if (playerBounds.overlaps(enemyBounds)) {

            // Solo aplicar empuje si NO está ya en empuje o invulnerable (evita "resetear" el empuje)
            if (!playerCharacter.isKnockedBack() && !playerCharacter.isInvulnerable()) {
                playerCharacter.isKnockedBack = true;
                playerCharacter.forceJump(0.7f); // Empuje vertical
                
                // Aplicar daño (takeDamage ya maneja la invulnerabilidad internamente)
                playerCharacter.takeDamage(10);

                // Empuje horizontal
                if (enemyCharacter.getX() < playerCharacter.getX()) {
                    playerCharacter.velocity.x = 150f; // Empujar a la derecha
                } else {
                    playerCharacter.velocity.x = -150f; // Empujar a la izquierda
                }
            }
        }
    }
}