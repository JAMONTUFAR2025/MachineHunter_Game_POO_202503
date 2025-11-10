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
 * Representa un proyectil o bala en el juego.
 * Cada bala tiene una posicion, velocidad, tipo de arma, y logica de colision.
 * Es responsable de su propio movimiento, animacion y ciclo de vida.
 * 
 * @author MachineHunterDev
 */
public class Bullet
{
    // === PROPIEDADES DE LA BALA ===
    public Vector2 position; // Posicion actual de la bala.
    public Vector2 velocity; // Velocidad y direccion de la bala.
    private SpriteAnimator animator; // Animador para el sprite de la bala.
    private WeaponType weaponType; // El tipo de arma que disparo esta bala.
    private float maxDistance; // La distancia maxima que la bala puede recorrer antes de desaparecer.
    private float distanceTraveled; // La distancia que la bala ha recorrido hasta ahora.
    private boolean piercing; // Si es verdadero, la bala puede atravesar a multiples enemigos.
    private Rectangle bounds; // La caja de colision de la bala.
    private List<Character> hitEnemies; // Una lista de los enemigos que ya han sido golpeados por esta bala (para balas perforantes).
    private Character owner; // El personaje que disparo la bala.

    /**
     * Constructor principal de la bala.
     * @param x La posicion inicial en el eje X.
     * @param y La posicion inicial en el eje Y.
     * @param velocity El vector de velocidad (direccion y magnitud) de la bala.
     * @param weaponType El tipo de arma que disparo la bala.
     * @param owner El personaje que disparo la bala.
     */
    public Bullet(float x, float y, Vector2 velocity, WeaponType weaponType, Character owner) {
        this.position = new Vector2(x, y);
        this.velocity = velocity;
        this.weaponType = weaponType;
        this.hitEnemies = new ArrayList<>();
        this.owner = owner;

        // Configura las propiedades de la bala (distancia, animacion, etc.) segun el tipo de arma.
        configureBullet(velocity.x > 0);

        // Inicializa la caja de colision.
        this.bounds = new Rectangle(x, y, animator.getCurrentSprite().getWidth(), animator.getCurrentSprite().getHeight());
    }

    /**
     * Constructor secundario para balas que solo se mueven horizontalmente.
     * @param x Posicion inicial en X.
     * @param y Posicion inicial en Y.
     * @param seeingRight La direccion de disparo (true para derecha, false para izquierda).
     * @param weaponType El tipo de arma que disparo la bala.
     * @param owner El personaje que disparo la bala.
     */
    public Bullet(float x, float y, boolean seeingRight, WeaponType weaponType, Character owner) {
        this(x, y, new Vector2(seeingRight ? 200f : -200f, 0), weaponType, owner);
    }

    /**
     * Configura las propiedades especificas de la bala segun su tipo de arma.
     * @param seeingRight La direccion en la que se dispara la bala.
     */
    private void configureBullet(boolean seeingRight) {
        this.maxDistance = 0;
        this.piercing = false;
        String basePath = "Bullets/"; // Carpeta base para las texturas de las balas.
        String textureName = "";

        switch (weaponType) {
            case LASER:
                maxDistance = 400f;
                textureName = "Laser";
                break;
            case ION:
                maxDistance = 100f;
                textureName = "Ion";
                break;
            case RAILGUN:
                maxDistance = 600f;
                piercing = true; // El Railgun es perforante.
                textureName = "Railgun";
                break;
            case SHOOTER:
                maxDistance = 350f;
                textureName = "Thunder";
                break;
            default:
                // No se hace nada para otros tipos.
        }

        // Carga los fotogramas de la animacion de la bala.
        List<Sprite> frames = loadBulletFrames(basePath + textureName, 2, !seeingRight);

        // Inicializa el animador de sprites.
        this.animator = new SpriteAnimator(frames, 0.1f, true);
        this.animator.start();

        // Si la carga de fotogramas falla, usa una textura de respaldo.
        if (frames.isEmpty()) {
            List<Sprite> plasmaFrames = new ArrayList<>();
            plasmaFrames.add(new Sprite(new Texture("plasma.png")));
            this.animator = new SpriteAnimator(plasmaFrames, 0.1f, true);
            this.animator.start();
        }
    }

