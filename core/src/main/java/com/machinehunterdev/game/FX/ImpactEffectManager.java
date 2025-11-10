package com.machinehunterdev.game.FX;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.machinehunterdev.game.DamageTriggers.WeaponType;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

/**
 * Gestor para los efectos de impacto en el juego.
 * Esta clase se encarga de administrar el ciclo de vida de multiples efectos de impacto,
 * desde su creacion hasta su eliminacion. Carga previamente las animaciones para
 * optimizar el rendimiento.
 * 
 * @author MachineHunterDev
 */
public class ImpactEffectManager {
    
    // Lista que contiene todos los efectos de impacto activos actualmente en pantalla.
    private List<ImpactEffect> activeEffects;
    
    // Un mapa que almacena las secuencias de fotogramas (sprites) para cada tipo de arma.
    // Esto evita tener que cargar las texturas cada vez que se crea un efecto.
    private Map<WeaponType, List<Sprite>> impactFrames;
    
    // La duracion de cada fotograma en las animaciones de impacto.
    private float frameDuration;

    /**
     * Constructor del gestor de efectos de impacto.
     * @param frameDuration La duracion, en segundos, de cada fotograma de la animacion.
     */
    public ImpactEffectManager(float frameDuration) {
        this.activeEffects = new ArrayList<>();
        this.frameDuration = frameDuration;
        this.impactFrames = new EnumMap<>(WeaponType.class);

        // Carga previamente los fotogramas de las animaciones de impacto para cada tipo de arma.
        impactFrames.put(WeaponType.LASER, loadSpriteFrames("FX/LaserImpact", 4));
        impactFrames.put(WeaponType.ION, loadSpriteFrames("FX/IonImpact", 4));
        impactFrames.put(WeaponType.RAILGUN, loadSpriteFrames("FX/RailgunImpact", 4));
        impactFrames.put(WeaponType.SHOOTER, loadSpriteFrames("FX/ShooterImpact", 4));
        impactFrames.put(WeaponType.PATROLLER, loadSpriteFrames("FX/PatrollerImpact", 4));
        impactFrames.put(WeaponType.FLYING, loadSpriteFrames("FX/FlyingImpact", 4));
    }

    /**
     * Carga una secuencia de fotogramas (sprites) desde los archivos de imagen.
     * @param basePath La ruta base de los archivos de sprites (sin el numero de fotograma ni la extension).
     * @param frameCount El numero de fotogramas que componen la animacion.
     * @return Una lista de los sprites cargados.
     */
    private List<Sprite> loadSpriteFrames(String basePath, int frameCount) {
        List<Sprite> frames = new ArrayList<>();
        for (int i = 1; i <= frameCount; i++) {
            frames.add(new Sprite(new Texture(basePath + i + ".png")));
        }
        return frames;
    }

    /**
     * Crea un nuevo efecto de impacto en la posicion especificada.
     * @param x La posicion en el eje X donde se creara el efecto.
     * @param y La posicion en el eje Y donde se creara el efecto.
     * @param weaponType El tipo de arma que causo el impacto, para seleccionar la animacion correcta.
     */
    public void createImpact(float x, float y, WeaponType weaponType) {
        List<Sprite> frames = impactFrames.get(weaponType);
        if (frames != null && !frames.isEmpty()) {
            activeEffects.add(new ImpactEffect(x, y, frames, frameDuration));
        }
    }

    /**
     * Actualiza todos los efectos de impacto activos.
     * Elimina los efectos que ya han terminado su animacion.
     * @param delta El tiempo transcurrido desde el ultimo fotograma.
     */
    public void update(float delta) {
        // Itera en orden inverso para poder eliminar elementos de la lista de forma segura.
        for (int i = activeEffects.size() - 1; i >= 0; i--) {
            ImpactEffect effect = activeEffects.get(i);
            effect.update(delta);
            if (effect.isFinished()) {
                activeEffects.remove(i);
            }
        }
    }

    /**
     * Dibuja todos los efectos de impacto activos en la pantalla.
     * @param batch El SpriteBatch utilizado para el renderizado.
     */
    public void draw(SpriteBatch batch) {
        for (ImpactEffect effect : activeEffects) {
            effect.draw(batch);
        }
    }

    /**
     * Libera todos los recursos (texturas) utilizados por los efectos de impacto.
     * Es importante llamar a este metodo para evitar fugas de memoria.
     */
    public void dispose() {
        // Libera las texturas de todos los fotogramas cargados.
        for (List<Sprite> frames : impactFrames.values()) {
            for (Sprite frame : frames) {
                frame.getTexture().dispose();
            }
        }
    }
}
