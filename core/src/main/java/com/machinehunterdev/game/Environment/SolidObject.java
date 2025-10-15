package com.machinehunterdev.game.Environment;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;

/**
 * Clase que representa un objeto sólido en el entorno del juego.
 * Versión optimizada que usa texturas compartidas para mayor eficiencia.
 * 
 * @author MachineHunterDev
 */
public class SolidObject 
{
    /** Rectángulo de colisión y posición */
    private Rectangle bounds;
    
    /** Textura compartida del objeto */
    private Texture texture;
    
    /** Indica si se puede caminar sobre este objeto */
    private boolean isWalkable;

    /**
     * Constructor para el objeto sólido.
     * @param x Posición X inicial
     * @param y Posición Y inicial
     * @param width Ancho del objeto
     * @param height Alto del objeto
     * @param texture Textura compartida a usar
     * @param walkable Indica si se puede caminar sobre él
     */
    public SolidObject(float x, float y, float width, float height, Texture texture, boolean walkable) {
        this.bounds = new Rectangle(x, y, width, height); 
        this.texture = texture;
        this.isWalkable = walkable;
    }

    /**
     * Renderiza el objeto en pantalla.
     * @param batch SpriteBatch para dibujar
     */
    public void render(SpriteBatch batch) {
        batch.draw(texture, bounds.x, bounds.y, bounds.width, bounds.height);
    }
    
    // Nota: No tiene método dispose() porque la textura es compartida

    /**
     * Verifica si el objeto es sólido (no se puede atravesar).
     * @return true si es sólido
     */
    public boolean isSolid() {
        return !isWalkable;
    }

    /**
     * Obtiene el rectángulo de colisión.
     * @return Rectángulo de colisión
     */
    public Rectangle getBounds() {
        return bounds;
    }
    
    /**
     * Verifica si se puede caminar sobre este objeto.
     * @return true si es caminable
     */
    public boolean isWalkable() {
        return isWalkable;
    }
}