    /**
     * Carga los fotogramas de la animacion de la bala desde los archivos.
     * @param basePath La ruta base de las texturas.
     * @param frameCount El numero de fotogramas a cargar.
     * @param flipped Indica si las texturas deben ser volteadas horizontalmente.
     * @return Una lista de los sprites cargados.
     */
    private List<Sprite> loadBulletFrames(String basePath, int frameCount, boolean flipped) {
        List<Sprite> frames = new ArrayList<>();
        for (int i = 1; i <= frameCount; i++) {
            String texturePath = basePath + i + (flipped ? "_flipped.png" : ".png");
            try {
                frames.add(new Sprite(new Texture(texturePath)));
            } catch (Exception e) {
                System.err.println("Advertencia: No se pudo cargar el fotograma de la bala: " + texturePath);
                return new ArrayList<>();
            }
        }
        return frames;
    }

    /**
     * Actualiza la posicion de la bala y comprueba si debe ser eliminada.
     * @param delta El tiempo transcurrido desde el ultimo fotograma.
     * @return Verdadero si la bala ha alcanzado su distancia maxima y debe ser eliminada.
     */
    public boolean update(float delta) {
        position.x += velocity.x * delta;
        position.y += velocity.y * delta;
        distanceTraveled += velocity.len() * delta;
        bounds.setPosition(position.x, position.y);
        
        animator.handleUpdate(delta); // Actualiza la animacion.

        // Comprueba si ha alcanzado la distancia maxima.
        return distanceTraveled >= maxDistance;
    }

    /**
     * Dibuja la bala en la pantalla.
     * @param batch El SpriteBatch utilizado para el renderizado.
     */
    public void draw(SpriteBatch batch) {
        Sprite currentSprite = animator.getCurrentSprite();
        if (currentSprite != null) {
            currentSprite.setPosition(position.x, position.y);
            animator.draw(batch);
        }
    }

    /**
     * Obtiene el personaje que disparo la bala.
     * @return El personaje propietario de la bala.
     */
    public Character getOwner() {
        return owner;
    }

    /**
     * Obtiene la caja de colision de la bala.
     * @return El rectangulo de colision.
     */
    public Rectangle getBounds() {
        return bounds;
    }

    /**
     * Comprueba si la bala es perforante (puede golpear a varios enemigos).
     * @return Verdadero si es perforante.
     */
    public boolean isPiercing() {
        return piercing;
    }

    /**
     * Obtiene el tipo de arma que disparo esta bala.
     * @return El tipo de arma.
     */
    public WeaponType getWeaponType() {
        return weaponType;
    }

    /**
     * Obtiene la cantidad de dano que inflige la bala.
     * @return La cantidad de dano.
     */
    public int getDamage() {
        return weaponType.getDamage();
    }

    /**
     * Comprueba si un enemigo especifico ya ha sido golpeado por esta bala.
     * Util para las balas perforantes.
     * @param enemy El enemigo a comprobar.
     * @return Verdadero si ya ha sido golpeado.
     */
    public boolean hasHit(Character enemy) {
        return hitEnemies.contains(enemy);
    }

    /**
     * Registra que un enemigo ha sido golpeado por esta bala.
     * @param enemy El enemigo que ha sido anadido a la lista de golpeados.
     */
    public void addHitEnemy(Character enemy) {
        hitEnemies.add(enemy);
    }

    /**
     * Libera los recursos (texturas) utilizados por la animacion de la bala.
     */
    public void dispose() {
        if (animator != null) {
            animator.dispose();
        }
    }
}