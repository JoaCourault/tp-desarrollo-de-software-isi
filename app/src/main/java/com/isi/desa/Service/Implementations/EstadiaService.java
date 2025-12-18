package com.isi.desa.Service.Implementations;

import com.isi.desa.Dao.Implementations.EstadiaDAO;
import com.isi.desa.Dao.Implementations.ReservaDAO;
import com.isi.desa.Dao.Repositories.EstadiaRepository;
import com.isi.desa.Dao.Repositories.HabitacionRepository;
import com.isi.desa.Dao.Repositories.HuespedRepository;
import com.isi.desa.Dao.Repositories.ReservaRepository;
import com.isi.desa.Dto.Estadia.CrearEstadiaRequestDTO;
import com.isi.desa.Dto.Estadia.EstadiaDTO;
import com.isi.desa.Model.Entities.Estadia.Estadia;
import com.isi.desa.Model.Entities.Habitacion.Habitacion;
import com.isi.desa.Model.Entities.Huesped.Huesped;
import com.isi.desa.Model.Entities.Reserva.Reserva;
import com.isi.desa.Model.Enums.EstadoHabitacion;
import com.isi.desa.Service.Interfaces.IEstadiaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.isi.desa.Model.Enums.EstadoReserva;
import java.math.BigDecimal;
import java.util.List;
import java.time.LocalDateTime;
import com.isi.desa.Dto.Estadia.EstadiaDetalleDTO;
import com.isi.desa.Dto.Factura.ItemFacturableDTO;
import com.isi.desa.Dto.ResponsableDePago.PayerDTO;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;

@Service
public class EstadiaService implements IEstadiaService {

    @Autowired
    private EstadiaDAO estadiaDAO;

    @Autowired
    private EstadiaRepository estadiaRepository;

    @Autowired
    private HabitacionRepository habitacionRepository;

    @Autowired
    private HuespedRepository huespedRepository;

    @Autowired
    private ReservaRepository reservaRepository;

    @Autowired
    private ReservaDAO reservaDAO;

    @Override
    @Transactional
    public EstadiaDTO ocuparHabitacion(CrearEstadiaRequestDTO request) {
        // VALIDACIONES
        if (request.getIdsHabitaciones() == null || request.getIdsHabitaciones().isEmpty()) {
            throw new IllegalArgumentException("Debe seleccionar al menos una habitación.");
        }
        if (request.getIdHuespedTitular() == null || request.getIdHuespedTitular().isBlank()) {
            throw new IllegalArgumentException("Es obligatorio designar un Titular para la estadía.");
        }

        // RECUPERAR ENTIDADES
        List<Habitacion> habitaciones = habitacionRepository.findAllById(request.getIdsHabitaciones());
        List<Huesped> todosLosHuespedes = huespedRepository.findAllById(request.getIdsHuespedes());

        // Buscamos Titular existente
        Huesped titularEntity = huespedRepository.findById(request.getIdHuespedTitular())
                .orElseThrow(() -> new IllegalArgumentException("El titular seleccionado no existe en la base de datos."));

        if (habitaciones.size() != request.getIdsHabitaciones().size()) {
            throw new IllegalArgumentException("Alguna de las habitaciones solicitadas no existe.");
        }

        // ESTADO HABITACIONES
        for (Habitacion hab : habitaciones) {
            // Solo verificamos que no esté rota.
            if (hab.getEstado() == EstadoHabitacion.FUERA_DE_SERVICIO) {
                throw new IllegalArgumentException("La habitación " + hab.getNumero() + " está fuera de servicio.");
            }
        }
        // CREAR ESTADÍA
        Estadia estadia = new Estadia();

        // (ID generado automáticamente por JPA)

        estadia.setCheckIn(request.getCheckIn().atTime(14, 0));
        estadia.setCheckOut(request.getCheckOut().atTime(10, 0));
        estadia.setCantNoches(request.getCantNoches());

        // Vinculamos Titular (columna id_huesped_titular)
        estadia.setHuesped(titularEntity);

        // GESTIÓN DE RESERVA
        if (request.getIdReserva() != null && !request.getIdReserva().isEmpty()) {
            // CASO A: Viene de una Reserva Existente -> La vinculamos
            Reserva reservaVinculada = reservaRepository.findById(request.getIdReserva())
                    .orElseThrow(() -> new RuntimeException("Reserva no encontrada con ID: " + request.getIdReserva()));

            // ACTUALIZAMOS EL ESTADO: De RESERVADA a EFECTIVIZADA
            reservaVinculada.setEstado(EstadoReserva.EFECTIVIZADA);
            reservaDAO.guardar(reservaVinculada); // Guardamos el cambio de estado
            estadia.setReserva(reservaVinculada);
        } else {
            // CASO B: Walk-In -> No hay reserva asociada
            estadia.setReserva(null);
        }

        // VALOR TOTAL
        BigDecimal total = BigDecimal.ZERO;
        for (Habitacion h : habitaciones) {
            if (h.getPrecio() != null) {
                total = total.add(h.getPrecio());
            }
        }
        total = total.multiply(new BigDecimal(request.getCantNoches()));
        estadia.setValorTotalEstadia(total);

        // RELACIONES
        estadia.setHabitaciones(habitaciones);
        estadia.setHuespedesHospedados(todosLosHuespedes);

        return estadiaDAO.save(estadia);
    }

