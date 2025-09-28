package com.machinehunterdev.game.Util;

// Importamos la interfaz State
import java.util.Stack;

// ** Clase que representa una máquina de estados **
public class StateMachine<T>
{
    // Estado actual de la máquina de estados
    public State<T> currentState;
    // Pila de estados
    public Stack<State<T>> stateStack;
    // Objeto propietario que utiliza la máquina de estados
    public T owner;

    // Constructor, inicializa la maquina de estados con el objeto propietario
    public StateMachine(T owner)
    {
        this.owner = owner;
        stateStack = new Stack<State<T>>();
    }

    // Ejecutar estado actual en cada frame
    public void execute()
    {
        if(currentState != null)
            currentState.execute();
    }

    // Coloca un nuevo estado en la pila y entra en él
    public void push(State<T> newState)
    {
        stateStack.push(currentState);
        currentState = newState;
        currentState.enter(owner);
    }

    // Saca el estado actual de la pila y vuelve al estado anterior
    public void pop()
    {
        stateStack.pop();
        currentState.exit();
        currentState = stateStack.peek();
    }

    // Cambia el estado actual por uno nuevo, saliendo del estado actual y entrando en el nuevo
    public void changeState(State<T> newState)
    {
        if(currentState != null)
        {
            stateStack.pop();
            currentState.exit();
        }

        stateStack.push(newState);
        currentState = newState;
        currentState.enter(owner);
    }

    // ** En C# seria asi, probablemente no se use **
    // Coloca un nuevo estado en la pila y espera a que termine para volver al estado anterior
    // public IEnumerator pushAndWait(State<T> newState)
    // {
    //     var oldState = currentState;
    //     push(newState);
    //     yield return new WaitUtil(() => currentState == oldState)
    // }

    // Devuelve el estado anterior sin sacarlo de la pila
    public State<T> getPreviousState()
    {
        return stateStack.elementAt(1);
    }
}
