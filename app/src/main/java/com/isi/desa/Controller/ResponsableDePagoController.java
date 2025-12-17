package com.isi.desa.Controller;

import com.isi.desa.Dto.Factura.ConfirmarFacturacionResult;
import com.isi.desa.Dto.Factura.ObtenerResponsablesDePagoParaFacturacionRequest;
import com.isi.desa.Dto.Factura.ObtenerResponsablesDePagoParaFacturacionResult;
import com.isi.desa.Dto.ResponsableDePago.*;
import com.isi.desa.Dto.Resultado;
import com.isi.desa.Service.Implementations.ResponsableDePagoService;
import com.isi.desa.Service.Interfaces.ILogger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;

@RestController
@RequestMapping("/ResponsablePago")
public class ResponsableDePagoController {
    @Autowired
    private ResponsableDePagoService responsableDePagoService;

    @Autowired
    private ILogger logger;

    // =========================================================
    // 1. Obtener responsables de pago
    // =========================================================
    @PostMapping("/ObtenerResponsablesDePago")
    public ResponseEntity<BuscarResponsableDePagoResult>
    buscarResponsableDePago(@RequestBody BuscarResponsableDePagoRequest request) {
        try {
            return ResponseEntity.ok(
                    responsableDePagoService.BuscarResponsableDePago(request)
            );
        } catch (Exception e) {
            logger.error("Error al obtener responsables de pago", e);

            BuscarResponsableDePagoResult error =
                    new BuscarResponsableDePagoResult();

            error.resultado.id = 1;
            error.resultado.mensaje =
                    "Error interno al obtener responsables de pago";

            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(error);
        }
    }
    // =========================================================
    // 2. Alta de responsable de pago
    // =========================================================
    @PostMapping("/AltaResponsableDePago")
    public ResponseEntity<AltaResponsableDePagoResult> AltaResponsableDePago(
            @RequestBody AltaResponsableDePagoRequest request
    ) {
        try {
            return ResponseEntity.ok(
                    responsableDePagoService.AltaResponsableDePago(request)
            );
        } catch (Exception e) {
            logger.error("Error al crear responsable de pago", e);

            AltaResponsableDePagoResult error =
                    new AltaResponsableDePagoResult();

            error.resultado.id = 1;
            error.resultado.mensaje =
                    "Error interno al crear responsable de pago";

            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(error);
        }
    }

    // =========================================================
    // 3. Modificar responsable de pago
    // =========================================================
    @PostMapping("/ModificarResponsableDePago")
    public ResponseEntity<ModificarResponsableDePagoResult> modificarResponsableDePago(
            @RequestBody ModificarResponsableDePagoRequest request
    ) {
        try {
            return ResponseEntity.ok(
                    responsableDePagoService.ModificarResponsableDePago(request)
            );
        } catch (Exception e) {
            logger.error("Error al modificar responsable de pago", e);

            ModificarResponsableDePagoResult error = new ModificarResponsableDePagoResult();
            error.resultado = new Resultado(); // Inicialización para evitar NPE
            error.resultado.id = 1;
            error.resultado.mensaje =
                    "Error interno al modificar responsable de pago";

            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(error);
        }
    }
    // =========================================================
    // 4. Obtener las distintas razones sociales
    // =========================================================
    @GetMapping("/ObtenerRazonesSociales")
    public ResponseEntity<List<String>> obtenerRazonesSociales() {
        try {
            List<String> razones = responsableDePagoService.obtenerRazonesSocialesResponsablesDePago();
            return ResponseEntity.ok(razones);
        } catch (Exception e) {
            logger.error("Error al obtener razones sociales", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Collections.emptyList());
        }
    }
}
