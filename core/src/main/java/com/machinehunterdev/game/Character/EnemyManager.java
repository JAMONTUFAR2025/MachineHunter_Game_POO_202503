package com.machinehunterdev.game.Character;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.machinehunterdev.game.DamageTriggers.Bullet;
import com.machinehunterdev.game.Environment.SolidObject;

import java.util.ArrayList;

public class EnemyManager {
    private ArrayList<IEnemy> enemies;

    public EnemyManager() {
        enemies = new ArrayList<>();
    }

    public void addEnemy(EnemyType type, Character character, ArrayList<Vector2> patrolPoints, float waitTime, float shootInterval) {
        switch (type) {
            case PATROLLER:
                enemies.add(new PatrollerEnemy(character, patrolPoints, waitTime));
                break;
            case SHOOTER:
                enemies.add(new ShooterEnemy(character, shootInterval));
                break;
            case FLYING:
                enemies.add(new FlyingEnemy(character, patrolPoints, waitTime));
                break;
        }
    }

    public void update(float delta, ArrayList<SolidObject> solidObjects, ArrayList<Bullet> bullets, Character playerCharacter) {
        for (IEnemy enemy : enemies) {
            ((BaseEnemy)enemy).getController().update(delta, solidObjects, bullets, playerCharacter);
        }
    }

    public void draw(SpriteBatch batch) {
        for (IEnemy enemy : enemies) {
            enemy.draw(batch);
        }
    }

    public ArrayList<IEnemy> getEnemies() {
        return enemies;
    }
}
