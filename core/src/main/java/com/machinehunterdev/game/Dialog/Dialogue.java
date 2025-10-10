package com.machinehunterdev.game.Dialog;

import java.util.List;

public class Dialogue {
    private List<String> lines;

    public Dialogue(List<String> lines) {
        this.lines = lines;
    }

    public List<String> getLines() {
        return lines;
    }
}