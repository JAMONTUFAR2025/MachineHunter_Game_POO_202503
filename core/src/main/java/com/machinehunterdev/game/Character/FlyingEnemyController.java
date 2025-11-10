package com.machinehunterdev.game.Character;

import com.badlogic.gdx.math.Vector2;
import com.machinehunterdev.game.DamageTriggers.Bullet;
import com.machinehunterdev.game.Environment.SolidObject;

import java.util.ArrayList;

/**
 * Controlador especifico para enemigos voladores.
 * Este controlador gestiona el comportamiento de un enemigo que no esta sujeto a la gravedad.
 * Implementa una maquina de estados simple para patrullar entre puntos en el aire y esperar.
 * No realiza comprobaciones de colisiones con el suelo, ya que vuela.
 * 
 * @author MachineHunterDev
 */
public class FlyingEnemyController extends CharacterController {

    // La lista de coordenadas (Vector2) que el enemigo volador seguira en su patrullaje.
    private ArrayList<Vector2> patrolPoints;
    
    // Enumeracion que define los posibles estados del enemigo volador.
    private enum State { PATROLLING, WAITING }
    
    // Almacena el estado actual del enemigo (patrullando o esperando).
    private State currentState;

    // Temporizadores para gestionar la logica de espera en un punto de patrullaje.
    private float waitTimer; // Contador de tiempo para la espera.
    private float waitTime; // Duracion total de la espera.

    // Gestion del objetivo de patrullaje actual.
    private Vector2 currentTarget; // Coordenada del punto de destino actual.
    private int currentTargetIndex; // Indice del punto de destino en la lista 'patrolPoints'.

    /**
     * Constructor del controlador para enemigos voladores.
     * @param enemyCharacter El objeto Character que este controlador manejara.
     * @param patrolPoints La lista de puntos (coordenadas) para el patrullaje aereo.
     * @param waitTime El tiempo en segundos que el enemigo debe esperar en cada punto.
     */
    public FlyingEnemyController(Character enemyCharacter, ArrayList<Vector2> patrolPoints, float waitTime) {
        super(enemyCharacter); // Llama al constructor de la clase base.
        this.patrolPoints = patrolPoints;
        this.waitTime = waitTime;
        this.currentState = State.PATROLLING; // El estado inicial es patrullar.
        this.waitTimer = 0f;
        this.currentTargetIndex = 0;

        // Si hay puntos de patrullaje, se establece el primero como objetivo inicial.
        if (patrolPoints != null && !patrolPoints.isEmpty()) {
            this.currentTarget = patrolPoints.get(currentTargetIndex);
        }
        
        // Es fundamental desactivar la gravedad para que el enemigo pueda volar.
        character.gravity = 0;
    }

    /**
     * Actualiza la logica del enemigo volador en cada fotograma.
     * @param delta El tiempo transcurrido desde el ultimo fotograma.
     * @param solidObjects Lista de objetos solidos del nivel (generalmente ignorados por este enemigo).
     * @param bullets Lista de balas presentes en el juego.
     * @param playerCharacter El personaje del jugador.
     * @param enemyCount El numero total de enemigos.
     */
    @Override
    public void update(float delta, ArrayList<SolidObject> solidObjects, ArrayList<Bullet> bullets, Character playerCharacter, int enemyCount) {
        handleHurtAnimation(); // Gestiona la animacion de recibir dano si es necesario.
        // Los enemigos voladores no necesitan comprobar colisiones con el suelo o plataformas.

        // Si no hay puntos de patrullaje, el enemigo simplemente se detiene.
        if (patrolPoints == null || patrolPoints.isEmpty()) {
            character.stopMoving();
            return;
        }

        // Maquina de estados que determina el comportamiento actual.
        switch (currentState) {
            case PATROLLING:
                handlePatrollingState(delta);
                break;
            case WAITING:
                handleWaitingState(delta);
                break;
        }
    }

    /**
     * Gestiona la logica del estado de patrullaje.
     * Mueve al enemigo hacia el punto de destino actual.
     * @param delta El tiempo transcurrido desde el ultimo fotograma.
     */
    private void handlePatrollingState(float delta) {
        // Se define una pequena distancia de tolerancia para considerar que ha llegado al punto.
        float tolerance = 5.0f;
        if (character.position.dst(currentTarget) <= tolerance) {
            // Si ha llegado, detiene su movimiento y cambia al estado de espera.
            character.velocity.set(0, 0);
            character.stopMoving();
            currentState = State.WAITING;
            waitTimer = 0f; // Reinicia el temporizador de espera.
            return;
        }

        // Si aun no ha llegado, calcula la direccion hacia el objetivo.
        Vector2 direction = new Vector2(currentTarget).sub(character.position).nor();
        // Establece la velocidad del personaje para que se mueva en esa direccion.
        character.velocity.set(direction.scl(character.speed));
    }


    /**
     * Gestiona la logica del estado de espera.
     * El enemigo permanece inmovil durante un tiempo determinado.
     * @param delta El tiempo transcurrido desde el ultimo fotograma.
     */
    private void handleWaitingState(float delta) {
        waitTimer += delta; // Incrementa el temporizador de espera.
        character.velocity.set(0, 0); // Asegura que el enemigo no se mueva.
        character.stopMoving();
        
        // Si el tiempo de espera ha concluido.
        if (waitTimer >= waitTime) {
            // Selecciona el siguiente punto de patrullaje de forma ciclica.
            currentTargetIndex = (currentTargetIndex + 1) % patrolPoints.size();
            currentTarget = patrolPoints.get(currentTargetIndex);
            // Cambia de nuevo al estado de patrullaje para moverse al siguiente punto.
            currentState = State.PATROLLING;
        }
    }
}