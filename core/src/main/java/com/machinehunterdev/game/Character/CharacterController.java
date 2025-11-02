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

        // Verificar colisión con el suelo principal
        if(charBounds.y <= GlobalSettings.GROUND_LEVEL && character.velocity.y <= 0) {
            character.landOn(GlobalSettings.GROUND_LEVEL);
            return;
        }

        // Verificar colisión con plataformas
        for (SolidObject obj : solidObjects) {
            if (obj.isWalkable() && !character.isFallingThroughPlatform) {
                Rectangle platformBounds = obj.getBounds();
                
                // Solo verificar si el personaje está cayendo y si hay superposición
                if (character.velocity.y <= 0 && charBounds.overlaps(platformBounds)) {
                    float platformTop = platformBounds.y + platformBounds.height;
                    
                    // La condición `charBounds.y >= platformTop - 5` asegura que aterrice encima y no en el lateral.
                    if (charBounds.y >= platformTop - 5) {
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
}