package com.machinehunterdev.game.GameStates;



import com.badlogic.gdx.Gdx;

import com.machinehunterdev.game.GameController;

import com.machinehunterdev.game.Dialog.DialogManager;

import com.machinehunterdev.game.Dialog.Dialog;

import com.machinehunterdev.game.Util.IState;



/**

 * Estado del juego dedicado exclusivamente a mostrar diálogos.

 * Se utiliza para escenas cinemáticas o interacciones con NPCs.

 * 

 * @author MachineHunterDev

 */

import com.badlogic.gdx.utils.JsonReader;

import com.badlogic.gdx.utils.JsonValue;



import java.util.ArrayList;

import java.util.List;



public class DialogState implements IState<GameController>, com.badlogic.gdx.InputProcessor {







    private DialogManager dialogManager;



    private Dialog currentDialog;



    private GameController owner;



    private String levelFile;



    private boolean isFlashback = false;







    public DialogState(Dialog dialog) {



        this.currentDialog = dialog;



        this.levelFile = null;



        this.isFlashback = false;



    }







    public DialogState(String flashbackDialogueSection, String levelFile) {



        this.levelFile = levelFile;



        this.currentDialog = loadFlashbackDialog(flashbackDialogueSection);



        this.isFlashback = true;



    }







    private Dialog loadFlashbackDialog(String sectionName) {



        List<String> lines = new ArrayList<>();



        try {



            JsonReader jsonReader = new JsonReader();



            JsonValue base = jsonReader.parse(Gdx.files.internal("Dialogos/Diagolos_flahsbacks.json"));



            JsonValue flashbacks = base.get("Flashbacks");



            for (JsonValue flashback : flashbacks) {



                if (flashback.getString("Name").equals(sectionName)) {



                    JsonValue texto = flashback.get("Texto");



                    for (JsonValue line : texto) {
                        lines.add(line.asString());
                    }



                    break;



                }



            }



        } catch (Exception e) {



            Gdx.app.error("DialogState", "Error al cargar el diálogo de flashback", e);



        }



        return new Dialog(lines);



    }







    @Override
    public void enter(GameController owner) {



        this.owner = owner;



        this.dialogManager = new DialogManager(owner.batch);



        dialogManager.showDialog(currentDialog, isFlashback);
        Gdx.input.setInputProcessor(this);



    }







    @Override
    public void execute() {
        if (dialogManager.isDialogActive()) {



            dialogManager.update(Gdx.graphics.getDeltaTime());



            dialogManager.render();



        }



    }







    @Override
    public void exit() {


        dialogManager.dispose();
        Gdx.input.setInputProcessor(null);
    }



    @Override
    public boolean keyDown(int keycode) {
        if (keycode == com.badlogic.gdx.Input.Keys.E) {
            if (dialogManager.isDialogActive()) {
                dialogManager.nextLine();
                if (!dialogManager.isDialogActive()) {
                    if (levelFile != null) {
                        owner.stateMachine.changeState(GameplayState.createForLevel(levelFile));
                    } else {
                        owner.stateMachine.pop(); // Regresar al estado anterior
                    }
                }
            }
        }
        return true;
    }

    @Override
    public boolean keyUp(int keycode) {
        return false;
    }

    @Override
    public boolean keyTyped(char character) {
        return false;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        return false;
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        return false;
    }

    @Override
    public boolean scrolled(float amountX, float amountY) {
        return false;
    }

    @Override
    public boolean touchCancelled(int screenX, int screenY, int pointer, int button) {
        return false;
    }
}
