package com.isi.desa.Service.Implementations.Validators.Huesped;

public class CuitValidatorFactory {

    private static final CuitValidatorFactory INSTANCE =
            new CuitValidatorFactory();

    private final CuitOpcionalValidator opcionalValidator =
            new CuitOpcionalValidator();

    private final ResponsableInscriptoCuitValidator responsableValidator =
            new ResponsableInscriptoCuitValidator();

    private CuitValidatorFactory() {}

    public static CuitValidatorFactory getInstance() {
        return INSTANCE;
    }

    public Object getValidator(String posicionIva) {
        if ("Responsable Inscripto".equalsIgnoreCase(posicionIva)) {
            return responsableValidator;
        }
        return opcionalValidator;
    }
}