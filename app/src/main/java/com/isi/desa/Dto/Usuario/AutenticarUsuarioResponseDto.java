package com.isi.desa.Dto.Usuario;

import com.isi.desa.Dto.Resultado;

public class AutenticarUsuarioResponseDto {
    public Resultado resultado;
    public UsuarioDTO usuario;

    public AutenticarUsuarioResponseDto() {
        this.resultado = new Resultado();
        this.usuario = null;
    }
}
