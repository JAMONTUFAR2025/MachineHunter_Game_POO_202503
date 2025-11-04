package com.machinehunterdev.game.GameStates;

import java.util.ArrayList;
import java.util.List;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;
import com.machinehunterdev.game.GameController;
import com.machinehunterdev.game.GameController;
import com.machinehunterdev.game.Dialog.Dialog;
import com.machinehunterdev.game.Dialog.DialogManager;
import com.machinehunterdev.game.Util.IState;
import com.machinehunterdev.game.Audio.AudioManager;

/**
 * Estado del juego dedicado exclusivamente a mostrar diálogos.
 * Se utiliza para escenas cinemáticas o interacciones con NPCs.
 * Implementa IState para la lógica de la máquina de estados y
 * InputProcessor para manejar la entrada del jugador (avanzar diálogo).
 *
 * @author MachineHunterDev
 */
public class DialogState implements IState<GameController>, InputProcessor {

    // --- Campos de la Instancia ---

    /**
     * Referencia al controlador principal del juego, usado para cambiar estados.
     */
    private GameController owner;

    /**
     * Gestor que maneja la lógica de renderizado y actualización de la caja de diálogo.
     */
    private DialogManager dialogManager;

    /**
     * El objeto de diálogo actual que contiene las líneas de texto a mostrar.
     */
    private Dialog currentDialog;

    /**
     * Opcional: Ruta al archivo del nivel al que se debe transicionar después del diálogo.
     * Usado principalmente para flashbacks que inician un nivel.
     */
    private String levelFile;

    /**
     * Bandera para indicar si el diálogo es un flashback (puede tener un estilo visual diferente).
     */
    private boolean isFlashback = false;

    // --- Constructores ---

    /**
     * Constructor para un diálogo estándar (ej. interacción con NPC).
     * El diálogo se proporciona directamente.
     *
     * @param dialog El objeto Dialog con las líneas a mostrar.
     */
    public DialogState(Dialog dialog) {
        this.currentDialog = dialog;
        this.levelFile = null; // No hay transición de nivel
        this.isFlashback = false;
    }

    /**
     * Constructor para un diálogo de tipo "flashback".
     * Carga el diálogo desde una sección específica de un archivo JSON y
     * almacena el nivel al que se debe ir al terminar.
     *
     * @param flashbackDialogueSection El nombre de la sección en el JSON de flashbacks.
     * @param levelFile                La ruta al archivo .tmx del nivel a cargar al finalizar.
     */
    public DialogState(String dialogueSection, String levelFile) {
        this.levelFile = levelFile;
        this.isFlashback = true;
        if (dialogueSection.equals("Final")) {
            this.currentDialog = loadDialog("Dialogos/Dialogos_personajes.json", "Dialogos_final", null);
        } else {
            this.currentDialog = loadDialog("Dialogos/Diagolos_flahsbacks.json", "Flashbacks", dialogueSection);
        }
    }

    // --- Métodos de la interfaz IState ---

    /**
     * Se llama una vez al entrar en este estado.
     * Inicializa el DialogManager y establece este estado como el procesador de entrada.
     *
     * @param owner El GameController que posee esta máquina de estados.
     */
    @Override
    public void enter(GameController owner) {
        this.owner = owner;
        this.dialogManager = new DialogManager(owner, owner.batch);
        dialogManager.showDialog(currentDialog, isFlashback);

        // Establece esta clase para que escuche los eventos de input (teclado, mouse)
        Gdx.input.setInputProcessor(this);

        if (isFlashback) {
            AudioManager.getInstance().playMusic("Audio/Soundtrack/Flashback.mp3", true, false);
        }
    }

    /**
     * Se llama en cada fotograma (lógica de actualización).
     * Actualiza y renderiza el diálogo si está activo.
     */
    @Override
    public void execute() {
        if (dialogManager.isDialogActive()) {
            // Actualiza la lógica del diálogo (ej. efecto de máquina de escribir)
            dialogManager.update(Gdx.graphics.getDeltaTime());
            // Dibuja el diálogo en la pantalla
            dialogManager.render();
        }
    }

    /**
     * Se llama una vez al salir de este estado.
     * Libera recursos y devuelve el control de entrada al estado anterior (o a null).
     */
    @Override
    public void exit() {
        dialogManager.dispose(); // Libera la fuente (BitmapFont), etc.
        Gdx.input.setInputProcessor(null); // Deja de escuchar eventos de input
        if (isFlashback) {
            AudioManager.getInstance().pauseMusic(false);
        }
    }

    @Override
    public void resume() {
        Gdx.input.setInputProcessor(this);
    }

    // --- Métodos de la interfaz InputProcessor ---

    /**
     * Maneja el evento de presionar una tecla.
     * Se usa la tecla 'E' para avanzar en el diálogo.
     *
     * @param keycode El código de la tecla presionada.
     * @return true si el evento fue manejado (consumido).
     */
        @Override
        public boolean keyDown(int keycode) {
            if (keycode == com.badlogic.gdx.Input.Keys.E) {
                if (dialogManager.isDialogActive()) {
                    dialogManager.nextLine();
                    if (!dialogManager.isDialogActive()) {
                        if (levelFile != null) {
                            if (levelFile.equals("credits")) {
                                owner.stateMachine.changeState(CreditState.instance);
                            } else {
                                owner.stateMachine.changeState(GameplayState.createForLevel(levelFile));
                            }
                        } else {
                            owner.stateMachine.pop(); // Regresar al estado anterior
                        }
                    }
                }
            }
            return true;
        }
    // --- Métodos privados de ayuda ---

    /**
     * Carga las líneas de diálogo de un flashback desde un archivo JSON.
     *
     * @param sectionName El nombre (identificador) del flashback a cargar.
     * @return Un objeto Dialog con las líneas cargadas.
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
                        break; // Stop after finding the filtered dialog
                    }
                }
            }
        } catch (Exception e) {
            Gdx.app.error("DialogState", "Error al cargar el diálogo: " + sectionName, e);
        }
        return new Dialog(lines);
    }

    // --- Métodos de InputProcessor no utilizados ---
    // Se implementan porque la interfaz InputProcessor lo requiere,
    // pero no necesitamos lógica para ellos en este estado.

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
