package com.machinehunterdev.game.GameStates;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;
import com.machinehunterdev.game.Dialog.Dialog;
import java.util.List;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;
import com.machinehunterdev.game.GameController;
import com.machinehunterdev.game.Character.PlayerController;
import com.machinehunterdev.game.DamageTriggers.Bullet;
import com.machinehunterdev.game.DamageTriggers.DamageSystem;
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
import com.machinehunterdev.game.Character.NPCController;

/**
 * Estado principal del juego que contiene toda la lógica de gameplay.
 * Gestiona personajes, enemigos, balas, colisiones, diálogos y pausa.
 * 
 * @author MachineHunterDev
 */
public class GameplayState implements State<GameController> {
    // === ENTIDADES DEL JUEGO ===
    
    /** Lista de objetos sólidos del entorno */
    private ArrayList<SolidObject> solidObjects;

    /** Jugador y su controlador */
    private Character playerCharacter;
    private PlayerController playerController;

    /** Enemigo de prueba y su controlador */
    private Character enemyCharacter;
    private EnemyController enemyController;

    private NPCController npcController;

    // === SISTEMAS DE RENDERIZADO ===
    
    /** SpriteBatch compartido del juego */
    private SpriteBatch gameBatch;
    
    /** Cámara del juego */
    private OrthographicCamera camera;
    
    /** Texturas del fondo y suelo */
    private Texture backgroundTexture;
    private Texture sueloTexture;
    private Texture blackTexture;

    // === SISTEMAS DE INTERFAZ ===
    
    /** Gestor de diálogos */
    private DialogManager dialogManager;
    private boolean isDialogActive = false;

    /** Interfaz de usuario durante el gameplay */
    private GameplayUI gameplayUI;

    // === SISTEMA DE PAUSA ===
    
    private boolean isPaused = false;
    private PauseUI pauseUI;

    private BitmapFont interactionFont;

    // === SISTEMA DE COMBATE ===
    
    /** Lista de balas activas */
    private ArrayList<Bullet> bullets;

    // === INSTANCIA SINGLETON ===
    
    public static GameplayState instance = new GameplayState();
    private GameController owner;

    /**
     * Constructor privado para implementar el patrón Singleton.
     */
    private GameplayState() {
        instance = this;
    }

    /**
     * Reanuda el juego desde el estado de pausa.
     */
    public void resumeGame() {
        isPaused = false;
        Gdx.input.setInputProcessor(null);
    }

    /**
     * Cambia al estado del menú principal.
     */
    public void exitToMainMenu() {
        owner.stateMachine.changeState(MainMenuState.instance);
    }

    /**
     * Inicializa el estado al entrar.
     * @param owner Controlador del juego propietario
     */
    @Override
    public void enter(GameController owner) {
        isPaused = false;
        this.owner = owner;
        this.gameBatch = owner.batch;
        this.camera = owner.camera;

        backgroundTexture = new Texture("FondoJuego.png");
        dialogManager = new DialogManager(this.gameBatch);
        gameplayUI = new GameplayUI(this.gameBatch);
        pauseUI = new PauseUI(this, this.gameBatch);
        bullets = new ArrayList<>();
        interactionFont = new BitmapFont(Gdx.files.internal("fonts/OrangeKid32.fnt"));

        Pixmap pixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        pixmap.setColor(0, 0, 0, 0.7f); // Black with some transparency
        pixmap.fill();
        blackTexture = new Texture(pixmap);
        pixmap.dispose();

        // Inicializar suelo sólido con textura compartida
        sueloTexture = new Texture("suelo.png");
        solidObjects = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            solidObjects.add(new SolidObject(i * 480, 0, 480, 32, sueloTexture, true));
            solidObjects.add(new SolidObject(64 + i * 480, 64, 200, 16, sueloTexture, true));
        }

