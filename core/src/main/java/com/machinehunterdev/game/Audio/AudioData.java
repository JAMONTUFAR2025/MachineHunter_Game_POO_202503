package com.machinehunterdev.game.Audio;

// Clase que almacena la informacion basica de un recurso de audio.
// Incluye un identificador unico y la ruta del archivo de audio.
public class AudioData {
    // Identificador unico del audio, definido por un enum o clase AudioId.
    public final AudioId id;
    // Ruta relativa o absoluta al archivo de audio en el sistema de archivos.
    public final String path;
    
    // Constructor que inicializa los campos id y path.
    public AudioData(AudioId id, String path) {
        this.id = id;
        this.path = path;
    }
}