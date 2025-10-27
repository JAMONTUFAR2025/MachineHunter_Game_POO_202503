package com.machinehunterdev.game.GameStates;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.Gdx;
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
import com.machinehunterdev.game.Character.EnemyType;
import com.machinehunterdev.game.Character.NPCController;
import com.machinehunterdev.game.Character.PlayerController;
import com.machinehunterdev.game.DamageTriggers.Bullet;
import com.machinehunterdev.game.DamageTriggers.DamageSystem;
import com.machinehunterdev.game.DamageTriggers.WeaponType;
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
    private Texture blackTexture;
    private Texture groundTexture;

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

    // === CONTROL DE ESTADO ===
    private GameController owner;
    private boolean ignoreInputOnFirstFrame = true;

    private GameplayState() {}

    public static GameplayState createForLevel(String levelFile) {
        Gdx.input.setInputProcessor(null);
        GameplayState state = new GameplayState();
        state.currentLevelFile = levelFile;
        return state;
    }

    public void resumeGame() {
        isPaused = false;
        Gdx.input.setInputProcessor(null);
    }

    public void exitToMainMenu() {
        owner.stateMachine.changeState(MainMenuState.instance);
    }

    public void restartLevel() {
        owner.stateMachine.changeState(createForLevel(currentLevelFile));
    }

    @Override
    public void enter(GameController owner) {
        GlobalSettings.currentLevelFile = this.currentLevelFile;
        this.ignoreInputOnFirstFrame = true;
        isPaused = false;
        levelCompleted = false;
        this.owner = owner;
        this.gameBatch = owner.batch;
        this.camera = owner.camera;
        loadLevel(currentLevelFile);
    }

    private void loadLevel(String levelFile) {
        currentLevel = LevelLoader.loadLevel(levelFile);
        initializeResources();
        initializeLevelObjects();
    }

    private void initializeResources() {
        backgroundTexture = new Texture(currentLevel.backgroundTexture);
        GlobalSettings.levelWidth = currentLevel.levelWidth; // Establecer el ancho del nivel en GlobalSettings
        dialogManager = new DialogManager(gameBatch);
        gameplayUI = new GameplayUI(gameBatch);
        pauseUI = new PauseUI(this, gameBatch);
        nextLevelUI = new NextLevelUI(this, gameBatch);
        bullets = new ArrayList<>();
        interactionFont = new BitmapFont(Gdx.files.internal("fonts/OrangeKid32.fnt"));
        impactEffectManager = new ImpactEffectManager(0.1f);
        groundTexture = new Texture(currentLevel.groundTexture);

        Pixmap pixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        pixmap.setColor(0, 0, 0, 0.7f);
        pixmap.fill();
        blackTexture = new Texture(pixmap);
        pixmap.dispose();
    }

    private void initializeLevelObjects() {
        initializeSolidObjects();
        initializePlayer();
        initializeEnemies();
        initializeNPCs();
    }

    private void initializeSolidObjects() {
        solidObjects = new ArrayList<>();
        for (LevelData.SolidObjectData objData : currentLevel.solidObjectsData) {
            SolidObject newObject = null;
            if (objData.type != null && !objData.type.isEmpty()) {
                // Crear objeto basado en el "type"
                newObject = new SolidObject(objData.x, objData.y, objData.type, objData.walkable);
            } else if (objData.texture != null && !objData.texture.equals(currentLevel.groundTexture)) {
                // Crear objeto con definición explícita (sistema antiguo)
                newObject = new SolidObject(objData.x, objData.y, objData.width, objData.height, new Texture(objData.texture), objData.walkable);
            }

            if (newObject != null) {
                solidObjects.add(newObject);
            }
        }
    }

    private void initializePlayer() {
        List<Sprite> playerIdleFrames = loadSpriteFrames("Player/PlayerIdle", 4);
        List<Sprite> playerRunFrames = loadSpriteFrames("Player/PlayerRun", 8);
        List<Sprite> playerJumpFrames = loadSpriteFrames("Player/PlayerJump", 1);
        List<Sprite> playerFallFrames = loadSpriteFrames("Player/PlayerFall", 1);
        List<Sprite> playerHurtFrames = loadSpriteFrames("Player/PlayerHurt", 1);
        List<Sprite> playerCrouchFrames = loadSpriteFrames("Player/PlayerCrouch", 4);
        List<Sprite> playerLaserAttackFrames = loadSpriteFrames("Player/PlayerLaserAttack", 2);
        List<Sprite> playerIonAttackFrames = loadSpriteFrames("Player/PlayerIonAttack", 2);
        List<Sprite> playerRailgunAttackFrames = loadSpriteFrames("Player/PlayerRailgunAttack", 2);
        List<Sprite> playerDeadFrames = loadSpriteFrames("Player/PlayerDead", 14);

        CharacterAnimator playerAnimator = new CharacterAnimator(
            playerIdleFrames, playerRunFrames, playerDeadFrames,
            playerJumpFrames, playerFallFrames, null,
            playerLaserAttackFrames, playerIonAttackFrames, playerRailgunAttackFrames,
            playerHurtFrames, playerCrouchFrames
        );

        playerCharacter = new Character(GlobalSettings.PLAYER_HEALTH, playerAnimator, null, 
        currentLevel.playerStartX, currentLevel.playerStartY, true);
        
        float adjustedPlayerY = findGroundY(playerCharacter.position.x, currentLevel.playerStartY, playerCharacter.getWidth());
        playerCharacter.position.y = adjustedPlayerY;
        playerCharacter.onGround = true;
        playerCharacter.velocity.y = 0;

        playerController = new PlayerController(playerCharacter);
    }

    private void initializeEnemies() {
        enemyManager = new EnemyManager();

        for (LevelData.EnemyData enemyData : currentLevel.enemies) {
            EnemySkin skin = EnemySkin.getSkin(enemyData.type);

            List<Sprite> enemyIdleFrames;

            // Nuevos frames de animación para enemigos voladores, cuatro para los demás
            if (enemyData.type == EnemyType.FLYING) {
                enemyIdleFrames = loadSpriteFrames(skin.idleFrames, 9);
            } else {
                enemyIdleFrames = loadSpriteFrames(skin.idleFrames, 4);
            }

            List<Sprite> enemyRunFrames = loadSpriteFrames(skin.runFrames, 4);
            List<Sprite> enemyDeadFrames = loadSpriteFrames(skin.deadFrames, 4);
            List<Sprite> enemyJumpFrames = loadSpriteFrames(skin.jumpFrames, 1);
            List<Sprite> enemyFallFrames = loadSpriteFrames(skin.fallFrames, 1);
            List<Sprite> enemyHurtFrames = loadSpriteFrames(skin.hurtFrames, 1);

            List<Sprite> enemyAttackFrames = loadSpriteFrames(skin.attackFrames, 2);
            CharacterAnimator enemyAnimator = new CharacterAnimator(
                enemyIdleFrames, enemyRunFrames, enemyDeadFrames,
                enemyJumpFrames, enemyFallFrames, enemyAttackFrames,
                null, null, null,
                enemyHurtFrames, null
            );

            Character enemy = new Character(enemyData.health, enemyAnimator, null, enemyData.x, enemyData.y, false);
            if (enemyData.type == EnemyType.SHOOTER) {
                enemy.switchWeapon(com.machinehunterdev.game.DamageTriggers.WeaponType.THUNDER);
            }

            if (enemyData.type == EnemyType.FLYING) {
                enemy.position.y -= enemy.getHeight() / 2;
            } else {
                float adjustedEnemyY = findGroundY(enemy.position.x, enemyData.y, enemy.getWidth());
                enemy.position.y = adjustedEnemyY;
                enemy.onGround = true;
                enemy.velocity.y = 0;
            }

            ArrayList<Vector2> patrolPoints = new ArrayList<>();
            if (enemyData.patrolPoints != null) {
                for (LevelData.Point point : enemyData.patrolPoints) {
                    patrolPoints.add(new Vector2(point.x, point.y));
                }
            }

            enemyManager.addEnemy(enemyData.type, enemy, patrolPoints, enemyData.waitTime, enemyData.shootInterval, enemyData.shootTime);
        }
    }

    private void initializeNPCs() {
        if (currentLevel.npcs.isEmpty()) {
            npcController = null;
            return;
        }
        
        LevelData.NPCData npcData = currentLevel.npcs.get(0);
        
        List<Sprite> npcIdleFrames = loadSpriteFrames(npcData.idleFrames, 4);

        CharacterAnimator npcAnimator = new CharacterAnimator(
            npcIdleFrames, null, null,
            null, null, null,
            null, null, null,
            null, null
        );
        
        Character npcCharacter = new Character(100, npcAnimator, null, npcData.x, npcData.y, false);
        
        float adjustedNpcY = findGroundY(npcCharacter.position.x, npcData.y, npcCharacter.getWidth());
        npcCharacter.position.y = adjustedNpcY;
        npcCharacter.onGround = true;
        npcCharacter.velocity.y = 0;
        
        List<Dialog> npcDialogues = loadNPCCDialogues(npcData.dialogues);
        
        npcController = new NPCController(npcCharacter, npcData.interactionRadius, npcDialogues);
    }

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

    private boolean transitioningToGameOver = false;

    @Override
    public void execute() {
        if (Gdx.input.isKeyJustPressed(GlobalSettings.CONTROL_PAUSE) && !levelCompleted) {
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

        if (playerCharacter.readyForGameOverTransition && !transitioningToGameOver) {
            transitioningToGameOver = true;
            CharacterAnimator animator = playerCharacter.characterAnimator;
            playerCharacter.characterAnimator = null; 
            GameOverState.setPlayerAnimator(animator);
            owner.stateMachine.changeState(GameOverState.instance);
        }
    }

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

        playerController.update(deltaTime, solidObjects, bullets, playerCharacter);
        
        updateEnemies(deltaTime);
        
        checkLevelCompletion();
        
        updateNPC(deltaTime);
        
        if (!ignoreInputOnFirstFrame) {
            handleNPCInteraction();
        }
        
        updateCombatSystems(deltaTime);
        
        playerController.centerCameraOnPlayer(camera);

        ignoreInputOnFirstFrame = false;
    }

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

    private void checkLevelCompletion() {
        if (enemyManager.getEnemies().isEmpty()) {
            levelCompleted = true;
            Gdx.input.setInputProcessor(nextLevelUI);
        }
    }

    private void updateNPC(float deltaTime) {
        if (npcController != null) {
            npcController.update(deltaTime, solidObjects, bullets, playerCharacter);
        }
    }

    private void handleNPCInteraction() {
        if (Gdx.input.isKeyJustPressed(GlobalSettings.CONTROL_INTERACT)) {
            if (npcController != null && npcController.isInRange() && playerCharacter.onGround) {
                List<Dialog> dialogues = npcController.getDialogues();
                if (dialogues != null && !dialogues.isEmpty()) {
                    dialogManager.showDialog(dialogues.get(0));
                    isDialogActive = true;
                }
            }
        }
    }

    private void updateCombatSystems(float deltaTime) {
        updateBullets(deltaTime);
        impactEffectManager.update(deltaTime);
        checkPlayerEnemyCollision();
        checkBulletEnemyCollision();
        checkBulletPlayerCollision();
    }

    private void drawGameWorld() {
        gameBatch.setProjectionMatrix(camera.combined);
        gameBatch.begin();

        gameBatch.setColor(1, 1, 1, 0.5f);
        int backgroundWidth = GlobalSettings.VIRTUAL_WIDTH;
        int mapWidth = GlobalSettings.levelWidth;
        int backgroundCount = (int) Math.ceil((float) mapWidth / backgroundWidth) + 1;
        for (int i = 0; i < backgroundCount; i++) {
            gameBatch.draw(backgroundTexture, i * backgroundWidth, 0);
        }
        gameBatch.setColor(1, 1, 1, 1);

        drawGround();

        for (SolidObject obj : solidObjects) {
            obj.render(gameBatch);
        }

        playerCharacter.draw(gameBatch);
        enemyManager.draw(gameBatch);

        if (npcController != null) {
            npcController.render(gameBatch);
            if (npcController.isInRange()) {
                drawNPCInteractionPrompt();
            }
        }

        drawBullets();
        impactEffectManager.draw(gameBatch);

        gameBatch.end();
    }

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

    private void drawGround() {
        int groundWidth = 320; // The user specified 320 pixels
        int groundCount = (int) Math.ceil((float) GlobalSettings.levelWidth / groundWidth);
        for (int i = 0; i < groundCount; i++) {
            gameBatch.draw(groundTexture, i * groundWidth, 0);
        }
    }

    private void handleDialogInput() {
        if (Gdx.input.isKeyJustPressed(GlobalSettings.CONTROL_INTERACT)) {
            if (dialogManager.isDialogActive()) {
                dialogManager.nextLine();
            }
        }
    }

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

                        /* Animación de impacto al contacto */
                        // Calcula la intersección de las cajas de colisión
                        Rectangle intersection = new Rectangle(playerBounds);
                        intersection.set(intersection.x, intersection.y, intersection.width, intersection.height);
                        intersection.x = Math.max(playerBounds.x, enemyBounds.x);
                        intersection.width = Math.min(playerBounds.x + playerBounds.width, enemyBounds.x + enemyBounds.width) - intersection.x;
                        intersection.y = Math.max(playerBounds.y, enemyBounds.y);
                        intersection.height = Math.min(playerBounds.y + playerBounds.height, enemyBounds.y + enemyBounds.height) - intersection.y;

                        // Calcula el punto central de la intersección
                        float impactX = intersection.x + intersection.width / 2;
                        float impactY = intersection.y + intersection.height / 2;

                        // Crea el efecto de impacto en el punto de colisión
                        switch (enemy.getType()) {
                            case PATROLLER:
                                impactEffectManager.createImpact(impactX, impactY, WeaponType.ELECTRIC);
                                break;
                            case SHOOTER:
                                impactEffectManager.createImpact(impactX, impactY, WeaponType.THUNDER);
                                break;
                            case FLYING:
                                impactEffectManager.createImpact(impactX, impactY, WeaponType.ION);
                                break;
                        }
                    }
                    break; 
                }
            }
        }
    }

        private void updateBullets(float deltaTime) {
        for (int i = bullets.size() - 1; i >= 0; i--) {
            Bullet bullet = bullets.get(i);
            
            if (bullet.update(deltaTime) || bullet.position.x < camera.position.x - GlobalSettings.VIRTUAL_WIDTH / 2 - 100 || bullet.position.x > camera.position.x + GlobalSettings.VIRTUAL_WIDTH / 2 + 100) {
                bullets.remove(i);
                bullet.dispose();
            }
        }
    }

    private void drawBullets() {
        for (Bullet bullet : bullets) {
            bullet.draw(gameBatch);
        }
    }

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
        disposeTexture(backgroundTexture);
        disposeTexture(blackTexture);
        disposeTexture(groundTexture);
        
        for (SolidObject obj : solidObjects) {
            obj.dispose();
        }

        if (interactionFont != null) {
            interactionFont.dispose();
        }
        
        if (dialogManager != null) dialogManager.dispose();
        if (gameplayUI != null) gameplayUI.dispose();
        if (pauseUI != null) pauseUI.dispose();
        if (nextLevelUI != null) nextLevelUI.dispose();
        if (impactEffectManager != null) impactEffectManager.dispose();
        
        for (Bullet bullet : bullets) {
            bullet.dispose();
        }
        
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

    private void disposeTexture(Texture texture) {
        if (texture != null) {
            texture.dispose();
        }
    }

    private float findGroundY(float x, float initialY, float characterWidth) {
        float closestGroundY = GlobalSettings.GROUND_LEVEL;

        for (SolidObject obj : solidObjects) {
            if (obj.isWalkable()) {
                Rectangle platform = obj.getBounds();
                if (x < platform.x + platform.width && x + characterWidth > platform.x) {
                    float platformTop = platform.y + platform.height;
                    if (platformTop <= initialY && platformTop > closestGroundY) {
                        closestGroundY = platformTop;
                    }
                }
            }
        }
        return closestGroundY;
    }

    public String getCurrentLevelFile() {
        return currentLevelFile;
    }

    public LevelData getCurrentLevel() {
        return currentLevel;
    }

    public GameController getOwner() {
        return owner;
    }
}