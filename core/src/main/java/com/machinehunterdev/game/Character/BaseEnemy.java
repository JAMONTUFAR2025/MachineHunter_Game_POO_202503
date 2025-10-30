package com.machinehunterdev.game.Character;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.machinehunterdev.game.DamageTriggers.Bullet;
import com.machinehunterdev.game.Environment.SolidObject;

import java.util.ArrayList;

/**
 * Implementación base de un enemigo.
 * Contiene la lógica común para todos los enemigos.
 * 
 * @author MachineHunterDev
 */
public abstract class BaseEnemy implements IEnemy {

    /* Características del enemigo */
    protected Character character;
    protected CharacterController controller;
    protected EnemyType enemyType;

    /**
     * Constructor de BaseEnemy.
     * @param character El personaje asociado al enemigo.
     * @param controller El controlador de comportamiento del enemigo.
     */
    public BaseEnemy(Character character, CharacterController controller, EnemyType enemyType) {
        this.character = character;
        this.controller = controller;
        this.enemyType = enemyType;
    }

    /**
     * Dibuja el enemigo en la pantalla.
     * @param batch El SpriteBatch utilizado para el dibujo.
     */
    @Override
    public void draw(SpriteBatch batch) {
        character.draw(batch);
    }

    /**
     * Obtiene el personaje asociado al enemigo.
     * @return El personaje del enemigo.
     */
    @Override
    public Character getCharacter() {
        return character;
    }

    /**
     * Obtiene el controlador del enemigo.
     * @return El controlador del enemigo.
     */
    public CharacterController getController() {
        return controller;
    }

    /**
     * Obtiene el tipo de enemigo.
     * @return El tipo de enemigo.
     */
    @Override
    public EnemyType getType() {
        return enemyType;
    }
}