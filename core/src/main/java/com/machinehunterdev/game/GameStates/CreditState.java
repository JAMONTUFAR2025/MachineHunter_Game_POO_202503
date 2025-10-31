package com.machinehunterdev.game.GameStates;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.machinehunterdev.game.GameController;
import com.machinehunterdev.game.UI.CreditUI;
import com.machinehunterdev.game.Util.IState;

public class CreditState implements IState<GameController> {

    public static CreditState instance = new CreditState();
    private CreditUI creditUI;

    private CreditState() {
        instance = this;
    }

    @Override
    public void enter(GameController owner) {
        SpriteBatch batch = new SpriteBatch();
        this.creditUI = new CreditUI(batch, owner);
        Gdx.input.setInputProcessor(this.creditUI);
    }

    @Override
    public void execute() {
        if (creditUI != null) {
            creditUI.draw();
        }
    }

    @Override
    public void exit() {
        Gdx.input.setInputProcessor(null);
        if (creditUI != null) {
            creditUI.dispose();
        }
    }
}
