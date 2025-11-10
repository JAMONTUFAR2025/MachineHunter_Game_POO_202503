package com.machinehunterdev.game.Environment;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;

/**
 * Representa un objeto solido en el entorno del juego.
 * Puede ser una plataforma sobre la que se puede caminar, una pared, o un elemento decorativo.
 * Esta clase actua como una fabrica que puede crear diferentes tipos de objetos
 * (como plataformas o cadenas) a partir de una cadena de texto descriptiva.
 * 
 * @author MachineHunterDev
 */
public class SolidObject 
{
    private Rectangle bounds; // La caja de colision y posicion del objeto.
    private Texture texture; // La textura visual del objeto.
    private boolean isWalkable; // Define si los personajes pueden caminar sobre este objeto.

    /**
     * Constructor que crea un objeto solido a partir de una cadena de tipo.
     * Analiza la cadena para determinar si es una plataforma o una cadena y la configura adecuadamente.
     * @param x La posicion inicial en el eje X.
     * @param y La posicion inicial en el eje Y.
     * @param typeString La cadena que describe el tipo de objeto (ej. "Platform_Red_Small").
     * @param walkable Verdadero si los personajes pueden caminar sobre el objeto.
     */
    public SolidObject(float x, float y, String typeString, boolean walkable) {
        if (typeString.startsWith("Platform")) {
            PlatformType type = PlatformType.parse(typeString);
            if (type != null) {
                this.bounds = new Rectangle(x, y, type.width, type.height); 
                this.texture = new Texture(type.texturePath);
                this.isWalkable = walkable;
            } else {
                // Si el tipo no es valido, crea un objeto vacio para evitar errores.
                this.bounds = new Rectangle(x, y, 0, 0);
                this.isWalkable = false;
            }
        } else if (typeString.startsWith("Chain")) {
            ChainType type = ChainType.parse(typeString);
            if (type != null) {
                System.out.println("Cargando textura de cadena: " + type.texturePath);
                this.bounds = new Rectangle(x, y, type.width, type.height); 
                this.texture = new Texture(type.texturePath);
                this.isWalkable = walkable;
            } else {
                this.bounds = new Rectangle(x, y, 0, 0);
                this.isWalkable = false;
            }
        } else {
            // Si el tipo de string no es reconocido, crea un objeto vacio.
            this.bounds = new Rectangle(x, y, 0, 0);
            this.isWalkable = false;
        }
    }

    /**
     * Constructor para crear un objeto solido con propiedades definidas explicitamente.
     * @param x Posicion inicial en X.
     * @param y Posicion inicial en Y.
     * @param width Ancho del objeto.
     * @param height Alto del objeto.
     * @param texture La textura a usar.
     * @param walkable Indica si se puede caminar sobre el.
     */
    public SolidObject(float x, float y, float width, float height, Texture texture, boolean walkable) {
        this.bounds = new Rectangle(x, y, width, height); 
        this.texture = texture;
        this.isWalkable = walkable;
    }

    /**
     * Renderiza el objeto en la pantalla.
     * @param batch El SpriteBatch utilizado para dibujar.
     */
    public void render(SpriteBatch batch) {
        if (texture != null) {
            batch.draw(texture, bounds.x, bounds.y, bounds.width, bounds.height);
        }
    }
    
    /**
     * Libera los recursos de la textura para evitar fugas de memoria.
     */
    public void dispose() {
        if (texture != null) {
            texture.dispose();
        }
    }

    /**
     * Comprueba si el objeto es solido (es decir, no se puede atravesar).
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