        // Inicializar jugador con animaciones
        List<Sprite> playerIdleFrames = loadSpriteFrames("Player/PlayerIdle", 4);
        List<Sprite> playerRunFrames = loadSpriteFrames("Player/PlayerRun", 8);
        List<Sprite> playerJumpFrames = loadSpriteFrames("Player/PlayerJump", 2);
        List<Sprite> playerFallFrames = loadSpriteFrames("Player/PlayerFall", 2);
        List<Sprite> playerHurtFrames = loadSpriteFrames("Player/PlayerHurt", 2);
        List<Sprite> playerCrouchFrames = loadSpriteFrames("Player/PlayerCrouch", 4);
        List<Sprite> playerLaserAttackFrames = loadSpriteFrames("Player/PlayerLaserAttack", 2);
        List<Sprite> playerIonAttackFrames = loadSpriteFrames("Player/PlayerIonAttack", 2);
        List<Sprite> playerRailgunAttackFrames = loadSpriteFrames("Player/PlayerRailgunAttack", 2);

        CharacterAnimator playerAnimator = new CharacterAnimator(
            playerIdleFrames, playerRunFrames, null,
            playerJumpFrames, playerFallFrames, null,
            playerLaserAttackFrames, playerIonAttackFrames, playerRailgunAttackFrames,
            playerHurtFrames, playerCrouchFrames
        );

        playerCharacter = new Character(GlobalSettings.PLAYER_HEALTH, playerAnimator, 50, 100);
        playerController = new PlayerController(playerCharacter);

        // Initialize NPC with player's animations
        CharacterAnimator npcAnimator = new CharacterAnimator(
            playerIdleFrames, playerRunFrames, null,
            playerJumpFrames, playerFallFrames, null,
            playerHurtFrames, playerCrouchFrames, null,
            null, null
        );
        Character npcCharacter = new Character(100, npcAnimator, 200, 100);

        // Load NPC dialogues
        JsonReader jsonReader = new JsonReader();
        JsonValue base = jsonReader.parse(Gdx.files.internal("Dialogos/Dialogos_personajes.json"));
        JsonValue dialogos = base.get("Dialogos_acto2");
        List<Dialog> npcDialogues = new ArrayList<>();
        if (dialogos != null) {
            for (JsonValue dialogo : dialogos) {
                List<String> lines = new ArrayList<>();
                JsonValue texto = dialogo.get("Texto");
                if (texto != null) {
                    for (JsonValue line : texto) {
                        lines.add(line.asString());
                    }
                }
                npcDialogues.add(new Dialog(lines));
            }
        }

        npcController = new NPCController(npcCharacter, 50f, npcDialogues);

        // Inicializar enemigo con animaciones
        List<Sprite> enemyIdleFrames = loadSpriteFrames("Enemy/PlayerIdle", 4);
        List<Sprite> enemyRunFrames = loadSpriteFrames("Enemy/PlayerRun", 8);
        List<Sprite> enemyJumpFrames = loadSpriteFrames("Enemy/PlayerJump", 2);
        List<Sprite> enemyFallFrames = loadSpriteFrames("Enemy/PlayerFall", 2);
        List<Sprite> enemyHurtFrames = loadSpriteFrames("Enemy/PlayerHurt", 2);
        List<Sprite> enemyDeadFrames = loadSpriteFrames("Enemy/Explosion", 4);

        CharacterAnimator enemyAnimator = new CharacterAnimator(
            enemyIdleFrames, enemyRunFrames, enemyDeadFrames,
            enemyJumpFrames, enemyFallFrames, null,
            enemyHurtFrames, null, null,
            null, null
        );

        enemyCharacter = new Character(50, enemyAnimator, 300, 100);

        // Configurar patrullaje del enemigo
        ArrayList<Vector2> patrolPoints = new ArrayList<>();
        patrolPoints.add(new Vector2(100, 100));
        patrolPoints.add(new Vector2(500, 100));

