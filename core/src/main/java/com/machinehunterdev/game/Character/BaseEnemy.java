package com.machinehunterdev.game.Character;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;

// Clase base abstracta para todos los enemigos del juego.
// Proporciona una implementacion comun de la interfaz IEnemy.
// Contiene referencias al personaje, su controlador y su tipo.
// Esta clase ayuda a evitar la duplicacion de codigo entre los diferentes tipos de enemigos.
public abstract class BaseEnemy implements IEnemy {

    // Referencia al personaje que representa al enemigo (logica de posicion, vida, etc).
    // El objeto Character maneja los atributos fisicos y de estado.
    protected Character character;
    
    // Controlador que define el comportamiento del enemigo (IA, movimiento, etc).
    // El CharacterController implementa la logica de decision del enemigo.
    protected CharacterController controller;
    
    // Tipo de enemigo, usado para identificarlo o aplicar logica especifica.
    // Proviene de la enumeracion EnemyType.
    protected EnemyType enemyType;

    // Constructor que inicializa las propiedades basicas del enemigo.
    // @param character El objeto Character que representa a este enemigo.
    // @param controller El controlador que gestionara el comportamiento del enemigo.
    // @param enemyType El tipo de enemigo.
    public BaseEnemy(Character character, CharacterController controller, EnemyType enemyType) {
        this.character = character;
        this.controller = controller;
        this.enemyType = enemyType;
    }

    // Dibuja el enemigo usando el SpriteBatch proporcionado.
    // Delega el dibujado al objeto Character asociado, que se encarga de renderizar el sprite correcto.
    @Override
    public void draw(SpriteBatch batch) {
        character.draw(batch);
    }

    // Devuelve el objeto Character asociado a este enemigo.
    // Permite acceder a las propiedades del personaje, como su posicion o vida.
    @Override
    public Character getCharacter() {
        return character;
    }

    // Devuelve el controlador que gestiona el comportamiento del enemigo.
    // Es util para manipular o consultar la IA del enemigo desde otras partes del codigo.
    public CharacterController getController() {
        return controller;
    }

    // Devuelve el tipo de enemigo (por ejemplo, comun, volador, jefe, etc).
    // Facilita la identificacion del enemigo para aplicar logicas especificas.
    @Override
    public EnemyType getType() {
        return enemyType;
    }
}