    @Override
    public EstadiaDetalleDTO buscarDetallePorHabitacion(Integer numero) {
        // Buscar estadía activa usando el Repository
        List<Estadia> estadias = estadiaRepository.findEstadiasActivasPorNumero(numero);

        if (estadias.isEmpty()) {
            throw new IllegalArgumentException("No se encontró una estadía activa para la habitación " + numero);
        }

        Estadia estadia = estadias.get(0);

        // Mapeo al DTO
        EstadiaDetalleDTO dto = new EstadiaDetalleDTO();
        dto.setIdEstadia(estadia.getIdEstadia());
        dto.setNroHabitacion(String.valueOf(numero));

        // Crear Items
        List<ItemFacturableDTO> items = new ArrayList<>();

        // Calcular noches
        LocalDateTime now = LocalDateTime.now();
        long noches = ChronoUnit.DAYS.between(estadia.getCheckIn(), now);
        if (noches < 1) noches = 1;

        // Calcular precio unitario
        BigDecimal precioNoche = BigDecimal.ZERO;
        if (estadia.getHabitaciones() != null && !estadia.getHabitaciones().isEmpty()) {
            precioNoche = estadia.getHabitaciones().get(0).getPrecio();
        }

        // Crear Item de Alojamiento
        ItemFacturableDTO itemAlojamiento = new ItemFacturableDTO();
        itemAlojamiento.setId(estadia.getIdEstadia());
        itemAlojamiento.setDescripcion("Alojamiento Habitación " + numero);
        itemAlojamiento.setCantidad((int) noches);
        itemAlojamiento.setPrecioUnitario(precioNoche);
        itemAlojamiento.setSeleccionado(true);

        items.add(itemAlojamiento);
        dto.setItems(items);

        // Mapear Ocupantes a PayerDTO
        List<PayerDTO> ocupantes = new ArrayList<>();
        if (estadia.getHuespedesHospedados() != null) {
            for (Huesped h : estadia.getHuespedesHospedados()) {
                PayerDTO payer = new PayerDTO();
                payer.setIdResponsable(h.getIdHuesped());
                payer.setNombre(h.getNombre());
                payer.setApellido(h.getApellido());
                payer.setDni(h.getNumDoc());
                payer.setCuit(h.getCuit());
                payer.setCondicionIva(h.getPosicionIva());
                payer.setEsPersonaJuridica(false);
                ocupantes.add(payer);
            }
        }
        dto.setOcupantes(ocupantes);

        return dto;
    }
}