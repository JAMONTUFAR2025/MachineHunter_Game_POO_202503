package com.machinehunterdev.game.Environment;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;

/**
 * Clase que representa un suelo sólido en el entorno del juego.
 * Versión antigua que carga su propia textura.
 * 
 * @author MachineHunterDev
 */
public class SolidFloor 
{
    /** Rectángulo de colisión y posición */
    private Rectangle bounds;
    
    /** Textura del suelo */
    private Texture texture;
    
    /** Indica si se puede caminar sobre este objeto */
    private boolean isWalkable;

    /**
     * Constructor para el suelo sólido.
     * @param x Posición X inicial
     * @param y Posición Y inicial  
     * @param width Ancho del suelo
     * @param height Alto del suelo
     * @param texturePath Ruta del archivo de imagen
     * @param walkable Indica si se puede caminar sobre él
     */
    public SolidFloor(float x, float y, float width, float height, String texturePath, boolean walkable) {
        this.bounds = new Rectangle(x, y, width, height); 
        this.texture = new Texture(texturePath); 
        this.isWalkable = walkable;
    }

    /**
     * Renderiza el suelo en pantalla.
     * @param batch SpriteBatch para dibujar
     */
    public void render(SpriteBatch batch) {
        batch.draw(texture, bounds.x, bounds.y, bounds.width, bounds.height);
    }
    
    /**
     * Libera los recursos de la textura.
     */
    public void dispose() {
        texture.dispose();
    }

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