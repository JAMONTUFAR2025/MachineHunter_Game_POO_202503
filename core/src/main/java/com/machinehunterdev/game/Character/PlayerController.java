package com.machinehunterdev.game.Character;

import java.util.ArrayList;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.machinehunterdev.game.Audio.AudioId;
import com.machinehunterdev.game.Audio.AudioManager;
import com.machinehunterdev.game.DamageTriggers.Bullet;
import com.machinehunterdev.game.DamageTriggers.WeaponType;
import com.machinehunterdev.game.Environment.SolidObject;
import com.machinehunterdev.game.Gameplay.GlobalSettings;

/**
 * Controlador especifico para el personaje del jugador.
 * Esta clase se encarga de traducir las entradas del teclado del usuario en acciones
 * para el personaje del jugador, como moverse, saltar y atacar.
 * Tambien gestiona la logica de la camara y los limites del mapa para el jugador.
 * 
 * @author MachineHunterDev
 */
public class PlayerController extends CharacterController {

    /**
     * Constructor que vincula este controlador con el personaje del jugador.
     * @param playerCharacter El personaje del jugador que sera controlado.
     */
    public PlayerController(Character playerCharacter) {
        super(playerCharacter);
    }

    /**
     * Actualiza el estado del jugador en cada fotograma.
     * Este es el metodo principal que orquesta la logica del jugador.
     * @param delta El tiempo transcurrido desde el ultimo fotograma.
     * @param solidObjects La lista de objetos solidos para gestionar colisiones.
     * @param bullets La lista de balas activas para disparar.
     * @param playerCharacter Referencia al propio jugador (puede ser redundante aqui).
     * @param enemyCount El numero de enemigos en el nivel.
     */
    @Override
    public void update(float delta, ArrayList<SolidObject> solidObjects, ArrayList<Bullet> bullets, Character playerCharacter, int enemyCount) {
        handleInput(bullets); // Procesa la entrada del teclado.
        checkDistanceToGround(solidObjects); // Calcula la distancia al suelo para animaciones y aterrizaje.

        // Aplica los limites horizontales del mapa para que el jugador no se salga de la pantalla.
        float playerWidth = character.getWidth();
        if (character.position.x < 0) {
            character.position.x = 0;
        } else if (character.position.x > GlobalSettings.levelWidth - playerWidth) {
            character.position.x = GlobalSettings.levelWidth - playerWidth;
            character.stopMoving();
        }

        // Realiza la comprobacion de colisiones con el suelo y las plataformas.
        checkCollisions(solidObjects);
    }

    /**
     * Gestiona la entrada del teclado para las acciones del jugador.
     * @param bullets La lista de balas a la que se anadiran nuevos proyectiles si el jugador dispara.
     */
    private void handleInput(ArrayList<Bullet> bullets) {
        // Gestion del cambio de armas con las teclas numericas.
        if (Gdx.input.isKeyJustPressed(GlobalSettings.CHANGE_WEAPON_LASER)) {
            handleWeaponSwitch(WeaponType.LASER);
        } else if (Gdx.input.isKeyJustPressed(GlobalSettings.CHANGE_WEAPON_ION)) {
            handleWeaponSwitch(WeaponType.ION);
        } else if (Gdx.input.isKeyJustPressed(GlobalSettings.CHANGE_WEAPON_RAILGUN)) {
            handleWeaponSwitch(WeaponType.RAILGUN);
        }

        // Logica de ataque: se ejecuta al mantener presionada la tecla de ataque mientras se esta en el suelo.
        if (Gdx.input.isKeyPressed(GlobalSettings.CONTROL_ATTACK) && character.onGround) {

            // Si el jugador esta agachado sobre una plataforma, intenta caer a traves de ella.
            if (character.isCrouching && character.onPlatform) {
                character.fallThroughPlatform();
            }
            
            // Si no es invulnerable, el jugador puede disparar.
            if(!character.isInvulnerable())
            {
                character.shoot(bullets);
                character.stopMoving(); // El jugador no puede moverse mientras dispara.

                // Permite al jugador cambiar de direccion mientras ataca.
                if (Gdx.input.isKeyPressed(GlobalSettings.CONTROL_MOVE_LEFT)) {
                    character.setSeeingRight(false);
                } else if (Gdx.input.isKeyPressed(GlobalSettings.CONTROL_MOVE_RIGHT)) {
                    character.setSeeingRight(true);
                }
            }
        } else {
            character.stopAttacking(); // Si no se presiona la tecla de ataque, se detiene el ataque.

            // Logica de agacharse: tiene prioridad sobre el movimiento.
            if (Gdx.input.isKeyPressed(GlobalSettings.CONTROL_CROUCH) && character.onGround) {
                character.setCrouching(true);
                character.stopMoving();

                // Permite al jugador cambiar de direccion mientras esta agachado.
                if (Gdx.input.isKeyPressed(GlobalSettings.CONTROL_MOVE_LEFT)) {
                    character.setSeeingRight(false);
                } else if (Gdx.input.isKeyPressed(GlobalSettings.CONTROL_MOVE_RIGHT)) {
                    character.setSeeingRight(true);
                }
            } else {
                character.setCrouching(false);

                // Logica de movimiento horizontal.
                if (Gdx.input.isKeyPressed(GlobalSettings.CONTROL_MOVE_LEFT)) {
                    character.moveLeft();
                } else if (Gdx.input.isKeyPressed(GlobalSettings.CONTROL_MOVE_RIGHT)) {
                    character.moveRight();
                } else {
                    character.stopMoving();
                }
            }
        }

         // Logica de salto: se ejecuta solo una vez al presionar la tecla de salto.
        if (Gdx.input.isKeyJustPressed(GlobalSettings.CONTROL_JUMP)) {
            character.jump();
        }
    }

