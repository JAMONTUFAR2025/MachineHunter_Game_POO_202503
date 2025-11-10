package com.machinehunterdev.game.Character;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.machinehunterdev.game.DamageTriggers.Bullet;
import com.machinehunterdev.game.Environment.SolidObject;

import java.util.ArrayList;

/**
 * Interfaz para enemigos en el juego.
 * Define los metodos esenciales que deben implementar todos los enemigos.
 * Esta interfaz asegura que cualquier clase de enemigo tenga una forma estandar
 * de ser dibujado y de acceder a su informacion basica.
 * 
 * @author MachineHunterDev
 */
public interface IEnemy {
    /**
     * Dibuja el enemigo en la pantalla.
     * Este metodo es responsable de renderizar el sprite del enemigo.
     * @param batch El SpriteBatch que se usara para dibujar.
     */
    void draw(SpriteBatch batch);

    /**
     * Obtiene la instancia del personaje asociado a este enemigo.
     * El objeto Character contiene la logica de estado, como la vida, la posicion, etc.
     * @return El objeto Character del enemigo.
     */
    Character getCharacter();

    /**
     * Obtiene el tipo de enemigo.
     * El tipo de enemigo se utiliza para diferenciar comportamientos y caracteristicas.
     * @return El tipo de enemigo (de la enumeracion EnemyType).
     */
    EnemyType getType();
}