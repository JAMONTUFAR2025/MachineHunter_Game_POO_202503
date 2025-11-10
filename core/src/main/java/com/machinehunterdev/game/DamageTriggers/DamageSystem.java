package com.machinehunterdev.game.DamageTriggers;

import com.machinehunterdev.game.Character.Character;

/**
 * Sistema centralizado para gestionar la aplicacion de dano en el juego.
 * Esta clase estatica proporciona metodos para aplicar diferentes tipos de dano
 * a los personajes, manejando la reduccion de salud, los efectos visuales,
 * el empuje (knockback) y la invulnerabilidad.
 * 
 * @author MachineHunterDev
 */
public class DamageSystem 
{
    
    /**
     * Metodo principal y mas generico para aplicar dano a un personaje.
     * @param target El personaje que recibe el dano.
     * @param source El personaje que causa el dano (puede ser null si la fuente es el entorno).
     * @param damageAmount La cantidad de dano a infligir.
     * @param damageType El tipo de dano (CONTACT, PROJECTILE, etc.).
     * @param knockback Verdadero si se debe aplicar un efecto de empuje.
     * @param knockbackForce La fuerza del empuje.
     */
    public static void applyDamage(Character target, Character source, int damageAmount, 
                                 DamageType damageType, boolean knockback, float knockbackForce) {
        // Reduce la salud del objetivo.
        target.health -= damageAmount;
        if (target.health <= 0) {
            target.isAlive = false;
            target.health = 0;
            // Asegura que los efectos visuales de dano se detengan al morir.
            target.setFlashTransparent(false);
            target.setTransparentFlashTimer(0f);
        }

        // Activa los efectos visuales de recibir dano (como un parpadeo).
        activateVisualEffects(target);

        // Aplica el empuje si esta configurado y hay una fuente de dano.
        if (knockback && knockbackForce > 0 && source != null) {
            applyKnockback(target, source, knockbackForce);
        }

        // Activa un breve periodo de invulnerabilidad para evitar dano multiple instantaneo.
        activateInvulnerability(target);

        // Activa la animacion de "herido" (HURT).
        activateHurtAnimation(target);
    }

    /**
     * Metodo de conveniencia para aplicar dano sin empuje.
     * Ideal para proyectiles y ataques a distancia.
     * @param target El personaje que recibe el dano.
     * @param damageAmount La cantidad de dano.
     * @param damageType El tipo de dano.
     */
    public static void applyDamageNoKnockback(Character target, int damageAmount, DamageType damageType) {
        applyDamage(target, null, damageAmount, damageType, false, 0.0f);
    }

    /**
     * Metodo de conveniencia para aplicar dano por contacto fisico.
     * Siempre aplica un empuje basado en la posicion relativa de los personajes.
     * @param target El personaje que recibe el dano.
     * @param source El personaje que causa el dano.
     * @param damageAmount La cantidad de dano.
     */
    public static void applyContactDamage(Character target, Character source, int damageAmount) {
        applyDamage(target, source, damageAmount, DamageType.CONTACT, true, 0.7f);
    }

    /**
     * Metodo de conveniencia para aplicar dano de proyectil.
     * Es funcionalmente similar a 'applyDamageNoKnockback'.
     * @param target El personaje que recibe el dano.
     * @param damageAmount La cantidad de dano.
     */
    public static void applyProjectileDamage(Character target, int damageAmount) {
        applyDamageNoKnockback(target, damageAmount, DamageType.PROJECTILE);
    }

    /**
     * Aplica dano sin activar el periodo de invulnerabilidad.
     * Util para ciertos tipos de enemigos o ataques que deben poder golpear repetidamente.
     * @param target El personaje que recibe el dano.
     * @param damageAmount La cantidad de dano.
     * @param knockback Verdadero si se debe aplicar empuje.
     */
    public static void applyDamageWithoutInvulnerability(Character target, int damageAmount, boolean knockback) {
        if (target == null || !target.isAlive()) {
            return;
        }

        // Aplica el dano.
        target.health -= damageAmount;
        if (target.health <= 0) {
            target.isAlive = false;
            target.health = 0;
            target.setFlashTransparent(false);
            target.setTransparentFlashTimer(0f);
        }

        // Activa los efectos visuales.
        activateVisualEffects(target);

        // Aplica un empuje simplificado si esta configurado.
        if (knockback) {
            target.isKnockedBack = true;
            target.forceJump(0.5f); // Un pequeno salto hacia arriba.
            target.velocity.x = target.isSeeingRight() ? -100f : 100f;
        }

        // NO se activa la invulnerabilidad en este metodo.

        // Activa la animacion de "herido".
        activateHurtAnimation(target);
    }

    /**
     * Activa los efectos visuales de parpadeo en el personaje que recibe dano.
     * @param target El personaje objetivo.
     */
    private static void activateVisualEffects(Character target) {
        target.setFlashTransparent(true);
        target.setTransparentFlashTimer(0.1f); // Duracion del parpadeo.
    }

    /**
     * Aplica el efecto de empuje (knockback) al personaje objetivo.
     * @param target El personaje que sera empujado.
     * @param source El personaje que causa el empuje.
     * @param knockbackForce La fuerza del empuje.
     */
    private static void applyKnockback(Character target, Character source, float knockbackForce) {
        target.isKnockedBack = true;
        target.forceJump(knockbackForce); // Empuja al personaje hacia arriba.
        
        // Empuja al personaje horizontalmente en la direccion opuesta a la fuente del dano.
        if (source.getX() < target.getX()) {
            target.velocity.x = 150f;
        } else {
            target.velocity.x = -150f;
        }
    }

    /**
     * Activa un periodo de invulnerabilidad temporal en el personaje.
     * @param target El personaje objetivo.
     */
    private static void activateInvulnerability(Character target) {
        target.setInvulnerable(true);
        target.setInvulnerabilityTimer(3.0f); // Duracion fija de 3 segundos.
    }

    /**
     * Activa la animacion de "herido" (HURT) en el personaje.
     * @param target El personaje objetivo.
     */
    private static void activateHurtAnimation(Character target) {
        // Comprueba si el jefe esta en fase dos para usar la animacion de 'ANGRY_HURT' si es necesario.
        if (target.isBossInPhaseTwo()) {
            target.setHurt(true);
            target.setHurtTimer(0.1f); // Duracion de la animacion de dano.
        } else {
            target.setHurt(true);
            target.setHurtTimer(0.1f); // Duracion de la animacion de dano.
        }
    }

    /**
     * Comprueba si un personaje puede recibir dano en su estado actual.
     * @param target El personaje a comprobar.
     * @return Verdadero si el personaje esta vivo y no es invulnerable.
     */
    public static boolean canTakeDamage(Character target) {
        return target != null && target.isAlive() && !target.isInvulnerable();
    }
}