    /**
     * Gestiona el cambio de arma del jugador.
     * @param newWeapon El nuevo tipo de arma a equipar.
     */
    private void handleWeaponSwitch(WeaponType newWeapon) {
        // No se puede cambiar de arma mientras se ataca o se es invulnerable.
        if (character.isAttacking() || character.isInvulnerable()) {
            AudioManager.getInstance().playSfx(AudioId.NotAvailable, null);
            return;
        }

        // No hace nada si se intenta cambiar al arma que ya esta equipada.
        if (character.getCurrentWeapon() == newWeapon) {
            AudioManager.getInstance().playSfx(AudioId.NotAvailable, null);
            return;
        }

        character.switchWeapon(newWeapon);
        AudioManager.getInstance().playSfx(AudioId.UIChange, null);
    }

    /**
     * Calcula la distancia vertical desde los pies del jugador hasta la superficie mas cercana debajo de el.
     * @param solidObjects La lista de objetos solidos para comprobar.
     */
    private void checkDistanceToGround(ArrayList<SolidObject> solidObjects) {
        float playerX = character.getX();
        float playerY = character.getY();
        float playerWidth = character.getWidth();

        float distanceToGround = Float.MAX_VALUE;
        float groundY = -1;

        // Comprueba la distancia al suelo principal.
        if (playerY >= GlobalSettings.GROUND_LEVEL) {
            float dist = playerY - GlobalSettings.GROUND_LEVEL;
            if (dist < distanceToGround) {
                distanceToGround = dist;
                groundY = GlobalSettings.GROUND_LEVEL;
            }
        }

        // Comprueba la distancia a cada plataforma.
        for (SolidObject obj : solidObjects) {
            if (obj.isWalkable()) {
                com.badlogic.gdx.math.Rectangle platform = obj.getBounds();
                float platformTop = platform.y + platform.height;

                if (playerY >= platformTop) {
                    // Comprueba si el jugador esta horizontalmente sobre la plataforma.
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

        // Si la distancia es muy pequena y el jugador esta cayendo, lo hace aterrizar.
        if (distanceToGround < 0.1f && character.velocity.y <= 0) {
            character.landOn(groundY);
        }
    }

    /**
     * Centra la camara del juego en el jugador, asegurandose de no mostrar areas fuera de los limites del nivel.
     * @param camera La camara del juego que seguira al jugador.
     */
    public void centerCameraOnPlayer(OrthographicCamera camera) {
        float halfWidth = character.getWidth() / 2f;
        float targetX = character.position.x + halfWidth;

        // Limita la posicion de la camara para que no se salga del nivel por la izquierda.
        if (targetX < GlobalSettings.VIRTUAL_WIDTH / 2) {
            camera.position.x = GlobalSettings.VIRTUAL_WIDTH / 2;
        // Limita la posicion de la camara para que no se salga del nivel por la derecha.
        } else if (targetX > (GlobalSettings.levelWidth - GlobalSettings.VIRTUAL_WIDTH / 2)) {
            camera.position.x = GlobalSettings.levelWidth - GlobalSettings.VIRTUAL_WIDTH / 2;
        } else {
            // Centra la camara en el jugador.
            camera.position.x = targetX;
        }
        
        // Mantiene la camara fija en el eje Y.
        camera.position.y = GlobalSettings.VIRTUAL_HEIGHT / 2;
        camera.update();
    }

    /**
     * Devuelve el personaje del jugador que este controlador esta manejando.
     * @return Una referencia al personaje del jugador.
     */
    public Character getPlayerCharacter() {
        return character;
    }
}