package com.machinehunterdev.game.Character;

import java.util.ArrayList;
import com.badlogic.gdx.math.Rectangle;
import com.machinehunterdev.game.Environment.SolidObject;

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
        // Asumimos que no está en el suelo hasta que se demuestre lo contrario
        character.onGround = false;

        // Obtener dimensiones y posición del personaje
        float charWidth = character.getWidth();
        float charHeight = character.getHeight();
        float charX = character.getX();
        float charY = character.getY();

        // Recorrer todos los objetos sólidos
        for (SolidObject obj : solidObjects) {
            // Solo considerar objetos sobre los que se puede caminar
            if (obj.isWalkable()) {
                Rectangle platform = obj.getBounds();

                // Solo verificar colisión si el personaje está cayendo (velocidad Y <= 0)
                if (character.velocity.y <= 0) {
                    float platformTop = platform.y + platform.height;

                    // Margen de error para evitar "temblores" al aterrizar
                    if (charY <= platformTop + 2f && 
                        charY + charHeight > platform.y && // Superposición en eje Y
                        charX + charWidth > platform.x &&  // Superposición en eje X
                        charX < platform.x + platform.width) {

                        // Hacer que el personaje aterrice en la plataforma
                        character.landOn(platformTop);
                        return; // Salir al encontrar la primera plataforma válida
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