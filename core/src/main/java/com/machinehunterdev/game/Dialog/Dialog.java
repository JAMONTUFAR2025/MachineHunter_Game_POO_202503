package com.machinehunterdev.game.Dialog;

import java.util.List;

/**
 * Representa un diálogo compuesto por múltiples líneas de texto.
 * 
 * @author MachineHunterDev
 */
public class Dialog {
    /** Lista de líneas que componen el diálogo */
    private List<String> lines;

    /**
     * Constructor del diálogo.
     * @param lines Lista de líneas de texto
     */
    public Dialog(List<String> lines) {
        this.lines = lines;
    }

    /**
     * Obtiene las líneas del diálogo.
     * @return Lista de líneas de texto
     */
    public List<String> getLines() {
        return lines;
    }
}