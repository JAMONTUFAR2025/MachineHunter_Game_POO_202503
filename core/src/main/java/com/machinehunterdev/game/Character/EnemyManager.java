package com.machinehunterdev.game.Character;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.machinehunterdev.game.DamageTriggers.Bullet;
import com.machinehunterdev.game.Environment.SolidObject;

import java.util.ArrayList;

/**
 * Gestor de múltiples enemigos en el juego.
 * Permite agregar, actualizar y dibujar enemigos.
 * 
 * @author MachineHunterDev
 */
public class EnemyManager {
    /* Lista de enemigos en el juego */
    private ArrayList<IEnemy> enemies;

    /**
     * Constructor del gestor de enemigos.
     */
    public EnemyManager() {
        enemies = new ArrayList<>();
    }

    /**
     * Agrega un nuevo enemigo al gestor.
     * @param type Tipo de enemigo a agregar.
     * @param character Personaje asociado al enemigo.
     * @param patrolPoints Puntos de patrullaje para el enemigo.
     * @param waitTime Tiempo de espera entre patrullas.
     * @param shootInterval Intervalo de disparo para enemigos que disparan.
     * @param shootTime Duración del ataque para enemigos que disparan.
     */
    public void addEnemy(EnemyType type, Character character, ArrayList<Vector2> patrolPoints, float waitTime, float shootInterval, float shootTime) {
        switch (type) {
            case PATROLLER:
                enemies.add(new PatrollerEnemy(character, patrolPoints, waitTime, type));
                break;
            case SHOOTER:
                enemies.add(new ShooterEnemy(character, shootInterval, shootTime, type));
                break;
            case FLYING:
                enemies.add(new FlyingEnemy(character, patrolPoints, waitTime, type));
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
     * Actualiza todos los enemigos gestionados.
     * @param delta Tiempo transcurrido desde el último frame.
     * @param solidObjects Lista de objetos sólidos para colisiones.
     * @param bullets Lista de balas activas para colisiones.
     * @param playerCharacter El personaje jugador para referencias.
     */
    public void update(float delta, ArrayList<SolidObject> solidObjects, ArrayList<Bullet> bullets, Character playerCharacter) {
        for (IEnemy enemy : enemies) {
            ((BaseEnemy)enemy).getController().update(delta, solidObjects, bullets, playerCharacter);
        }
    }

    public void pause() {
        for (IEnemy enemy : enemies) {
            enemy.getCharacter().stopMoving();
            enemy.getCharacter().velocity.set(0, 0);
        }
    }

    /**
     * Actualiza las animaciones de todos los enemigos gestionados.
     * @param delta Tiempo transcurrido desde el último frame.
     */
    public void updateCharacterAnimations(float delta) {
        for (IEnemy enemy : enemies) {
            enemy.getCharacter().update(delta);
        }
    }

    /**
     * Dibuja todos los enemigos gestionados.
     * @param batch El SpriteBatch utilizado para dibujar.
     */
    public void draw(SpriteBatch batch) {
        for (IEnemy enemy : enemies) {
            enemy.draw(batch);
        }
    }

    /**
     * Obtiene la lista de enemigos gestionados.
     * @return Lista de enemigos.
     */
    public ArrayList<IEnemy> getEnemies() {
        return enemies;
    }

    /**
     * Obtiene el controlador de un enemigo específico.
     * @param character El personaje asociado al enemigo.
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
