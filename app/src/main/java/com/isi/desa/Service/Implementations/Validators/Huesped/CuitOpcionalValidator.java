package com.isi.desa.Service.Implementations.Validators.Huesped;

public class CuitOpcionalValidator {

    public String validateCuit(String cuit) {
        if (cuit == null || cuit.trim().isEmpty()) return null;

        String regex = "\\d{2}-\\d{8}-\\d{1}";
        if (!cuit.matches(regex)) {
            return "El CUIT debe tener el formato XX-XXXXXXXX-X";
        }
        return null;
    }
}
