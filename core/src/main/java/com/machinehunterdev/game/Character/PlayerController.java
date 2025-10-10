package com.machinehunterdev.game.Character;

import java.util.ArrayList;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.machinehunterdev.game.Environment.SolidObject;
import com.machinehunterdev.game.Gameplay.GlobalSettings;

/**
 * Controlador específico para el personaje del jugador.
 * Maneja la entrada del teclado, daño, cámara y límites del mapa.
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
     */
    @Override
    public void update(float delta, ArrayList<SolidObject> solidObjects) {
        // Procesar entrada del teclado (movimiento y salto)
        handleInput();

        // Actualizar física del personaje (incluye animación gracias a Character.update())
        character.update(delta);

        // Aplicar límites horizontales del mapa (evitar que salga de la pantalla)
        float playerWidth = character.getWidth();
        if (character.position.x < 0) {
            character.position.x = 0;
        } else if (character.position.x > 1440 - playerWidth) {
            character.position.x = 1440 - playerWidth;
            character.stopMoving(); // Detener movimiento al tocar el borde
        }

        // Verificar colisiones con el entorno
        checkCollisions(solidObjects);
    }

    /**
     * Maneja la entrada del teclado para movimiento y salto.
     * Usa teclas A/D para moverse y ESPACIO para saltar.
     */
    private void handleInput() {
        if (Gdx.input.isKeyPressed(Keys.A)) {
            character.moveLeft();
        } else if (Gdx.input.isKeyPressed(Keys.D)) {
            character.moveRight();
        } else {
            character.stopMoving(); // Detener si no se presiona ninguna tecla de movimiento
        }

        // Salto (solo si se presiona ESPACIO y está en el suelo)
        if (Gdx.input.isKeyJustPressed(Keys.SPACE)) {
            character.jump();
        }
    }

    /**
     * Centra la cámara en el jugador, respetando los límites del mapa.
     * @param camera Cámara a centrar.
     */
    public void centerCameraOnPlayer(OrthographicCamera camera) {
        // Calcular el centro del personaje (posición + mitad del ancho)
        float halfWidth = character.getWidth() / 2f;
        float targetX = character.position.x + halfWidth;

        // Limitar la cámara a los bordes del mapa (1440 de ancho total)
        if (targetX < GlobalSettings.VIRTUAL_WIDTH / 2) {
            // Está en el borde izquierdo: fijar cámara al inicio
            camera.position.x = GlobalSettings.VIRTUAL_WIDTH / 2;
        } else if (targetX > (1440 - GlobalSettings.VIRTUAL_WIDTH / 2)) {
            // Está en el borde derecho: fijar cámara al final
            camera.position.x = 1440 - GlobalSettings.VIRTUAL_WIDTH / 2;
        } else {
            // Centrar cámara en el jugador
            camera.position.x = targetX;
        }
        
        // Mantener la cámara centrada verticalmente
        camera.position.y = GlobalSettings.VIRTUAL_HEIGHT / 2;
        camera.update(); // Aplicar cambios
    }

    /**
     * Devuelve el personaje del jugador controlado por este controlador.
     * @return Referencia al personaje del jugador.
     */
    public Character getPlayerCharacter() {
        return character;
    }
}