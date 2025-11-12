package com.machinehunterdev.game.GameStates;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;
import com.machinehunterdev.game.Audio.AudioId;
import com.machinehunterdev.game.Audio.AudioManager;
import com.machinehunterdev.game.Character.BaseEnemy;
import com.machinehunterdev.game.Character.BossEnemyController;
import com.machinehunterdev.game.Character.Character;
import com.machinehunterdev.game.Character.CharacterAnimator;
import com.machinehunterdev.game.Character.EnemyManager;
import com.machinehunterdev.game.Character.EnemySkin;
import com.machinehunterdev.game.Character.EnemyType;
import com.machinehunterdev.game.Character.IEnemy;
import com.machinehunterdev.game.Character.NPCController;
import com.machinehunterdev.game.Character.PlayerController;
import com.machinehunterdev.game.DamageTriggers.Bullet;
import com.machinehunterdev.game.DamageTriggers.DamageSystem;
import com.machinehunterdev.game.DamageTriggers.WeaponType;
import com.machinehunterdev.game.Dialog.Dialog;
import com.machinehunterdev.game.Dialog.DialogManager;
import com.machinehunterdev.game.Environment.SolidObject;
import com.machinehunterdev.game.FX.ImpactEffectManager;
import com.machinehunterdev.game.FX.LandingEffectManager;
import com.machinehunterdev.game.FX.LandingEffectManager.EffectType;
import com.machinehunterdev.game.GameController;
import com.machinehunterdev.game.Gameplay.GlobalSettings;
import com.machinehunterdev.game.Levels.LevelData;
import com.machinehunterdev.game.Levels.LevelLoader;
import com.machinehunterdev.game.UI.GameplayUI;
import com.machinehunterdev.game.UI.NextLevelUI;
import com.machinehunterdev.game.Util.IState;
import com.machinehunterdev.game.Util.SpriteAnimator;

/**
 * Representa el estado principal del juego donde ocurre la accion.
 * Maneja la logica del juego, la actualizacion de entidades, la renderizacion
 * y la interaccion del usuario durante el juego.
 */
public class GameplayState implements IState<GameController> {
    // === DATOS DEL NIVEL ===
    // Almacena los datos del nivel actual cargado.
    private LevelData currentLevel;
    // Ruta del archivo del nivel actual.
    private String currentLevelFile;
    
    // === ENTIDADES DEL JUEGO ===
    // Lista de objetos solidos en el nivel (plataformas, paredes, etc.).
    private ArrayList<SolidObject> solidObjects;
    // El personaje principal controlado por el jugador.
    private Character playerCharacter;
    // Controlador para la logica del jugador.
    private PlayerController playerController;
    // Administrador de todos los enemigos en el nivel.
    private EnemyManager enemyManager;
    // Lista de controladores para los personajes no jugables (NPCs).
    private List<NPCController> npcControllers;

    // === SISTEMAS DE RENDERIZADO ===
    // Batch para dibujar los sprites del juego.
    private SpriteBatch gameBatch;
    // Camara ortografica para la vista del juego.
    private OrthographicCamera camera;
    // Textura del fondo del nivel.
    private Texture backgroundTexture;
    // Textura de un pixel negro, usada para superposiciones o fondos de UI.
    private Texture blackTexture;
    // Textura del suelo del nivel.
    private Texture groundTexture;

    // === SISTEMAS DE INTERFAZ ===
    // Administrador para mostrar y controlar los dialogos.
    private DialogManager dialogManager;
    // Indica si un dialogo esta activo actualmente.
    private boolean isDialogActive = false;
    // Interfaz de usuario durante el juego (HUD, etc.).
    private GameplayUI gameplayUI;
    // Dialogos activos de los NPCs.
    private List<Dialog> activeNPCDialogues = new ArrayList<>();

    // === SISTEMA DE PAUSA ===
    // Interfaz de usuario para la transicion al siguiente nivel.
    private NextLevelUI nextLevelUI;
    // Indica si el nivel ha sido completado.
    private boolean levelCompleted = false;
    // Fuente para mostrar texto de interaccion.
    private BitmapFont interactionFont;

    // === SISTEMA DE COMBATE ===
    // Lista de balas activas en el juego.
    private ArrayList<Bullet> bullets;
    // Administrador de efectos de impacto.
    private ImpactEffectManager impactEffectManager;
    private LandingEffectManager landingEffectManager;
    // Renderizador de formas para depuracion o elementos simples.
    private ShapeRenderer shapeRenderer;
    // Textura para la advertencia de ataque de trueno.
    private Texture thunderWarningTexture;
    // Textura para la advertencia de invocacion de enemigos.
    private Texture summonWarningTexture;
    // Animacion para el ataque de trueno.
    private SpriteAnimator thunderAttackAnimator;
    private boolean wasBossStriking = false;

    // === CONTROL DE ESTADO ===
    // Referencia al controlador principal del juego.
    private GameController owner;
    // Bandera para ignorar la entrada del usuario en el primer frame.
    private boolean ignoreInputOnFirstFrame = true;
    //private boolean isBossPhase2 = false;


    // Constructor privado para forzar el uso del metodo estatico de creacion.
    private GameplayState() {}

    /**
     * Crea una nueva instancia de GameplayState para un nivel especifico.
     * @param levelFile La ruta del archivo del nivel a cargar.
     * @return Una nueva instancia de GameplayState.
     */
    public static GameplayState createForLevel(String levelFile) {
        // Desactiva el procesador de entrada para evitar interacciones no deseadas.
        Gdx.input.setInputProcessor(null);
        GameplayState state = new GameplayState();
        state.currentLevelFile = levelFile;
        return state;
    }



    /**
     * Sale del nivel actual y regresa al menu principal.
     */
    public void exitToMainMenu() {
        // Si el juego esta en pausa, quita el estado de pausa.
        owner.stateMachine.pop(); 
        // Cambia al estado del menu principal.
        owner.stateMachine.changeState(MainMenuState.instance);
    }

    /**
     * Reinicia el nivel actual.
     */
    public void restartLevel() {
        // Detiene la musica actual.
        AudioManager.getInstance().stopMusic(false);
        // Si el juego esta en pausa, quita el estado de pausa.
        owner.stateMachine.pop(); 
        // Cambia a una nueva instancia del nivel actual.
        owner.stateMachine.changeState(createForLevel(currentLevelFile));
    }

    /**
     * Se llama cuando se entra a este estado. Inicializa los componentes del juego.
     * @param owner El controlador principal del juego.
     */
    @Override
    public void enter(GameController owner) {
        // Establece el archivo del nivel actual en la configuracion global.
        GlobalSettings.currentLevelFile = this.currentLevelFile;
        // Ignora la entrada del usuario en el primer frame para evitar activaciones accidentales.
        this.ignoreInputOnFirstFrame = true;

        // Reinicia el estado de completado del nivel.
        levelCompleted = false;
        this.owner = owner;
        this.gameBatch = owner.batch;
        this.camera = owner.camera;
        // Establece la camara en el administrador de audio para efectos 3D.
        AudioManager.getInstance().setCamera(this.camera);

        // Selecciona y reproduce la musica de fondo segun el nivel actual.
        if (currentLevelFile.equals("Levels/Level 0.json") || 
            currentLevelFile.equals("Levels/Level 1.json") || 
            currentLevelFile.equals("Levels/Level 2.json") || 
            currentLevelFile.equals("Levels/Level 4.json")) {
            AudioManager.getInstance().playMusic("Audio/Soundtrack/NormalBattleTheme.mp3", true, false);
        } else if (currentLevelFile.equals("Levels/Level 3.json")) {
            AudioManager.getInstance().playMusic("Audio/Soundtrack/GeminiBattle.mp3", true, false);
        } else if (currentLevelFile.equals("Levels/Level 5.json")) {
            AudioManager.getInstance().playMusic("Audio/Soundtrack/ChatGPTBattle.mp3", true, false);
        }

        // Carga los datos del nivel.
        loadLevel(currentLevelFile);
    }

