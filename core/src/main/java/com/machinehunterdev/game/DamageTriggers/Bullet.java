package com.machinehunterdev.game.DamageTriggers;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

/**
 * Representa una bala disparada por el jugador.
 * Soporta diferentes tipos de armas con comportamientos específicos.
 * @author MachineHunterDev
 */
public class Bullet 
{
    public Vector2 position;
    public Vector2 velocity;
    private Texture texture;
    private WeaponType weaponType;
    private float maxDistance;      // Distancia máxima que recorre la bala
    private float distanceTraveled; // Distancia recorrida actualmente
    private boolean piercing;       // Si la bala puede atravesar enemigos
    private Rectangle bounds;

    /**
     * Constructor de la bala.
     * @param x Posición inicial en X
     * @param y Posición inicial en Y
     * @param seeingRight Dirección de disparo
     * @param weaponType Tipo de arma que disparó la bala
     */
    public Bullet(float x, float y, boolean seeingRight, WeaponType weaponType) {
        this.position = new Vector2(x, y);
        this.weaponType = weaponType;
        
        // Configurar propiedades según el tipo de arma
        configureBullet(seeingRight);
        
        this.bounds = new Rectangle(x, y, texture.getWidth(), texture.getHeight());
    }

    /**
     * Configura las propiedades de la bala según el tipo de arma.
     * @param seeingRight Dirección de disparo
     */
    private void configureBullet(boolean seeingRight) {
        float speed = 0;
        this.maxDistance = 0;
        this.piercing = false;
        String texturePath = "";

        switch (weaponType) {
            case LASER:
                speed = 400f;
                maxDistance = 400f; // Larga distancia
                texturePath = "plasma.png";
                break;
            case ION:
                speed = 300f;
                maxDistance = 100f; // Corta distancia
                texturePath = "plasma.png";
                break;
            case RAILGUN:
                speed = 500f;
                maxDistance = 600f; // Muy larga distancia
                piercing = true; // Puede atravesar enemigos
                texturePath = "plasma.png";
                break;
        }

        this.velocity = new Vector2(seeingRight ? speed : -speed, 0);
        
        // Cargar textura (usa una textura por defecto si no existe)
        try {
            this.texture = new Texture(texturePath);
        } catch (Exception e) {
            // Textura por defecto si no se encuentra la específica
            this.texture = new Texture("plasma.png");
        }
    }

    /**
     * Actualiza la posición de la bala y la distancia recorrida.
     * @param delta Tiempo transcurrido desde el último frame
     * @return true si la bala debe ser eliminada
     */
    public boolean update(float delta) {
        position.x += velocity.x * delta;
        distanceTraveled += Math.abs(velocity.x * delta);
        bounds.setPosition(position.x, position.y);
        
        // Verificar si ha alcanzado la distancia máxima
        return distanceTraveled >= maxDistance;
    }

    /**
     * Dibuja la bala en pantalla.
     * @param batch SpriteBatch para renderizar
     */
    public void draw(SpriteBatch batch) {
        batch.draw(texture, position.x, position.y);
    }

    /**
     * Obtiene el rectángulo de colisión de la bala.
     * @return Rectángulo de colisión
     */
    public Rectangle getBounds() {
        return bounds;
    }

    /**
     * Verifica si la bala puede atravesar enemigos.
     * @return true si es perforante
     */
    public boolean isPiercing() {
        return piercing;
    }

    /**
     * Obtiene el tipo de arma que disparó esta bala.
     * @return Tipo de arma
     */
    public WeaponType getWeaponType() {
        return weaponType;
    }

    /**
     * Libera los recursos de la textura.
     */
    public void dispose() {
        if (texture != null) {
            texture.dispose();
        }
    }
}