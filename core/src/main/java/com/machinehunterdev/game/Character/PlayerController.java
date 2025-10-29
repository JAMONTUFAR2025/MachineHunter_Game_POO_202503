package com.machinehunterdev.game.Character;

import java.util.ArrayList;
import com.badlogic.gdx.Gdx;
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
    public void update(float delta, ArrayList<SolidObject> solidObjects, ArrayList<Bullet> bullets, Character playerCharacter) {
        checkDistanceToGround(solidObjects);
        handleInput(bullets);

        // Aplicar límites horizontales del mapa
        float playerWidth = character.getWidth();
        if (character.position.x < 0) {
            character.position.x = 0;
        } else if (character.position.x > GlobalSettings.levelWidth - playerWidth) {
            character.position.x = GlobalSettings.levelWidth - playerWidth;
            character.stopMoving();
        }

        checkCollisions(solidObjects);
    }

    /**
     * Maneja la entrada del teclado para movimiento, salto, ataque y agachado.
     * @param bullets Lista de balas activas para disparar
     */
    private void handleInput(ArrayList<Bullet> bullets) {
        // Cambio de armas con teclas numéricas (mientras no este atacando)
        if (Gdx.input.isKeyJustPressed(GlobalSettings.CHANGE_WEAPON_LASER) && !character.isAttacking) {
            character.switchWeapon(WeaponType.LASER);
        } else if (Gdx.input.isKeyJustPressed(GlobalSettings.CHANGE_WEAPON_ION) && !character.isAttacking) {
            character.switchWeapon(WeaponType.ION);
        } else if (Gdx.input.isKeyJustPressed(GlobalSettings.CHANGE_WEAPON_RAILGUN) && !character.isAttacking) {
            character.switchWeapon(WeaponType.RAILGUN);
        }

        // Ataque con arma actual (solo si está en el suelo y no está invulnerable ni agachado)
        if (Gdx.input.isKeyPressed(GlobalSettings.CONTROL_ATTACK) && character.onGround && !character.isInvulnerable() && !character.isCrouching) {
            character.shoot(bullets);
            character.stopMoving();

            // Permitir girar durante ataque
            if (Gdx.input.isKeyPressed(GlobalSettings.CONTROL_MOVE_LEFT)) {
                character.setSeeingRight(false);
            } else if (Gdx.input.isKeyPressed(GlobalSettings.CONTROL_MOVE_RIGHT)) {
                character.setSeeingRight(true);
            }
        } else {
            character.stopAttacking();

            // Agachado con prioridad sobre movimiento
            if (Gdx.input.isKeyPressed(GlobalSettings.CONTROL_CROUCH) && character.onGround) {
                character.setCrouching(true);
                character.stopMoving();

                // Permitir girar durante agachado
                if (Gdx.input.isKeyPressed(GlobalSettings.CONTROL_MOVE_LEFT)) {
                    character.setSeeingRight(false);
                } else if (Gdx.input.isKeyPressed(GlobalSettings.CONTROL_MOVE_RIGHT)) {
                    character.setSeeingRight(true);
                }
            } else {
                character.setCrouching(false);

                // Movimiento horizontal
                if (Gdx.input.isKeyPressed(GlobalSettings.CONTROL_MOVE_LEFT)) {
                    character.moveLeft();
                } else if (Gdx.input.isKeyPressed(GlobalSettings.CONTROL_MOVE_RIGHT)) {
                    character.moveRight();
                } else {
                    character.stopMoving();
                }
            }
        }

        // Salto
        if (Gdx.input.isKeyJustPressed(GlobalSettings.CONTROL_JUMP)) {
            if (character.isCrouching && character.onGround) {
                character.fallThroughPlatform();
            } else {
                character.jump();
            }
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
        float groundY = -1;

        // Distancia al suelo principal
        if (playerY >= GlobalSettings.GROUND_LEVEL) {
            float dist = playerY - GlobalSettings.GROUND_LEVEL;
            if (dist < distanceToGround) {
                distanceToGround = dist;
                groundY = GlobalSettings.GROUND_LEVEL;
            }
        }

        // Distancia a plataformas
        for (SolidObject obj : solidObjects) {
            if (obj.isWalkable()) {
                com.badlogic.gdx.math.Rectangle platform = obj.getBounds();
                float platformTop = platform.y + platform.height;

                if (playerY >= platformTop) {
                    float overlapLeft = Math.max(playerX, platform.x);
                    float overlapRight = Math.min(playerX + playerWidth, platform.x + platform.width);

                    if (overlapRight > overlapLeft) {
                        float distance = playerY - platformTop;
                        if (distance < distanceToGround) {
                            distanceToGround = distance;
                            groundY = platformTop;
                        }
                    }
                }
            }
        }

        character.setDistanceToGround(distanceToGround);

        if (distanceToGround < 0.1f && character.velocity.y <= 0) {
            character.landOn(groundY);
        }
    }

    /**
     * Centra la cámara en el jugador, respetando los límites del mapa.
     * Cambiar esto para distintos niveles en el futuro.
     * @param camera Cámara a centrar.
     */
    public void centerCameraOnPlayer(OrthographicCamera camera) {
        float halfWidth = character.getWidth() / 2f;
        float targetX = character.position.x + halfWidth;

        if (targetX < GlobalSettings.VIRTUAL_WIDTH / 2) {
            camera.position.x = GlobalSettings.VIRTUAL_WIDTH / 2;
        } else if (targetX > (GlobalSettings.levelWidth - GlobalSettings.VIRTUAL_WIDTH / 2)) {
            camera.position.x = GlobalSettings.levelWidth - GlobalSettings.VIRTUAL_WIDTH / 2;
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