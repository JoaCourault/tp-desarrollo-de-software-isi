package com.isi.desa.Dto;

public class Resultado {
    public Integer id; // 0: Exito, 1: Error en la operacion (500), 2: NoEncontrado (404), 3: AccesoDenegado (401)
    public String mensaje;

    public Resultado() {}

    // Constructor con argumentos
    public Resultado(Integer id, String mensaje) {
        this.id = id;
        this.mensaje = mensaje;
    }

    @Override
    public String toString() {
        return "Resultado{ " + "id=" + id + ", mensaje=" + mensaje + " }";
    }
}
