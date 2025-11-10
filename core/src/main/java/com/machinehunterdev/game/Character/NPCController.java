package com.machinehunterdev.game.Character;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.machinehunterdev.game.Dialog.Dialog;
import com.machinehunterdev.game.Environment.SolidObject;
import com.machinehunterdev.game.DamageTriggers.Bullet;

import java.util.ArrayList;
import java.util.List;

/**
 * Controlador especifico para Personajes No Jugables (NPCs).
 * Esta clase gestiona el comportamiento basico de un NPC, que principalmente consiste
 * en detectar si el jugador esta cerca para iniciar una interaccion (dialogo)
 * y mirar hacia el jugador.
 * 
 * @author MachineHunterDev
 */
public class NPCController extends CharacterController {

    // El radio alrededor del NPC en el que el jugador puede interactuar con el.
    private float interactionRadius;
    
    // La lista de dialogos que este NPC puede mostrar.
    private List<Dialog> dialogues;
    
    // Bandera que indica si el jugador esta actualmente dentro del radio de interaccion.
    private boolean inRange;

    /**
     * Constructor del controlador de NPC.
     * @param character El objeto Character que representa al NPC.
     * @param interactionRadius El radio de interaccion.
     * @param dialogues La lista de dialogos asociados a este NPC.
     */
    public NPCController(Character character, float interactionRadius, List<Dialog> dialogues) {
        super(character);
        this.interactionRadius = interactionRadius;
        this.dialogues = dialogues;
        this.inRange = false;
    }

    /**
     * Actualiza el estado del NPC en cada fotograma.
     * @param delta El tiempo transcurrido desde el ultimo fotograma.
     * @param solidObjects La lista de objetos solidos para colisiones.
     * @param bullets La lista de balas (generalmente ignoradas por los NPCs).
     * @param playerCharacter La referencia al personaje del jugador.
     * @param enemyCount El numero de enemigos en el nivel.
     */
    @Override
    public void update(float delta, ArrayList<SolidObject> solidObjects, ArrayList<Bullet> bullets, Character playerCharacter, int enemyCount) {
        // Realiza comprobaciones de colisiones basicas si el NPC tiene un cuerpo fisico.
        if (character != null) {
            checkCollisions(solidObjects);
        }

        // Si el jugador existe, comprueba la distancia para la interaccion.
        if (playerCharacter != null) {
            // Comprueba si la distancia entre el NPC y el jugador es menor o igual al radio de interaccion.
            if (character.position.dst(playerCharacter.position) <= interactionRadius) {
                inRange = true;
            } else {
                inRange = false;
            }

            // Hace que el NPC siempre mire hacia la direccion del jugador.
            if (playerCharacter.position.x > character.position.x) {
                character.isSeeingRight = true;
            } else {
                character.isSeeingRight = false;
            }
        }
    }

    /**
     * Renderiza el personaje del NPC.
     * @param batch El SpriteBatch utilizado para dibujar.
     */
    public void render(SpriteBatch batch) {
        if (character != null) {
            character.draw(batch);
        }
    }

    /**
     * Comprueba si el jugador esta dentro del rango de interaccion del NPC.
     * @return Verdadero si el jugador esta en rango, falso en caso contrario.
     */
    public boolean isInRange() {
        return inRange;
    }

    /**
     * Obtiene la lista de dialogos asociados a este NPC.
     * @return Una lista de objetos Dialog.
     */
    public List<Dialog> getDialogues() {
        return dialogues;
    }
}
