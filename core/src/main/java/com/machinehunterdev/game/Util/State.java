package com.machinehunterdev.game.Util;

/**
 * Interfaz que representa un estado en la máquina de estados.
 * Define el ciclo de vida básico de cualquier estado del juego.
 * 
 * @param <T> Tipo del objeto propietario que utiliza la máquina de estados
 * @author MachineHunterDev
 */
public interface State<T>
{
    /**
     * Se ejecuta una vez cuando se entra en el estado.
     * Se utiliza para inicializar recursos y configurar el estado.
     * @param owner Objeto propietario que utiliza la máquina de estados
     */
    void enter(T owner);

    /**
     * Se ejecuta en cada frame mientras se está en el estado.
     * Contiene la lógica principal del estado (actualización, renderizado, etc.).
     */
    void execute();

    /**
     * Se ejecuta una vez cuando se sale del estado.
     * Se utiliza para liberar recursos y limpiar el estado.
     */
    void exit();
}