package com.machinehunterdev.game.DamageTriggers;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.machinehunterdev.game.Character.Character;
import com.machinehunterdev.game.Util.SpriteAnimator;

import java.util.ArrayList;
import java.util.List;

/**
 * Representa una bala disparada por el jugador.
 * Soporta diferentes tipos de armas con comportamientos específicos.
 * @author MachineHunterDev
 */
public class Bullet
{
    /* Propiedades para las balas */
    public Vector2 position;
    public Vector2 velocity;
    private SpriteAnimator animator;
    private WeaponType weaponType;
    private float maxDistance;
    private float distanceTraveled;
    private boolean piercing;
    private Rectangle bounds;
    private List<Character> hitEnemies;
    private Character owner;

    /**
     * Constructor de la bala.
     * @param x Posición inicial en X
     * @param y Posición inicial en Y
     * @param seeingRight Dirección de disparo
     * @param weaponType Tipo de arma que disparó la bala
     */
    public Bullet(float x, float y, boolean seeingRight, WeaponType weaponType, Character owner) {
        this.position = new Vector2(x, y);
        this.weaponType = weaponType;
        this.hitEnemies = new ArrayList<>();
        this.owner = owner;

        // Configurar propiedades según el tipo de arma
        configureBullet(seeingRight);

        // Inicializar el rectángulo de colisión
        this.bounds = new Rectangle(x, y, animator.getCurrentSprite().getWidth(), animator.getCurrentSprite().getHeight());
    }

    /**
     * Configura las propiedades de la bala según el tipo de arma.
     * @param seeingRight Dirección de disparo.
     */
    private void configureBullet(boolean seeingRight) {
        float speed = 0;
        this.maxDistance = 0;
        this.piercing = false;
        String basePath = "Bullets/"; // Carpeta base para las texturas de las balas
        String textureName = "";

        switch (weaponType) {
            case LASER:
                speed = 200f;
                maxDistance = 400f;
                textureName = "Laser";
                break;
            case ION:
                speed = 150f;
                maxDistance = 100f;
                textureName = "Ion";
                break;
            case RAILGUN:
                speed = 250f;
                maxDistance = 600f;
                piercing = true;
                textureName = "Railgun";
                break;
            case SHOOTER:
                speed = 100f;
                maxDistance = 350f;
                textureName = "Thunder";
                break;
            default:
        }

        this.velocity = new Vector2(seeingRight ? speed : -speed, 0);

        // Cargar las texturas de la bala según el tipo de arma
        List<Sprite> frames = loadBulletFrames(basePath + textureName, 2, !seeingRight);

        // Inicializar el animador de sprites
        this.animator = new SpriteAnimator(frames, 0.1f, true);
        this.animator.start(); // Start the animation

        // Usamos textura provisional si no se cargaron frames
        if (frames.isEmpty()) {
            List<Sprite> plasmaFrames = new ArrayList<>();
            plasmaFrames.add(new Sprite(new Texture("plasma.png")));
            this.animator = new SpriteAnimator(plasmaFrames, 0.1f, true);
            this.animator.start();
        }
    }

    /**
     * Nuevo método para cargar los frames de la bala.
     * @param basePath Ruta base de las texturas.
     * @param frameCount Número de frames a cargar.
     * @param flipped Indica si las texturas deben ser volteadas horizontalmente.
     * @return Lista de sprites cargados.
     */
    private List<Sprite> loadBulletFrames(String basePath, int frameCount, boolean flipped) {
        List<Sprite> frames = new ArrayList<>();
        for (int i = 1; i <= frameCount; i++) {
            String texturePath = basePath + i + (flipped ? "_flipped.png" : ".png");
            try {
                frames.add(new Sprite(new Texture(texturePath)));
            } catch (Exception e) {
                // Manejar el error de carga de textura
                System.err.println("Warning: Could not load bullet frame: " + texturePath);
                return new ArrayList<>();
            }
        }
        return frames;
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
        
        animator.handleUpdate(delta); // Update the animation

        // Verificar si ha alcanzado la distancia máxima
        return distanceTraveled >= maxDistance;
    }

    /**
     * Dibuja la bala en pantalla.
     * @param batch SpriteBatch para renderizar
     */
    public void draw(SpriteBatch batch) {
        Sprite currentSprite = animator.getCurrentSprite();
        if (currentSprite != null) {
            currentSprite.setPosition(position.x, position.y);
            animator.draw(batch);
        }
    }

    /**
     * Obtiene el personaje que disparó la bala.
     * @return El personaje propietario de la bala.
     */
    public Character getOwner() {
        return owner;
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
        if (animator != null) {
            animator.dispose();
        }
    }
}