package com.machinehunterdev.game.Dialog;

import java.util.List;

/**
 * Representa una unidad de dialogo en el juego.
 * Un dialogo es, en esencia, una coleccion de lineas de texto que se muestran
 * de forma secuencial. Esta clase actua como un contenedor simple para esas lineas.
 * 
 * @author MachineHunterDev
 */
public class Dialog {
    
    /**
     * La lista de cadenas de texto (String) que componen el dialogo completo.
     * Cada elemento de la lista es una linea que se mostrara en el cuadro de dialogo.
     */
    private List<String> lines;

    /**
     * Constructor para crear un nuevo objeto Dialog.
     * @param lines Una lista de cadenas de texto que seran las lineas del dialogo.
     */
    public Dialog(List<String> lines) {
        this.lines = lines;
    }

    /**
     * Obtiene la lista completa de lineas que componen este dialogo.
     * @return Una lista de cadenas de texto.
     */
    public List<String> getLines() {
        return lines;
    }
}