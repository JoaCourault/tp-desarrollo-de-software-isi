package com.isi.desa.Controller;

import com.isi.desa.Dto.Factura.*;
import com.isi.desa.Service.Implementations.FacturacionService;
import com.isi.desa.Service.Interfaces.ILogger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import com.isi.desa.Dto.Resultado;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/Factura")
public class FacturacionController {

    @Autowired
    private FacturacionService facturacionService;

    @Autowired
    private ILogger logger;

    // Obtener responsables de pago
    @PostMapping("/ObtenerResponsablesDePagoParaFacturacion")
    public ResponseEntity<ObtenerResponsablesDePagoParaFacturacionResult>
    obtenerResponsablesDePagoParaFacturacion(
            @RequestBody ObtenerResponsablesDePagoParaFacturacionRequest requestDTO
    ) {
        try {
            return ResponseEntity.ok(
                    facturacionService.obtenerResponsablesDePagoParaFacturacion(requestDTO)
            );
        } catch (Exception e) {
            logger.error("Error al obtener responsables de pago", e);

            ObtenerResponsablesDePagoParaFacturacionResult error =
                    new ObtenerResponsablesDePagoParaFacturacionResult();

            error.resultado.id = 1;
            error.resultado.mensaje =
                    "Error interno al obtener responsables de pago";

            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(error);
        }
    }


    // Generar facturación

    @PostMapping("/GenerarFacturacionHabitacion")
    public ResponseEntity<GenerarFacturacionHabitacionResult>
    generarFacturacionHabitacion(
            @RequestBody GenerarFacturacionHabitacionRequest requestDTO
    ) {
        try {
            return ResponseEntity.ok(
                    facturacionService.generarFacturacionParaHabitacion(requestDTO)
            );
        } catch (Exception e) {
            logger.error("Error al generar la facturación", e);

            GenerarFacturacionHabitacionResult error =
                    new GenerarFacturacionHabitacionResult();

            error.resultado.id = 1;
            error.resultado.mensaje =
                    "Error interno al generar la facturación";

            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(error);
        }
    }

    // Confirmar facturación

    @PostMapping("/ConfirmarFacturacion")
    public ResponseEntity<ConfirmarFacturacionResult>
    confirmarFacturacion(
            @RequestBody ConfirmarFacturacionRequest requestDTO
    ) {
        try {
            return ResponseEntity.ok(
                    facturacionService.confirmarFacturacion(requestDTO)
            );
        } catch (Exception e) {
            logger.error("Error al confirmar la facturación", e);

            ConfirmarFacturacionResult error =
                    new ConfirmarFacturacionResult();

            error.resultado.id = 1;
            error.resultado.mensaje =
                    "Error interno al confirmar la facturación";

            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(error);
        }
    }


    // MANEJO DE JSON INVÁLIDO

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ConfirmarFacturacionResult>
    handleJsonParseError(HttpMessageNotReadableException ex) {

        logger.error("Error al parsear JSON de facturación", ex);

        Resultado resultado = new Resultado();
        resultado.id = 1;
        resultado.mensaje =
                ex.getMostSpecificCause().getMessage();

        ConfirmarFacturacionResult error =
                new ConfirmarFacturacionResult();
        error.resultado = resultado;

        return ResponseEntity
                .badRequest()
                .body(error);
    }
    @PostMapping("/Generar")
    public ResponseEntity<?> generarFacturaReal(@RequestBody GenerarFacturaRequestDTO request) {
        try {
            GenerarFacturaResultDTO resultado = facturacionService.generarFacturaYCheckOut(request);
            return ResponseEntity.ok(resultado);
        } catch (IllegalArgumentException e) {
            Resultado error = new Resultado(1, e.getMessage());
            return ResponseEntity.badRequest().body(error);
        } catch (Exception e) {
            e.printStackTrace();
            Resultado error = new Resultado(500, "Error interno al facturar: " + e.getMessage());
            return ResponseEntity.internalServerError().body(error);
        }
    }
}
