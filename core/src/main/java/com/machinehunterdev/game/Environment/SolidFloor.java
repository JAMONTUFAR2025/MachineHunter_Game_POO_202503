package com.machinehunterdev.game.Environment;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;

// Clase que representa un suelo sólido en el entorno del juego
public class SolidFloor 
{
    // Atributos de la entidad
    private Rectangle bounds; // Representa la posición (x, y) y el tamaño (width, height)
    private Texture texture; // La imagen que se usará para dibujar el suelo
    private boolean isWalkable; // Puede que quieras diferentes tipos de suelos sólidos (e.g., pared, suelo)

    /**
     * Constructor para el suelo sólido.
     * @param x Posición X inicial.
     * @param y Posición Y inicial.
     * @param width Ancho del suelo.
     * @param height Alto del suelo.
     * @param texturePath Ruta del archivo de imagen (ej: "suelo.png").
     * @param walkable Indica si es solo visual o un obstáculo sólido.
     */
    public SolidFloor(float x, float y, float width, float height, String texturePath, boolean walkable) {
        // Inicializa el rectángulo de colisión y posición
        this.bounds = new Rectangle(x, y, width, height); 
        
        // Carga la textura (ASEGÚRATE de disponer de esta imagen en la carpeta 'assets')
        this.texture = new Texture(texturePath); 
        
        this.isWalkable = walkable;
    }

    // --- Métodos de Dibujo (Renderizado) ---
    
    // Método para dibujar el suelo en pantalla
    public void render(SpriteBatch batch) {
        // Dibuja la textura usando la posición y tamaño del rectángulo
        batch.draw(texture, bounds.x, bounds.y, bounds.width, bounds.height);
    }
    
    // Método para liberar recursos (IMPORTANTE en LibGDX)
    public void dispose() {
        texture.dispose();
    }

    // --- Métodos de Colisión y Estado ---

    public boolean isSolid() {
        // Un suelo sólido es aquel que NO es 'walkable' (i.e., es un muro)
        // O puedes cambiar la lógica para que 'isWalkable' sea si se PUEDE caminar sobre él.
        // Asumiendo que quieres un obstáculo:
        return !isWalkable;
    }

    public Rectangle getBounds() {
        return bounds;
    }
    
    // Mantenemos tu método original por consistencia
    public boolean isWalkable() {
        return isWalkable;
    }
}
