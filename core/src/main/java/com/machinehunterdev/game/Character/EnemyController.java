package com.machinehunterdev.game.Character;

import java.security.Key;
import java.util.ArrayList;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Input.Keys;
import com.machinehunterdev.game.Environment.SolidObject;

// -- CONTROLADOR PARA ENEMIGOS --
public class EnemyController extends CharacterController {
    // Para hace pruebas
    boolean moveLeft = true;

    public EnemyController(Character enemyCharacter) {
        super(enemyCharacter);
    }

    @Override
    public void update(float delta, ArrayList<SolidObject> solidObjects) {

        // PRUEBAS
        // Al presionar i cambiar el valor del banderin
        if(Gdx.input.isKeyJustPressed(Keys.I))
        {
            moveLeft = !moveLeft;
        }

        // Ejemplo: enemigo que patrulla
        if (!character.onGround) {
            // Si no está en suelo, no hacer nada (o aplicar IA)
        } else {
            // Moverse en la dirección actual
            if(moveLeft)
                character.moveLeft();
            else
                character.moveRight();

            // Aquí podrías añadir lógica para girar al borde de una plataforma
        }

        // Actualizar física
        character.update(delta);

        // Aplicar límites horizontales del mapa (evitar que salga de la pantalla)
        float enemyWidth = character.getWidth();
        if (character.position.x < 0) {
            character.position.x = 0;
        } else if (character.position.x > 1440 - enemyWidth) {
            character.position.x = 1440 - enemyWidth;
            character.stopMoving(); // Detener movimiento al tocar el borde
        }

        

        // Verificar colisiones con el entorno
        checkCollisions(solidObjects);
    }

    // Metodo para que el enemigo dañe al jugador
    public void dealDamage(Character player, int damage) {
        player.takeDamage(damage);
    }
}