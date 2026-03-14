/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package service;

import DAO.ActividadDiaDAO;
import DAO.ReservaDAO;
import Model.Actividad;
import Model.ActividadDia;
import Model.Reserva;

import java.time.LocalDate;
import java.util.List;

/**
 * Servicio encargado de gestionar la lógica de negocio relacionada con las
 * reservas.
 * <p>
 * Actúa como intermediario entre la capa de presentación (controladores) y la
 * capa de acceso a datos (DAO), aplicando todas las reglas de negocio antes de
 * persistir o modificar una reserva.
 * </p>
 *
 * <h2>Responsabilidades principales:</h2>
 * <ul>
 * <li>Crear reservas validando reglas de negocio</li>
 * <li>Cancelar reservas existentes</li>
 * <li>Consultar reservas por cliente</li>
 * <li>Controlar aforo máximo por actividad</li>
 * <li>Evitar reservas duplicadas</li>
 * <li>Impedir reservas en fechas pasadas</li>
 * </ul>
 *
 * @author Alejandro
 * @version 1.0
 */
public class ReservaService {

    /**
     * DAO de acceso a datos de reservas
     */
    private final ReservaDAO reservaDAO;

    /**
     * DAO de acceso a datos de la relación actividad-día
     */
    private final ActividadDiaDAO actividadDiaDAO;

    /**
     * Constructor del servicio de reservas.
     *
     * @param reservaDAO DAO encargado de la persistencia de reservas
     * @param actividadDiaDAO DAO encargado de recuperar información de la
     * actividad asociada a la reserva
     */
    public ReservaService(
            ReservaDAO reservaDAO,
            ActividadDiaDAO actividadDiaDAO
    ) {
        this.reservaDAO = reservaDAO;
        this.actividadDiaDAO = actividadDiaDAO;
    }

    /**
     * Crea una nueva reserva aplicando previamente todas las validaciones de
     * negocio.
     *
     * @param reserva objeto reserva a persistir
     * @throws IllegalArgumentException si alguna regla de negocio no se cumple
     */
    public void crearReserva(Reserva reserva) {
        validarReserva(reserva);
        reservaDAO.create(reserva);
    }

    /**
     * Cancela una reserva existente.
     * <p>
     * La cancelación suele implicar una baja lógica (marcar como inactiva),
     * dependiendo de la implementación del DAO.
     *
     * @param idReserva identificador de la reserva a cancelar
     */
    public void cancelarReserva(int idReserva) {
        reservaDAO.cancel(idReserva);
    }

    /**
     * Obtiene todas las reservas asociadas a un cliente.
     *
     * @param idCliente identificador del cliente
     * @return lista de reservas del cliente
     */
    public List<Reserva> obtenerReservasCliente(int idCliente) {
        return reservaDAO.findByCliente(idCliente);
    }

    /**
     * Obtiene únicamente las reservas activas de un cliente.
     *
     * @param idCliente identificador del cliente
     * @return lista de reservas activas
     */
    public List<Reserva> obtenerReservasActivasCliente(int idCliente) {
        return reservaDAO.findActivasByCliente(idCliente);
    }

    /**
     * Comprueba si existe aforo disponible para una actividad en una fecha
     * concreta.
     *
     * @param ad relación actividad-día
     * @param fecha fecha de la clase
     * @return {@code true} si existen plazas disponibles, {@code false} en caso
     * contrario
     */
    public boolean hayAforoDisponible(ActividadDia ad, LocalDate fecha) {

        ActividadDia real = actividadDiaDAO.findById(ad.getIdActividadDia());

        int ocupadas = reservaDAO
                .countActivasPorActividadDiaYFecha(
                        ad.getIdActividadDia(),
                        fecha
                );

        return ocupadas < real.getActividad().getAforoMaximo();
    }

    /**
     * Valida que una reserva cumple todas las reglas de negocio antes de ser
     * creada.
     *
     * <h3>Reglas aplicadas:</h3>
     * <ul>
     * <li>La reserva no puede ser nula</li>
     * <li>Debe existir cliente válido</li>
     * <li>Debe existir actividad válida</li>
     * <li>No se pueden reservar fechas pasadas</li>
     * <li>No se puede superar el aforo máximo</li>
     * <li>No se permite doble reserva para la misma actividad y fecha</li>
     * </ul>
     *
     * @param reserva reserva a validar
     * @throws IllegalArgumentException si alguna validación falla
     */
    private void validarReserva(Reserva reserva) {

        if (reserva == null) {
            throw new IllegalArgumentException("La reserva no puede ser nula");
        }

        if (reserva.getCliente() == null) {
            throw new IllegalArgumentException("Cliente no válido");
        }

        if (reserva.getActividadDia() == null) {
            throw new IllegalArgumentException("Actividad no válida");
        }

        if (reserva.getFechaClase() == null) {
            throw new IllegalArgumentException("Debe indicarse la fecha de la clase");
        }

        // No reservar en el pasado
        if (reserva.getFechaClase().isBefore(LocalDate.now())) {
            throw new IllegalArgumentException(
                    "No se pueden reservar clases pasadas"
            );
        }

        // Recuperar actividad-día real
        ActividadDia ad = actividadDiaDAO.findById(
                reserva.getActividadDia().getIdActividadDia()
        );

        if (ad == null) {
            throw new IllegalArgumentException("La actividad no existe");
        }

        Actividad actividad = ad.getActividad();

        // Control de aforo
        int plazasOcupadas = reservaDAO
                .findByFecha(reserva.getFechaClase())
                .stream()
                .filter(r
                        -> r.getActividadDia().getIdActividadDia()
                == ad.getIdActividadDia()
                ).collect(java.util.stream.Collectors.toList())
                .size();

        if (plazasOcupadas >= actividad.getAforoMaximo()) {
            throw new IllegalArgumentException(
                    "No hay plazas disponibles para esta actividad"
            );
        }

        // Evitar doble reserva
        boolean yaReservada = reservaDAO
                .findByCliente(reserva.getCliente().getIdCliente())
                .stream()
                .anyMatch(r
                        -> r.isActiva()
                && r.getActividadDia().getIdActividadDia()
                == ad.getIdActividadDia()
                && r.getFechaClase().equals(reserva.getFechaClase())
                );

        if (yaReservada) {
            throw new IllegalArgumentException(
                    "Ya tienes una reserva para esta actividad"
            );
        }
    }
}
