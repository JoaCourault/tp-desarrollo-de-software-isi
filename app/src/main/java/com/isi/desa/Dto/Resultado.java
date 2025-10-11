package com.isi.desa.Dto;

public class Resultado {
    public Integer id; // 0: Exito, 1: Error en la operacion (500), 2: NoEncontrado (404), 3: AccesoDenegado (401)
    public String mensaje;
}
