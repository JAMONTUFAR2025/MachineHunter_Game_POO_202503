package com.machinehunterdev.game.GameStates;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;
import com.machinehunterdev.game.Character.Character;
import com.machinehunterdev.game.Character.CharacterAnimator;
import com.machinehunterdev.game.Character.EnemyManager;
import com.machinehunterdev.game.Character.EnemySkin;
import com.machinehunterdev.game.Character.NPCController;
import com.machinehunterdev.game.Character.PlayerController;
import com.machinehunterdev.game.DamageTriggers.Bullet;
import com.machinehunterdev.game.DamageTriggers.DamageSystem;
import com.machinehunterdev.game.Dialog.Dialog;
import com.machinehunterdev.game.Dialog.DialogManager;
import com.machinehunterdev.game.Environment.SolidObject;
import com.machinehunterdev.game.FX.ImpactEffectManager;
import com.machinehunterdev.game.GameController;
import com.machinehunterdev.game.Gameplay.GlobalSettings;
import com.machinehunterdev.game.Levels.LevelData;
import com.machinehunterdev.game.Levels.LevelLoader;
import com.machinehunterdev.game.UI.GameplayUI;
import com.machinehunterdev.game.UI.NextLevelUI;
import com.machinehunterdev.game.UI.PauseUI;
import com.machinehunterdev.game.Util.IState;

/**
 * Estado principal del juego que puede cargar cualquier nivel.
 * Gestiona personajes, enemigos, balas, colisiones, diálogos y pausa.
 * 
 * @author MachineHunterDev
 */
public class GameplayState implements IState<GameController> {
    // === DATOS DEL NIVEL ===
    private LevelData currentLevel;
    private String currentLevelFile;
    
    // === ENTIDADES DEL JUEGO ===
    private ArrayList<SolidObject> solidObjects;
    private Character playerCharacter;
    private PlayerController playerController;
    private EnemyManager enemyManager;
    private NPCController npcController;

    // === SISTEMAS DE RENDERIZADO ===
    private SpriteBatch gameBatch;
    private OrthographicCamera camera;
    private Texture backgroundTexture;
    private Texture sueloTexture;
    private Texture blackTexture;

    // === SISTEMAS DE INTERFAZ ===
    private DialogManager dialogManager;
    private boolean isDialogActive = false;
    private GameplayUI gameplayUI;

    // === SISTEMA DE PAUSA ===
    private boolean isPaused = false;
    private PauseUI pauseUI;
    private NextLevelUI nextLevelUI;
    private boolean levelCompleted = false;
    private BitmapFont interactionFont;

    // === SISTEMA DE COMBATE ===
    private ArrayList<Bullet> bullets;
    private ImpactEffectManager impactEffectManager;

    // === INSTANCIA ===
    private GameController owner;

    /**
     * Constructor privado - usar createForLevel() en su lugar
     */
    private GameplayState() {}

