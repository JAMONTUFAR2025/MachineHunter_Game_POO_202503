package com.machinehunterdev.game.Character;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.machinehunterdev.game.DamageTriggers.Bullet;
import com.machinehunterdev.game.Environment.SolidObject;

import java.util.ArrayList;

public interface IEnemy {
    void update(float delta, ArrayList<SolidObject> solidObjects, ArrayList<Bullet> bullet);
    void draw(SpriteBatch batch);
    Character getCharacter(); // Para acceder a la lgica de CharacterController
}