package com.machinehunterdev.game.GameStates;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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
import com.machinehunterdev.game.Character.CharacterAnimator;
import com.machinehunterdev.game.Util.State;

public class GameplayState implements State<GameController> {
    // SUELO SÓLIDO
    private ArrayList<SolidObject> solidObjects;

    // ** ATRIBUTOS DEL JUGADOR **
    private Character playerCharacter;   // Jugador con CharacterAnimator
    private PlayerController playerController;

    // -- ENEMIGO DE PRUEBA --
    private Character enemyCharacter;   // Enemigo también con CharacterAnimator
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

        // --- INICIALIZAR JUGADOR CON CHARACTERANIMATOR ---
        List<Sprite> playerIdleFrames = loadSpriteFrames("Player/Idle/PlayerIdle", 4);
        List<Sprite> playerRunFrames = loadSpriteFrames("Player/Run/PlayerRun", 8); // Ajusta según tus assets
        //List<Sprite> playerDeadFrames = loadSpriteFrames("Player/Dead", 3);
        //List<Sprite> playerJumpFrames = loadSpriteFrames("Player/Jump", 2);
        //List<Sprite> playerFallFrames = loadSpriteFrames("Player/Fall", 2);
        //List<Sprite> playerAttackFrames = loadSpriteFrames("Player/Attack", 5);

        CharacterAnimator playerAnimator = new CharacterAnimator(
            gameBatch,
            playerIdleFrames, playerRunFrames, null, 
            null, null, null // dead, jump, fall, attack no disponibles
        );

        playerCharacter = new Character(GlobalSettings.PLAYER_HEALTH, playerAnimator, 50, 100);
        playerController = new PlayerController(playerCharacter);

        // --- INICIALIZAR ENEMIGO CON CHARACTERANIMATOR ---
        // Para el enemigo, usamos solo animaciones básicas
        List<Sprite> enemyIdleFrames = loadSpriteFrames("Enemy/Idle/PlayerIdle", 4);
        List<Sprite> enemyRunFrames = loadSpriteFrames("Enemy/Run/PlayerRun", 8);
        //List<Sprite> enemyDeadFrames = loadSpriteFrames("Enemy/Dead/PlayerDead", 3);

        CharacterAnimator enemyAnimator = new CharacterAnimator(
            gameBatch,
            enemyIdleFrames, enemyRunFrames, null,
            null, null, null // dead, jump, fall, attack no disponibles
        );

        enemyCharacter = new Character(50, enemyAnimator, 300, 100);
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
            return;
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

        // ✅ Dibuja ambos personajes (el método draw() ya maneja animaciones y volteo)
        playerCharacter.draw();
        enemyCharacter.draw();

        gameBatch.end();
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

        if (playerBounds.overlaps(enemyBounds) && !playerCharacter.isInvulnerable()) {
            // Aplicar daño
            playerCharacter.takeDamage(10);
            
            // Aplicar empuje
            playerCharacter.isKnockedBack = true;
            playerCharacter.forceJump(0.7f);
            
            // Empuje horizontal
            if (enemyCharacter.getX() < playerCharacter.getX()) {
                playerCharacter.velocity.x = 150f;
            } else {
                playerCharacter.velocity.x = -150f;
            }
        }
    }

    // ✅ Método auxiliar para cargar frames de animación
    private List<Sprite> loadSpriteFrames(String basePath, int frameCount) {
        List<Sprite> frames = new ArrayList<>();
        for (int i = 1; i <= frameCount; i++) {
            frames.add(new Sprite(new Texture(basePath + i + ".png")));
        }
        return frames;
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

        // ✅ Liberar diálogo
        if (dialogManager != null) {
            dialogManager.dispose();
        }

        // Nota: Los personajes con CharacterAnimator gestionan sus propias texturas
        // Si más adelante implementas dispose() en Character, llámalo aquí
    }
}