package com.machinehunterdev.game.Character;

import java.util.ArrayList;
import com.badlogic.gdx.math.Rectangle;
import com.machinehunterdev.game.Environment.SolidObject;
import com.machinehunterdev.game.Gameplay.GlobalSettings;

/**
 * Clase base abstracta para controladores de personajes.
 * Define la lógica común de colisiones y actualización.
 * Se extiende para crear controladores específicos (jugador, enemigos, etc.).
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
     * Solo detecta colisiones desde arriba (para aterrizar en plataformas).
     * @param solidObjects Lista de objetos sólidos en el nivel.
     */
    protected void checkCollisions(ArrayList<SolidObject> solidObjects) {
        character.onGround = false;

        float charWidth = character.getWidth();
        float charHeight = character.getHeight();
        float charX = character.getX();
        float charY = character.getY();

        // Posición de los pies
        float feetY = charY;
        float feetLeft = charX;
        float feetRight = charX + charWidth;

        // Caer en el suelo, no en una plataforma
        if(feetY <= GlobalSettings.GROUND_LEVEL) {
            character.landOn(GlobalSettings.GROUND_LEVEL);
            return;
        }

        for (SolidObject obj : solidObjects) {
            if (obj.isWalkable()) {
                Rectangle platform = obj.getBounds();
                float platformTop = platform.y + platform.height;

                if (character.velocity.y <= 0) {
                    // Verificar que los pies estén en la zona de aterrizaje
                    if (feetY <= platformTop + 2f && feetY >= platformTop - 5f) {
                        // Verificar superposición horizontal significativa
                        float overlapLeft = Math.max(feetLeft, platform.x);
                        float overlapRight = Math.min(feetRight, platform.x + platform.width);
                        float overlapWidth = overlapRight - overlapLeft;

                        // Aterrizar solo si hay superposición significativa (al menos 1 píxel)
                        if (overlapWidth > 0) {
                            character.landOn(platformTop);
                            return;
                        }
                    }
                }
            }
        }
}

    /**
     * Método abstracto que debe implementar cada controlador específico.
     * Contiene la lógica de actualización por frame (entrada, IA, etc.).
     * @param delta Tiempo transcurrido desde el último frame.
     * @param solidObjects Lista de objetos sólidos para colisiones.
     */
    public abstract void update(float delta, ArrayList<SolidObject> solidObjects);
}