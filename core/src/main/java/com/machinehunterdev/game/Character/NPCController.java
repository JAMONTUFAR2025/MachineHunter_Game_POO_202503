package com.machinehunterdev.game.Character;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.machinehunterdev.game.Dialog.Dialog;
import com.machinehunterdev.game.Environment.SolidObject;
import com.machinehunterdev.game.DamageTriggers.Bullet;

import java.util.ArrayList;
import java.util.List;

/**
 * Controlador para personajes no jugables (NPC).
 * Maneja interacciones y comportamiento de los NPCs.
 * 
 * @author MachineHunterDev
 */
public class NPCController extends CharacterController {

    /* Radio de interacción */
    private float interactionRadius;
    /* Diálogos del NPC */
    private List<Dialog> dialogues;
    /* Estado de interacción */
    private boolean inRange;

    /**
     * Constructor del controlador de NPC.
     * @param character El personaje NPC asociado.
     * @param interactionRadius Radio de interacción.
     * @param dialogues Diálogos del NPC.
     */
    public NPCController(Character character, float interactionRadius, List<Dialog> dialogues) {
        super(character);
        this.interactionRadius = interactionRadius;
        this.dialogues = dialogues;
        this.inRange = false;
    }

    /**
     * Actualiza el estado del NPC.
     * @param delta Tiempo transcurrido desde la última actualización.
     * @param solidObjects Objetos sólidos en el entorno.
     * @param bullets Balas en el entorno.
     * @param playerCharacter El personaje jugador.
     */
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

            // Hacer que el NPC mire hacia el jugador
            if (playerCharacter.position.x > character.position.x) {
                character.isSeeingRight = true;
            } else {
                character.isSeeingRight = false;
            }
        }
    }

    /**
     * Renderiza el NPC.
     * @param batch El SpriteBatch utilizado para dibujar.
     */
    public void render(SpriteBatch batch) {
        if (character != null) {
            character.draw(batch);
        }
    }

    /**
     * Verifica si el jugador está dentro del rango de interacción.
     * @return true si el jugador está en rango, false en caso contrario.
     */
    public boolean isInRange() {
        return inRange;
    }

    /**
     * Obtiene los diálogos del NPC.
     * @return Lista de diálogos del NPC.
     */
    public List<Dialog> getDialogues() {
        return dialogues;
    }
}
