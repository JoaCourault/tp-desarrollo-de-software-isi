package com.isi.desa.Utils.Mappers;

import com.isi.desa.Dto.Factura.FacturaDTO;
import com.isi.desa.Dto.NotaDeCredito.NotaDeCreditoDTO;
import com.isi.desa.Model.Entities.Factura.Factura;

public class FacturacionMapper {
    public static FacturaDTO factura_entityToDto (Factura factura) {
        FacturaDTO facturaDTO = new FacturaDTO();

        facturaDTO.idFactura = factura.getIdFactura();
        facturaDTO.detalle = factura.getDetalle();
        facturaDTO.total = factura.getTotal();
        facturaDTO.nombre = factura.getNombre();
        facturaDTO.responsableDePago = ResponsableDePagoMapper.entityToDto(
                factura.getResponsableDePago()
        );
        facturaDTO.pago = PagoMapper.entityToDto(factura.getPago());
        facturaDTO.notaDeCredito = NotaDeCreditoMapper.entityToDto(factura.getNotaDeCredito());
        facturaDTO.servicios = ServicioMapper.listaEntityToDto(factura.getServicios());
        facturaDTO.estadias = EstadiaMapper.entityListToDtoList(factura.getEstadias());
        return facturaDTO;
    }

    public static Factura factura_dtoToEntity (FacturaDTO facturaDto) {
        Factura factura = new Factura();

        factura.setIdFactura(facturaDto.idFactura);
        factura.setDetalle(facturaDto.detalle);
        factura.setTotal(facturaDto.total);
        factura.setNombre(facturaDto.nombre);
        factura.setResponsableDePago(
                ResponsableDePagoMapper.dtoToEntity(facturaDto.responsableDePago)
        );
        factura.setPago(PagoMapper.dtoToEntity(facturaDto.pago));
        factura.setNotaDeCredito(
                NotaDeCreditoMapper.dtoToEntity(facturaDto.notaDeCredito)
        );
        factura.setServicios(ServicioMapper.listaDtoToEntity(facturaDto.servicios));
        factura.setEstadias(EstadiaMapper.dtoLisToEntitiesList(facturaDto.estadias));
        return factura;
    }
}
