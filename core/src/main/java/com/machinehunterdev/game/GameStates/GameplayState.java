package com.machinehunterdev.game.GameStates;

import java.util.ArrayList;
import java.util.Arrays;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.machinehunterdev.game.Character.Character;
import com.machinehunterdev.game.Character.EnemyController;
import com.machinehunterdev.game.Character.PlayerController;
import com.machinehunterdev.game.Environment.SolidObject;
import com.machinehunterdev.game.GameController;
import com.machinehunterdev.game.Gameplay.GlobalSettings;
import com.machinehunterdev.game.Dialog.Dialogue;
import com.machinehunterdev.game.Dialog.DialogManager;
import com.machinehunterdev.game.Util.State;

public class GameplayState implements State<GameController>
{
    // SUELO SÓLIDO
    private ArrayList<SolidObject> solidObjects;
    // ** ATRIBUTOS DEL JUGADOR **
    private Texture playerTexture;
    private Character playerCharacter;
    private PlayerController playerController;

    // -- ENEMIGO DE PRUEBA --
    private Texture enemyTexture;
    private Character enemyCharacter;
    private EnemyController enemyController;

    // - - SPRITEBATCH DEL GAMECONTROLLER - -
    private SpriteBatch gameBatch;

    // CAMARA
    private OrthographicCamera camera;

    // Instancia singleton del GameplayState
    public static GameplayState instance = new GameplayState();

    // Constructor privado para evitar instanciación externa
    private GameplayState() 
    {
        instance = this;
    }

    // Poseedor es el GameController
    private GameController owner;

    // ✅ Atributos para diálogo
    private DialogManager dialogManager;
    private boolean isDialogActive = false;

    @Override
    public void enter(GameController owner) 
    {
        this.owner = owner;
        this.gameBatch = owner.batch;
        this.camera = owner.camera;

        // ✅ Inicializar DialogManager
        dialogManager = new DialogManager();

        // - - INICIALIZAR SUELO SOLIDO - -
        solidObjects = new ArrayList<>();
        for(int i = 0; i < 3; i++)
        {
            solidObjects.add(new SolidObject(i * 480, 0, 480, 32, "suelo.png", true));
            solidObjects.add(new SolidObject(64 + i * 480, 64, 200, 16, "suelo.png", true));
        }

        // --- INICIALIZAR JUGADOR Y CONTROLADOR ---
        playerTexture = new Texture("roberto.png"); 
        playerCharacter = new Character(100, playerTexture, 50, 100); 
        playerController = new PlayerController(playerCharacter);

        // --- INICIALIZAR ENEMIGO DE PRUEBA Y CONTROLADOR ---
        enemyTexture = new Texture("enemy.png"); 
        enemyCharacter = new Character(50, enemyTexture, 300, 100); 
        enemyController = new EnemyController(enemyCharacter);
    }

    @Override
    public void execute() 
    {
        if (isDialogActive) {
        dialogManager.update(Gdx.graphics.getDeltaTime());
        handleDialogInput();
        // ❌ No dibujes el juego aquí
        dialogManager.render();
        return;
    }

        // --- LÓGICA DEL JUEGO ---

        if (Gdx.input.isKeyJustPressed(com.badlogic.gdx.Input.Keys.T)) {
        Gdx.app.log("GameplayState", "Tecla T presionada");
        Dialogue dialog = new Dialogue(Arrays.asList("¡Hola!", "Este es un diálogo.", "Presiona para continuar."));
        dialogManager.showDialog(dialog);
        isDialogActive = true;
    }

        if (Gdx.input.isKeyJustPressed(com.badlogic.gdx.Input.Keys.Q)) {
            owner.stateMachine.changeState(MainMenuState.instance);
        }

        // 1. Actualización del controlador y personaje
        playerController.update(Gdx.graphics.getDeltaTime(), solidObjects);
        enemyController.update(Gdx.graphics.getDeltaTime(), solidObjects);
        playerController.centerCameraOnPlayer(camera);

        // --- DIBUJAR ---
        gameBatch.setProjectionMatrix(camera.combined);
        
        gameBatch.begin();

        // Dibuja múltiples fondos para crear un efecto de desplazamiento
        for(int i = 0; i < 3; i++)
        {
            gameBatch.draw(new Texture("FondoJuego.png"), i * GlobalSettings.VIRTUAL_WIDTH, 0);
        }

        // Dibuja los suelos sólidos
        for(SolidObject floor : solidObjects) {
            floor.render(gameBatch);
        }

        // Dibuja el personaje del jugador
        Texture texture = playerCharacter.getTexture();
        float x = playerCharacter.getX();
        float y = playerCharacter.getY();
        float w = texture.getWidth();
        float h = texture.getHeight();

        if (playerCharacter.isSeeingRight()) {
            gameBatch.draw(texture, x, y);
        } else {
            gameBatch.draw(texture, x + w, y, -w, h);
        }

        // Dibuja el personaje enemigo
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

        // ✅ Si hay diálogo activo, dibujar encima del juego
        if (isDialogActive) {
            dialogManager.render();
        }
    }

    public void resize(int width, int height) {
        if (dialogManager != null) {
            dialogManager.resize(width, height);
        }
    }

    // ✅ Manejar input del diálogo
    private void handleDialogInput() {
    if (Gdx.input.justTouched()) {
        if (dialogManager.isDialogActive()) {
            dialogManager.nextLine();
        } else {
            isDialogActive = false;
        }
    }
}

    @Override
    public void exit() 
    {
        // --- LIBERAR RECURSOS DEL SUELO ---
        for (SolidObject object : solidObjects) {
            object.dispose();
        }
        
        // --- LIBERAR TEXTURA DEL JUGADOR ---
        if (playerTexture != null) {
            playerTexture.dispose();
        }

        // ✅ Liberar recursos del diálogo
        if (dialogManager != null) {
            dialogManager.dispose();
        }
    }
}