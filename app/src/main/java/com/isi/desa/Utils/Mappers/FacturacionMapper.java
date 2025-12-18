package com.isi.desa.Utils.Mappers;

import com.isi.desa.Dto.Factura.FacturaDTO;
import com.isi.desa.Model.Entities.Factura.Factura;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class FacturacionMapper {

    // Inyección de dependencias
    @Autowired
    private ResponsableDePagoMapper responsableDePagoMapper;

    @Autowired
    private PagoMapper pagoMapper;

    @Autowired
    private NotaDeCreditoMapper notaDeCreditoMapper;

    @Autowired
    private ServicioMapper servicioMapper;

    @Autowired
    private EstadiaMapper estadiaMapper;


    public FacturaDTO factura_entityToDto(Factura factura) {
        if (factura == null) return null;

        FacturaDTO facturaDTO = new FacturaDTO();

        facturaDTO.idFactura = factura.getIdFactura();
        facturaDTO.detalle = factura.getDetalle();
        facturaDTO.total = factura.getTotal();
        facturaDTO.nombre = factura.getNombre();

        // Uso de las instancias inyectadas
        facturaDTO.responsableDePago = responsableDePagoMapper.entityToDto(factura.getResponsableDePago());
        facturaDTO.pago = pagoMapper.entityToDto(factura.getPago());
        facturaDTO.notaDeCredito = notaDeCreditoMapper.entityToDto(factura.getNotaDeCredito());
        facturaDTO.servicios = servicioMapper.listaEntityToDto(factura.getServicios());
        facturaDTO.estadias = estadiaMapper.entityListToDtoList(factura.getEstadias());

        return facturaDTO;
    }

    public Factura factura_dtoToEntity(FacturaDTO facturaDto) {
        if (facturaDto == null) return null;

        Factura factura = new Factura();

        factura.setIdFactura(facturaDto.idFactura);
        factura.setDetalle(facturaDto.detalle);
        factura.setTotal(facturaDto.total);
        factura.setNombre(facturaDto.nombre);

        // Uso de las instancias inyectadas
        factura.setResponsableDePago(responsableDePagoMapper.dtoToEntity(facturaDto.responsableDePago));
        factura.setPago(pagoMapper.dtoToEntity(facturaDto.pago));
        factura.setNotaDeCredito(notaDeCreditoMapper.dtoToEntity(facturaDto.notaDeCredito));
        factura.setServicios(servicioMapper.listaDtoToEntity(facturaDto.servicios));
        factura.setEstadias(estadiaMapper.dtoLisToEntitiesList(facturaDto.estadias));

        return factura;
    }
}