    /**
     * Crea una instancia de GameplayState para un nivel específico.
     * @param levelFile Archivo JSON del nivel a cargar
     * @return Nueva instancia de GameplayState
     */
    public static GameplayState createForLevel(String levelFile) {
        // Solución temporal para evitar problemas de input al cambiar de estado
        Gdx.input.setInputProcessor(null);
        GameplayState state = new GameplayState();
        state.currentLevelFile = levelFile;
        return state;
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

    public void retryLevel() {
        Gdx.input.setInputProcessor(null);
        restartLevel();
    }

    public void restartLevel() {
        owner.stateMachine.changeState(createForLevel(currentLevelFile));
    }

    /**
     * Inicializa el estado al entrar.
     * @param owner Controlador del juego propietario
     */
    @Override
    public void enter(GameController owner) {
        GlobalSettings.currentLevelFile = this.currentLevelFile;
        isPaused = false;
        levelCompleted = false;
        this.owner = owner;
        this.gameBatch = owner.batch;
        this.camera = owner.camera;

        // Cargar el nivel
        loadLevel(currentLevelFile);
    }

    /**
     * Carga un nivel específico desde archivo JSON.
     * @param levelFile Ruta del archivo JSON del nivel
     */
    private void loadLevel(String levelFile) {
        currentLevel = LevelLoader.loadLevel(levelFile);
        
        // Inicializar recursos
        initializeResources();
        
        // Inicializar objetos del nivel
        initializeLevelObjects();
    }

    /**
     * Inicializa los recursos gráficos comunes.
     */
    private void initializeResources() {
        backgroundTexture = new Texture(currentLevel.backgroundTexture);
        dialogManager = new DialogManager(gameBatch);
        gameplayUI = new GameplayUI(gameBatch);
        pauseUI = new PauseUI(this, gameBatch);
        nextLevelUI = new NextLevelUI(this, gameBatch);
        bullets = new ArrayList<>();
        interactionFont = new BitmapFont(Gdx.files.internal("fonts/OrangeKid32.fnt"));
        impactEffectManager = new ImpactEffectManager(0.1f);

        Pixmap pixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        pixmap.setColor(0, 0, 0, 0.7f);
        pixmap.fill();
        blackTexture = new Texture(pixmap);
        pixmap.dispose();

        sueloTexture = new Texture(currentLevel.groundTexture);
    }

    /**
     * Inicializa todos los objetos del nivel (jugador, enemigos, NPCs, etc.).
     */
    private void initializeLevelObjects() {
        // Inicializar objetos sólidos
        initializeSolidObjects();
        
        // Inicializar jugador
        initializePlayer();
        
        // Inicializar enemigos
        initializeEnemies();
        
        // Inicializar NPCs
        initializeNPCs();
    }

    /**
     * Inicializa los objetos sólidos del nivel.
     */
    private void initializeSolidObjects() {
        solidObjects = new ArrayList<>();
        for (LevelData.SolidObjectData objData : currentLevel.solidObjectsData) {
            solidObjects.add(new SolidObject(
                objData.x, objData.y, objData.width, objData.height,
                sueloTexture, objData.walkable
            ));
        }
    }

    /**
     * Inicializa el jugador con sus animaciones.
     */
    private void initializePlayer() {
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

        playerCharacter = new Character(GlobalSettings.PLAYER_HEALTH, playerAnimator, null, 
                                      currentLevel.playerStartX, currentLevel.playerStartY, true);
        playerController = new PlayerController(playerCharacter);
    }

    /**
     * Inicializa todos los enemigos del nivel.
     */
    private void initializeEnemies() {
        enemyManager = new EnemyManager();

        for (LevelData.EnemyData enemyData : currentLevel.enemies) {
            EnemySkin skin = EnemySkin.getSkin(enemyData.type);

            List<Sprite> enemyIdleFrames = loadSpriteFrames(skin.idleFrames, 4);
            List<Sprite> enemyRunFrames = loadSpriteFrames(skin.runFrames, 4);
            List<Sprite> enemyDeadFrames = loadSpriteFrames(skin.deadFrames, 4);
            List<Sprite> enemyJumpFrames = loadSpriteFrames(skin.jumpFrames, 1);
            List<Sprite> enemyFallFrames = loadSpriteFrames(skin.fallFrames, 1);
            List<Sprite> enemyHurtFrames = loadSpriteFrames(skin.hurtFrames, 1);

            CharacterAnimator enemyAnimator = new CharacterAnimator(
                enemyIdleFrames, enemyRunFrames, enemyDeadFrames,
                enemyJumpFrames, enemyFallFrames, null,
                null, null, null,
                enemyHurtFrames, null
            );

            Character enemy = new Character(enemyData.health, enemyAnimator, null, enemyData.x, enemyData.y, false);

            ArrayList<Vector2> patrolPoints = new ArrayList<>();
            if (enemyData.patrolPoints != null) {
                for (LevelData.Point point : enemyData.patrolPoints) {
                    patrolPoints.add(new Vector2(point.x, point.y));
                }
            }

            enemyManager.addEnemy(enemyData.type, enemy, patrolPoints, enemyData.waitTime, enemyData.shootInterval);
        }
    }

    /**
     * Inicializa los NPCs del nivel.
     */
    private void initializeNPCs() {
        if (currentLevel.npcs.isEmpty()) {
            npcController = null;
            return;
        }
        
        LevelData.NPCData npcData = currentLevel.npcs.get(0); // Soporta solo 1 NPC por ahora
        
        List<Sprite> npcIdleFrames = loadSpriteFrames(npcData.idleFrames, 4);
        List<Sprite> npcRunFrames = loadSpriteFrames(npcData.runFrames, 8);
        List<Sprite> npcJumpFrames = loadSpriteFrames(npcData.jumpFrames, 2);
        List<Sprite> npcFallFrames = loadSpriteFrames(npcData.fallFrames, 2);
        List<Sprite> npcHurtFrames = loadSpriteFrames(npcData.hurtFrames, 2);
        List<Sprite> npcCrouchFrames = loadSpriteFrames(npcData.crouchFrames, 4);

        CharacterAnimator npcAnimator = new CharacterAnimator(
            npcIdleFrames, npcRunFrames, null,
            npcJumpFrames, npcFallFrames, null,
            npcHurtFrames, npcCrouchFrames, null,
            null, null
        );
        
        Character npcCharacter = new Character(100, npcAnimator, null, npcData.x, npcData.y, false);
        
        // Cargar diálogos del NPC
        List<Dialog> npcDialogues = loadNPCCDialogues(npcData.dialogues);
        
        npcController = new NPCController(npcCharacter, npcData.interactionRadius, npcDialogues);
    }

    /**
     * Carga los diálogos de un NPC desde el archivo de diálogo del nivel.
     */
    private List<Dialog> loadNPCCDialogues(List<String> dialogueSections) {
        List<Dialog> npcDialogues = new ArrayList<>();
        
        try {
            JsonReader jsonReader = new JsonReader();
            JsonValue base = jsonReader.parse(Gdx.files.internal(currentLevel.dialogueFile));
            
            for (String section : dialogueSections) {
                JsonValue dialogos = base.get(section);
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
            }
        } catch (Exception e) {
            Gdx.app.error("GameplayState", "Error al cargar diálogos del NPC", e);
        }
        
        return npcDialogues;
    }

    /**
     * Ejecuta la lógica del estado cada frame.
     */
    @Override
    public void execute() {
        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE) && !levelCompleted) {
            isPaused = !isPaused;
            if (isPaused) {
                Gdx.input.setInputProcessor(pauseUI);
            } else {
                Gdx.input.setInputProcessor(null);
            }
        }

