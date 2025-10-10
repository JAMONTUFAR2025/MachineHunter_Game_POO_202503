package com.machinehunterdev.game.Dialog;

import com.badlogic.gdx.Gdx;
import com.machinehunterdev.game.GameController;
import com.machinehunterdev.game.Util.State;

public class DialogueState implements State<GameController> {
    private DialogManager dialogManager;
    private Dialogue currentDialog;
    private GameController owner;

    public DialogueState(Dialogue dialog) {
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
