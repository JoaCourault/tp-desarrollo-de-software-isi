package com.isi.desa.Service.Interfaces;
import com.isi.desa.Model.Entities.Huesped.Huesped;
import com.isi.desa.Dto.HuespedDTO;
import java.util.List;

public interface IHuespedService {
    // La capa de servicio recibe el DTO de criterios
     List<Huesped> buscarHuespedes(HuespedDTO criterios);
}
