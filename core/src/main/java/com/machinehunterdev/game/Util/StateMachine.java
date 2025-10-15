package com.machinehunterdev.game.Util;

import java.util.Stack;

/**
 * Clase que implementa una máquina de estados con pila.
 * Permite gestionar múltiples estados del juego con transiciones controladas.
 * 
 * @param <T> Tipo del objeto propietario que utiliza la máquina de estados
 * @author MachineHunterDev
 */
public class StateMachine<T>
{
    /** Estado actual de la máquina de estados */
    public State<T> currentState;
    
    /** Pila de estados para permitir navegación hacia atrás */
    public Stack<State<T>> stateStack;
    
    /** Objeto propietario que utiliza la máquina de estados */
    public T owner;

    /**
     * Constructor de la máquina de estados.
     * @param owner Objeto propietario que utilizará la máquina de estados
     */
    public StateMachine(T owner)
    {
        this.owner = owner;
        stateStack = new Stack<State<T>>();
    }

    /**
     * Ejecuta el estado actual en cada frame.
     */
    public void execute()
    {
        if(currentState != null)
            currentState.execute();
    }

    /**
     * Coloca un nuevo estado en la pila y entra en él.
     * El estado anterior permanece en la pila para poder regresar a él.
     * @param newState Nuevo estado a agregar a la pila
     */
    public void push(State<T> newState)
    {
        stateStack.push(newState);
        currentState = newState;
        currentState.enter(owner);
    }

    /**
     * Saca el estado actual de la pila y vuelve al estado anterior.
     * Útil para implementar menús, pausas, o diálogos temporales.
     */
    public void pop()
    {
        if (!stateStack.isEmpty()) {
            currentState.exit();
            stateStack.pop();
            if (!stateStack.isEmpty()) {
                currentState = stateStack.peek();
            } else {
                currentState = null;
            }
        }
    }

    /**
     * Cambia el estado actual por uno nuevo.
     * Sale del estado actual y entra en el nuevo, reemplazando completamente el estado anterior.
     * @param newState Nuevo estado que reemplazará al actual
     */
    public void changeState(State<T> newState)
    {
        if(currentState != null)
        {
            currentState.exit();
            stateStack.pop();
        }

        stateStack.push(newState);
        currentState = newState;
        currentState.enter(owner);
    }

    /**
     * Devuelve el estado anterior sin sacarlo de la pila.
     * @return Estado anterior en la pila
     */
    public State<T> getPreviousState()
    {
        if (stateStack.size() > 1) {
            return stateStack.elementAt(stateStack.size() - 2);
        }
        return null;
    }
}