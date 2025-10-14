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
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;
import com.machinehunterdev.game.GameController;
import com.machinehunterdev.game.Character.PlayerController;
import com.badlogic.gdx.math.Vector2;
import com.machinehunterdev.game.Character.Character;
import com.machinehunterdev.game.Character.EnemyController;
import com.machinehunterdev.game.Environment.SolidObject;
import com.machinehunterdev.game.Gameplay.GlobalSettings;
import com.machinehunterdev.game.UI.GameplayUI;
import com.machinehunterdev.game.Dialog.Dialog;
import com.machinehunterdev.game.Dialog.DialogManager;
import com.machinehunterdev.game.Character.CharacterAnimator;
import com.machinehunterdev.game.UI.PauseUI;
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

    private Texture backgroundTexture;
    private Texture sueloTexture;

    // ✅ Diálogo
    private DialogManager dialogManager;
    private boolean isDialogActive = false;

    // Instancia singleton
    public static GameplayState instance = new GameplayState();

    private GameplayUI gameplayUI;

    // Pause Menu
    private boolean isPaused = false;
    private PauseUI pauseUI;

    private GameplayState() {
        instance = this;
    }

    private GameController owner;

    public void resumeGame() {
        isPaused = false;
        Gdx.input.setInputProcessor(null); // Assuming gameplay uses polling
    }

    public void exitToMainMenu() {
        owner.stateMachine.changeState(MainMenuState.instance);
    }



    @Override
    public void enter(GameController owner) {
        isPaused = false;
        this.owner = owner;
        this.gameBatch = owner.batch;
        this.camera = owner.camera;

        backgroundTexture = new Texture("FondoJuego.png");

        // ✅ Inicializar diálogo
        dialogManager = new DialogManager(this.gameBatch);
        gameplayUI = new GameplayUI(this.gameBatch);

        pauseUI = new PauseUI(this, this.gameBatch);

        // - - INICIALIZAR SUELO SOLIDO - -
        sueloTexture = new Texture("suelo.png");
        solidObjects = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            solidObjects.add(new SolidObject(i * 480, 0, 480, 32, sueloTexture, true));
            solidObjects.add(new SolidObject(64 + i * 480, 64, 200, 16, sueloTexture, true));
        }

        // --- INICIALIZAR JUGADOR CON CHARACTERANIMATOR ---
        List<Sprite> playerIdleFrames = loadSpriteFrames("Player/PlayerIdle", 4);
        List<Sprite> playerRunFrames = loadSpriteFrames("Player/PlayerRun", 8); // Ajusta según tus assets
        List<Sprite> playerHurtFrames = loadSpriteFrames("Player/PlayerHurt", 2);
        //List<Sprite> playerDeadFrames = loadSpriteFrames("Player/Dead", 3);
        List<Sprite> playerJumpFrames = loadSpriteFrames("Player/PlayerJump", 2);
        List<Sprite> playerFallFrames = loadSpriteFrames("Player/PlayerFall", 2);
        //List<Sprite> playerAttackFrames = loadSpriteFrames("Player/Attack", 5);
        List<Sprite> playerCrouchFrames = loadSpriteFrames("Player/PlayerCrouch", 4);

        CharacterAnimator playerAnimator = new CharacterAnimator(
            playerIdleFrames, playerRunFrames, null,
            playerJumpFrames, playerFallFrames, null, // attackFrames
            playerHurtFrames, playerCrouchFrames
        );

        playerCharacter = new Character(GlobalSettings.PLAYER_HEALTH, playerAnimator, 50, 100);
        playerController = new PlayerController(playerCharacter);

        // --- INICIALIZAR ENEMIGO CON CHARACTERANIMATOR ---
        // Para el enemigo, usamos solo animaciones básicas
        List<Sprite> enemyIdleFrames = loadSpriteFrames("Enemy/PlayerIdle", 4);
        List<Sprite> enemyRunFrames = loadSpriteFrames("Enemy/PlayerRun", 8);
        //List<Sprite> enemyDeadFrames = loadSpriteFrames("Enemy/PlayerDead", 3);
        List<Sprite> enemyJumpFrames = loadSpriteFrames("Enemy/PlayerJump", 2);
        List<Sprite> enemyFallFrames = loadSpriteFrames("Enemy/PlayerFall", 2);
        //List<Sprite> enemyDeadFrames = loadSpriteFrames("Enemy/Dead/PlayerDead", 3);

        CharacterAnimator enemyAnimator = new CharacterAnimator(
            enemyIdleFrames, enemyRunFrames, null,
            enemyJumpFrames, enemyFallFrames, null,
            null, null // dead, jump, fall, attack, hurt, crouch no disponibles
        );

        enemyCharacter = new Character(50, enemyAnimator, 300, 100);

        // Crear puntos de patrullaje para el enemigo
        ArrayList<Vector2> patrolPoints = new ArrayList<>();
        patrolPoints.add(new Vector2(100, 100)); // Punto A
        patrolPoints.add(new Vector2(500, 100)); // Punto B

        enemyController = new EnemyController(enemyCharacter, patrolPoints, 1.0f, 3.0f); // Corre por 1s, espera 3s
    }

    @Override
    public void execute() {
        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            isPaused = !isPaused;
            if (isPaused) {
                Gdx.input.setInputProcessor(pauseUI);
            } else {
                Gdx.input.setInputProcessor(null);
            }
        }

        if (isPaused) {
            // Draw the game world (as a static background)
            drawGameWorld();
            // Then draw the pause UI on top
            pauseUI.draw();
        } else {
            // --- Update game logic ---
            updateGameLogic();

            // --- Draw game world ---
            drawGameWorld();

            // --- Draw dialog if active ---
            if (isDialogActive) {
                dialogManager.render();
            }

            // --- Draw Gameplay UI ---
            if (gameplayUI != null) {
                gameplayUI.draw(playerCharacter.getHealth());
            }

            // --- Handle death ---
            if (!playerCharacter.isAlive()) {
                owner.stateMachine.changeState(GameOverState.instance);
                return;
            }
        }
    }

    private void updateGameLogic() {
        float deltaTime = Gdx.graphics.getDeltaTime();

        // ✅ Si hay diálogo activo, manejar solo eso
        if (isDialogActive) {
            dialogManager.update(deltaTime);
            handleDialogInput();
            return;
        }

        // --- ACTIVAR DIÁLOGO CON TECLA T ---
        if (Gdx.input.isKeyJustPressed(Input.Keys.T)) {
            JsonReader jsonReader = new JsonReader();
            JsonValue base = jsonReader.parse(Gdx.files.internal("Dialogos/Diagolos_flahsbacks.json"));
            JsonValue flashbacks = base.get("Flashbacks");
            JsonValue flashback1 = flashbacks.get(0);
            JsonValue texto = flashback1.get("Texto");
            
            List<String> lines = new ArrayList<>();
            for (JsonValue line : texto) {
                lines.add(line.asString());
            }

            Dialog dialog = new Dialog(lines);
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
    }

    private void drawGameWorld() {
        // --- DIBUJAR ESCENA ---
        gameBatch.setProjectionMatrix(camera.combined);
        gameBatch.begin();

        // Fondo
        for (int i = 0; i < 3; i++) {
            gameBatch.draw(backgroundTexture, i * GlobalSettings.VIRTUAL_WIDTH, 0);
        }

        // Suelos sólidos
        for (SolidObject floor : solidObjects) {
            floor.render(gameBatch);
        }

        // ✅ Dibuja ambos personajes (el método draw() ya maneja animaciones y volteo)
        playerCharacter.draw(gameBatch);
        enemyCharacter.draw(gameBatch);

        gameBatch.end();

        
    }

    // ✅ Manejo de input para diálogo
    private void handleDialogInput() {
        if (Gdx.input.isKeyJustPressed(Input.Keys.E)) {
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
            playerCharacter.isOverlappingEnemy = true;
            if (!playerCharacter.isInvulnerable()) {
                // --- Resolución de colisión (empujar al jugador) ---
                float overlapX = Math.min(playerBounds.x + playerBounds.width, enemyBounds.x + enemyBounds.width) - Math.max(playerBounds.x, enemyBounds.x);
                if (playerCharacter.getX() < enemyCharacter.getX()) {
                    playerCharacter.position.x -= overlapX; // Empujar a la izquierda
                } else {
                    playerCharacter.position.x += overlapX; // Empujar a la derecha
                }

                // --- Aplicar daño y empuje (knockback) ---
                playerCharacter.takeDamage(1);
                playerCharacter.isKnockedBack = true;
                playerCharacter.forceJump(0.7f);
                
                // Empuje horizontal
                if (enemyCharacter.getX() < playerCharacter.getX()) {
                    playerCharacter.velocity.x = 150f;
                } else {
                    playerCharacter.velocity.x = -150f;
                }
            }
        } else {
            playerCharacter.isOverlappingEnemy = false;
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
        if (gameplayUI != null) {
            gameplayUI.resize(width, height);
        }
    }

    @Override
    public void exit() {
        if (sueloTexture != null) {
            sueloTexture.dispose();
        }

        // ✅ Liberar diálogo
        if (dialogManager != null) {
            dialogManager.dispose();
        }

        if (gameplayUI != null) {
            gameplayUI.dispose();
        }

        if (pauseUI != null) {
            pauseUI.dispose();
        }


        if (backgroundTexture != null) {
            backgroundTexture.dispose();
        }

        // Nota: Los personajes con CharacterAnimator gestionan sus propias texturas
        // Si más adelante implementas dispose() en Character, llámalo aquí
    }
}