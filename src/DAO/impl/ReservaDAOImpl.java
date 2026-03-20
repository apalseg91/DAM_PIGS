/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package DAO.impl;

import DAO.ReservaDAO;
import Model.Actividad;
import Model.ActividadDia;
import Model.Cliente;
import Model.Dia;
import Model.Reserva;
import util.DBConnection;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

/**
 * Implementación JDBC del DAO de Reserva.
 *
 * Gestiona la persistencia y consulta de reservas realizadas por los clientes
 * para actividades concretas.
 *
 * Incluye: - Alta de reservas - Cancelación (baja lógica) - Consultas por
 * cliente - Consultas por fecha - Control de aforo
 *
 * Compatible con Java 8 para mantener integración con iReport 5.6.0.
 *
 * @author Alejandro
 */
public class ReservaDAOImpl implements ReservaDAO {

    private static final DateTimeFormatter TIME_FMT
            = DateTimeFormatter.ofPattern("HH:mm");

    /**
     * Inserta una nueva reserva.
     *
     * Genera el ID mediante SEQ_RESERVA. La fecha de reserva se establece
     * automáticamente con SYSDATE.
     */
    private static final String SQL_INSERT
            = "INSERT INTO RESERVA "
            + "(id_reserva, id_cliente, id_act_dia, fecha_clase, fecha_reserva, estado_activa) "
            + "VALUES (SEQ_RESERVA.NEXTVAL, ?, ?, ?, SYSDATE, ?)";

    /**
     * Cancela una reserva (baja lógica).
     *
     * Cambia el estado_activa a '0'.
     */
    private static final String SQL_CANCEL
            = "UPDATE RESERVA "
            + "SET estado_activa = '0' "
            + "WHERE id_reserva = ?";

    /**
     * Obtiene reservas básicas por cliente.
     */
    private static final String SQL_FIND_BY_CLIENTE
            = "SELECT id_reserva, id_cliente, id_act_dia, fecha_clase, fecha_reserva, estado_activa "
            + "FROM RESERVA "
            + "WHERE id_cliente = ? "
            + "ORDER BY fecha_clase";

    /**
     * Obtiene reservas activas en una fecha concreta.
     */
    private static final String SQL_FIND_BY_FECHA
            = "SELECT id_reserva, id_cliente, id_act_dia, fecha_clase, fecha_reserva, estado_activa "
            + "FROM RESERVA "
            + "WHERE fecha_clase = ? "
            + "AND estado_activa = '1'";

    /**
     * Obtiene el detalle completo de reservas de un cliente.
     *
     * Incluye información de actividad y día asociado.
     */
    private static final String SQL_FIND_RESERVA_BY_CLIENTE_ID
            = "SELECT "
            + "r.id_reserva        AS id_reserva, "
            + "r.id_cliente        AS id_cliente, "
            + "r.id_act_dia        AS id_act_dia, "
            + "r.fecha_clase       AS fecha_clase, "
            + "r.fecha_reserva     AS fecha_reserva, "
            + "r.estado_activa     AS estado_activa, "
            + "a.id_actividad      AS id_actividad, "
            + "a.nombre            AS nombre_actividad, "
            + "a.hora_inicio       AS hora_inicio, "
            + "a.hora_fin          AS hora_fin, "
            + "a.aforo_maximo      AS aforo_maximo, "
            + "d.id_dia            AS id_dia, "
            + "d.codigo            AS codigo_dia, "
            + "d.nombre            AS nombre_dia "
            + "FROM RESERVA r "
            + "JOIN ACTIVIDAD_DIA ad  ON r.id_act_dia = ad.id_act_dia "
            + "JOIN ACTIVIDAD a       ON ad.id_actividad = a.id_actividad "
            + "JOIN DIA d             ON ad.id_dia = d.id_dia "
            + "WHERE r.id_cliente = ? "
            + "ORDER BY r.fecha_clase";

