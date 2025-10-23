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
        float charWidth = character.getWidth();
        //float charHeight = character.getHeight();
        float charX = character.getX();
        float charY = character.getY();

        // Posición de los pies del personaje
        float feetY = charY;
        float feetLeft = charX;
        float feetRight = charX + charWidth;

        // Verificar colisión con el suelo principal
        if(feetY <= GlobalSettings.GROUND_LEVEL) {
            character.landOn(GlobalSettings.GROUND_LEVEL);
            return;
        }

        // Verificar colisión con plataformas
        for (SolidObject obj : solidObjects) {
            if (obj.isWalkable()) {
                Rectangle platform = obj.getBounds();
                float platformTop = platform.y + platform.height;

                // Solo verificar si el personaje está cayendo
                if (character.velocity.y <= 0) {
                    // Calcular superposición horizontal
                    float overlapLeft = Math.max(feetLeft, platform.x);
                    float overlapRight = Math.min(feetRight, platform.x + platform.width);
                    float overlapWidth = overlapRight - overlapLeft;

                    // Si hay superposición horizontal y el personaje está a punto de aterrizar en la parte superior de la plataforma
                    // (es decir, sus pies están justo por encima o en la parte superior de la plataforma)
                    // La condición charY >= platformTop - 5 y charY <= platformTop + 5 crea una pequeña ventana vertical
                    // para detectar el aterrizaje, evitando que el personaje se "teletransporte" si golpea el lateral.
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
    public abstract void update(float delta, ArrayList<SolidObject> solidObjects, ArrayList<Bullet> bullets, Character playerCharacter);
}