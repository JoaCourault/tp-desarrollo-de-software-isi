package com.isi.desa.Dto.ResponsableDePago;

public class PayerDTO {
    // Usamos public para ahorrar getters/setters si prefieres,
    // o private con getters/setters (recomendado en Java).
    // Aquí uso private con getters/setters para seguir estándares.

    private String idResponsable;
    private String nombre;
    private String apellido;
    private String dni;
    private String cuit;
    private String condicionIva;
    private boolean esPersonaJuridica;
    private String razonSocial;

    public PayerDTO() {}

    // Getters y Setters
    public String getIdResponsable() { return idResponsable; }
    public void setIdResponsable(String idResponsable) { this.idResponsable = idResponsable; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getApellido() { return apellido; }
    public void setApellido(String apellido) { this.apellido = apellido; }

    public String getDni() { return dni; }
    public void setDni(String dni) { this.dni = dni; }

    public String getCuit() { return cuit; }
    public void setCuit(String cuit) { this.cuit = cuit; }

    public String getCondicionIva() { return condicionIva; }
    public void setCondicionIva(String condicionIva) { this.condicionIva = condicionIva; }

    public boolean isEsPersonaJuridica() { return esPersonaJuridica; }
    public void setEsPersonaJuridica(boolean esPersonaJuridica) { this.esPersonaJuridica = esPersonaJuridica; }

    public String getRazonSocial() { return razonSocial; }
    public void setRazonSocial(String razonSocial) { this.razonSocial = razonSocial; }
}