    /**
     * Obtiene reservas activas de un cliente.
     */
    private static final String SQL_FIND_ACTIVAS_BY_CLIENTE
            = "SELECT "
            + "r.id_reserva        AS id_reserva, "
            + "r.id_cliente        AS id_cliente, "
            + "r.id_act_dia        AS id_act_dia, "
            + "r.fecha_clase       AS fecha_clase, "
            + "r.fecha_reserva     AS fecha_reserva, "
            + "r.estado_activa     AS estado_activa, "
            + "a.id_actividad      AS id_actividad, "
            + "a.nombre            AS nombre_actividad, "
            + "a.hora_inicio       AS hora_inicio, "
            + "a.hora_fin          AS hora_fin, "
            + "a.aforo_maximo      AS aforo_maximo, "
            + "d.id_dia            AS id_dia, "
            + "d.codigo            AS codigo_dia, "
            + "d.nombre            AS nombre_dia "
            + "FROM RESERVA r "
            + "JOIN ACTIVIDAD_DIA ad  ON r.id_act_dia = ad.id_act_dia "
            + "JOIN ACTIVIDAD a       ON ad.id_actividad = a.id_actividad "
            + "JOIN DIA d             ON ad.id_dia = d.id_dia "
            + "WHERE r.id_cliente = ? "
            + "AND r.estado_activa = 1 "
            + "ORDER BY r.fecha_clase";

    /**
     * Cuenta el número de reservas activas para una actividad, en una fecha
     * concreta.
     *
     * Se utiliza para validar el aforo máximo.
     */
    private static final String SQL_COUNT_AFOTO
            = "SELECT COUNT(*) "
            + "FROM RESERVA "
            + "WHERE id_act_dia = ? "
            + "AND fecha_clase = ? "
            + "AND estado_activa = 1";

    /**
     * Crea una nueva reserva.
     *
     * <p>
     * Captura errores de base de datos (triggers de negocio) y los transforma
     * en excepciones de dominio para que puedan ser gestionadas en la capa
     * superior.
     * </p>
     *
     * @param reserva objeto reserva a persistir
     * @throws IllegalStateException si se produce una violación de reglas de
     * negocio
     */
    @Override
    public void create(Reserva reserva) {

        try (Connection con = DBConnection.getConnection(); PreparedStatement ps = con.prepareStatement(SQL_INSERT)) {

            ps.setInt(1, reserva.getCliente().getIdCliente());
            ps.setInt(2, reserva.getActividadDia().getIdActividadDia());
            ps.setDate(3, Date.valueOf(reserva.getFechaClase()));
            ps.setString(4, reserva.isActiva() ? "1" : "0");

            ps.executeUpdate();

        } catch (SQLException e) {

            /**
             * Traducción de errores Oracle a excepciones de negocio.
             */
            int errorCode = e.getErrorCode();

            if (errorCode == 20001) {
                throw new IllegalStateException(
                        "No hay plazas disponibles para esta actividad"
                );
            }

            if (errorCode == 20002) {
                throw new IllegalStateException(
                        "Ya tienes una reserva para esta actividad en esa fecha"
                );
            }

            throw new RuntimeException("Error al crear la reserva", e);
        }
    }

    /**
     * Cancela una reserva existente.
     *
     * @param idReserva identificador de la reserva
     */
    @Override
    public void cancel(int idReserva) {

        try (Connection con = DBConnection.getConnection(); PreparedStatement ps = con.prepareStatement(SQL_CANCEL)) {

            ps.setInt(1, idReserva);
            ps.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Error al cancelar la reserva", e);
        }
    }

    /**
     * Obtiene todas las reservas de un cliente.
     *
     * @param idCliente identificador del cliente
     * @return lista de reservas
     */
    @Override
    public List<Reserva> findByCliente(int idCliente) {

        List<Reserva> reservas = new ArrayList<>();

        try (Connection con = DBConnection.getConnection(); PreparedStatement ps = con.prepareStatement(SQL_FIND_RESERVA_BY_CLIENTE_ID)) {

            ps.setInt(1, idCliente);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    reservas.add(mapRowToReserva(rs));
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error al obtener reservas del cliente", e);
        }

        return reservas;
    }

