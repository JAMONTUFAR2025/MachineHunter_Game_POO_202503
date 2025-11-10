package com.machinehunterdev.game.Environment;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;

/**
 * Clase que representa un suelo solido en el entorno del juego.
 * Esta parece ser una version mas antigua o simplificada para crear objetos solidos,
 * ya que carga su propia textura directamente a partir de una ruta, a diferencia de
 * 'SolidObject' que puede interpretar tipos.
 * 
 * @author MachineHunterDev
 */
public class SolidFloor 
{
    /** La caja de colision que define la posicion y las dimensiones del suelo. */
    private Rectangle bounds;
    
    /** La textura visual del suelo. */
    private Texture texture;
    
    /** Define si los personajes pueden caminar sobre este objeto. */
    private boolean isWalkable;

    /**
     * Constructor para el suelo solido.
     * @param x La posicion inicial en el eje X.
     * @param y La posicion inicial en el eje Y.
     * @param width El ancho del suelo.
     * @param height El alto del suelo.
     * @param texturePath La ruta al archivo de imagen para la textura.
     * @param walkable Verdadero si los personajes pueden caminar sobre este objeto.
     */
    public SolidFloor(float x, float y, float width, float height, String texturePath, boolean walkable) {
        this.bounds = new Rectangle(x, y, width, height); 
        this.texture = new Texture(texturePath); 
        this.isWalkable = walkable;
    }

    /**
     * Renderiza el suelo en la pantalla.
     * @param batch El SpriteBatch utilizado para dibujar.
     */
    public void render(SpriteBatch batch) {
        batch.draw(texture, bounds.x, bounds.y, bounds.width, bounds.height);
    }
    
    /**
     * Libera los recursos de la textura para evitar fugas de memoria.
     */
    public void dispose() {
        texture.dispose();
    }

    /**
     * Comprueba si el objeto es solido (no se puede atravesar).
     * Un objeto es solido si no es 'walkable'.
     * @return Verdadero si es solido.
     */
    public boolean isSolid() {
        return !isWalkable;
    }

    /**
     * Obtiene la caja de colision del objeto.
     * @return El rectangulo de colision.
     */
    public Rectangle getBounds() {
        return bounds;
    }
    
    /**
     * Comprueba si se puede caminar sobre este objeto.
     * @return Verdadero si es caminable.
     */
    public boolean isWalkable() {
        return isWalkable;
    }
}