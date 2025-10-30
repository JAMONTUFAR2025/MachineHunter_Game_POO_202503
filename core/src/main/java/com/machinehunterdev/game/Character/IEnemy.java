package com.machinehunterdev.game.Character;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.machinehunterdev.game.DamageTriggers.Bullet;
import com.machinehunterdev.game.Environment.SolidObject;

import java.util.ArrayList;

/**
 * Interfaz para enemigos en el juego.
 * Define los métodos esenciales que deben implementar todos los enemigos.
 * 
 * @author MachineHunterDev
 */
public interface IEnemy {
    void draw(SpriteBatch batch); // Para dibujar el enemigo
    Character getCharacter(); // Para acceder a la lógica de CharacterController
    EnemyType getType(); // Para obtener el tipo de enemigo
}