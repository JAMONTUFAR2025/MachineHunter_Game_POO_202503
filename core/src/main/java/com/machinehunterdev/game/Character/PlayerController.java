package com.machinehunterdev.game.Character;

import java.util.ArrayList;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.machinehunterdev.game.DamageTriggers.Bullet;
import com.machinehunterdev.game.DamageTriggers.WeaponType;
import com.machinehunterdev.game.Environment.SolidObject;
import com.machinehunterdev.game.Gameplay.GlobalSettings;

/**
 * Controlador específico para el personaje del jugador.
 * Maneja la entrada del teclado, daño, cámara y límites del mapa.
 * 
 * @author MachineHunterDev
 */
public class PlayerController extends CharacterController {
    
    private float shootCooldown = 0.5f; // Cooldown of 0.5 seconds
    private float shootTimer = 0;

    /**
     * Constructor que vincula este controlador con el personaje del jugador.
     * @param playerCharacter El personaje del jugador a controlar.
     */
    public PlayerController(Character playerCharacter) {
        super(playerCharacter);
    }

    /**
     * Actualiza el estado del jugador cada frame.
     * Maneja entrada, daño, física, colisiones y límites del mapa.
     * @param delta Tiempo transcurrido desde el último frame.
     * @param solidObjects Lista de objetos sólidos para colisiones.
     * @param bullets Lista de balas activas para disparar.
     */
    @Override
    public void update(float delta, ArrayList<SolidObject> solidObjects, ArrayList<Bullet> bullets) {
        if (shootTimer > 0) {
            shootTimer -= delta;
        }
        checkDistanceToGround(solidObjects);
        handleInput(bullets);
        character.update(delta);

        // Aplicar límites horizontales del mapa
        float playerWidth = character.getWidth();
        if (character.position.x < 0) {
            character.position.x = 0;
        } else if (character.position.x > 1440 - playerWidth) {
            character.position.x = 1440 - playerWidth;
            character.stopMoving();
        }

        checkCollisions(solidObjects);
    }

    /**
     * Maneja la entrada del teclado para movimiento, salto, ataque y agachado.
     * @param bullets Lista de balas activas para disparar
     */
    private void handleInput(ArrayList<Bullet> bullets) {
        // Cambio de armas con teclas numéricas
        if (Gdx.input.isKeyJustPressed(Keys.J)) {
            character.switchWeapon(WeaponType.LASER);
        } else if (Gdx.input.isKeyJustPressed(Keys.K)) {
            character.switchWeapon(WeaponType.ION);
        } else if (Gdx.input.isKeyJustPressed(Keys.L)) {
            character.switchWeapon(WeaponType.RAILGUN);
        }

        // Ataque con arma actual
        if (Gdx.input.isKeyPressed(Keys.SPACE) && character.onGround && !character.isInvulnerable()) {
            character.shoot(bullets);
            character.stopMoving();
            shootTimer = shootCooldown;

            // Permitir girar durante ataque
            if (Gdx.input.isKeyPressed(Keys.A)) {
                character.setSeeingRight(false);
            } else if (Gdx.input.isKeyPressed(Keys.D)) {
                character.setSeeingRight(true);
            }
        } else {
            character.stopAttacking();

            // Agachado con prioridad sobre movimiento
            if (Gdx.input.isKeyPressed(Keys.S) && character.onGround) {
                character.setCrouching(true);
                character.stopMoving();

                // Permitir girar durante agachado
                if (Gdx.input.isKeyPressed(Keys.A)) {
                    character.setSeeingRight(false);
                } else if (Gdx.input.isKeyPressed(Keys.D)) {
                    character.setSeeingRight(true);
                }
            } else {
                character.setCrouching(false);

                // Movimiento horizontal
                if (Gdx.input.isKeyPressed(Keys.A)) {
                    character.moveLeft();
                } else if (Gdx.input.isKeyPressed(Keys.D)) {
                    character.moveRight();
                } else {
                    character.stopMoving();
                }
            }
        }

        // Salto
        if (Gdx.input.isKeyJustPressed(Keys.W)) {
            character.jump();
        }
    }

    /**
     * Calcula la distancia vertical desde el jugador hasta el suelo o plataforma más cercana.
     * @param solidObjects Lista de objetos sólidos para comprobar.
     */
    private void checkDistanceToGround(ArrayList<SolidObject> solidObjects) {
        float playerX = character.getX();
        float playerY = character.getY();
        float playerWidth = character.getWidth();

        float distanceToGround = Float.MAX_VALUE;

        // Distancia al suelo principal
        if (playerY >= GlobalSettings.GROUND_LEVEL) {
            distanceToGround = playerY - GlobalSettings.GROUND_LEVEL;
        }

        // Distancia a plataformas
        for (SolidObject obj : solidObjects) {
            if (obj.isWalkable()) {
                com.badlogic.gdx.math.Rectangle platform = obj.getBounds();
                float platformTop = platform.y + platform.height;

                if (playerY > platformTop) {
                    float overlapLeft = Math.max(playerX, platform.x);
                    float overlapRight = Math.min(playerX + playerWidth, platform.x + platform.width);

                    if (overlapRight > overlapLeft) {
                        float distance = playerY - platformTop;
                        if (distance < distanceToGround) {
                            distanceToGround = distance;
                        }
                    }
                }
            }
        }

        character.setDistanceToGround(distanceToGround);
    }

    /**
     * Centra la cámara en el jugador, respetando los límites del mapa.
     * @param camera Cámara a centrar.
     */
    public void centerCameraOnPlayer(OrthographicCamera camera) {
        float halfWidth = character.getWidth() / 2f;
        float targetX = character.position.x + halfWidth;

        if (targetX < GlobalSettings.VIRTUAL_WIDTH / 2) {
            camera.position.x = GlobalSettings.VIRTUAL_WIDTH / 2;
        } else if (targetX > (1440 - GlobalSettings.VIRTUAL_WIDTH / 2)) {
            camera.position.x = 1440 - GlobalSettings.VIRTUAL_WIDTH / 2;
        } else {
            camera.position.x = targetX;
        }
        
        camera.position.y = GlobalSettings.VIRTUAL_HEIGHT / 2;
        camera.update();
    }

    /**
     * Devuelve el personaje del jugador controlado por este controlador.
     * @return Referencia al personaje del jugador.
     */
    public Character getPlayerCharacter() {
        return character;
    }
}