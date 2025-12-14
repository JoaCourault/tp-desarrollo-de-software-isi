package com.isi.desa.Utils.Mappers;

import com.isi.desa.Dto.Servicio.ServicioDTO;
import com.isi.desa.Model.Entities.Servicio.Bar;
import com.isi.desa.Model.Entities.Servicio.LavadoYPlanchado;
import com.isi.desa.Model.Entities.Servicio.Sauna;
import com.isi.desa.Model.Entities.Servicio.Servicio;

public class ServicioMapper {
    public static ServicioDTO entityToDto(Servicio entity) {
        if (entity == null) return null;
        if (entity instanceof Bar bar) {
            ServicioDTO.BarDTO dto = new ServicioDTO.BarDTO();
            dto.id = bar.getId();
            dto.fecha = bar.getFecha();
            dto.precio = bar.getPrecio();
            dto.detalle = bar.getDetalle();
            return dto;
        } else if (entity instanceof Sauna sauna) {
            ServicioDTO.SaunaDTO dto = new ServicioDTO.SaunaDTO();
            dto.id = sauna.getId();
            dto.fecha = sauna.getFecha();
            dto.precio = sauna.getPrecio();
            dto.cantidadPersonas = sauna.getCantidadPersonas();
            return dto;
        } else if (entity instanceof LavadoYPlanchado lavado) {
            ServicioDTO.LavadoYPlanchadoDTO dto = new ServicioDTO.LavadoYPlanchadoDTO();
            dto.id = lavado.getId();
            dto.fecha = lavado.getFecha();
            dto.precio = lavado.getPrecio();
            dto.cantidadPrendas = lavado.getCantidadPrendas();
            return dto;
        }
        return null;
    }

    public static Servicio dtoToEntity(ServicioDTO dto) {
        if (dto == null) return null;
        if (dto instanceof ServicioDTO.BarDTO barDto) {
            Bar entity = new Bar();
            entity.setId(barDto.id);
            entity.setFecha(barDto.fecha);
            entity.setPrecio(barDto.precio);
            entity.setDetalle(barDto.detalle);
            return entity;
        } else if (dto instanceof ServicioDTO.SaunaDTO saunaDto) {
            Sauna entity = new Sauna();
            entity.setId(saunaDto.id);
            entity.setFecha(saunaDto.fecha);
            entity.setPrecio(saunaDto.precio);
            entity.setCantidadPersonas(saunaDto.cantidadPersonas);
            return entity;
        } else if (dto instanceof ServicioDTO.LavadoYPlanchadoDTO lavadoDto) {
            LavadoYPlanchado entity = new LavadoYPlanchado();
            entity.setId(lavadoDto.id);
            entity.setFecha(lavadoDto.fecha);
            entity.setPrecio(lavadoDto.precio);
            entity.setCantidadPrendas(lavadoDto.cantidadPrendas);
            return entity;
        }
        return null;
    }

    public static java.util.List<ServicioDTO> listaEntityToDto(java.util.List<Servicio> entities) {
        if (entities == null) return null;
        java.util.List<ServicioDTO> dtos = new java.util.ArrayList<>();
        for (Servicio entity : entities) {
            dtos.add(entityToDto(entity));
        }
        return dtos;
    }
    public static java.util.List<Servicio> listaDtoToEntity(java.util.List<ServicioDTO> dtos) {
        if (dtos == null) return null;
        java.util.List<Servicio> entities = new java.util.ArrayList<>();
        for (ServicioDTO dto : dtos) {
            entities.add(dtoToEntity(dto));
        }
        return entities;
    }
}
