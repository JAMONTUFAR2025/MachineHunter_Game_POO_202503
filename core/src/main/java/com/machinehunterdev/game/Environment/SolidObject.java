package com.machinehunterdev.game.Environment;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;



public class SolidObject 
{
    private Rectangle bounds;
    private Texture texture;
    private boolean isWalkable;

    
    public SolidObject(float x, float y, String typeString, boolean walkable) {
        if (typeString.startsWith("Platform")) {
            PlatformType type = PlatformType.parse(typeString);
            if (type != null) {
                this.bounds = new Rectangle(x, y, type.width, type.height); 
                this.texture = new Texture(type.texturePath);
                this.isWalkable = walkable;
            } else {
                this.bounds = new Rectangle(x, y, 0, 0);
                this.isWalkable = false;
            }
        } else if (typeString.startsWith("Chain")) {
            ChainType type = ChainType.parse(typeString);
            if (type != null) {
                System.out.println("Loading chain texture: " + type.texturePath);
                this.bounds = new Rectangle(x, y, type.width, type.height); 
                this.texture = new Texture(type.texturePath);
                this.isWalkable = walkable;
            } else {
                this.bounds = new Rectangle(x, y, 0, 0);
                this.isWalkable = false;
            }
        } else {
            this.bounds = new Rectangle(x, y, 0, 0);
            this.isWalkable = false;
        }
    }

    /**
     * Constructor para objetos definidos explícitamente.
     * @param x Posición X inicial
     * @param y Posición Y inicial
     * @param width Ancho del objeto
     * @param height Alto del objeto
     * @param texture Textura a usar
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
        if (texture != null) {
            batch.draw(texture, bounds.x, bounds.y, bounds.width, bounds.height);
        }
    }
    
    /**
     * Libera los recursos de la textura.
     */
    public void dispose() {
        if (texture != null) {
            texture.dispose();
        }
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