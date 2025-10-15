package com.machinehunterdev.game.GameStates;

import com.badlogic.gdx.Gdx;
import com.machinehunterdev.game.GameController;
import com.machinehunterdev.game.Dialog.DialogManager;
import com.machinehunterdev.game.Dialog.Dialog;
import com.machinehunterdev.game.Util.State;

/**
 * Estado del juego dedicado exclusivamente a mostrar diálogos.
 * Se utiliza para escenas cinemáticas o interacciones con NPCs.
 * 
 * @author MachineHunterDev
 */
public class DialogState implements State<GameController> {
    /** Gestor de diálogos */
    private DialogManager dialogManager;
    
    /** Diálogo actual a mostrar */
    private Dialog currentDialog;
    
    /** Controlador del juego propietario */
    private GameController owner;

    /**
     * Constructor del estado de diálogo.
     * @param dialog Diálogo a mostrar en este estado
     */
    public DialogState(Dialog dialog) {
        currentDialog = dialog;
    }

    /**
     * Inicializa el estado al entrar.
     * @param owner Controlador del juego propietario
     */
    @Override
    public void enter(GameController owner) {
        this.owner = owner;
        this.dialogManager = new DialogManager(owner.batch);
        dialogManager.showDialog(currentDialog);
    }

    /**
     * Ejecuta la lógica del estado cada frame.
     */
    @Override
    public void execute() {
        handleInput();
        if (dialogManager.isDialogActive()) {
            dialogManager.render();
        }
    }

    /**
     * Limpia los recursos al salir del estado.
     */
    @Override
    public void exit() {
        dialogManager.dispose();
    }

    /**
     * Maneja la entrada del usuario para avanzar en el diálogo.
     */
    private void handleInput() {
        if (Gdx.input.justTouched()) {
            if (dialogManager.isDialogActive()) {
                dialogManager.nextLine();
            } else {
                owner.stateMachine.pop(); // Regresar al estado anterior
            }
        }
    }
}