package com.machinehunterdev.game.Character;

import com.badlogic.gdx.math.Vector2;

public class PatrolPoint {
    public Vector2 position;
    public String action;

    public PatrolPoint(Vector2 position, String action) {
        this.position = position;
        this.action = action;
    }
}
