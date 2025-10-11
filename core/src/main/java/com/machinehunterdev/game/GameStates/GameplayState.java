package com.machinehunterdev.game.GameStates;

import java.util.ArrayList;
import java.util.Arrays;

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
import com.machinehunterdev.game.Dialog.Dialogue;
import com.machinehunterdev.game.Dialog.DialogManager;
import com.machinehunterdev.game.Util.State;

public class GameplayState implements State<GameController> {
    // SUELO SÓLIDO
    private ArrayList<SolidObject> solidObjects;

    // ** ATRIBUTOS DEL JUGADOR **
    private Character playerCharacter;   // Jugador animado
    private PlayerController playerController;

    // -- ENEMIGO DE PRUEBA --
    private Texture enemyTexture;
    private Character enemyCharacter;
    private EnemyController enemyController;

    // - - SPRITEBATCH DEL GAMECONTROLLER - -
    private SpriteBatch gameBatch;

    // CAMARA
    private OrthographicCamera camera;

    // ✅ Diálogo
    private DialogManager dialogManager;
    private boolean isDialogActive = false;

    // Instancia singleton
    public static GameplayState instance = new GameplayState();

    private GameplayState() {
        instance = this;
    }

    private GameController owner;

    @Override
    public void enter(GameController owner) {
        this.owner = owner;
        this.gameBatch = owner.batch;
        this.camera = owner.camera;

        // ✅ Inicializar diálogo
        dialogManager = new DialogManager();

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

        // --- INICIALIZAR ENEMIGO DE PRUEBA ---
        enemyTexture = new Texture("enemy.png");
        enemyCharacter = new Character(50, enemyTexture, 300, 100);
        enemyController = new EnemyController(enemyCharacter);
    }

    @Override
    public void execute() {
        float deltaTime = Gdx.graphics.getDeltaTime();

        // ✅ Si hay diálogo activo, manejar solo eso
        if (isDialogActive) {
            dialogManager.update(deltaTime);
            handleDialogInput();
            dialogManager.render();
            return;
        }

        // --- ACTIVAR DIÁLOGO CON TECLA T ---
        if (Gdx.input.isKeyJustPressed(Input.Keys.T)) {
            Dialogue dialog = new Dialogue(Arrays.asList("¡Hola!", "Este es un diálogo.", "Presiona para continuar."));
            dialogManager.showDialog(dialog);
            isDialogActive = true;
            return; // Evita procesar lógica del juego en el mismo frame
        }

        // --- LÓGICA DEL JUEGO ---
        playerController.update(deltaTime, solidObjects);
        enemyController.update(deltaTime, solidObjects);

        // ✅ Colisión jugador-enemigo
        checkPlayerEnemyCollision();

        playerController.centerCameraOnPlayer(camera);

        // Cambio de estado si se presiona Q o el jugador muere
        if (Gdx.input.isKeyJustPressed(Input.Keys.Q) || !playerCharacter.isAlive()) {
            owner.stateMachine.changeState(MainMenuState.instance);
            return;
        }

        // --- DIBUJAR ESCENA ---
        gameBatch.setProjectionMatrix(camera.combined);
        gameBatch.begin();

        // Fondo
        for (int i = 0; i < 3; i++) {
            gameBatch.draw(new Texture("FondoJuego.png"), i * GlobalSettings.VIRTUAL_WIDTH, 0);
        }

        // Suelos sólidos
        for (SolidObject floor : solidObjects) {
            floor.render(gameBatch);
        }

        // ✅ Dibuja el JUGADOR ANIMADO (ya maneja volteo internamente)
        playerCharacter.draw();

        // Dibuja el ENEMIGO
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

        // ✅ Renderizar diálogo encima del juego (aunque no debería llegar aquí si isDialogActive == true)
        // Pero por seguridad, lo dejamos como respaldo
        if (isDialogActive) {
            dialogManager.render();
        }
    }

    // ✅ Manejo de input para diálogo
    private void handleDialogInput() {
        if (Gdx.input.justTouched()) {
            if (dialogManager.isDialogActive()) {
                dialogManager.nextLine();
            } else {
                isDialogActive = false;
            }
        }
    }

    // ✅ Colisión jugador-enemigo con empuje y daño
    private void checkPlayerEnemyCollision() {
        Rectangle playerBounds = playerCharacter.getBounds();
        Rectangle enemyBounds = enemyCharacter.getBounds();

        if (playerBounds.overlaps(enemyBounds)) {
            if (!playerCharacter.isKnockedBack() && !playerCharacter.isInvulnerable()) {
                playerCharacter.isKnockedBack = true;
                playerCharacter.forceJump(0.7f);
                playerCharacter.takeDamage(10);

                // Empuje horizontal
                if (enemyCharacter.getX() < playerCharacter.getX()) {
                    playerCharacter.velocity.x = 150f;
                } else {
                    playerCharacter.velocity.x = -150f;
                }
            }
        }
    }

    public void resize(int width, int height) {
        if (dialogManager != null) {
            dialogManager.resize(width, height);
        }
    }

    @Override
    public void exit() {
        // Liberar suelos
        for (SolidObject object : solidObjects) {
            object.dispose();
        }

        // Liberar textura del enemigo
        if (enemyTexture != null) {
            enemyTexture.dispose();
        }

        // ✅ Liberar diálogo
        if (dialogManager != null) {
            dialogManager.dispose();
        }

        // Nota: el jugador animado ya gestiona sus propias texturas internamente,
        // así que no necesitamos dispose() aquí (asumiendo que Character lo hace en su dispose())
    }
}