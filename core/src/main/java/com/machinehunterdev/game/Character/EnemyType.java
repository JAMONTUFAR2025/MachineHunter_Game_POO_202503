package com.machinehunterdev.game.Character;

/**
 * Tipos de enemigos disponibles en el juego.
 * Esta enumeracion define las categorias de enemigos que pueden aparecer.
 * Se utiliza para gestionar la logica de creacion y comportamiento de los enemigos.
 * 
 * @author MachineHunterDev
 */
public enum EnemyType {
    /**
     * Enemigo que patrulla un area determinada.
     * Generalmente se mueve de un punto a otro.
     */
    PATROLLER,

    /**
     * Enemigo que dispara proyectiles al jugador.
     * Puede ser estatico o movil.
     */
    SHOOTER,

    /**
     * Enemigo que vuela y no se ve afectado por la gravedad.
     * Ataca desde el aire.
     */
    FLYING,

    /**
     * Jefe final del juego, con comportamientos y ataques especiales.
     * Representa un desafio mayor para el jugador.
     */
    BOSS_GEMINI,

    /**
     * Otro jefe final del juego, con sus propias habilidades unicas.
     * Ofrece una experiencia de combate diferente.
     */
    BOSS_CHATGPT
}
