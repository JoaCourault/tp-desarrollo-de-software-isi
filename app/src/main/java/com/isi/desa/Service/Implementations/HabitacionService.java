package com.isi.desa.Service.Implementations;

import com.isi.desa.Dao.Interfaces.IHabitacionDAO;
import com.isi.desa.Dto.Habitacion.HabitacionDTO;
import com.isi.desa.Service.Interfaces.IHabitacionService;
import com.isi.desa.Service.Interfaces.Validators.IHabitacionValidator;
import com.isi.desa.Utils.Mappers.HabitacionMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class HabitacionService implements IHabitacionService {

    @Autowired
    private IHabitacionDAO dao;

    @Autowired
    private IHabitacionValidator validator;

    // -----------------------------------------------------------
    // CREAR HABITACIÓN
    // -----------------------------------------------------------
    @Override
    @Transactional
    public HabitacionDTO crear(HabitacionDTO dto) {

        RuntimeException err = validator.validateCreate(dto);
        if (err != null) throw err;

        // Validar duplicados por Número y Piso
        boolean duplicada = dao.listar().stream().anyMatch(h ->
                h.getNumero() != null && dto.numero != null &&
                        h.getPiso() != null && dto.piso != null &&
                        h.getNumero().equals(dto.numero) &&
                        h.getPiso().equals(dto.piso)
        );
        if (duplicada) throw new RuntimeException("Ya existe una habitación con ese número y piso.");

        // Generar ID si no viene
        if (dto.idHabitacion == null || dto.idHabitacion.isBlank()) {
            String uuidRaw = UUID.randomUUID().toString().replace("-", "");
            dto.idHabitacion = "HA_" + uuidRaw.substring(0, 15);
        } else if (dao.existsById(dto.idHabitacion)) {
            throw new RuntimeException("Ya existe una habitación con el ID: " + dto.idHabitacion);
        }

        var entity = HabitacionMapper.dtoToEntity(dto);
        var saved = dao.save(entity);

        return HabitacionMapper.entityToDTO(saved);
    }

    // -----------------------------------------------------------
    // MODIFICAR HABITACIÓN
    // -----------------------------------------------------------
    @Override
    @Transactional
    public HabitacionDTO modificar(HabitacionDTO dto) {

        RuntimeException err = validator.validateUpdate(dto);
        if (err != null) throw err;

        if (!dao.existsById(dto.idHabitacion)) {
            throw new RuntimeException("No se encontró habitación con ID: " + dto.idHabitacion);
        }

        // Validar que no choque número/piso con OTRA habitación distinta a la actual
        boolean duplicada = dao.listar().stream().anyMatch(h ->
                h.getIdHabitacion() != null &&
                        !h.getIdHabitacion().equalsIgnoreCase(dto.idHabitacion) && // Que no sea yo mismo
                        h.getNumero() != null && dto.numero != null &&
                        h.getPiso() != null && dto.piso != null &&
                        h.getNumero().equals(dto.numero) &&
                        h.getPiso().equals(dto.piso)
        );
        if (duplicada) throw new RuntimeException("Ya existe otra habitación con ese número y piso.");

        var entity = HabitacionMapper.dtoToEntity(dto);
        var saved = dao.save(entity);

        return HabitacionMapper.entityToDTO(saved);
    }

    // -----------------------------------------------------------
    // LISTAR TODAS
    // -----------------------------------------------------------
    @Override
    @Transactional(readOnly = true)
    public List<HabitacionDTO> listar() {
        return dao.listar().stream()
                .map(HabitacionMapper::entityToDTO)
                .collect(Collectors.toList());
    }
}