    /**
     * Carga los datos del nivel desde un archivo JSON.
     * @param levelFile La ruta del archivo del nivel.
     */
    private void loadLevel(String levelFile) {
        currentLevel = LevelLoader.loadLevel(levelFile);
        initializeResources();
        initializeLevelObjects();
    }

    /**
     * Inicializa los recursos graficos y de audio necesarios para el nivel.
     */
    private void initializeResources() {
        // Carga la textura de fondo del nivel.
        backgroundTexture = new Texture(currentLevel.backgroundTexture);
        // Establece el ancho del nivel en la configuracion global.
        GlobalSettings.levelWidth = currentLevel.levelWidth; 
        // Inicializa el administrador de dialogos.
        dialogManager = new DialogManager(owner, gameBatch);
        // Inicializa la interfaz de usuario del juego.
        gameplayUI = new GameplayUI(gameBatch);

        // Inicializa la interfaz de usuario para la transicion al siguiente nivel.
        nextLevelUI = new NextLevelUI(this, gameBatch);
        // Inicializa la lista de balas.
        bullets = new ArrayList<>();
        // Carga la fuente para las interacciones.
        interactionFont = new BitmapFont(Gdx.files.internal("fonts/OrangeKid32.fnt"));
        // Inicializa el administrador de efectos de impacto.
        impactEffectManager = new ImpactEffectManager(0.1f);
        // Inicializa el administrador de efectos de aterrizaje.
        landingEffectManager = new LandingEffectManager(0.1f);
        // Carga la textura del suelo.
        groundTexture = new Texture(currentLevel.groundTexture);
        // Inicializa el renderizador de formas para la depuracion.
        shapeRenderer = new ShapeRenderer();
        // Carga las texturas de advertencia para ataques de jefes.
        thunderWarningTexture = new Texture("FX/ThunderWarning.png");
        summonWarningTexture = new Texture("FX/SummonWarning.png");
        // Carga los frames de animacion para el ataque de trueno.
        List<Sprite> thunderFrames = loadSpriteFrames("FX/ThunderAttack", 5);
        thunderAttackAnimator = new SpriteAnimator(thunderFrames, 0.1f, false);

        // Crea una textura de un pixel negro semitransparente para superposiciones.
        Pixmap pixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        pixmap.setColor(0, 0, 0, 0.7f);
        pixmap.fill();
        blackTexture = new Texture(pixmap);
        pixmap.dispose();
    }

    /**
     * Inicializa los objetos del nivel, como objetos solidos, jugador, enemigos y NPCs.
     */
    private void initializeLevelObjects() {
        initializeSolidObjects();
        initializePlayer();
        initializeEnemies();
        initializeNPCs();
    }

    /**
     * Inicializa los objetos solidos del nivel.
     */
    private void initializeSolidObjects() {
        solidObjects = new ArrayList<>();
        for (LevelData.SolidObjectData objData : currentLevel.solidObjectsData) {
            SolidObject newObject = null;
            if (objData.type != null && !objData.type.isEmpty()) {
                // Crear objeto basado en el "type"
                newObject = new SolidObject(objData.x, objData.y, objData.type, objData.walkable);
            } else if (objData.texture != null && !objData.texture.equals(currentLevel.groundTexture)) {
                // Crear objeto con definicion explicita (sistema antiguo)
                newObject = new SolidObject(objData.x, objData.y, objData.width, objData.height, new Texture(objData.texture), objData.walkable);
            }

            if (newObject != null) {
                solidObjects.add(newObject);
            }
        }
    }

    /**
     * Inicializa el personaje del jugador y su controlador.
     */
    private void initializePlayer() {
        // Carga los frames de animacion para el jugador.
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

        // Crea el animador del personaje del jugador con los frames cargados.
        CharacterAnimator playerAnimator = new CharacterAnimator(
            playerIdleFrames, playerRunFrames, playerDeadFrames,
            playerJumpFrames, playerFallFrames, null,
            playerLaserAttackFrames, playerIonAttackFrames, playerRailgunAttackFrames,
            playerHurtFrames, null, playerCrouchFrames, null, null, null, null
        );

        // Crea la instancia del personaje del jugador.
        playerCharacter = new Character(GlobalSettings.PLAYER_HEALTH, playerAnimator, null, 
        currentLevel.playerStartX, currentLevel.playerStartY, true);
        // Establece el hitbox del jugador.
        playerCharacter.setHitbox(currentLevel.playerHitbox);
        
        // Ajusta la posicion Y del jugador para que este sobre el suelo.
        float adjustedPlayerY = findGroundY(playerCharacter.position.x, currentLevel.playerStartY, playerCharacter.getWidth());
        playerCharacter.position.y = adjustedPlayerY;
        playerCharacter.onGround = true;
        playerCharacter.velocity.y = 0;

        // Inicializa el controlador del jugador.
        playerController = new PlayerController(playerCharacter);
        // Establece el jugador en el administrador de audio.
        AudioManager.getInstance().setPlayer(playerCharacter);
    }

    /**
     * Inicializa los enemigos del nivel.
     */
    private void initializeEnemies() {
        enemyManager = new EnemyManager();

        for (LevelData.EnemyData enemyData : currentLevel.enemies) {
            // Obtiene la skin del enemigo segun su tipo.
            EnemySkin skin = EnemySkin.getSkin(enemyData.type);

            // Carga los frames de animacion para el enemigo.
            List<Sprite> enemyIdleFrames;
            if (enemyData.type == EnemyType.FLYING) {
                enemyIdleFrames = loadSpriteFrames(skin.idleFrames, 9);
            } else {
                enemyIdleFrames = loadSpriteFrames(skin.idleFrames, 4);
            }

            List<Sprite> enemyRunFrames = loadSpriteFrames(skin.runFrames, 4);
            List<Sprite> enemyDeadFrames;
            if (enemyData.type == EnemyType.BOSS_GEMINI || enemyData.type == EnemyType.BOSS_CHATGPT) {
                enemyDeadFrames = loadSpriteFrames(skin.deadFrames, 10);
            } else {
                enemyDeadFrames = loadSpriteFrames(skin.deadFrames, 4);
            }

            List<Sprite> enemyJumpFrames = loadSpriteFrames(skin.jumpFrames, 1);
            List<Sprite> enemyFallFrames = loadSpriteFrames(skin.fallFrames, 1);
            List<Sprite> enemyHurtFrames = loadSpriteFrames(skin.hurtFrames, 1);
            List<Sprite> enemyAngryHurtFrames = loadSpriteFrames(skin.angryHurtFrames, 1);
            List<Sprite> enemyAttackFrames = loadSpriteFrames(skin.attackFrames, 2);

            // Carga animaciones especificas para jefes.
            List<Sprite> idleRageFrames = null;
            List<Sprite> attack1Frames = null;
            List<Sprite> attack2Frames = null;
            List<Sprite> summonFrames = null;

            if (enemyData.type == EnemyType.BOSS_GEMINI || enemyData.type == EnemyType.BOSS_CHATGPT) {
                idleRageFrames = loadSpriteFrames(skin.idleRageFrames, 4); 
                attack1Frames = loadSpriteFrames(skin.attack1Frames, 8); 
                attack2Frames = loadSpriteFrames(skin.attack2Frames, 8); 
                summonFrames = loadSpriteFrames(skin.summonFrames, 8); 
            } else if (enemyData.type == EnemyType.SHOOTER) {
                attack1Frames = loadSpriteFrames(skin.attack1Frames, 3);
            }

            // Crea el animador del personaje enemigo.
            CharacterAnimator enemyAnimator = new CharacterAnimator(
                enemyIdleFrames, enemyRunFrames, enemyDeadFrames,
                enemyJumpFrames, enemyFallFrames, enemyAttackFrames,
                null, null, null,
                enemyHurtFrames, enemyAngryHurtFrames, null,
                idleRageFrames, attack1Frames, attack2Frames, summonFrames
            );

            // Establece la salud del enemigo segun su tipo.
            int health = 0;
            switch (enemyData.type) {
                case PATROLLER:
                    health = com.machinehunterdev.game.Gameplay.GlobalSettings.PATROLLER_HEALTH;
                    break;
                case SHOOTER:
                    health = com.machinehunterdev.game.Gameplay.GlobalSettings.SHOOTER_HEALTH;
                    break;
                case FLYING:
                    health = com.machinehunterdev.game.Gameplay.GlobalSettings.FLYING_HEALTH;
                    break;
                case BOSS_GEMINI:
                    health = com.machinehunterdev.game.Gameplay.GlobalSettings.BOSS_GEMINI_HEALTH;
                    break;
                case BOSS_CHATGPT:
                    health = com.machinehunterdev.game.Gameplay.GlobalSettings.BOSS_CHATGPT_HEALTH;
                    break;
                default:
                    health = 50; // Salud por defecto si el tipo no es reconocido.
            }

            // Crea la instancia del personaje enemigo.
            Character enemy = new Character(health, enemyAnimator, null, enemyData.x, enemyData.y, false);
            // Usa el hitbox de los datos del nivel si existe, de lo contrario usa el de la skin.
            LevelData.HitboxData hitboxToUse = enemyData.hitbox != null ? enemyData.hitbox : skin.hitbox;
            enemy.setHitbox(hitboxToUse);
            // Si es un enemigo tipo SHOOTER, le asigna el arma correspondiente.
            if (enemyData.type == EnemyType.SHOOTER) {
                enemy.switchWeapon(com.machinehunterdev.game.DamageTriggers.WeaponType.SHOOTER);
            }

            // Ajusta la posicion Y del enemigo para que este sobre el suelo o en el aire si es volador.
            if (enemyData.type == EnemyType.FLYING) {
                enemy.position.y -= enemy.getHeight() / 2;
            } else {
                float adjustedEnemyY = findGroundY(enemy.position.x, enemyData.y, enemy.getWidth());
                enemy.position.y = adjustedEnemyY;
                enemy.onGround = true;
                enemy.velocity.y = 0;
            }

            // Agrega el enemigo al administrador de enemigos.
            enemyManager.addEnemy(enemyData.type, enemy, enemyData.patrolPoints, enemyData.waitTime, enemyData.shootInterval, enemyData.shootTime, false);

            // Si es un jefe, lo establece en la interfaz de usuario.
            if (enemyData.type == EnemyType.BOSS_GEMINI || enemyData.type == EnemyType.BOSS_CHATGPT) {
                gameplayUI.setBoss((com.machinehunterdev.game.Character.Character) enemy, enemyData.name, enemyData.type);
            }
        }
    }

