package com.machinehunterdev.game.Character;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.machinehunterdev.game.DamageTriggers.Bullet;
import com.machinehunterdev.game.Environment.SolidObject;

import java.util.ArrayList;

/**
 * Gestor central para todos los enemigos en el juego.
 * Esta clase se encarga de crear, almacenar, actualizar y dibujar
 * todos los enemigos presentes en un nivel. Actua como un contenedor
 * y un punto de control para la logica de los enemigos.
 * 
 * @author MachineHunterDev
 */
public class EnemyManager {
    
    // La lista que contiene todas las instancias de enemigos activos en el juego.
    private ArrayList<IEnemy> enemies;

    /**
     * Constructor del gestor de enemigos.
     * Inicializa la lista de enemigos.
     */
    public EnemyManager() {
        enemies = new ArrayList<>();
    }

    /**
     * Anade un nuevo enemigo al gestor.
     * Este metodo actua como una fabrica (factory) para crear diferentes tipos de enemigos
     * segun el 'EnemyType' proporcionado.
     * 
     * @param type El tipo de enemigo a crear (PATROLLER, SHOOTER, etc.).
     * @param character El objeto Character base para el enemigo (contiene salud, posicion, etc.).
     * @param patrolPoints Los puntos de patrullaje para enemigos que se mueven.
     * @param waitTime El tiempo de espera en cada punto de patrullaje.
     * @param shootInterval El intervalo de disparo para enemigos que atacan a distancia.
     * @param shootTime La duracion del ataque para enemigos que disparan.
     */
    public void addEnemy(EnemyType type, Character character, java.util.List<com.machinehunterdev.game.Levels.LevelData.Point> patrolPoints, float waitTime, float shootInterval, float shootTime) {
        character.setEnemyType(type); // Asigna el tipo de enemigo al objeto Character.
        
        // Un switch para crear la instancia correcta del enemigo segun su tipo.
        switch (type) {
            case PATROLLER:
                enemies.add(new PatrollerEnemy(character, (java.util.ArrayList<com.machinehunterdev.game.Levels.LevelData.Point>) patrolPoints, waitTime, type));
                break;
            case SHOOTER:
                enemies.add(new ShooterEnemy(character, shootInterval, shootTime, type));
                break;
            case FLYING:
                // Convierte los puntos de patrullaje a un formato adecuado para enemigos voladores.
                ArrayList<Vector2> flyingPatrolPoints = new ArrayList<>();
                for (com.machinehunterdev.game.Levels.LevelData.Point point : patrolPoints) {
                    flyingPatrolPoints.add(new Vector2(point.x, point.y));
                }
                enemies.add(new FlyingEnemy(character, flyingPatrolPoints, waitTime, type));
                break;
            case BOSS_GEMINI:
                enemies.add(new BossEnemy(character, EnemyType.BOSS_GEMINI));
                break;
            case BOSS_CHATGPT:
                enemies.add(new BossEnemy(character, EnemyType.BOSS_CHATGPT));
                break;
        }
    }

    /**
     * Actualiza la logica de todos los enemigos gestionados (IA, comportamiento).
     * @param delta El tiempo transcurrido desde el ultimo fotograma.
     * @param solidObjects La lista de objetos solidos para las colisiones.
     * @param bullets La lista de balas activas en el juego.
     * @param playerCharacter La referencia al personaje del jugador.
     */
    public void update(float delta, ArrayList<SolidObject> solidObjects, ArrayList<Bullet> bullets, Character playerCharacter) {
        for (IEnemy enemy : enemies) {
            // Los jefes pueden tener una logica de actualizacion mas compleja que necesita la lista de otros enemigos.
            if (enemy instanceof BossEnemy) {
                ((BossEnemy) enemy).getController().update(delta, solidObjects, bullets, playerCharacter, enemies.size(), enemies);
            } else {
                ((BaseEnemy) enemy).getController().update(delta, solidObjects, bullets, playerCharacter, enemies.size());
            }
        }
    }

    /**
     * Pone en pausa a todos los enemigos, deteniendo su movimiento.
     */
    public void pause() {
        for (IEnemy enemy : enemies) {
            enemy.getCharacter().stopMoving();
            enemy.getCharacter().velocity.set(0, 0);
        }
    }

    /**
     * Actualiza las animaciones y la fisica basica de todos los enemigos.
     * Se llama por separado de la logica de la IA para una mejor organizacion.
     * @param delta El tiempo transcurrido desde el ultimo fotograma.
     */
    public void updateCharacterAnimations(float delta) {
        for (IEnemy enemy : enemies) {
            enemy.getCharacter().update(delta);
        }
    }

    /**
     * Dibuja todos los enemigos gestionados en la pantalla.
     * @param batch El SpriteBatch utilizado para el renderizado.
     */
    public void draw(SpriteBatch batch) {
        for (IEnemy enemy : enemies) {
            enemy.draw(batch);
        }
    }

    /**
     * Obtiene la lista completa de enemigos gestionados.
     * @return Una ArrayList de objetos IEnemy.
     */
    public ArrayList<IEnemy> getEnemies() {
        return enemies;
    }

    /**
     * Obtiene el controlador de un enemigo especifico a partir de su objeto Character.
     * @param character El personaje asociado al enemigo cuyo controlador se desea obtener.
     * @return El controlador del enemigo, o null si no se encuentra.
     */
    public CharacterController getController(Character character) {
        for (IEnemy enemy : enemies) {
            if (enemy.getCharacter() == character) {
                return ((BaseEnemy) enemy).getController();
            }
        }
        return null;
    }
}
