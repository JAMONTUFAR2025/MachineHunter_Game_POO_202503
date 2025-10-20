package com.machinehunterdev.game.Character;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.machinehunterdev.game.Dialog.Dialog;
import com.machinehunterdev.game.Environment.SolidObject;
import com.machinehunterdev.game.DamageTriggers.Bullet;

import java.util.ArrayList;
import java.util.List;

public class NPCController extends CharacterController {

    private float interactionRadius;
    private List<Dialog> dialogues;
    private boolean inRange;

    public NPCController(Character character, float interactionRadius, List<Dialog> dialogues) {
        super(character);
        this.interactionRadius = interactionRadius;
        this.dialogues = dialogues;
        this.inRange = false;
    }

    @Override
    public void update(float delta, ArrayList<SolidObject> solidObjects, ArrayList<Bullet> bullets, Character playerCharacter) {
        if (character != null) {
            character.update(delta);
            checkCollisions(solidObjects);
        }

        if (playerCharacter != null) {
            if (character.position.dst(playerCharacter.position) <= interactionRadius) {
                inRange = true;
            } else {
                inRange = false;
            }

            // Make NPC face the player
            if (playerCharacter.position.x > character.position.x) {
                character.isSeeingRight = true;
            } else {
                character.isSeeingRight = false;
            }
        }
    }

    public void render(SpriteBatch batch) {
        if (character != null) {
            character.draw(batch);
        }
    }

    public boolean isInRange() {
        return inRange;
    }

    public List<Dialog> getDialogues() {
        return dialogues;
    }
}
