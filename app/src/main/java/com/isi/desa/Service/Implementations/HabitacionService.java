package com.isi.desa.Service.Implementations;

import com.isi.desa.Dao.Interfaces.IHabitacionDAO;
import com.isi.desa.Dao.Repositories.ReservaRepository;
import com.isi.desa.Dto.Habitacion.DisponibilidadDiaDTO;
import com.isi.desa.Dto.Habitacion.HabitacionDTO;
import com.isi.desa.Dto.Habitacion.HabitacionDisponibilidadDTO;
import com.isi.desa.Model.Entities.Habitacion.HabitacionEntity;
import com.isi.desa.Model.Entities.Reserva.Reserva;
import com.isi.desa.Service.Interfaces.IHabitacionService;
import com.isi.desa.Utils.Mappers.HabitacionMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

@Service
public class HabitacionService implements IHabitacionService {

    @Autowired
    private IHabitacionDAO dao;

    @Autowired
    private ReservaRepository reservaRepo;

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
                .map(HabitacionMapper::entityToDTO)
                .toList();
    }

    @Override
    public List<HabitacionDisponibilidadDTO> obtenerDisponibilidad(LocalDate desde, LocalDate hasta) {
        // 1. Obtenemos todas las habitaciones del hotel
        List<HabitacionDTO> habitaciones = this.listar();
        List<HabitacionDisponibilidadDTO> resultado = new ArrayList<>();

        // 2. Calculamos la duración del rango de búsqueda (incluyendo el día final)
        long dias = ChronoUnit.DAYS.between(desde, hasta) + 1;

        // 3. Procesamos cada habitación
        for (HabitacionDTO h : habitaciones) {
            HabitacionDisponibilidadDTO fila = new HabitacionDisponibilidadDTO();
            fila.habitacion = h;
            fila.disponibilidad = new ArrayList<>();

            // 4. Consultamos a la BD solo las reservas que afecten a ESTA habitación en ESTE rango
            List<Reserva> reservasEnRango = reservaRepo.findReservasEnRango(h.id_habitacion, desde, hasta);

            // 5. Generamos la celda para cada día del rango solicitado
            for (int i = 0; i < dias; i++) {
                DisponibilidadDiaDTO dia = new DisponibilidadDiaDTO();
                LocalDate fechaActual = desde.plusDays(i);
                dia.fecha = fechaActual;

                // --- LÓGICA DE DETERMINACIÓN DE ESTADO ---
                if (h.estado != null && !h.estado.name().equals("DISPONIBLE")) {
                    dia.estado = h.estado.name();
                }
                // CASO B: La habitación está operativa, verificamos si hay reserva para hoy
                else {
                    boolean estaReservada = reservasEnRango.stream().anyMatch(reserva ->
                            // Verificamos si la fecha actual cae dentro del rango [Inicio, Fin] de la reserva
                            (fechaActual.isEqual(reserva.getFechaDesde()) || fechaActual.isAfter(reserva.getFechaDesde())) &&
                                    (fechaActual.isEqual(reserva.getFechaHasta()) || fechaActual.isBefore(reserva.getFechaHasta()))
                    );

                    if (estaReservada) {
                        dia.estado = "RESERVADA";
                    } else {
                        dia.estado = "DISPONIBLE";
                    }
                }

                fila.disponibilidad.add(dia);
            }
            resultado.add(fila);
        }
        return resultado;
    }
}