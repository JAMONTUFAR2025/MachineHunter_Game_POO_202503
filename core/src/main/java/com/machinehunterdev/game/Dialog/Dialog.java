package com.machinehunterdev.game.Dialog;

import java.util.List;

public class Dialog {
    private List<String> lines;

    public Dialog(List<String> lines) {
        this.lines = lines;
    }

    public List<String> getLines() {
        return lines;
    }
}