    /**
     * Inicializa los personajes no jugables (NPCs) del nivel.
     */
    private void initializeNPCs() {
        npcControllers = new ArrayList<>();
        if (currentLevel.npcs.isEmpty()) {
            return;
        }

        for (LevelData.NPCData npcData : currentLevel.npcs) {
            // Carga los frames de animacion para el NPC.
            List<Sprite> npcIdleFrames = loadSpriteFrames(npcData.idleFrames, 4);

            // Crea el animador del personaje NPC.
            CharacterAnimator npcAnimator = new CharacterAnimator(
                npcIdleFrames, null, null,
                null, null, null,
                null, null, null,
                null, null, null, null, null, null, null
            );
            
            // Crea la instancia del personaje NPC.
            Character npcCharacter = new Character(100, npcAnimator, null, npcData.x, npcData.y, false);
            
            // Ajusta la posicion Y del NPC para que este sobre el suelo.
            float adjustedNpcY = findGroundY(npcCharacter.position.x, npcData.y, npcCharacter.getWidth());
            npcCharacter.position.y = adjustedNpcY;
            npcCharacter.onGround = true;
            npcCharacter.velocity.y = 0;
            
            // Carga los dialogos asociados al NPC.
            List<Dialog> npcDialogues = loadNPCCDialogues(npcData.dialogues);
            
            // Agrega el controlador del NPC a la lista.
            npcControllers.add(new NPCController(npcCharacter, npcData.interactionRadius, npcDialogues));
        }
    }

