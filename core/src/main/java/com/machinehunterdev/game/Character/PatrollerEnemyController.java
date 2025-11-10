package com.machinehunterdev.game.Character;

import java.util.ArrayList;
import com.badlogic.gdx.math.Vector2;
import com.machinehunterdev.game.DamageTriggers.Bullet;
import com.machinehunterdev.game.Environment.SolidObject;
import com.machinehunterdev.game.Gameplay.GlobalSettings;
import com.machinehunterdev.game.Levels.LevelData;

/**
 * Controlador especifico para enemigos con sistema de patrullaje.
 * Implementa una maquina de estados para gestionar el patrullaje y la espera.
 * Este controlador mueve al enemigo entre una serie de puntos definidos.
 * 
 * @author MachineHunterDev
 */
public class PatrollerEnemyController extends CharacterController {
    
    // Lista de puntos de patrullaje que el enemigo debe seguir.
    private ArrayList<PatrolPoint> patrolPoints;
    
    // Enumeracion para los estados de la maquina de estados del enemigo.
    private enum State { PATROLLING, WAITING }
    private State currentState; // Estado actual del enemigo.
    
    // Temporizador para controlar el tiempo de espera en un punto de patrullaje.
    private float waitTimer;
    private float waitTime; // Tiempo total que debe esperar.
    
    // Puntos de referencia para el patrullaje.
    private PatrolPoint currentTarget; // El punto de destino actual.
    private int currentTargetIndex; // Indice del punto actual en la lista.

    /**
     * Constructor del controlador de enemigos patrulleros.
     * @param enemyCharacter El personaje enemigo a controlar.
     * @param patrolPointsData La lista de datos de puntos de patrullaje.
     * @param waitTime El tiempo de espera en cada punto de patrullaje.
     */
    public PatrollerEnemyController(Character enemyCharacter, ArrayList<LevelData.Point> patrolPointsData, float waitTime) {
        super(enemyCharacter);
        this.patrolPoints = new ArrayList<PatrolPoint>();
        // Convierte los datos de puntos en objetos PatrolPoint.
        for (LevelData.Point pointData : patrolPointsData) {
            this.patrolPoints.add(new PatrolPoint(new Vector2(pointData.x, pointData.y), pointData.action));
        }
        this.waitTime = waitTime;
        
        // Inicializa la maquina de estados.
        this.currentState = State.PATROLLING;
        this.waitTimer = 0f;
        this.currentTargetIndex = 0;

        // Establece el primer punto de patrullaje como el objetivo inicial.
        if (patrolPoints != null && !patrolPoints.isEmpty()) {
            this.currentTarget = patrolPoints.get(currentTargetIndex);
        }
    }

    /**
     * Actualiza el estado del enemigo en cada fotograma.
     * @param delta Tiempo transcurrido desde la ultima actualizacion.
     * @param solidObjects Lista de objetos solidos para gestionar colisiones.
     * @param bullets Lista de balas en el juego.
     * @param playerCharacter El personaje del jugador.
     * @param enemyCount El numero de enemigos en el nivel.
     */
    @Override
    public void update(float delta, ArrayList<SolidObject> solidObjects, ArrayList<Bullet> bullets, Character playerCharacter, int enemyCount) {
        handleHurtAnimation(); // Gestiona la animacion de ser herido.
        checkCollisions(solidObjects); // Comprueba colisiones con el entorno.

        // Si no hay puntos de patrullaje o el enemigo no esta en el suelo, no hace nada.
        if (patrolPoints == null || patrolPoints.isEmpty() || !character.onGround) {
            character.stopMoving();
            return;
        }

        // Maquina de estados para el comportamiento de patrullaje.
        switch (currentState) {
            case PATROLLING:
                handlePatrollingState(delta);
                break;
            case WAITING:
                handleWaitingState(delta);
                break;
        }

        // Aplica los limites del mapa para que el enemigo no se salga.
        float enemyWidth = character.getWidth();
        if (character.position.x < 0) character.position.x = 0;
        else if (character.position.x > GlobalSettings.levelWidth - enemyWidth) character.position.x = GlobalSettings.levelWidth - enemyWidth;
    }

    /**
     * Gestiona el estado de patrullaje.
     * @param delta Tiempo transcurrido desde la ultima actualizacion.
     */
    private void handlePatrollingState(float delta) {
        // Si el enemigo ha llegado al punto objetivo (con una pequena tolerancia).
        float tolerance = 5f; // Tolerancia para evitar oscilaciones.
        if (Math.abs(character.position.x - currentTarget.position.x) <= tolerance) {
            character.stopMoving();
            character.velocity.x = 0; // Asegura que el movimiento horizontal se detenga.
            currentState = State.WAITING; // Cambia al estado de espera.
            waitTimer = 0f;
            return;
        }

        // Se mueve hacia el punto objetivo.
        if (character.position.x < currentTarget.position.x) {
            character.moveRight();
        } else {
            character.moveLeft();
        }
    }

    /**
     * Gestiona el estado de espera.
     * @param delta Tiempo transcurrido desde la ultima actualizacion.
     */
    private void handleWaitingState(float delta) {
        waitTimer += delta;
        character.stopMoving();
        // Si ha esperado el tiempo suficiente.
        if (waitTimer >= waitTime) {
            // Realiza una accion si esta definida en el punto de patrullaje (saltar o caer).
            if (currentTarget.action != null) {
                if (currentTarget.action.equalsIgnoreCase("Jump")) {
                    if (character.onGround) { // Solo puede saltar si esta en el suelo.
                        character.jump();
                    }
                } else if (currentTarget.action.equalsIgnoreCase("Fall")) {
                    if (character.onGround) { // Solo puede caer si esta en una plataforma.
                        character.fallThroughPlatform();
                    }
                }
            }

            // Avanza al siguiente punto de patrullaje de forma ciclica.
            currentTargetIndex = (currentTargetIndex + 1) % patrolPoints.size();
            currentTarget = patrolPoints.get(currentTargetIndex);
            currentState = State.PATROLLING; // Vuelve al estado de patrullaje.
        }
    }
}
