package com.isi.desa.Dto.Direccion;

public class DireccionDTO {
    // 1. Campos PRIVADOS (Buena práctica de encapsulamiento)
    public String id;
    public String pais;
    public String provincia;
    public String localidad;

    // El front envía "codigoPostal", este nombre debe coincidir EXACTO
    public String codigoPostal;

    public String calle;
    public String numero;
    public String departamento;
    public int piso; // String es mejor para evitar errores de conversión

    public DireccionDTO() {}

    // --- 2. GETTERS Y SETTERS (Obligatorios para que Spring funcione bien) ---

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getPais() { return pais; }
    public void setPais(String pais) { this.pais = pais; }

    public String getProvincia() { return provincia; }
    public void setProvincia(String provincia) { this.provincia = provincia; }

    public String getLocalidad() { return localidad; }
    public void setLocalidad(String localidad) { this.localidad = localidad; }

    public String getCodigoPostal() { return codigoPostal; }
    public void setCodigoPostal(String codigoPostal) { this.codigoPostal = codigoPostal; }

    public String getCalle() { return calle; }
    public void setCalle(String calle) { this.calle = calle; }

    public String getNumero() { return numero; }
    public void setNumero(String numero) { this.numero = numero; }

    public String getDepartamento() { return departamento; }
    public void setDepartamento(String departamento) { this.departamento = departamento; }

    public int getPiso() { return piso; }
    public void setPiso(int piso) { this.piso = piso; }
}