        enemyController = new EnemyController(enemyCharacter, patrolPoints, 1.0f, 3.0f);
    }

    /**
     * Ejecuta la lógica del estado cada frame.
     */
    @Override
    public void execute() {
        // Manejo de pausa con tecla ESC
        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            isPaused = !isPaused;
            if (isPaused) {
                Gdx.input.setInputProcessor(pauseUI);
            } else {
                Gdx.input.setInputProcessor(null);
            }
        }

        if (isPaused) {
            drawGameWorld();
            pauseUI.draw();
        } else {
            updateGameLogic();
            drawGameWorld();

            if (isDialogActive) {
                dialogManager.render();
            }

            if (gameplayUI != null) {
                gameplayUI.draw(playerCharacter.getHealth(), playerCharacter.getCurrentWeapon());
            }

            // Verificar muerte del jugador
            if (!playerCharacter.isAlive()) {
                owner.stateMachine.changeState(GameOverState.instance);
                return;
            }
        }
    }

    /**
     * Actualiza toda la lógica del juego.
     */
    private void updateGameLogic() {
        float deltaTime = Gdx.graphics.getDeltaTime();

        if (isDialogActive) {
            dialogManager.update(deltaTime);
            if (!dialogManager.isDialogActive()) {
                isDialogActive = false;
            }
            handleDialogInput();
            return;
        }

        // Actualizar personajes y balas
        playerController.update(deltaTime, solidObjects, bullets);
        if (enemyCharacter != null) {
            // Siempre actualiza el personaje enemigo mientras exista,
            // incluso si está muerto, para permitir que se reproduzca la animación de muerte.
            enemyController.update(deltaTime, solidObjects, bullets);

            if (!enemyCharacter.isAlive() && enemyCharacter.isReadyForRemoval()) {
                // El enemigo está muerto y la animación ha terminado, eliminarlo
                enemyCharacter.dispose(); // Libera sus recursos
                enemyCharacter = null;
                enemyController = null;
            }
        }

        if (npcController != null) {
            npcController.update(deltaTime, solidObjects, bullets);
            npcController.updateInteraction(playerCharacter.position);
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.E)) {
            if (npcController != null && npcController.isInRange()) {
                List<Dialog> dialogues = npcController.getDialogues();
                if (dialogues != null && !dialogues.isEmpty()) {
                    dialogManager.showDialog(dialogues.get(0));
                    isDialogActive = true;
                    return;
                }
            }
        }
        updateBullets(deltaTime);

        checkPlayerEnemyCollision();
        playerController.centerCameraOnPlayer(camera);
    }

    /**
     * Renderiza el mundo del juego.
     */
    private void drawGameWorld() {
        gameBatch.setProjectionMatrix(camera.combined);
        gameBatch.begin();

        // Dibujar fondo
        for (int i = 0; i < 3; i++) {
            gameBatch.draw(backgroundTexture, i * GlobalSettings.VIRTUAL_WIDTH, 0);
        }

        // Dibujar objetos sólidos
        for (SolidObject floor : solidObjects) {
            floor.render(gameBatch);
        }

        // Dibujar personajes
        playerCharacter.draw(gameBatch);
        if (enemyCharacter != null) {
            enemyCharacter.draw(gameBatch);
        }

        if (npcController != null) {
            npcController.render(gameBatch);

            if (npcController.isInRange()) {
                GlyphLayout layout = new GlyphLayout();
                String message = "E para interactuar";
                interactionFont.getData().setScale(0.5f);
                layout.setText(interactionFont, message);

                float boxWidth = layout.width + 20; // 10 pixels padding on each side
                float boxHeight = layout.height + 10; // 5 pixels padding on top/bottom
                float boxX = npcController.character.position.x + (npcController.character.getWidth() / 2) - (boxWidth / 2);
                float boxY = npcController.character.position.y + npcController.character.getHeight() + 10;

                gameBatch.draw(blackTexture, boxX, boxY, boxWidth, boxHeight);

                float textX = boxX + 10;
                float textY = boxY + layout.height + 5;
                interactionFont.draw(gameBatch, layout, textX, textY);
                interactionFont.getData().setScale(1.0f);
            }
        }

        drawBullets();

        gameBatch.end();
    }

    /**
     * Maneja la entrada para avanzar en diálogos.
     */
    private void handleDialogInput() {
        if (Gdx.input.isKeyJustPressed(Input.Keys.E)) {
            if (dialogManager.isDialogActive()) {
                dialogManager.nextLine();
            }
        }
    }

    /**
     * Verifica colisiones entre jugador y enemigo.
     */
    private void checkPlayerEnemyCollision() {
        if (enemyCharacter == null || !enemyCharacter.isAlive()) return;
        
        Rectangle playerBounds = playerCharacter.getBounds();
        Rectangle enemyBounds = enemyCharacter.getBounds();

        if (playerBounds.overlaps(enemyBounds)) {
            playerCharacter.isOverlappingEnemy = true;
            
            // Usar el sistema centralizado de daño
            if (DamageSystem.canTakeDamage(playerCharacter)) {
                // Resolver colisión (empujar al jugador)
                float overlapX = Math.min(playerBounds.x + playerBounds.width, enemyBounds.x + enemyBounds.width) - 
                            Math.max(playerBounds.x, enemyBounds.x);
                if (playerCharacter.getX() < enemyCharacter.getX()) {
                    playerCharacter.position.x -= overlapX;
                } else {
                    playerCharacter.position.x += overlapX;
                }

                // Aplicar daño por contacto
                DamageSystem.applyContactDamage(playerCharacter, enemyCharacter, 1);
            }
        } else {
            playerCharacter.isOverlappingEnemy = false;
        }
    }

    /**
     * Actualiza las balas y verifica colisiones.
     */
    private void updateBullets(float deltaTime) {
        for (int i = bullets.size() - 1; i >= 0; i--) {
            Bullet bullet = bullets.get(i);
            
            // Si la bala ha alcanzado su distancia máxima, eliminarla
            if (bullet.update(deltaTime)) {
                bullets.remove(i);
                bullet.dispose();
            }
            // También eliminar balas que salgan de los límites del mapa
            else if (bullet.position.x < -100 || bullet.position.x > 1540) {
                bullets.remove(i);
                bullet.dispose();
            }
        }
        checkBulletEnemyCollision();
    }

    /**
     * Dibuja todas las balas activas.
     */
    private void drawBullets() {
        for (Bullet bullet : bullets) {
            bullet.draw(gameBatch);
        }
    }

    /**
     * Verifica colisiones entre balas y enemigos.
     */
    private void checkBulletEnemyCollision() {
        if (enemyCharacter == null || !enemyCharacter.isAlive()) return;

        for (int i = bullets.size() - 1; i >= 0; i--) {
            Bullet bullet = bullets.get(i);
            if (bullet.getBounds().overlaps(enemyCharacter.getBounds())) {
                // Si la bala es perforante, verificar si ya ha golpeado a este enemigo
                if (bullet.isPiercing()) {
                    if (!bullet.hasHit(enemyCharacter)) {
                        if (enemyCharacter.isAlive()) {
                            enemyCharacter.takeDamageWithoutVulnerability(bullet.getDamage());
                            bullet.addHitEnemy(enemyCharacter); // Registrar el impacto
                        }
                    }
                } else {
                    // Si no es perforante, aplicar daño y eliminar la bala
                    if (enemyCharacter.isAlive()) {
                        enemyCharacter.takeDamageWithoutVulnerability(bullet.getDamage());
                    }
                    bullets.remove(i);
                    bullet.dispose();
                }
            }
        }
    }

    /**
     * Carga frames de animación desde archivos numerados.
     */
    private List<Sprite> loadSpriteFrames(String basePath, int frameCount) {
        List<Sprite> frames = new ArrayList<>();
        for (int i = 1; i <= frameCount; i++) {
            frames.add(new Sprite(new Texture(basePath + i + ".png")));
        }
        return frames;
    }

    /**
     * Maneja el redimensionamiento de la ventana.
     */
    public void resize(int width, int height) {
        if (dialogManager != null) {
            dialogManager.resize(width, height);
        }
        if (gameplayUI != null) {
            gameplayUI.resize(width, height);
        }
    }

    /**
     * Libera todos los recursos al salir del estado.
     */
    @Override
    public void exit() {
        if (sueloTexture != null) {
            sueloTexture.dispose();
        }

        if (dialogManager != null) {
            dialogManager.dispose();
        }

        if (gameplayUI != null) {
            gameplayUI.dispose();
        }

        if (pauseUI != null) {
            pauseUI.dispose();
        }

        if (interactionFont != null) {
            interactionFont.dispose();
        }

        if (backgroundTexture != null) {
            backgroundTexture.dispose();
        }

        if (blackTexture != null) {
            blackTexture.dispose();
        }

        for (Bullet bullet : bullets) {
            bullet.dispose();
        }

        if (playerCharacter != null) {
            playerCharacter.dispose();
        }
        if (enemyCharacter != null) {
            enemyCharacter.dispose();
        }
        if (npcController != null && npcController.character != null) {
            npcController.character.dispose();
        }
        // No es necesario liberar los controladores directamente ya que no contienen recursos desechables
    }
}