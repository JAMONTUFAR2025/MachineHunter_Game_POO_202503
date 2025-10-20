package com.machinehunterdev.game.Character;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.machinehunterdev.game.DamageTriggers.Bullet;
import com.machinehunterdev.game.Environment.SolidObject;

import java.util.ArrayList;

public abstract class BaseEnemy implements IEnemy {
    protected Character character;
    protected CharacterController controller;

    public BaseEnemy(Character character, CharacterController controller) {
        this.character = character;
        this.controller = controller;
    }

    @Override
    public void update(float delta, ArrayList<SolidObject> solidObjects, ArrayList<Bullet> bullets) {
        controller.update(delta, solidObjects, bullets, null);
    }

    @Override
    public void draw(SpriteBatch batch) {
        character.draw(batch);
    }

    @Override
    public Character getCharacter() {
        return character;
    }

    public CharacterController getController() {
        return controller;
    }
}