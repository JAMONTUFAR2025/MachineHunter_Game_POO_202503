package com.machinehunterdev.game.GameStates;

import java.util.ArrayList;
import java.util.List;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;
import com.machinehunterdev.game.GameController;
import com.machinehunterdev.game.Dialog.Dialog;
import com.machinehunterdev.game.Dialog.DialogManager;
import com.machinehunterdev.game.Util.IState;
import com.machinehunterdev.game.Audio.AudioManager;

/**
 * Estado del juego dedicado exclusivamente a mostrar dialogos.
 * Este estado se utiliza para escenas cinematicas, interacciones con NPCs,
 * o flashbacks que cuentan la historia. Pausa la accion del juego principal.
 * 
 * @author MachineHunterDev
 */
public class DialogState implements IState<GameController>, InputProcessor {

    // === CAMPOS DE LA INSTANCIA ===
    private GameController owner; // Referencia al controlador principal del juego.
    private DialogManager dialogManager; // Gestor que renderiza y controla el dialogo.
    private Dialog currentDialog; // El objeto de dialogo con el texto a mostrar.
    private String levelFile; // Opcional: ruta al nivel a cargar despues del dialogo.
    private boolean isFlashback = false; // Bandera para indicar si el dialogo es un flashback.
    private String dialogueSection; // Nombre de la seccion de dialogo a cargar desde JSON.

    // === CONSTRUCTORES ===

    /**
     * Constructor para un dialogo estandar (ej. interaccion con NPC).
     * @param dialog El objeto Dialog con las lineas a mostrar.
     */
    public DialogState(Dialog dialog) {
        this.currentDialog = dialog;
        this.levelFile = null; // No hay transicion de nivel.
        this.isFlashback = false;
    }

    /**
     * Constructor para un dialogo de tipo "flashback" o cinematico.
     * Carga el dialogo desde un archivo JSON y puede transicionar a un nivel al finalizar.
     * @param dialogueSection El nombre de la seccion en el JSON a cargar.
     * @param levelFile La ruta al archivo .json del nivel a cargar al finalizar.
     */
    public DialogState(String dialogueSection, String levelFile) {
        this.levelFile = levelFile;
        this.isFlashback = true;
        this.dialogueSection = dialogueSection;
        // Carga el dialogo desde el archivo JSON correspondiente.
        if (dialogueSection.equals("Final")) {
            this.currentDialog = loadDialog("Dialogos/Dialogos_personajes.json", "Dialogos_final", null);
        } else {
            this.currentDialog = loadDialog("Dialogos/Diagolos_flahsbacks.json", "Flashbacks", dialogueSection);
        }
    }

    // === METODOS DE LA INTERFAZ IState ===

    /**
     * Se llama una vez al entrar en este estado.
     * Inicializa el DialogManager y establece el procesador de entrada.
     * @param owner El GameController que posee esta maquina de estados.
     */
    @Override
    public void enter(GameController owner) {
        this.owner = owner;
        this.dialogManager = new DialogManager(owner, owner.batch);
        dialogManager.showDialog(currentDialog, isFlashback);

        // Establece esta clase para que escuche los eventos de entrada.
        Gdx.input.setInputProcessor(this);

        // Reproduce musica especifica si es un flashback.
        if (isFlashback) {
            if ("Flashback3".equals(dialogueSection)) {
                AudioManager.getInstance().playMusic("Audio/Soundtrack/WarningGemini.mp3", true, false);
            } else if ("Flashback5".equals(dialogueSection)) {
                AudioManager.getInstance().playMusic("Audio/Soundtrack/WarningChatGPT.mp3", true, false);
            } else {
                AudioManager.getInstance().playMusic("Audio/Soundtrack/Flashback.mp3", true, false);
            }
        }
    }

    /**
     * Se llama en cada fotograma.
     * Actualiza y renderiza el dialogo si esta activo.
     */
    @Override
    public void execute() {
        if (dialogManager.isDialogActive()) {
            dialogManager.update(Gdx.graphics.getDeltaTime());
            dialogManager.render();
        }
    }

    /**
     * Se llama una vez al salir de este estado.
     * Libera recursos y el procesador de entrada.
     */
    @Override
    public void exit() {
        dialogManager.dispose();
        Gdx.input.setInputProcessor(null);
    }

    /**
     * Se llama al reanudar este estado.
     */
    @Override
    public void resume() {
        Gdx.input.setInputProcessor(this);
    }

    // === METODOS DE LA INTERFAZ InputProcessor ===

    /**
     * Maneja el evento de presionar una tecla.
     * Se usa la tecla 'E' para avanzar en el dialogo.
     * @param keycode El codigo de la tecla presionada.
     * @return true si el evento fue manejado.
     */
    @Override
    public boolean keyDown(int keycode) {
        if (keycode == com.machinehunterdev.game.Gameplay.GlobalSettings.CONTROL_INTERACT) {
            if (dialogManager.isDialogActive()) {
                dialogManager.nextLine();
                // Si el dialogo ha terminado...
                if (!dialogManager.isDialogActive()) {
                    // ...y hay un nivel al que transicionar...
                    if (levelFile != null) {
                        if (levelFile.equals("credits")) {
                            owner.stateMachine.changeState(CreditState.instance);
                        } else {
                            owner.stateMachine.changeState(GameplayState.createForLevel(levelFile));
                        }
                    } else {
                        // ...o si no, simplemente vuelve al estado anterior (GameplayState).
                        owner.stateMachine.pop();
                    }
                }
            }
        }
        return true;
    }

    // === METODOS PRIVADOS DE AYUDA ===

    /**
     * Carga las lineas de dialogo desde un archivo JSON.
     * @param fileName El nombre del archivo JSON.
     * @param sectionName La seccion principal dentro del JSON (ej. "Flashbacks").
     * @param filter El nombre especifico del dialogo a cargar dentro de la seccion.
     * @return Un objeto Dialog con las lineas cargadas.
     */
    private Dialog loadDialog(String fileName, String sectionName, String filter) {
        List<String> lines = new ArrayList<>();
        try {
            JsonReader jsonReader = new JsonReader();
            JsonValue base = jsonReader.parse(Gdx.files.internal(fileName));
            JsonValue section = base.get(sectionName);

            for (JsonValue dialogValue : section) {
                if (filter == null || dialogValue.getString("Name").equals(filter)) {
                    JsonValue texto = dialogValue.get("Texto");
                    if (texto.isArray()) {
                        for (JsonValue line : texto) {
                            lines.add(line.asString());
                        }
                    } else {
                        lines.add(texto.asString());
                    }
                    if (filter != null) {
                        break; // Detenerse despues de encontrar el dialogo filtrado.
                    }
                }
            }
        } catch (Exception e) {
            Gdx.app.error("DialogState", "Error al cargar el dialogo: " + sectionName, e);
        }
        return new Dialog(lines);
    }

    // --- METODOS DE InputProcessor NO UTILIZADOS ---
    @Override public boolean keyUp(int keycode) { return false; }
    @Override public boolean keyTyped(char character) { return false; }
    @Override public boolean touchDown(int screenX, int screenY, int pointer, int button) { return false; }
    @Override public boolean touchUp(int screenX, int screenY, int pointer, int button) { return false; }
    @Override public boolean touchDragged(int screenX, int screenY, int pointer) { return false; }
    @Override public boolean mouseMoved(int screenX, int screenY) { return false; }
    @Override public boolean scrolled(float amountX, float amountY) { return false; }
    @Override public boolean touchCancelled(int screenX, int screenY, int pointer, int button) { return false; }
}
