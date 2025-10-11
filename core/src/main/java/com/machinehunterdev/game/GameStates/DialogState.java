package com.machinehunterdev.game.GameStates;

import com.badlogic.gdx.Gdx;
import com.machinehunterdev.game.GameController;
import com.machinehunterdev.game.Dialog.DialogManager;
import com.machinehunterdev.game.Dialog.Dialog;
import com.machinehunterdev.game.Util.State;

public class DialogState implements State<GameController> {
    private DialogManager dialogManager;
    private Dialog currentDialog;
    private GameController owner;

    public DialogState(Dialog dialog) {
        dialogManager = new DialogManager(); // Ya no necesita cámara
        currentDialog = dialog;
    }

    @Override
    public void enter(GameController owner) {
        this.owner = owner;
        dialogManager.showDialog(currentDialog);
    }

    @Override
    public void execute() {
        handleInput();
        if (dialogManager.isDialogActive()) {
            dialogManager.render();
        }
    }

    @Override
    public void exit() {
        dialogManager.dispose();
    }

    private void handleInput() {
        if (Gdx.input.justTouched()) {
            if (dialogManager.isDialogActive()) {
                dialogManager.nextLine();
            } else {
                owner.stateMachine.pop();
            }
        }
    }
}
