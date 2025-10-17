package com.machinehunterdev.game.DamageTriggers;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.machinehunterdev.game.Character.Character;
import java.util.ArrayList;
import java.util.List;

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
    private List<Character> hitEnemies; // Lista de enemigos ya golpeados

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
        this.hitEnemies = new ArrayList<>(); // Inicializar la lista

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
                speed = 200f;
                maxDistance = 400f; // Larga distancia
                texturePath = "Bullets/laser1.png";
                break;
            case ION:
                speed = 150f;
                maxDistance = 100f; // Corta distancia
                texturePath = "Bullets/ion1.png";
                break;
            case RAILGUN:
                speed = 250f;
                maxDistance = 600f; // Muy larga distancia
                piercing = true; // Puede atravesar enemigos
                texturePath = "Bullets/railgun1.png";
                break;
        }

        this.velocity = new Vector2(seeingRight ? speed : -speed, 0);
        // Girar la textura si dispara a la izquierda
        if (!seeingRight) {
            texturePath = texturePath.replace(".png", "_flipped.png");
        }

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
     * Obtiene el daño de la bala.
     * @return El daño de la bala
     */
    public int getDamage() {
        return weaponType.getDamage();
    }

    /**
     * Verifica si un enemigo ya ha sido golpeado por esta bala.
     * @param enemy Enemigo a verificar
     * @return true si ya ha sido golpeado
     */
    public boolean hasHit(Character enemy) {
        return hitEnemies.contains(enemy);
    }

    /**
     * Registra que un enemigo ha sido golpeado.
     * @param enemy Enemigo a añadir a la lista de golpeados
     */
    public void addHitEnemy(Character enemy) {
        hitEnemies.add(enemy);
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