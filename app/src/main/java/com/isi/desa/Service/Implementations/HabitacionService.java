package com.isi.desa.Service.Implementations;

import com.isi.desa.Dao.Interfaces.IHabitacionDAO;
import com.isi.desa.Dto.Habitacion.HabitacionDTO;
import com.isi.desa.Model.Entities.Habitacion.HabitacionEntity;
import com.isi.desa.Service.Interfaces.IHabitacionService;
import com.isi.desa.Utils.Mappers.HabitacionMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class HabitacionService implements IHabitacionService {

    @Autowired
    private IHabitacionDAO dao;

    @Override
    public HabitacionDTO crear(HabitacionDTO dto) {
        HabitacionEntity entity = HabitacionMapper.dtoToEntity(dto);
        HabitacionEntity creado = dao.crear(entity);
        return HabitacionMapper.entityToDTO(creado);
    }

    @Override
    public HabitacionDTO modificar(HabitacionDTO dto) {
        HabitacionEntity entity = HabitacionMapper.dtoToEntity(dto);
        HabitacionEntity modificado = dao.modificar(entity);
        return HabitacionMapper.entityToDTO(modificado);
    }

    @Override
    public List<HabitacionDTO> listar() {
        return dao.listar().stream()
                .map(HabitacionMapper::entityToDTO)   // 👈 acá ya coincide el tipo
                .toList();
    }
}