    /**
     * Obtiene reservas activas en una fecha.
     *
     * @param fecha fecha de clase
     * @return lista de reservas activas
     */
    @Override
    public List<Reserva> findByFecha(LocalDate fecha) {

        List<Reserva> reservas = new ArrayList<>();

        try (Connection con = DBConnection.getConnection(); PreparedStatement ps = con.prepareStatement(SQL_FIND_BY_FECHA)) {

            ps.setDate(1, Date.valueOf(fecha));

            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                reservas.add(mapRowToReservaMin(rs));
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error al buscar reservas por fecha", e);
        }

        return reservas;
    }

    /**
     * Obtiene reservas activas de un cliente.
     */
    @Override
    public List<Reserva> findActivasByCliente(int idCliente) {

        List<Reserva> reservas = new ArrayList<>();

        try (Connection con = DBConnection.getConnection(); PreparedStatement ps = con.prepareStatement(SQL_FIND_ACTIVAS_BY_CLIENTE)) {

            ps.setInt(1, idCliente);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    reservas.add(mapRowToReserva(rs));
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error al cargar reservas activas", e);
        }

        return reservas;
    }

    /**
     * Cuenta reservas activas para una actividad y fecha concreta.
     *
     * @param idActividadDia identificador actividad_dia
     * @param fecha fecha de clase
     * @return número de reservas activas
     */
    @Override
    public int countActivasPorActividadDiaYFecha(int idActividadDia, LocalDate fecha) {

        try (Connection con = DBConnection.getConnection(); PreparedStatement ps = con.prepareStatement(SQL_COUNT_AFOTO)) {

            ps.setInt(1, idActividadDia);
            ps.setDate(2, Date.valueOf(fecha));

            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error comprobando aforo", e);
        }

        return 0;
    }

    /**
     * Mapea un ResultSet completo a un objeto Reserva con actividad y día
     * asociados.
     */
    private Reserva mapRowToReserva(ResultSet rs) throws SQLException {

        Reserva r = new Reserva();
        r.setIdReserva(rs.getInt("id_reserva"));

        Cliente c = new Cliente();
        c.setIdCliente(rs.getInt("id_cliente"));
        r.setCliente(c);

        Actividad actividad = new Actividad();
        actividad.setIdActividad(rs.getInt("id_actividad"));
        actividad.setNombre(rs.getString("nombre_actividad"));
        actividad.setHoraInicio(
                LocalTime.parse(rs.getString("hora_inicio"), TIME_FMT)
        );
        actividad.setHoraFin(
                LocalTime.parse(rs.getString("hora_fin"), TIME_FMT)
        );
        actividad.setAforoMaximo(rs.getInt("aforo_maximo"));

        Dia dia = new Dia();
        dia.setIdDia(rs.getInt("id_dia"));
        dia.setCodigo(rs.getString("codigo_dia"));
        dia.setNombre(rs.getString("nombre_dia"));

        ActividadDia ad = new ActividadDia();
        ad.setIdActividadDia(rs.getInt("id_act_dia"));
        ad.setActividad(actividad);
        ad.setDia(dia);

        r.setActividadDia(ad);
        r.setFechaClase(rs.getDate("fecha_clase").toLocalDate());

        Date fr = rs.getDate("fecha_reserva");
        if (fr != null) {
            r.setFechaReserva(fr.toLocalDate());
        }

        r.setActiva(rs.getInt("estado_activa") == 1);

        return r;
    }

    /**
     * Mapeo reducido para consultas simples.
     */
    private Reserva mapRowToReservaMin(ResultSet rs) throws SQLException {

        Reserva r = new Reserva();
        r.setIdReserva(rs.getInt("id_reserva"));
        r.setFechaClase(rs.getDate("fecha_clase").toLocalDate());
        r.setActiva(rs.getInt("estado_activa") == 1);

        ActividadDia ad = new ActividadDia();
        ad.setIdActividadDia(rs.getInt("id_act_dia"));
        r.setActividadDia(ad);

        return r;
    }
}
