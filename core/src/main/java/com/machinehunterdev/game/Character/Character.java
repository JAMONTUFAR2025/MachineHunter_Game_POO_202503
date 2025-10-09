package com.machinehunterdev.game.Character;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

// Clase base para todos los personajes del juego
public class Character 
{
    // Atributos comunes para todos los personajes
    protected int health;
    protected Texture texture; // Textura del personaje

    // ** ATRIBUTOS DE MOVIMIENTO/FISICA **
    protected Vector2 position; // Posición (x, y) del personaje
    protected Vector2 velocity; // Velocidad (vx, vy) del personaje

    protected float speed = 200.0f;     // Velocidad horizontal (píxeles/segundo)
    protected float jumpForce = 400.0f; // Fuerza vertical para el salto
    protected float gravity = -800.0f;  // Aceleración de la gravedad (píxeles/segundo^2)

    // Estados del personaje
    public boolean isMoving;
    public boolean isAttacking;
    public boolean isAlive;
    public boolean onGround; // Para controlar si está en el suelo y puede saltar

    // Constructor
    public Character(int health, Texture texture, float x, float y) 
    {
        this.health = health;
        this.texture = texture;

        // Inicializar vectores de posición y velocidad
        this.position = new Vector2(x, y);
        this.velocity = new Vector2(0, 0);

        this.isMoving = false;
        this.isAttacking = false;
        this.isAlive = true;
        this.onGround = false;
    }

    // ---
    
    // Método principal de actualización de física y lógica del juego
    // El 'delta' es el tiempo transcurrido desde el último frame
    public void update(float delta) 
    {
        // 1. Aplicar gravedad a la velocidad Y
        if (!onGround) {
            velocity.y += gravity * delta;
        }

        // 2. Aplicar velocidad para cambiar la posición
        position.x += velocity.x * delta;
        position.y += velocity.y * delta;

        // - - DETECTAR SUELO - -
        // 3. Lógica de colisión simple con el suelo
        if (position.y < 32) {
            position.y = 32;
            velocity.y = 0;
            onGround = true;
        }
        
        // 4. Reiniciar velocidad horizontal si no se está moviendo
        if (!isMoving) {
            velocity.x = 0;
        }
    }

    // Métodos comunes para todos los personajes
    public void attack() 
    {
        // Lógica de ataque
        isAttacking = true;
    }

    public void takeDamage(int damage) 
    {
        // Lógica para recibir daño
        health -= damage;
        if (health <= 0) 
        {
            isAlive = false;
            health = 0;
        }
    }

    // --- Métodos de Control para el PlayerController ---
    public void moveLeft() {
        velocity.x = -speed;
        isMoving = true;
    }

    public void moveRight() {
        velocity.x = speed;
        isMoving = true;
    }

    public void stopMoving() {
        isMoving = false; // Detiene el flag de movimiento, lo que reinicia velocity.x en update()
    }

    public void jump() {
        if (onGround) {
            velocity.y = jumpForce;
            onGround = false;
        }
    }

    // ** GETTERS ADICIONALES PARA LIBGDX **
    // Necesarios para dibujar el personaje en la pantalla
    public float getX() {
        return position.x;
    }

    public float getY() {
        return position.y;
    }

    public Texture getTexture() {
        return texture;
    }

    // Getters originales
    public int getHealth() 
    {
        return health;
    }

    public boolean isMoving() 
    {
        return isMoving;
    }

    public boolean isAttacking() 
    {
        return isAttacking;
    }

    public boolean isAlive() 
    {
        return isAlive;
    }
}
