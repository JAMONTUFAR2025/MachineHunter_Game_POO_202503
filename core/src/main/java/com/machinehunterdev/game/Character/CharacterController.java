package com.machinehunterdev.game.Character;

import java.util.ArrayList;
import com.badlogic.gdx.math.Rectangle;
import com.machinehunterdev.game.DamageTriggers.Bullet;
import com.machinehunterdev.game.Environment.SolidObject;
import com.machinehunterdev.game.Gameplay.GlobalSettings;

/**
 * Clase base abstracta para los controladores de personajes.
 * Un controlador es responsable de la "inteligencia" de un personaje, ya sea
 * respondiendo a la entrada del jugador (en el caso de PlayerController) o
 * ejecutando una IA (en el caso de los controladores de enemigos).
 * 
 * Define la logica comun de colisiones y actualizacion que todos los controladores deben tener.
 * 
 * @author MachineHunterDev
 */
public abstract class CharacterController {
    
    // Referencia al objeto Character que este controlador maneja.
    // El controlador actua sobre este personaje para moverlo, hacerlo atacar, etc.
    public Character character;

    /**
     * Constructor que vincula el controlador con un personaje especifico.
     * @param character El personaje que sera controlado.
     */
    public CharacterController(Character character) {
        this.character = character;
    }

    /**
     * Verifica y gestiona las colisiones del personaje con los objetos solidos del entorno.
     * Esta implementacion se centra en la colision vertical para detectar cuando el personaje
     * aterriza sobre el suelo o una plataforma.
     * @param solidObjects Una lista de todos los objetos solidos presentes en el nivel.
     */
    protected void checkCollisions(ArrayList<SolidObject> solidObjects) {
        Rectangle charBounds = character.getBounds();
        float charWidth = charBounds.width;
        float charX = charBounds.x;
        float charY = charBounds.y;

        // La posicion de los pies del personaje, que es crucial para detectar el suelo.
        float feetY = charY;
        float feetLeft = charX;
        float feetRight = charX + charWidth;

        // Primero, comprueba la colision con el suelo principal del nivel.
        if(feetY <= GlobalSettings.GROUND_LEVEL && character.velocity.y <= 0) {
            character.landOn(GlobalSettings.GROUND_LEVEL); // Llama al metodo para aterrizar.
            return; // Si aterriza en el suelo, no es necesario comprobar mas colisiones.
        }

        // Luego, comprueba la colision con cada plataforma del nivel.
        for (SolidObject obj : solidObjects) {
            // Solo considera plataformas sobre las que se puede caminar y si el personaje no esta intentando atravesarlas.
            if (obj.isWalkable() && !character.isFallingThroughPlatform) {
                Rectangle platform = obj.getBounds();
                float platformTop = platform.y + platform.height;

                // Solo comprueba la colision si el personaje esta cayendo (velocidad Y negativa o cero).
                if (character.velocity.y <= 0) {
                    // Calcula la superposicion horizontal entre el personaje y la plataforma.
                    float overlapLeft = Math.max(feetLeft, platform.x);
                    float overlapRight = Math.min(feetRight, platform.x + platform.width);
                    float overlapWidth = overlapRight - overlapLeft;

                    // Si hay superposicion horizontal y los pies del personaje estan a la altura de la parte superior de la plataforma.
                    if (overlapWidth > 0 && charY >= platformTop - 5 && charY <= platformTop + 5) {
                        character.landOn(platformTop); // Aterriza sobre la plataforma.
                        return; // Termina la comprobacion.
                    }
                }
            }
        }

        // Si no se ha detectado ninguna colision con una superficie, el personaje esta en el aire.
        character.onGround = false;
    }

    /**
     * Metodo abstracto que debe ser implementado por cada controlador especifico (PlayerController, EnemyController, etc.).
     * Contiene la logica de actualizacion que se ejecuta en cada fotograma del juego.
     * @param delta El tiempo transcurrido desde el ultimo fotograma.
     * @param solidObjects La lista de objetos solidos para gestionar colisiones.
     * @param bullets La lista de balas activas para detectar impactos.
     * @param playerCharacter La referencia al personaje del jugador.
     * @param enemyCount El numero de enemigos en el nivel.
     */
    public abstract void update(float delta, ArrayList<SolidObject> solidObjects, ArrayList<Bullet> bullets, Character playerCharacter, int enemyCount);

    /**
     * Sobrecarga del metodo update para anadir compatibilidad con una lista de enemigos.
     * Por defecto, simplemente llama al otro metodo update. Puede ser sobreescrito si se necesita
     * logica de interaccion entre enemigos.
     */
    public void update(float delta, ArrayList<SolidObject> solidObjects, ArrayList<Bullet> bullets, Character playerCharacter, int enemyCount, ArrayList<IEnemy> enemies) {
        update(delta, solidObjects, bullets, playerCharacter, enemyCount);
    }

    /**
     * Gestiona la transicion de salida de la animacion de "herido" (HURT).
     * Cuando la animacion de dano termina, esta funcion se encarga de devolver al personaje
     * a su estado de animacion anterior.
     */
    protected void handleHurtAnimation() {
        CharacterAnimator.AnimationState currentAnimation = character.characterAnimator.getCurrentState();
        
        // Si la animacion de 'HURT' ha terminado.
        if (currentAnimation == CharacterAnimator.AnimationState.HURT && character.characterAnimator.isAnimationFinished(CharacterAnimator.AnimationState.HURT)) {
            CharacterAnimator.AnimationState previousState = character.characterAnimator.getPreviousState();
            character.characterAnimator.resumeAnimation(previousState); // Reanuda la animacion que fue interrumpida.

            // Comprueba si la animacion interrumpida era un ataque.
            boolean wasAttacking = previousState == CharacterAnimator.AnimationState.ATTACK ||
                                   previousState == CharacterAnimator.AnimationState.ATTACK1 ||
                                   previousState == CharacterAnimator.AnimationState.ATTACK2 ||
                                   previousState == CharacterAnimator.AnimationState.SUMMON;

            // Si no estaba atacando, se asegura de que las banderas de ataque esten desactivadas.
            if (!wasAttacking) {
                character.isAttacking = false;
                character.isPerformingSpecialAttack = false;
            }
        // Si la animacion de 'ANGRY_HURT' (para jefes) ha terminado.
        } else if (currentAnimation == CharacterAnimator.AnimationState.ANGRY_HURT && character.characterAnimator.isAnimationFinished(CharacterAnimator.AnimationState.ANGRY_HURT)) {
            // Vuelve al estado de reposo en modo furia.
            character.characterAnimator.setCurrentAnimation(CharacterAnimator.AnimationState.IDLE_RAGE);
            character.isPerformingSpecialAttack = false;
        }
    }
}