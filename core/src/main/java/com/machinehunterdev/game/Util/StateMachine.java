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
    /** Estado actual de la maquina de estados */
    public IState<T> currentState;
    
    /** Pila de estados para permitir navegacion hacia atras */
    public Stack<IState<T>> stateStack;
    
    /** Objeto propietario que utiliza la maquina de estados */
    public T owner;

    /**
     * Constructor de la maquina de estados.
     * Inicializa el objeto propietario y la pila de estados.
     * @param owner Objeto propietario que utilizara la maquina de estados
     */
    public StateMachine(T owner)
    {
        this.owner = owner;
        stateStack = new Stack<IState<T>>();
    }

    /**
     * Ejecuta el estado actual en cada frame.
     * Si no hay un estado actual, no hace nada.
     */
    public void execute()
    {
        if(currentState != null)
            currentState.execute();
    }

    /**
     * Coloca un nuevo estado en la pila y entra en el.
     * El estado anterior permanece en la pila para poder regresar a el.
     * @param newState Nuevo estado a agregar a la pila
     */
    public void push(IState<T> newState)
    {
        stateStack.push(newState);
        currentState = newState;
        currentState.enter(owner);
    }

    /**
     * Saca el estado actual de la pila y vuelve al estado anterior.
     * Util para implementar menus, pausas, o dialogos temporales.
     */
    public void pop()
    {
        if (!stateStack.isEmpty()) {
            currentState.exit();
            stateStack.pop();
            if (!stateStack.isEmpty()) {
                currentState = stateStack.peek();
                currentState.resume();
            } else {
                currentState = null;
            }
        }
    }

    /**
     * Cambia el estado actual por uno nuevo.
     * Sale del estado actual y entra en el nuevo, reemplazando completamente el estado anterior.
     * @param newState Nuevo estado que reemplazara al actual
     */
    public void changeState(IState<T> newState)
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
     * @return Estado anterior en la pila o null si no hay un estado anterior.
     */
    public IState<T> getPreviousState()
    {
        if (stateStack.size() > 1) {
            return stateStack.elementAt(stateStack.size() - 2);
        }
        return null;
    }
}