        if (!isPaused) {
            updateGameLogic();
        }

        drawGameWorld();

        if (isPaused) {
            pauseUI.draw();
        } else if (levelCompleted) {
            nextLevelUI.draw();
        } else {
            if (isDialogActive) {
                dialogManager.render();
            }
            if (gameplayUI != null) {
                gameplayUI.draw(playerCharacter.getHealth(), playerCharacter.getCurrentWeapon());
            }
        }

        if (playerCharacter.isReadyForGameOver) {
            owner.stateMachine.changeState(GameOverState.instance);
        }
    }

    /**
     * Actualiza toda la lógica del juego.
     */
    private void updateGameLogic() {
        if (levelCompleted) return;
        
        float deltaTime = Gdx.graphics.getDeltaTime();

        if (isDialogActive) {
            dialogManager.update(deltaTime);
            if (!dialogManager.isDialogActive()) {
                isDialogActive = false;
            }
            handleDialogInput();
            return;
        }

        // Actualizar jugador
        playerController.update(deltaTime, solidObjects, bullets, playerCharacter);
        
        // Actualizar enemigos
        updateEnemies(deltaTime);
        
        // Verificar finalización del nivel
        checkLevelCompletion();
        
        // Actualizar NPC
        updateNPC(deltaTime);
        
        // Manejar interacción con NPC
        handleNPCInteraction();
        
        // Actualizar sistema de combate
        updateCombatSystems(deltaTime);
        
        // Centrar cámara
        playerController.centerCameraOnPlayer(camera);
    }

    /**
     * Actualiza todos los enemigos y elimina los que están muertos.
     */
    private void updateEnemies(float deltaTime) {
        enemyManager.update(deltaTime, solidObjects, bullets, playerCharacter);

        ArrayList<com.machinehunterdev.game.Character.IEnemy> enemies = enemyManager.getEnemies();
        for (int i = enemies.size() - 1; i >= 0; i--) {
            com.machinehunterdev.game.Character.IEnemy enemy = enemies.get(i);
            if (!enemy.getCharacter().isAlive() && enemy.getCharacter().isReadyForRemoval()) {
                enemy.getCharacter().dispose();
                enemies.remove(i);
            }
        }
    }

    /**
     * Verifica si el nivel ha sido completado (todos los enemigos derrotados).
     */
    private void checkLevelCompletion() {
        if (enemyManager.getEnemies().isEmpty()) {
            levelCompleted = true;
            Gdx.input.setInputProcessor(nextLevelUI);
        }
    }

    /**
     * Obtiene el archivo del nivel actual.
     * @return Ruta del archivo del nivel actual
     */
    public String getCurrentLevelFile() {
        return currentLevelFile;
    }

    public LevelData getCurrentLevel() {
        return currentLevel;
    }

    public GameController getOwner() {
        return owner;
    }

    /**
     * Actualiza el NPC si existe.
     */
    private void updateNPC(float deltaTime) {
        if (npcController != null) {
            npcController.update(deltaTime, solidObjects, bullets, playerCharacter);
        }
    }

    /**
     * Maneja la interacción con el NPC (tecla E).
     */
    private void handleNPCInteraction() {
        if (Gdx.input.isKeyJustPressed(Input.Keys.E)) {
            if (npcController != null && npcController.isInRange()) {
                List<Dialog> dialogues = npcController.getDialogues();
                if (dialogues != null && !dialogues.isEmpty()) {
                    dialogManager.showDialog(dialogues.get(0));
                    isDialogActive = true;
                }
            }
        }
    }

    /**
     * Actualiza el sistema de combate (balas, efectos de impacto).
     */
    private void updateCombatSystems(float deltaTime) {
        updateBullets(deltaTime);
        impactEffectManager.update(deltaTime);
        checkPlayerEnemyCollision();
        checkBulletEnemyCollision();
        checkBulletPlayerCollision();
    }

    /**
     * Renderiza el mundo del juego.
     */
    private void drawGameWorld() {
        gameBatch.setProjectionMatrix(camera.combined);
        gameBatch.begin();

        // Dibujar fondo con opacidad reducida
        gameBatch.setColor(1, 1, 1, 0.5f); // 50% de opacidad
        int backgroundWidth = GlobalSettings.VIRTUAL_WIDTH;
        int mapWidth = 1440; // Ajusta según tu mapa
        int backgroundCount = (int) Math.ceil((float) mapWidth / backgroundWidth) + 1;
        for (int i = 0; i < backgroundCount; i++) {
            gameBatch.draw(backgroundTexture, i * backgroundWidth, 0);
        }
        gameBatch.setColor(1, 1, 1, 1); // Restaurar opacidad completa

        // Dibujar objetos sólidos
        for (SolidObject floor : solidObjects) {
            floor.render(gameBatch);
        }

        // Dibujar personajes
        playerCharacter.draw(gameBatch);
        enemyManager.draw(gameBatch);

        // Dibujar NPC
        if (npcController != null) {
            npcController.render(gameBatch);
            if (npcController.isInRange()) {
                drawNPCInteractionPrompt();
            }
        }

        // Dibujar sistema de combate
        drawBullets();
        impactEffectManager.draw(gameBatch);

        gameBatch.end();
    }

    /**
     * Dibuja el mensaje de interacción con el NPC.
     */
    private void drawNPCInteractionPrompt() {
        GlyphLayout layout = new GlyphLayout();
        String message = "E para interactuar";
        interactionFont.getData().setScale(0.5f);
        layout.setText(interactionFont, message);

        float boxWidth = layout.width + 20;
        float boxHeight = layout.height + 10;
        float boxX = npcController.character.position.x + (npcController.character.getWidth() / 2) - (boxWidth / 2);
        float boxY = npcController.character.position.y + npcController.character.getHeight() + 10;

        gameBatch.draw(blackTexture, boxX, boxY, boxWidth, boxHeight);

        float textX = boxX + 10;
        float textY = boxY + layout.height + 5;
        interactionFont.draw(gameBatch, layout, textX, textY);
        interactionFont.getData().setScale(1.0f);
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
     * Verifica colisiones entre jugador y enemigos.
     */
    private void checkPlayerEnemyCollision() {
        playerCharacter.isOverlappingEnemy = false;
        for (com.machinehunterdev.game.Character.IEnemy enemy : enemyManager.getEnemies()) {
            Character enemyCharacter = enemy.getCharacter();
            if (enemyCharacter.isAlive()) {
                Rectangle playerBounds = playerCharacter.getBounds();
                Rectangle enemyBounds = enemyCharacter.getBounds();

                if (playerBounds.overlaps(enemyBounds)) {
                    playerCharacter.isOverlappingEnemy = true;

                    if (DamageSystem.canTakeDamage(playerCharacter)) {
                        float overlapX = Math.min(playerBounds.x + playerBounds.width, enemyBounds.x + enemyBounds.width) -
                                    Math.max(playerBounds.x, enemyBounds.x);
                        if (playerCharacter.getX() < enemyCharacter.getX()) {
                            playerCharacter.position.x -= overlapX;
                        } else {
                            playerCharacter.position.x += overlapX;
                        }
                        DamageSystem.applyContactDamage(playerCharacter, enemyCharacter, 1);
                    }
                    break; 
                }
            }
        }
    }

    /**
     * Actualiza las balas y verifica colisiones.
     */
    private void updateBullets(float deltaTime) {
        for (int i = bullets.size() - 1; i >= 0; i--) {
            Bullet bullet = bullets.get(i);
            
            if (bullet.update(deltaTime) || bullet.position.x < -100 || bullet.position.x > 1540) {
                bullets.remove(i);
                bullet.dispose();
            }
        }
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
        for (int i = bullets.size() - 1; i >= 0; i--) {
            Bullet bullet = bullets.get(i);
            if (bullet.getOwner() == playerCharacter) {
                for (com.machinehunterdev.game.Character.IEnemy enemy : enemyManager.getEnemies()) {
                    Character enemyCharacter = enemy.getCharacter();
                    if (enemyCharacter.isAlive() && bullet.getBounds().overlaps(enemyCharacter.getBounds())) {
                        if (bullet.isPiercing()) {
                            if (!bullet.hasHit(enemyCharacter)) {
                                enemyCharacter.takeDamageWithoutVulnerability(bullet.getDamage());
                                bullet.addHitEnemy(enemyCharacter);
                                impactEffectManager.createImpact(bullet.position.x, bullet.position.y, bullet.getWeaponType());
                            }
                        } else {
                            enemyCharacter.takeDamageWithoutVulnerability(bullet.getDamage());
                            impactEffectManager.createImpact(bullet.position.x, bullet.position.y, bullet.getWeaponType());
                            bullets.remove(i);
                            bullet.dispose();
                            break; 
                        }
                    }
                }
            }
        }
    }

    /**
     * Verifica colisiones entre balas y el jugador.
     */
    private void checkBulletPlayerCollision() {
        for (int i = bullets.size() - 1; i >= 0; i--) {
            Bullet bullet = bullets.get(i);
            if (bullet.getOwner() != playerCharacter) {
                if (DamageSystem.canTakeDamage(playerCharacter) && playerCharacter.isAlive() && bullet.getBounds().overlaps(playerCharacter.getBounds())) {
                    DamageSystem.applyContactDamage(playerCharacter, bullet.getOwner(), bullet.getDamage());
                    impactEffectManager.createImpact(bullet.position.x, bullet.position.y, bullet.getWeaponType());
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
        if (basePath == null) {
            return null;
        }
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
        // Liberar texturas
        disposeTexture(sueloTexture);
        disposeTexture(backgroundTexture);
        disposeTexture(blackTexture);
        
        // Liberar fuentes
        if (interactionFont != null) {
            interactionFont.dispose();
        }
        
        // Liberar sistemas
        if (dialogManager != null) dialogManager.dispose();
        if (gameplayUI != null) gameplayUI.dispose();
        if (pauseUI != null) pauseUI.dispose();
        if (nextLevelUI != null) nextLevelUI.dispose();
        if (impactEffectManager != null) impactEffectManager.dispose();
        
        // Liberar balas
        for (Bullet bullet : bullets) {
            bullet.dispose();
        }
        
        // Liberar personajes
        if (playerCharacter != null) playerCharacter.dispose();
        if (enemyManager != null) {
            for (com.machinehunterdev.game.Character.IEnemy enemy : enemyManager.getEnemies()) {
                enemy.getCharacter().dispose();
            }
        }
        if (npcController != null && npcController.character != null) {
            npcController.character.dispose();
        }
    }

    /**
     * Método auxiliar para liberar texturas de forma segura.
     */
    private void disposeTexture(Texture texture) {
        if (texture != null /*&& Esto no funciona: !texture.isDisposed()*/) {
            texture.dispose();
        }
    }
}