    /**
     * Carga los dialogos de los NPCs desde un archivo JSON.
     * @param dialogueIds Lista de IDs de dialogos a cargar.
     * @return Una lista de objetos Dialog.
     */
    private List<Dialog> loadNPCCDialogues(List<String> dialogueIds) {
        List<Dialog> npcDialogues = new ArrayList<>();
        if (currentLevel.dialogueFile == null || currentLevel.dialogueFile.isEmpty()) {
            return npcDialogues;
        }

        try {
            JsonReader jsonReader = new JsonReader();
            JsonValue base = jsonReader.parse(Gdx.files.internal(currentLevel.dialogueFile));

            for (String dialogueIdStr : dialogueIds) {
                // Itera a traves de todas las secciones de dialogo (ej. "Dialogos_Tutorial", "Dialogos_acto2")
                for (JsonValue dialogueSection : base) {
                    if (dialogueSection.isArray()) { // Asegura que sea un array de dialogos
                        for (JsonValue dialogValue : dialogueSection) {
                            if (dialogValue.has("Name") && dialogValue.getString("Name").equals(dialogueIdStr)) {
                                List<String> lines = new ArrayList<>();
                                JsonValue texto = dialogValue.get("Texto");
                                if (texto != null) {
                                    if (texto.isArray()) {
                                        for (JsonValue line : texto) {
                                            lines.add(line.asString());
                                        }
                                    } else { // Si "Texto" es una sola cadena
                                        lines.add(texto.asString());
                                    }
                                }
                                npcDialogues.add(new Dialog(lines));
                                break; // Se encontro el dialogo, pasa al siguiente dialogueIdStr
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            Gdx.app.error("GameplayState", "Error al cargar dialogos del NPC", e);
        }

        return npcDialogues;
    }

    // Bandera para controlar la transicion a la pantalla de "Game Over".
    private boolean transitioningToGameOver = false;
    // Bandera para controlar la animacion de derrota del jefe.
    private boolean isBossDefeatedAndAnimationFinished = false;
    // Textura y sprite para el frame final del jefe.
    private Texture bossFinalFrameTexture;
    private Sprite bossFinalFrameSprite;

    /**
     * Metodo principal de actualizacion del estado del juego.
     * Se llama en cada frame del juego.
     */
    @Override
    public void execute() {
        // Actualiza el administrador de audio.
        AudioManager.getInstance().update(Gdx.graphics.getDeltaTime());

        // Codigo de depuracion: Mata a todos los enemigos al presionar F9.
        if(Gdx.input.isKeyJustPressed(com.badlogic.gdx.Input.Keys.F9)) {
            killAllEnemies();
        }

        // Maneja la entrada para pausar el juego.
        if (Gdx.input.isKeyJustPressed(GlobalSettings.CONTROL_PAUSE) && !levelCompleted && !isDialogActive) {
            owner.stateMachine.push(new PauseState(this));
            return; // Detiene la ejecucion del resto del frame.
        }

        // Actualiza la logica del juego y dibuja el mundo.
        updateGameLogic();
        drawGameWorld();

        // Si el nivel esta completado, dibuja la interfaz de "Siguiente Nivel".
        if (levelCompleted) {
            nextLevelUI.draw();
        } else {
            // Si hay un dialogo activo, lo renderiza.
            if (isDialogActive) {
                dialogManager.render();
            }
            // Dibuja la interfaz de usuario del juego (HUD).
            if (gameplayUI != null) {
                gameplayUI.draw(playerCharacter.getHealth(), playerCharacter.getCurrentWeapon(), playerCharacter.isInvulnerable());
            }
        }

        // Maneja la transicion a la pantalla de "Game Over" si el jugador esta listo.
        if (playerCharacter.readyForGameOverTransition && !transitioningToGameOver) {
            transitioningToGameOver = true;
            CharacterAnimator animator = playerCharacter.characterAnimator;
            playerCharacter.characterAnimator = null; 
            GameOverState.setPlayerAnimator(animator);
            owner.stateMachine.changeState(GameOverState.instance);
        }

        // if (!isBossPhase2 && (currentLevelFile.equals("Levels/Level 3.json") || currentLevelFile.equals("Levels/Level 5.json"))) {
        //     for (com.machinehunterdev.game.Character.IEnemy enemy : enemyManager.getEnemies()) {
        //         if (enemy instanceof com.machinehunterdev.game.Character.BossEnemy) {
        //             Character boss = enemy.getCharacter();
        //             if (boss.getHealth() <= boss.getMaxHealth() / 2) {
        //                 /* Esto no se usara de momento */
        //                 //AudioManager.getInstance().playMusic("Audio/Soundtrack/BossPhase2.mp3", true, false);
        //                 isBossPhase2 = true;
        //                 break;
        //             }
        //         }
        //     }
        // }
    }

    /**
     * Actualiza la logica principal del juego.
     */
    private void updateGameLogic() {
        if (levelCompleted) return;

        float deltaTime = Gdx.graphics.getDeltaTime();

        // Actualiza las animaciones de todos los personajes.
        playerCharacter.update(deltaTime);
        enemyManager.updateCharacterAnimations(deltaTime);
        if (npcControllers != null) {
            for (NPCController npcController : npcControllers) {
                npcController.character.update(deltaTime);
            }
        }

        // Si hay un dialogo activo, actualiza el administrador de dialogos y maneja la entrada.
        if (isDialogActive) {
            dialogManager.update(deltaTime);
            if (!dialogManager.isDialogActive()) {
                isDialogActive = false;
                playerCharacter.isPaused = false;
                for (com.machinehunterdev.game.Character.IEnemy enemy : enemyManager.getEnemies()) {
                    enemy.getCharacter().isPaused = false;
                }
                if (npcControllers != null) {
                    for (NPCController npcController : npcControllers) {
                        npcController.character.isPaused = false;
                    }
                }
            }
            handleDialogInput();
        } else {
            // El resto de la logica del juego solo se ejecuta si no hay dialogo.
            playerController.update(deltaTime, solidObjects, bullets, playerCharacter, enemyManager.getEnemies().size());
            updateEnemies(deltaTime);
            
            // Se manejan los efectos de aterrizaje.
            landingEffectManager.update(deltaTime);
            handleLandingEffects();

            checkLevelCompletion();
            updateNPC(deltaTime);

            // Maneja la interaccion con NPCs si no se ignora la entrada.
            if (!ignoreInputOnFirstFrame) {
                handleNPCInteraction();
            }

            // Actualiza los sistemas de combate y centra la camara.
            updateCombatSystems(deltaTime);
            playerController.centerCameraOnPlayer(camera);

            // Desactiva la bandera para ignorar la entrada.
            ignoreInputOnFirstFrame = false;
        }
    }

    /**
     * Actualiza la logica de los enemigos.
     * @param deltaTime El tiempo transcurrido desde el ultimo frame.
     */
    private void updateEnemies(float deltaTime) {
        enemyManager.update(deltaTime, solidObjects, bullets, playerCharacter);

        // Maneja la invocacion de nuevos enemigos por parte de los jefes.
        ArrayList<EnemyType> enemiesToSummon = new ArrayList<>();
        for (com.machinehunterdev.game.Character.IEnemy enemy : enemyManager.getEnemies()) {
            if (enemy instanceof com.machinehunterdev.game.Character.BossEnemy) {
                BossEnemyController controller = (BossEnemyController) ((BaseEnemy) enemy).getController();
                EnemyType enemyToSummon = controller.getEnemyToSummon();
                if (enemyToSummon != null) {
                    enemiesToSummon.add(enemyToSummon);
                    controller.clearSummonRequest();
                }
            }
        }

        // Invoca a los enemigos solicitados.
        for (EnemyType type : enemiesToSummon) {
            summonEnemy(type);
        }

        // Elimina a los enemigos muertos que estan listos para ser removidos.
        ArrayList<com.machinehunterdev.game.Character.IEnemy> enemies = enemyManager.getEnemies();
        for (int i = enemies.size() - 1; i >= 0; i--) {
            com.machinehunterdev.game.Character.IEnemy enemy = enemies.get(i);
            if (!enemy.getCharacter().isAlive() && enemy.getCharacter().isReadyForRemoval()) {
                if (enemy.getType() == EnemyType.BOSS_GEMINI || enemy.getType() == EnemyType.BOSS_CHATGPT) {
                    // Guarda el frame final del jefe para mostrarlo despues
                    if (!isBossDefeatedAndAnimationFinished) {
                        isBossDefeatedAndAnimationFinished = true;
                        // Mismo frame para GEMINI y CHATGPT
                        bossFinalFrameTexture = new Texture("Enemy/GeminiEXE/GeminiEXEDeath10.png");
                        bossFinalFrameSprite = new Sprite(bossFinalFrameTexture);

                        // Necesita posicionarlo correctamente antes de deshechar el jefe
                        Character bossCharacter = enemy.getCharacter();
                        // Se ajusta la posicion X para centrar el sprite
                        bossFinalFrameSprite.setPosition(bossCharacter.position.x - 10, bossCharacter.position.y);
                    }
                }
                enemy.getCharacter().dispose();
                enemies.remove(i);
            }
        }
    }

    /**
     * Invoca nuevos enemigos en el juego.
     * @param type El tipo de enemigo a invocar.
     */
    private void summonEnemy(EnemyType type) {
        // Obtiene la skin del enemigo segun su tipo.
        EnemySkin skin = EnemySkin.getSkin(type);

        // Carga los frames de animacion para el enemigo invocado.
        List<Sprite> enemyIdleFrames;
        if (type == EnemyType.FLYING) {
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
        List<Sprite> attack1Frames = null;

        if (type == EnemyType.SHOOTER)
        {
            attack1Frames = loadSpriteFrames(skin.attack1Frames, 3);
        }

        // Crea dos animadores para los dos enemigos invocados.
        CharacterAnimator enemyAnimator1 = new CharacterAnimator(
            enemyIdleFrames, enemyRunFrames, enemyDeadFrames,
            enemyJumpFrames, enemyFallFrames, enemyAttackFrames,
            null, null, null,
            enemyHurtFrames, null, null, null, attack1Frames, null, null
        );

        CharacterAnimator enemyAnimator2 = new CharacterAnimator(
            enemyIdleFrames, enemyRunFrames, enemyDeadFrames,
            enemyJumpFrames, enemyFallFrames, enemyAttackFrames,
            null, null, null,
            enemyHurtFrames, null, null, null, attack1Frames, null, null
        );

        // Establece la salud de los enemigos invocados.
        int health = 1;
        switch (type) {
            case PATROLLER:
                health = com.machinehunterdev.game.Gameplay.GlobalSettings.PATROLLER_HEALTH;
                break;
            case SHOOTER:
                health = com.machinehunterdev.game.Gameplay.GlobalSettings.SHOOTER_HEALTH / 2; // Menos salud para enemigos disparadores invocados
                break;
            case FLYING:
                health = com.machinehunterdev.game.Gameplay.GlobalSettings.FLYING_HEALTH;
                break;
            default:
                health = 1;
                break;
        }

        // Crea las instancias de los personajes enemigos invocados.
        Character enemy1 = new Character(health, enemyAnimator1, null, 88, 480, false);
        Character enemy2 = new Character(health, enemyAnimator2, null, 352, 480, false);

        // Ajusta la posicion Y de los enemigos invocados.
        if (type == EnemyType.FLYING) {
            enemy1.position.y = 200; 
            enemy2.position.y = 200;
        } else {
            float adjustedY1 = findGroundY(enemy1.position.x, 480, enemy1.getWidth());
            enemy1.position.y = adjustedY1;
            enemy1.onGround = true;
            enemy1.velocity.y = 0;

            float adjustedY2 = findGroundY(enemy2.position.x, 480, enemy2.getWidth());
            enemy2.position.y = adjustedY2;
            enemy2.onGround = true;
            enemy2.velocity.y = 0;
        }

        // Asigna armas si son SHOOTER.
        if (type == EnemyType.SHOOTER) {
            enemy1.switchWeapon(com.machinehunterdev.game.DamageTriggers.WeaponType.SHOOTER);
            enemy2.switchWeapon(com.machinehunterdev.game.DamageTriggers.WeaponType.SHOOTER);
        }

        // Define puntos de patrulla y tiempos para los enemigos invocados.
        ArrayList<LevelData.Point> patrolPoints1 = new ArrayList<>();
        ArrayList<LevelData.Point> patrolPoints2 = new ArrayList<>();
        float waitTime = 0, shootInterval = 0, shootTime = 0;

        switch (type) {
            case PATROLLER:
                LevelData.Point p1 = new LevelData.Point();
                p1.x = 352; p1.y = 32; p1.action = "Jump"; patrolPoints1.add(p1);
                LevelData.Point p2 = new LevelData.Point();
                p2.x = 88; p2.y = 32; p2.action = "Jump"; patrolPoints1.add(p2);
                LevelData.Point p3 = new LevelData.Point();
                p3.x = 88; p3.y = 32; p3.action = "Jump"; patrolPoints2.add(p3);
                LevelData.Point p4 = new LevelData.Point();
                p4.x = 352; p4.y = 32; p4.action = "Jump"; patrolPoints2.add(p4);
                waitTime = 1.0f;
                break;
            case SHOOTER:
                shootInterval = 2.0f;
                shootTime = 1f;
                break;
            case FLYING:
                LevelData.Point p5 = new LevelData.Point();
                p5.x = 138; p5.y = 168; patrolPoints1.add(p5);
                LevelData.Point p6 = new LevelData.Point();
                p6.x = 138; p6.y = 96; patrolPoints1.add(p6);
                LevelData.Point p7 = new LevelData.Point();
                p7.x = 302; p7.y = 96; patrolPoints2.add(p7);
                LevelData.Point p8 = new LevelData.Point();
                p8.x = 302; p8.y = 168; patrolPoints2.add(p8);
                waitTime = 0.5f;
                break;
            default:
                break;
        }

        // Agrega los enemigos invocados al administrador de enemigos.
        enemyManager.addEnemy(type, enemy1, patrolPoints1, waitTime, shootInterval, shootTime, true);
        enemyManager.addEnemy(type, enemy2, patrolPoints2, waitTime, shootInterval, shootTime, true);
    }

    /**
     * Verifica si el nivel ha sido completado (todos los enemigos han sido derrotados).
     */
    private void checkLevelCompletion() {
        if (enemyManager.getEnemies().isEmpty()) {
            // Reproduce la musica de nivel completado.
            AudioManager.getInstance().playMusic("Audio/Soundtrack/LevelCompleted.mp3", false, false);
            levelCompleted = true;
            // Establece el procesador de entrada para la interfaz de "Siguiente Nivel".
            Gdx.input.setInputProcessor(nextLevelUI);
        }
    }

    /**
     * Actualiza la logica de los NPCs.
     * @param deltaTime El tiempo transcurrido desde el ultimo frame.
     */
    private void updateNPC(float deltaTime) {
        if (npcControllers != null) {
            for (NPCController npcController : npcControllers) {
                npcController.update(deltaTime, solidObjects, bullets, playerCharacter, enemyManager.getEnemies().size());
            }
        }
    }

    /**
     * Maneja la interaccion del jugador con los NPCs.
     */
    private void handleNPCInteraction() {
        // Si se presiona la tecla de interaccion y no hay un dialogo activo.
        if (Gdx.input.isKeyJustPressed(GlobalSettings.CONTROL_INTERACT) && !isDialogActive) {
            if (npcControllers != null) {
                for (NPCController npcController : npcControllers) {
                    // Si el jugador esta en el rango de interaccion del NPC y en el suelo.
                    if (npcController.isInRange() && playerCharacter.onGround) {
                        // Copia la lista de dialogos del NPC.
                        activeNPCDialogues = new ArrayList<>(npcController.getDialogues()); 
                        if (!activeNPCDialogues.isEmpty()) {
                            // Muestra el primer dialogo y lo remueve de la lista.
                            dialogManager.showDialog(activeNPCDialogues.remove(0), false); 
                            isDialogActive = true;

                            // Pausa al jugador, enemigos y otros NPCs durante el dialogo.
                            playerCharacter.isPaused = true;
                            for (com.machinehunterdev.game.Character.IEnemy enemy : enemyManager.getEnemies()) {
                                enemy.getCharacter().isPaused = true;
                            }
                            if (npcControllers != null) {
                                for (NPCController npc : npcControllers) {
                                    npc.character.isPaused = true;
                                }
                            }
                            break; // Solo interactua con un NPC a la vez.
                        }
                    }
                }
            }
        }
    }

    /**
     * Actualiza los sistemas de combate del juego.
     * @param deltaTime El tiempo transcurrido desde el ultimo frame.
     */
    private void updateCombatSystems(float deltaTime) {
        updateBullets(deltaTime);
        impactEffectManager.update(deltaTime);
        checkPlayerEnemyCollision();
        checkBulletEnemyCollision();
        checkBulletPlayerCollision();
        checkLightningCollisions();
    }

    /**
     * Dibuja todos los elementos del mundo del juego.
     */
    public void drawGameWorld() {
        // Establece la matriz de proyeccion de la camara.
        gameBatch.setProjectionMatrix(camera.combined);
        gameBatch.begin();

        // Dibuja el fondo del nivel, repitiendolo si es necesario.
        gameBatch.setColor(1, 1, 1, 0.75f);
        int backgroundWidth = GlobalSettings.VIRTUAL_WIDTH;
        int mapWidth = GlobalSettings.levelWidth;
        int backgroundCount = (int) Math.ceil((float) mapWidth / backgroundWidth) + 1;
        for (int i = 0; i < backgroundCount; i++) {
            gameBatch.draw(backgroundTexture, i * backgroundWidth, 0);
        }
        gameBatch.setColor(1, 1, 1, 1);

        // Dibuja el suelo.
        drawGround();

        // Dibuja los objetos solidos.
        for (SolidObject obj : solidObjects) {
            obj.render(gameBatch);
        }

        // Dibuja los enemigos y al jugador.
        enemyManager.draw(gameBatch);
        playerCharacter.draw(gameBatch);

        // Dibuja los NPCs y el prompt de interaccion si estan en rango.
        if (npcControllers != null) {
            for (NPCController npcController : npcControllers) {
                npcController.render(gameBatch);
                if (npcController.isInRange()) {
                    drawNPCInteractionPrompt(npcController);
                }
            }
        }

        // Dibuja las balas y los efectos de impacto y aterrizaje.
        drawBullets();
        impactEffectManager.draw(gameBatch);
        landingEffectManager.draw(gameBatch);

        // Dibuja las advertencias y animaciones de ataques de jefes.
        for (com.machinehunterdev.game.Character.IEnemy enemy : enemyManager.getEnemies()) {
            if (enemy instanceof com.machinehunterdev.game.Character.BossEnemy) {
                BossEnemyController controller = (BossEnemyController) ((BaseEnemy) enemy).getController();
                if (controller.isLightningAttackActive()) {
                    if (controller.isLightningWarning()) {
                        float x = controller.getLightningPlayerX();
                        gameBatch.draw(thunderWarningTexture, x, 32, 40, 448);
                        wasBossStriking = false; // Resetea para el proximo golpe
                    } else if (controller.isLightningStriking()) {
                        if (!wasBossStriking) {
                            thunderAttackAnimator.start();
                            wasBossStriking = true;
                        }
                        thunderAttackAnimator.handleUpdate(Gdx.graphics.getDeltaTime());
                        Sprite frame = thunderAttackAnimator.getCurrentSprite();
                        if (frame != null) {
                            float x = controller.getLightningPlayerX();
                            frame.setPosition(x, 32);
                            frame.setSize(40, 448);
                            frame.draw(gameBatch);
                        }
                    }
                }

                if (controller.isSummonWarning()) {
                    gameBatch.draw(summonWarningTexture, 88, 32, 40, 448);
                    gameBatch.draw(summonWarningTexture, 352, 32, 40, 448);
                }
            }
        }

        // Dibuja el frame final del jefe si ha sido derrotado.
        if (isBossDefeatedAndAnimationFinished && bossFinalFrameSprite != null) {
            bossFinalFrameSprite.draw(gameBatch);
        }

        gameBatch.end();

        // Renderiza hitboxes para depuracion (comentado).
        //debugRenderHitboxes();
    }

    /**
     * Dibuja el prompt de interaccion para los NPCs.
     * @param npcController El controlador del NPC.
     */
    private void drawNPCInteractionPrompt(NPCController npcController) {
        GlyphLayout layout = new GlyphLayout();
        String message = "E para interactuar";
        interactionFont.getData().setScale(0.5f);
        layout.setText(interactionFont, message);

        float boxWidth = layout.width + 20;
        float boxHeight = layout.height + 10;
        float boxX = npcController.character.position.x + (npcController.character.getWidth() / 2) - (boxWidth / 2);
        float boxY = npcController.character.position.y + npcController.character.getHeight() + 10;

        // Dibuja el fondo del cuadro de texto.
        gameBatch.draw(blackTexture, boxX, boxY, boxWidth, boxHeight);

        // Dibuja el texto de interaccion.
        float textX = boxX + 10;
        float textY = boxY + layout.height + 5;
        interactionFont.draw(gameBatch, layout, textX, textY);
        interactionFont.getData().setScale(1.0f);
    }

    /**
     * Dibuja el suelo del nivel.
     */
    private void drawGround() {
        int groundWidth = 320; // Ancho del sprite del suelo.
        int groundCount = (int) Math.ceil((float) GlobalSettings.levelWidth / groundWidth);
        for (int i = 0; i < groundCount; i++) {
            gameBatch.draw(groundTexture, i * groundWidth, 0);
        }
    }

    /**
     * Maneja la entrada del usuario durante un dialogo activo.
     */
    private void handleDialogInput() {
        if (Gdx.input.isKeyJustPressed(GlobalSettings.CONTROL_INTERACT)) {
            if (dialogManager.isDialogActive()) {
                dialogManager.nextLine(); // Avanza a la siguiente linea del dialogo.
                if (!dialogManager.isDialogActive()) { // Si el dialogo actual ha terminado.
                    if (activeNPCDialogues != null && !activeNPCDialogues.isEmpty()) {
                        // Muestra el siguiente dialogo si hay mas.
                        dialogManager.showDialog(activeNPCDialogues.remove(0), false);
                    } else {
                        // Todos los dialogos han sido mostrados, desactiva el modo dialogo.
                        isDialogActive = false;
                        playerCharacter.isPaused = false;
                        for (com.machinehunterdev.game.Character.IEnemy enemy : enemyManager.getEnemies()) {
                            enemy.getCharacter().isPaused = false;
                        }
                        if (npcControllers != null) {
                            for (NPCController npc : npcControllers) {
                                npc.character.isPaused = false;
                            }
                        }
                    }
                }
            }
        }
    }

    /**
     * Verifica colisiones entre el jugador y los enemigos.
     */
    private void checkPlayerEnemyCollision() {
        for (com.machinehunterdev.game.Character.IEnemy enemy : enemyManager.getEnemies()) {
            Character enemyCharacter = enemy.getCharacter();
            if (enemyCharacter.isAlive()) {
                Rectangle playerBounds = playerCharacter.getBounds();
                Rectangle enemyBounds = enemyCharacter.getBounds();

                if (playerBounds.overlaps(enemyBounds)) {
                    if (DamageSystem.canTakeDamage(playerCharacter)) {
                        // Reproduce sonido de dano al jugador.
                        AudioManager.getInstance().playSfx(AudioId.PlayerHurt, playerCharacter);
                        // Aplica dano al jugador por contacto.
                        DamageSystem.applyContactDamage(playerCharacter, enemyCharacter, 1);

                        /* Animacion de impacto al contacto */
                        // Calcula la interseccion de las cajas de colision.
                        Rectangle intersection = new Rectangle(playerBounds);
                        intersection.set(intersection.x, intersection.y, intersection.width, intersection.height);
                        intersection.x = Math.max(playerBounds.x, enemyBounds.x);
                        intersection.width = Math.min(playerBounds.x + playerBounds.width, enemyBounds.x + enemyBounds.width) - intersection.x;
                        intersection.y = Math.max(playerBounds.y, enemyBounds.y);
                        intersection.height = Math.min(playerBounds.y + playerBounds.height, enemyBounds.y + enemyBounds.height) - intersection.y;

                        // Calcula el punto central de la interseccion.
                        float impactX = intersection.x + intersection.width / 2;
                        float impactY = intersection.y + intersection.height / 2;

                        // Crea el efecto de impacto en el punto de colision segun el tipo de enemigo.
                        switch (enemy.getType()) {
                            case PATROLLER:
                                impactEffectManager.createImpact(impactX, impactY, WeaponType.PATROLLER);
                                break;
                            case SHOOTER:
                                impactEffectManager.createImpact(impactX, impactY, WeaponType.SHOOTER);
                                break;
                            case FLYING:
                                impactEffectManager.createImpact(impactX, impactY, WeaponType.FLYING);
                                break;
                            case BOSS_GEMINI:
                            case BOSS_CHATGPT:
                                impactEffectManager.createImpact(impactX, impactY, WeaponType.SHOOTER);
                                break;
                            default:
                        }
                    }
                    break; // Solo un enemigo puede danar al jugador por contacto a la vez.
                }
            }
        }
    }

    /**
     * Actualiza la posicion y el estado de las balas.
     * @param deltaTime El tiempo transcurrido desde el ultimo frame.
     */
    private void updateBullets(float deltaTime) {
        for (int i = bullets.size() - 1; i >= 0; i--) {
            Bullet bullet = bullets.get(i);
            
            /* DESTRUIR BALA SI */
            // 1. Ha salido de la pantalla
            // 2. Ha chocado con el suelo
            if (bullet.update(deltaTime) 
            || bullet.position.x < camera.position.x - GlobalSettings.VIRTUAL_WIDTH / 2 - 100 
            || bullet.position.x > camera.position.x + GlobalSettings.VIRTUAL_WIDTH / 2 + 100
            || bullet.position.y < GlobalSettings.GROUND_LEVEL) {
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

    private void checkBulletEnemyCollision() {
        List<Character> enemiesHitThisFrame = new ArrayList<>();
        for (int i = bullets.size() - 1; i >= 0; i--) {
            Bullet bullet = bullets.get(i);
            if (bullet.getOwner() == playerCharacter) {
                for (com.machinehunterdev.game.Character.IEnemy enemy : enemyManager.getEnemies()) {
                    Character enemyCharacter = enemy.getCharacter();
                    if (enemyCharacter.isAlive() && bullet.getBounds().overlaps(enemyCharacter.getBounds())) {
                        if (bullet.isPiercing()) {
                            if (!bullet.hasHit(enemyCharacter)) {
                                enemyCharacter.takeDamageWithoutVulnerability(bullet.getDamage());
                                if (enemyCharacter.isAlive()) {
                                    if (!enemiesHitThisFrame.contains(enemyCharacter)) {
                                        AudioManager.getInstance().playSfx(AudioId.EnemyHurt, enemyCharacter, GlobalSettings.ANNOYING_VOLUME);
                                        enemiesHitThisFrame.add(enemyCharacter);
                                    }
                                } else {
                                    handleEnemyDeath(enemy, enemyCharacter);
                                }
                                bullet.addHitEnemy(enemyCharacter);
                                impactEffectManager.createImpact(bullet.position.x, bullet.position.y, bullet.getWeaponType());
                            }
                        } else {
                            enemyCharacter.takeDamageWithoutVulnerability(bullet.getDamage());
                            if (enemyCharacter.isAlive()) {
                                if (!enemiesHitThisFrame.contains(enemyCharacter)) {
                                    AudioManager.getInstance().playSfx(AudioId.EnemyHurt, enemyCharacter, GlobalSettings.ANNOYING_VOLUME);
                                    enemiesHitThisFrame.add(enemyCharacter);
                                }
                            } else {
                                handleEnemyDeath(enemy, enemyCharacter);
                            }
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

    private void handleEnemyDeath(com.machinehunterdev.game.Character.IEnemy enemy, Character enemyCharacter) {
        if (enemy.getType() == EnemyType.BOSS_GEMINI || enemy.getType() == EnemyType.BOSS_CHATGPT) {
            enemyCharacter.isPerformingSpecialAttack = false;
            AudioManager.getInstance().playSfx(AudioId.BossDeath, enemyCharacter, GlobalSettings.ANNOYING_VOLUME * 6f);

            // Eliminar a todos los enemigos restantes al morir el jefe
            for (com.machinehunterdev.game.Character.IEnemy remainingEnemy : enemyManager.getEnemies()) {
                // Verifica que el enemigo esté vivo y no sea el jefe que acaba de morir
                if (remainingEnemy.getCharacter().isAlive() && remainingEnemy != enemy) {
                    remainingEnemy.getCharacter().takeDamageWithoutVulnerability(remainingEnemy.getCharacter().getHealth());
                    AudioManager.getInstance().playSfx(AudioId.Explosion, remainingEnemy.getCharacter());
                }
            }
        } else {
            AudioManager.getInstance().playSfx(AudioId.Explosion, enemyCharacter);
        }
    }

    private void checkBulletPlayerCollision() {
        for (int i = bullets.size() - 1; i >= 0; i--) {
            Bullet bullet = bullets.get(i);
            if (bullet.getOwner() != playerCharacter) {
                if (DamageSystem.canTakeDamage(playerCharacter) && playerCharacter.isAlive() && bullet.getBounds().overlaps(playerCharacter.getBounds())) {
                    AudioManager.getInstance().playSfx(AudioId.PlayerHurt, playerCharacter);
                    DamageSystem.applyContactDamage(playerCharacter, bullet.getOwner(), bullet.getDamage());
                    impactEffectManager.createImpact(bullet.position.x, bullet.position.y, bullet.getWeaponType());
                    bullets.remove(i);
                    bullet.dispose();
                }
            }
        }
    }

    /* PARA DEPURACION, DESTRUIR TODOS LOS ENEMIGOS */
    private void killAllEnemies() {
        for (com.machinehunterdev.game.Character.IEnemy enemy : enemyManager.getEnemies()) {
            enemy.getCharacter().takeDamageWithoutVulnerability(enemy.getCharacter().getHealth());
        }
    }

    private void checkLightningCollisions() {
        for (com.machinehunterdev.game.Character.IEnemy enemy : enemyManager.getEnemies()) {
            if (enemy instanceof com.machinehunterdev.game.Character.BossEnemy) {
                BossEnemyController controller = (BossEnemyController) ((BaseEnemy) enemy).getController();
                if (controller.isLightningStriking()) {
                    // + 10 para centrar la hitbox
                    Rectangle lightningBounds = new Rectangle(controller.getLightningPlayerX() + 10, 32, 20, 448);
                    if (DamageSystem.canTakeDamage(playerCharacter) && playerCharacter.isAlive() && lightningBounds.overlaps(playerCharacter.getBounds())) {
                        AudioManager.getInstance().playSfx(AudioId.PlayerHurt, playerCharacter);
                        DamageSystem.applyContactDamage(playerCharacter, enemy.getCharacter(), 1);
                        impactEffectManager.createImpact(playerCharacter.position.x + playerCharacter.getWidth() / 2, playerCharacter.position.y + playerCharacter.getHeight() / 2, WeaponType.PATROLLER);
                    }
                }
            }
        }
    }

    /**
     * Carga una secuencia de imagenes (frames) desde archivos para crear una animacion.
     * Este metodo asume que los frames estan nombrados secuencialmente (ej. "PlayerIdle1.png", "PlayerIdle2.png").
     * Es una utilidad clave para la inicializacion de personajes y efectos visuales.
     *
     * @param basePath La ruta base y el prefijo del nombre de los archivos de imagen (ej. "Player/PlayerIdle").
     * @param frameCount El numero total de frames a cargar en la secuencia.
     * @return Una lista de objetos {@link Sprite}, donde cada sprite es un frame de la animacion.
     *         Retorna `null` si la ruta base es nula, para evitar errores.
     */
    private List<Sprite> loadSpriteFrames(String basePath, int frameCount) { // Declaracion del metodo privado que devuelve una lista de Sprites.
        if (basePath == null) { // Comprueba si la ruta base proporcionada es nula.
            return null; // Si es nula, retorna null para prevenir NullPointerException.
        }
        List<Sprite> frames = new ArrayList<>(); // Crea una nueva lista para almacenar los sprites cargados.
        for (int i = 1; i <= frameCount; i++) { // Bucle que itera desde 1 hasta el numero total de frames.
            // Concatena la ruta base, el numero de frame y la extension ".png" para formar la ruta completa del archivo.
            // Crea una nueva Textura a partir del archivo y luego un nuevo Sprite a partir de esa textura.
            frames.add(new Sprite(new Texture(basePath + i + ".png"))); // Anade el nuevo sprite a la lista de frames.
        }
        return frames; // Devuelve la lista completa de frames de la animacion.
    }

    /**
     * Se llama automaticamente cuando la ventana del juego cambia de tamano.
     * Este metodo propaga el evento de redimensionamiento a los componentes de la
     * interfaz de usuario (UI) que necesitan ajustar su layout al nuevo tamano.
     *
     * @param width El nuevo ancho de la ventana en pixeles.
     * @param height La nueva altura de la ventana en pixeles.
     */
    public void resize(int width, int height) { // Declaracion del metodo publico que se activa al redimensionar la ventana.
        if (dialogManager != null) { // Comprueba si el administrador de dialogos ha sido inicializado.
            dialogManager.resize(width, height); // Llama al metodo resize del administrador de dialogos para que ajuste su viewport.
        }
        if (gameplayUI != null) { // Comprueba si la interfaz de usuario del juego (HUD) ha sido inicializada.
            gameplayUI.resize(width, height); // Llama al metodo resize de la UI para que ajuste su viewport.
        }
    }

    /**
     * Se llama cuando el estado del juego esta a punto de ser destruido o cambiado.
     * Este metodo es crucial para la gestion de memoria, ya que se encarga de liberar
     * todos los recursos (texturas, fuentes, etc.) que fueron cargados para este nivel.
     * Previene fugas de memoria al asegurar que los recursos no persistan despues de
     * que el estado ya no este en uso.
     */
    @Override // Indica que este metodo sobrescribe un metodo de la interfaz IState.
    public void exit() { // Metodo para liberar todos los recursos del estado.
        // Libera las texturas principales del nivel.
        disposeTexture(backgroundTexture); // Llama al metodo de utilidad para liberar la textura de fondo.
        disposeTexture(blackTexture); // Libera la textura negra usada para superposiciones.
        disposeTexture(groundTexture); // Libera la textura del suelo.
        
        // Itera sobre todos los objetos solidos y libera sus recursos.
        for (SolidObject obj : solidObjects) {
            obj.dispose(); // Cada objeto solido es responsable de liberar sus propias texturas.
        }

        // Libera la fuente de texto.
        if (interactionFont != null) {
            interactionFont.dispose();
        }
        
        // Libera los recursos de los sistemas de UI y efectos.
        if (dialogManager != null) dialogManager.dispose();
        if (gameplayUI != null) gameplayUI.dispose();
        if (nextLevelUI != null) nextLevelUI.dispose();
        if (impactEffectManager != null) impactEffectManager.dispose();
        if (landingEffectManager != null) landingEffectManager.dispose();
        if (shapeRenderer != null) shapeRenderer.dispose();

        // Libera las texturas de advertencia y animaciones de ataques.
        disposeTexture(thunderWarningTexture);
        disposeTexture(summonWarningTexture);
        disposeTexture(bossFinalFrameTexture);
        if (thunderAttackAnimator != null) {
            thunderAttackAnimator.dispose();
        }
        
        // Libera los recursos de todas las balas activas.
        for (Bullet bullet : bullets) {
            bullet.dispose();
        }
        
        // Libera los recursos de todos los personajes.
        if (playerCharacter != null) playerCharacter.dispose(); // Libera los recursos del jugador.
        if (enemyManager != null) {
            for (com.machinehunterdev.game.Character.IEnemy enemy : enemyManager.getEnemies()) {
                enemy.getCharacter().dispose(); // Libera los recursos de cada enemigo.
            }
        }
        if (npcControllers != null) {
            for (NPCController npcController : npcControllers) {
                if (npcController != null && npcController.character != null) {
                    npcController.character.dispose(); // Libera los recursos de cada NPC.
                }
            }
        }
    }

    /**
     * Metodo de utilidad para liberar de forma segura la memoria de una textura.
     * Comprueba si la textura no es nula antes de intentar llamar a su metodo `dispose()`,
     * lo que previene errores de tipo `NullPointerException`.
     *
     * @param texture La textura que se desea liberar.
     */
    private void disposeTexture(Texture texture) { // Declaracion del metodo privado que recibe un objeto Texture.
        if (texture != null) { // Comprueba si el objeto de textura no es nulo.
            texture.dispose(); // Si no es nulo, libera la memoria asociada a la textura.
        }
    }

    /**
     * Calcula la coordenada Y del suelo mas proximo debajo de una posicion inicial.
     * Este metodo es esencial para colocar correctamente a los personajes sobre plataformas
     * o sobre el suelo principal del nivel, asegurando que no caigan a traves de ellos.
     *
     * @param x La coordenada X del personaje.
     * @param initialY La coordenada Y inicial del personaje (generalmente su posicion actual o de aparicion).
     * @param characterWidth El ancho del personaje, para detectar colisiones con el ancho de las plataformas.
     * @return La coordenada Y de la superficie mas alta sobre la que el personaje puede estar.
     */
    private float findGroundY(float x, float initialY, float characterWidth) { // Declaracion del metodo privado que calcula la Y del suelo.
        // Inicializa la Y del suelo mas cercano con el nivel del suelo global por defecto.
        float closestGroundY = GlobalSettings.GROUND_LEVEL;

        // Itera sobre todos los objetos solidos del nivel.
        for (SolidObject obj : solidObjects) {
            if (obj.isWalkable()) { // Comprueba si el objeto es una superficie sobre la que se puede caminar.
                Rectangle platform = obj.getBounds(); // Obtiene el rectangulo de colision de la plataforma.
                // Comprueba si el personaje esta horizontalmente sobre la plataforma.
                if (x < platform.x + platform.width && x + characterWidth > platform.x) {
                    float platformTop = platform.y + platform.height; // Calcula la parte superior de la plataforma.
                    // Comprueba si la parte superior de la plataforma esta por debajo de la posicion inicial del personaje
                    // y si es mas alta que la ultima superficie encontrada.
                    if (platformTop <= initialY && platformTop > closestGroundY) {
                        closestGroundY = platformTop; // Actualiza la Y del suelo mas cercano.
                    }
                }
            }
        }
        return closestGroundY; // Devuelve la coordenada Y del suelo mas alto encontrado.
    }

    private void handleLandingEffects() {
        if (playerCharacter.justLanded) {
            landingEffectManager.createEffect(playerCharacter.position.x + (playerCharacter.getWidth() / 2), playerCharacter.position.y, EffectType.SMOKE);
            playerCharacter.justLanded = false;
        }

        if (enemyManager != null) {
            for (com.machinehunterdev.game.Character.IEnemy enemy : enemyManager.getEnemies()) {
                if (enemy.getCharacter().justLanded) {
                    landingEffectManager.createEffect(enemy.getCharacter().position.x + (enemy.getCharacter().getWidth() / 2), enemy.getCharacter().position.y, EffectType.SPARK);
                    enemy.getCharacter().justLanded = false;
                }
            }
        }
    }

    /**
     * Dibuja los contornos de las cajas de colision (hitboxes) para depuracion.
     * Este metodo es extremadamente util durante el desarrollo para visualizar
     * las areas de colision de los personajes y ajustar su tamano y posicion.
     * No se llama en la version final del juego.
     */
    private void debugRenderHitboxes() { // Declaracion del metodo privado para renderizar hitboxes.
        shapeRenderer.setProjectionMatrix(camera.combined); // Asegura que los dibujos se alineen con la camara del juego.
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line); // Inicia el renderizado de formas en modo de lineas.
        shapeRenderer.setColor(Color.RED); // Establece el color de las lineas a rojo para alta visibilidad.

        // Dibuja el hitbox del jugador.
        if (playerCharacter != null) { // Comprueba que el jugador exista.
            Rectangle playerBounds = playerCharacter.getBounds(); // Obtiene el rectangulo de colision del jugador.
            shapeRenderer.rect(playerBounds.x, playerBounds.y, playerBounds.width, playerBounds.height); // Dibuja el rectangulo.
        }

        // Dibuja los hitboxes de los enemigos.
        if (enemyManager != null) { // Comprueba que el administrador de enemigos exista.
            for (com.machinehunterdev.game.Character.IEnemy enemy : enemyManager.getEnemies()) { // Itera sobre todos los enemigos.
                if (enemy != null && enemy.getCharacter() != null) { // Comprueba que el enemigo y su personaje no sean nulos.
                    Rectangle enemyBounds = enemy.getCharacter().getBounds(); // Obtiene el rectangulo de colision del enemigo.
                    shapeRenderer.rect(enemyBounds.x, enemyBounds.y, enemyBounds.width, enemyBounds.height); // Dibuja el rectangulo.
                }
            }
        }

        shapeRenderer.end(); // Finaliza el renderizado de formas.
    }

    /**
     * Recupera la ruta del archivo JSON que define el nivel actual.
     * Este metodo es crucial para la gestion de niveles, permitiendo al juego
     * identificar y, si es necesario, recargar el nivel en curso.
     * @return Una cadena que representa la ruta relativa al archivo del nivel actual.
     */
    public String getCurrentLevelFile() { // Declaracion del metodo publico que devuelve un String.
        return currentLevelFile; // Devuelve el valor de la variable de instancia 'currentLevelFile'.
    }

    /**
     * Proporciona acceso a todos los datos estructurados del nivel actual.
     * Esto incluye informacion como la disposicion de objetos solidos,
     * la posicion inicial del jugador, los enemigos, NPCs, y texturas de fondo.
     * @return Un objeto {@link LevelData} que encapsula todos los detalles del nivel cargado.
     */
    public LevelData getCurrentLevel() { // Declaracion del metodo publico que devuelve un objeto LevelData.
        return currentLevel; // Devuelve el objeto LevelData que representa el nivel actual.
    }

    /**
     * Obtiene la instancia del controlador principal del juego (GameController).
     * Este metodo es fundamental para que el estado del juego pueda interactuar
     * con el sistema de maquina de estados y otros componentes globales del juego.
     * @return La instancia de {@link GameController} que posee este estado.
     */
    public GameController getOwner() { // Declaracion del metodo publico que devuelve un objeto GameController.
        return owner; // Devuelve la referencia al GameController que creo y gestiona este estado.
    }

    @Override // Indica que este metodo sobrescribe un metodo de la interfaz IState.
    public void resume() { // Metodo llamado cuando el estado del juego se reanuda.
        // Se llama cuando el estado se reanuda (por ejemplo, despues de salir del menu de pausa).
        // Restablece la bandera para ignorar la entrada en el primer frame.
        // Esto previene que una pulsacion de tecla (como la de pausa) que causo la pausa
        // sea procesada inmediatamente al reanudar el juego, evitando acciones no deseadas.
        this.ignoreInputOnFirstFrame = true; // Establece la bandera a true para ignorar la entrada en el siguiente frame.
    }
}