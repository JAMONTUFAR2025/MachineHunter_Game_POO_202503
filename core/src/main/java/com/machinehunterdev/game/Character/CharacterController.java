package com.machinehunterdev.game.Character;

import java.util.ArrayList;
import com.badlogic.gdx.math.Rectangle;
import com.machinehunterdev.game.DamageTriggers.Bullet;
import com.machinehunterdev.game.Environment.SolidObject;
import com.machinehunterdev.game.Gameplay.GlobalSettings;

/**
 * Clase base abstracta para controladores de personajes.
 * Define la lógica común de colisiones y actualización.
 * Se extiende para crear controladores específicos (jugador, enemigos, etc.).
 * 
 * @author MachineHunterDev
 */
public abstract class CharacterController {
    /** Referencia al personaje que este controlador maneja */
    public Character character;

    /**
     * Constructor que vincula el controlador con un personaje.
     * @param character El personaje a controlar.
     */
    public CharacterController(Character character) {
        this.character = character;
    }

    /**
     * Verifica colisiones del personaje con objetos sólidos del entorno.
     * Detecta colisiones desde arriba para aterrizar en plataformas y suelo.
     * @param solidObjects Lista de objetos sólidos en el nivel.
     */
    protected void checkCollisions(ArrayList<SolidObject> solidObjects) {
        Rectangle charBounds = character.getBounds();
        float charWidth = charBounds.width;
        float charX = charBounds.x;
        float charY = charBounds.y;

        // Posición de los pies del personaje (usando el hitbox)
        float feetY = charY;
        float feetLeft = charX;
        float feetRight = charX + charWidth;

        // Verificar colisión con el suelo principal
        if(feetY <= GlobalSettings.GROUND_LEVEL && character.velocity.y <= 0) {
            character.landOn(GlobalSettings.GROUND_LEVEL);
            return;
        }

        // Verificar colisión con plataformas
        for (SolidObject obj : solidObjects) {
            if (obj.isWalkable() && !character.isFallingThroughPlatform) {
                Rectangle platform = obj.getBounds();
                float platformTop = platform.y + platform.height;

                // Solo verificar si el personaje está cayendo
                if (character.velocity.y <= 0) {
                    // Calcular superposición horizontal
                    float overlapLeft = Math.max(feetLeft, platform.x);
                    float overlapRight = Math.min(feetRight, platform.x + platform.width);
                    float overlapWidth = overlapRight - overlapLeft;

                    // Si hay superposición horizontal y el personaje está a punto de aterrizar en la parte superior de la plataforma
                    if (overlapWidth > 0 && charY >= platformTop - 5 && charY <= platformTop + 5) {
                        character.landOn(platformTop);
                        return;
                    }
                }
            }
        }

        // Si no hay colisión, el personaje está en el aire
        character.onGround = false;
    }

    /**
     * Método abstracto que debe implementar cada controlador específico.
     * Contiene la lógica de actualización por frame (entrada, IA, etc.).
     * @param delta Tiempo transcurrido desde el último frame.
     * @param solidObjects Lista de objetos sólidos para colisiones.
     * @param bullets Lista de balas activas para colisiones.
     */
    public abstract void update(float delta, ArrayList<SolidObject> solidObjects, ArrayList<Bullet> bullets, Character playerCharacter, int enemyCount);

    protected void handleHurtAnimation() {
        CharacterAnimator.AnimationState currentAnimation = character.characterAnimator.getCurrentState();
        if (currentAnimation == CharacterAnimator.AnimationState.HURT && character.characterAnimator.isAnimationFinished(CharacterAnimator.AnimationState.HURT)) {
            CharacterAnimator.AnimationState previousState = character.characterAnimator.getPreviousState();
            character.characterAnimator.resumeAnimation(previousState);

            // Reset attack flags if we are not resuming an attack
            boolean wasAttacking = previousState == CharacterAnimator.AnimationState.ATTACK ||
                                   previousState == CharacterAnimator.AnimationState.ATTACK1 ||
                                   previousState == CharacterAnimator.AnimationState.ATTACK2 ||
                                   previousState == CharacterAnimator.AnimationState.SUMMON;

            if (!wasAttacking) {
                character.isAttacking = false;
                character.isPerformingSpecialAttack = false;
            }
        } else if (currentAnimation == CharacterAnimator.AnimationState.ANGRY_HURT && character.characterAnimator.isAnimationFinished(CharacterAnimator.AnimationState.ANGRY_HURT)) {
            character.characterAnimator.setCurrentAnimation(CharacterAnimator.AnimationState.IDLE_RAGE);
            character.isPerformingSpecialAttack = false;
        }
    }
}