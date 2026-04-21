/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package DAO;

import Model.Reserva;
import java.time.LocalDate;
import java.util.List;

/**
 * Interfaz DAO para la gestión de la entidad {@link Reserva}.
 * 
 * Define las operaciones de persistencia y consulta relacionadas
 * con las reservas realizadas por los clientes en actividades.
 * 
 * Forma parte de la capa de acceso a datos (DAO), permitiendo
 * desacoplar la lógica de negocio del mecanismo de almacenamiento.
 * 
 * Incluye operaciones para cancelación de reservas, consultas
 * por cliente y fecha, así como control de aforo mediante
 * el recuento de reservas activas.
 * 
 * Las implementaciones concretas serán responsables de ejecutar
 * las consultas SQL correspondientes.
 * 
 * @author Alejandro
 * @version 1.0
 */
public interface ReservaDAO {

    /**
     * Registra una nueva reserva en la base de datos.
     *
     * @param reserva Objeto Reserva con la información a almacenar.
     */
    void create(Reserva reserva);

    /**
     * Cancela una reserva existente.
     * 
     * La cancelación puede implementarse como borrado lógico
     * para conservar el histórico de reservas.
     *
     * @param idReserva Identificador único de la reserva.
     */
    void cancel(int idReserva);

    /**
     * Recupera todas las reservas asociadas a un cliente.
     *
     * @param idCliente Identificador único del cliente.
     * @return Lista de reservas del cliente.
     */
    List<Reserva> findByCliente(int idCliente);

    /**
     * Obtiene todas las reservas registradas para una fecha concreta.
     *
     * @param fecha Fecha de la reserva.
     * @return Lista de reservas correspondientes a esa fecha.
     */
    List<Reserva> findByFecha(LocalDate fecha);

    /**
     * Recupera únicamente las reservas activas de un cliente.
     *
     * @param idCliente Identificador único del cliente.
     * @return Lista de reservas activas del cliente.
     */
    List<Reserva> findActivasByCliente(int idCliente);
    
    /**
     * Cuenta el número de reservas activas para una actividad
     * concreta en una fecha determinada.
     * 
     * Este método se utiliza para controlar el aforo máximo
     * permitido antes de registrar una nueva reserva.
     *
     * @param idActividadDia Identificador de la relación actividad-día.
     * @param fecha Fecha para la que se desea realizar el recuento.
     * @return Número de reservas activas registradas.
     */
    int countActivasPorActividadDiaYFecha(int idActividadDia, LocalDate fecha);
}
