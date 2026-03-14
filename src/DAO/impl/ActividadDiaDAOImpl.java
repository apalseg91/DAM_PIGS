/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

package DAO.impl;

import DAO.ActividadDiaDAO;
import Model.Actividad;
import Model.ActividadDia;
import Model.Dia;
import util.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * Implementación del DAO para la entidad {@link ActividadDia}.
 *
 * Gestiona la relación entre ACTIVIDAD y DIA,
 * permitiendo insertar, eliminar y consultar asociaciones.
 *
 * Compatible con Java 8 para mantener coherencia con iReport 5.6.0.
 *
 * @author Alejandro
 */
public class ActividadDiaDAOImpl implements ActividadDiaDAO {

    /**
     * Inserta una nueva relación actividad-día.
     */
    private static final String SQL_INSERT =
            "INSERT INTO ACTIVIDAD_DIA (id_act_dia, id_actividad, id_dia) " +
            "VALUES (SEQ_ACTIVIDAD_DIA.NEXTVAL, ?, ?)";

    /**
     * Elimina todas las relaciones asociadas a una actividad.
     */
    private static final String SQL_DELETE_BY_ACTIVIDAD =
            "DELETE FROM ACTIVIDAD_DIA " +
            "WHERE id_actividad = ?";

    /**
     * Obtiene todas las relaciones de una actividad concreta.
     */
    private static final String SQL_FIND_BY_ACTIVIDAD =
            "SELECT ad.id_act_dia, " +
            "a.id_actividad, a.nombre AS nombre_actividad, " +
            "d.id_dia, d.codigo, d.nombre AS nombre_dia " +
            "FROM ACTIVIDAD_DIA ad " +
            "JOIN ACTIVIDAD a ON a.id_actividad = ad.id_actividad " +
            "JOIN DIA d ON d.id_dia = ad.id_dia " +
            "WHERE ad.id_actividad = ?";

    /**
     * Obtiene una relación actividad-día por su ID.
     */
    private static final String SQL_FIND_BY_ID =
            "SELECT ad.id_act_dia, " +
            "a.id_actividad, a.nombre, a.descripcion, " +
            "a.hora_inicio, a.hora_fin, a.aforo_maximo, " +
            "d.id_dia, d.codigo, d.nombre " +
            "FROM ACTIVIDAD_DIA ad " +
            "JOIN ACTIVIDAD a ON a.id_actividad = ad.id_actividad " +
            "JOIN DIA d ON d.id_dia = ad.id_dia " +
            "WHERE ad.id_act_dia = ?";

    /**
     * Crea una nueva asociación entre actividad y día.
     *
     * @param actividadDia entidad a persistir
     */
    @Override
    public void create(ActividadDia actividadDia) {

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(SQL_INSERT)) {

            ps.setInt(1, actividadDia.getActividad().getIdActividad());
            ps.setInt(2, actividadDia.getDia().getIdDia());

            ps.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Elimina todas las asociaciones de una actividad.
     *
     * @param idActividad identificador de la actividad
     */
    @Override
    public void deleteByActividad(int idActividad) {

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(SQL_DELETE_BY_ACTIVIDAD)) {

            ps.setInt(1, idActividad);
            ps.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Devuelve todas las asociaciones actividad-día
     * correspondientes a una actividad concreta.
     *
     * @param idActividad identificador de la actividad
     * @return lista de asociaciones
     */
    @Override
    public List<ActividadDia> findByActividad(int idActividad) {

        List<ActividadDia> lista = new ArrayList<>();

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(SQL_FIND_BY_ACTIVIDAD)) {

            ps.setInt(1, idActividad);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {

                Actividad actividad = new Actividad();
                actividad.setIdActividad(rs.getInt("id_actividad"));
                actividad.setNombre(rs.getString("nombre_actividad"));

                Dia dia = new Dia();
                dia.setIdDia(rs.getInt("id_dia"));
                dia.setCodigo(rs.getString("codigo"));
                dia.setNombre(rs.getString("nombre_dia"));

                ActividadDia ad = new ActividadDia();
                ad.setIdActividadDia(rs.getInt("id_act_dia"));
                ad.setActividad(actividad);
                ad.setDia(dia);

                lista.add(ad);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return lista;
    }

    /**
     * Busca una asociación actividad-día por su ID.
     *
     * @param idActividadDia identificador de la relación
     * @return entidad encontrada o null
     */
    @Override
    public ActividadDia findById(int idActividadDia) {

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(SQL_FIND_BY_ID)) {

            ps.setInt(1, idActividadDia);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {

                DateTimeFormatter formatter =
                        DateTimeFormatter.ofPattern("HH:mm");

                Actividad actividad = new Actividad();
                actividad.setIdActividad(rs.getInt("id_actividad"));
                actividad.setNombre(rs.getString("nombre"));
                actividad.setDescripcion(rs.getString("descripcion"));
                actividad.setHoraInicio(
                        LocalTime.parse(rs.getString("hora_inicio"), formatter)
                );
                actividad.setHoraFin(
                        LocalTime.parse(rs.getString("hora_fin"), formatter)
                );
                actividad.setAforoMaximo(rs.getInt("aforo_maximo"));

                Dia dia = new Dia();
                dia.setIdDia(rs.getInt("id_dia"));
                dia.setCodigo(rs.getString("codigo"));
                dia.setNombre(rs.getString("nombre"));

                ActividadDia ad = new ActividadDia();
                ad.setIdActividadDia(rs.getInt("id_act_dia"));
                ad.setActividad(actividad);
                ad.setDia(dia);

                return ad;
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }
}