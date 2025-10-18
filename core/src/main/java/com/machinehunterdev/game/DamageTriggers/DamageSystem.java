package com.machinehunterdev.game.DamageTriggers;

import com.machinehunterdev.game.Character.Character;

/**
 * Sistema centralizado para gestionar todo el daño en el juego.
 * Maneja diferentes tipos de daño con efectos específicos.
 * @author MachineHunterDev
 */
public class DamageSystem 
{
    
    /**
     * Aplica daño a un personaje objetivo.
     * @param target Personaje que recibe el daño
     * @param source Personaje que causa el daño (puede ser null)
     * @param damageAmount Cantidad de daño
     * @param damageType Tipo de daño
     * @param knockback Si aplica empuje
     * @param knockbackForce Fuerza del empuje (0.0f si no hay empuje)
     */
    public static void applyDamage(Character target, Character source, int damageAmount, 
                                 DamageType damageType, boolean knockback, float knockbackForce) {
        target.health -= damageAmount;
        if (target.health <= 0) {
            target.isAlive = false;
            target.health = 0;
            target.setFlashRed(false); // Reinicia flashRed al morir
            target.setRedFlashTimer(0f); // Reinicia redFlashTimer al morir
        }

        // Activar efectos visuales
        activateVisualEffects(target);

        // Aplicar empuje si está configurado
        if (knockback && knockbackForce > 0 && source != null) {
            applyKnockback(target, source, knockbackForce);
        }

        // Activar invulnerabilidad
        activateInvulnerability(target);

        // Activar animación de daño
        activateHurtAnimation(target);
    }

    /**
     * Aplica daño sin empuje (ideal para balas y ataques a distancia).
     * @param target Personaje que recibe el daño
     * @param damageAmount Cantidad de daño
     * @param damageType Tipo de daño
     */
    public static void applyDamageNoKnockback(Character target, int damageAmount, DamageType damageType) {
        applyDamage(target, null, damageAmount, damageType, false, 0.0f);
    }

    /**
     * Aplica daño por contacto (con empuje basado en posición relativa).
     * @param target Personaje que recibe el daño
     * @param source Personaje que causa el daño
     * @param damageAmount Cantidad de daño
     */
    public static void applyContactDamage(Character target, Character source, int damageAmount) {
        applyDamage(target, source, damageAmount, DamageType.CONTACT, true, 0.7f);
    }

    /**
     * Aplica daño de proyectil (sin empuje).
     * @param target Personaje que recibe el daño
     * @param damageAmount Cantidad de daño
     */
    public static void applyProjectileDamage(Character target, int damageAmount) {
        applyDamageNoKnockback(target, damageAmount, DamageType.PROJECTILE);
    }

    /**
     * Aplica daño sin activar la invulnerabilidad (ideal para enemigos).
     * @param target Personaje que recibe el daño
     * @param damageAmount Cantidad de daño
     * @param knockback Si aplica empuje
     */
    public static void applyDamageWithoutInvulnerability(Character target, int damageAmount, boolean knockback) {
        if (target == null || !target.isAlive()) {
            return;
        }

        // Aplicar daño
        target.health -= damageAmount;
        if (target.health <= 0) {
            target.isAlive = false;
            target.health = 0;
            target.setFlashRed(false); // Reinicia flashRed al morir
            target.setRedFlashTimer(0f); // Reinicia redFlashTimer al morir
        }

        // Activar efectos visuales
        activateVisualEffects(target);

        // Aplicar empuje si está configurado
        if (knockback) {
            // Simplified knockback logic for this case
            target.isKnockedBack = true;
            target.forceJump(0.5f); // A small hop
            target.velocity.x = target.isSeeingRight() ? -100f : 100f;
        }

        // NO activar invulnerabilidad
        // activateInvulnerability(target);

        // Activar animación de daño
        activateHurtAnimation(target);
    }

    /**
     * Activa los efectos visuales al recibir daño.
     */
    private static void activateVisualEffects(Character target) {
        target.setFlashRed(true);
        target.setRedFlashTimer(0.1f);
    }

    /**
     * Aplica el efecto de empuje al personaje objetivo.
     */
    private static void applyKnockback(Character target, Character source, float knockbackForce) {
        target.isKnockedBack = true;
        target.forceJump(knockbackForce);
        
        // Empuje horizontal basado en la posición relativa
        if (source.getX() < target.getX()) {
            target.velocity.x = 150f;
        } else {
            target.velocity.x = -150f;
        }
    }

    /**
     * Activa la invulnerabilidad temporal.
     */
    private static void activateInvulnerability(Character target) {
        target.setInvulnerable(true);
        target.setInvulnerabilityTimer(5.0f); // Duración fija de 5 segundos
    }

    /**
     * Activa la animación de daño (HURT).
     */
    private static void activateHurtAnimation(Character target) {
        target.setHurt(true);
        target.setHurtTimer(0.1f); // Duración de la animación de daño
    }

    /**
     * Verifica si un personaje puede recibir daño.
     * @param target Personaje objetivo
     * @return true si puede recibir daño
     */
    public static boolean canTakeDamage(Character target) {
        return target != null && target.isAlive() && !target.isInvulnerable();
    }
}
