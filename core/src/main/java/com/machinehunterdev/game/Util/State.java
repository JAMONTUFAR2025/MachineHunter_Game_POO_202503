package com.machinehunterdev.game.Util;

// ** Interfaz que representa un estado en la máquina de estados **
// T es el tipo del objeto propietario que utiliza la máquina de estados
public interface State<T>
{

    // ** Estos métodos son públicos por defecto en una interfaz **

    // Cuando se entra en el estado, se ejecuta una vez
    void enter(T owner);

    // Se ejecuta en cada frame mientras se estás en el estado
    void execute();

    // Cuando se sale del estado, se ejecuta una vez
    void exit();
}
