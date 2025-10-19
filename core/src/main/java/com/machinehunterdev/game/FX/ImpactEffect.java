package com.machinehunterdev.game.FX;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.machinehunterdev.game.Util.SpriteAnimator;

import java.util.List;

public class ImpactEffect {
    private Vector2 position;
    private SpriteAnimator animator;
    private boolean isFinished;

    public ImpactEffect(float x, float y, List<Sprite> frames, float frameDuration) {
        this.position = new Vector2(x, y);
        this.animator = new SpriteAnimator(frames, frameDuration, false); // Not looping
        this.animator.start();
        this.isFinished = false;
    }

    public void update(float delta) {
        animator.handleUpdate(delta);
        if (animator.isFinished()) {
            isFinished = true;
        }
    }

    public void draw(SpriteBatch batch) {
        if (!isFinished) {
            Sprite currentSprite = animator.getCurrentSprite();
            if (currentSprite != null) {
                currentSprite.setPosition(position.x, position.y);
                animator.draw(batch);
            }
        }
    }

    public boolean isFinished() {
        return isFinished;
    }

}
