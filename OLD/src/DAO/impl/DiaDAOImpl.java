package DAO.impl;

import DAO.DiaDAO;
import Model.Dia;
import util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Implementación JDBC del DAO de Día.
 *
 * Gestiona las operaciones de consulta sobre la entidad DIA.
 *
 * Se utiliza principalmente para asociar actividades
 * a días concretos de la semana.
 *
 * Compatible con Java 8 para mantener integración con iReport 5.6.0.
 *
 * @author Alejandro
 */
public class DiaDAOImpl implements DiaDAO {

    /**
     * Obtiene todos los días registrados en el sistema.
     *
     * Se ordenan por su identificador.
     */
    private static final String SQL_FIND_ALL =
            "SELECT id_dia, codigo, nombre " +
            "FROM DIA " +
            "ORDER BY id_dia";

    /**
     * Busca un día específico por su identificador.
     */
    private static final String SQL_FIND_BY_ID =
            "SELECT id_dia, codigo, nombre " +
            "FROM DIA " +
            "WHERE id_dia = ?";

    /**
     * Devuelve el listado completo de días.
     *
     * @return lista de objetos Dia
     */
    @Override
    public List<Dia> findAll() {

        List<Dia> dias = new ArrayList<>();

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(SQL_FIND_ALL);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Dia d = new Dia();
                d.setIdDia(rs.getInt("id_dia"));
                d.setCodigo(rs.getString("codigo"));
                d.setNombre(rs.getString("nombre"));
                dias.add(d);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return dias;
    }

    /**
     * Busca un día por su identificador.
     *
     * @param idDia identificador del día
     * @return objeto Dia si existe, null en caso contrario
     */
    @Override
    public Dia findById(int idDia) {

        Dia dia = null;

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(SQL_FIND_BY_ID)) {

            ps.setInt(1, idDia);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                dia = new Dia();
                dia.setIdDia(rs.getInt("id_dia"));
                dia.setCodigo(rs.getString("codigo"));
                dia.setNombre(rs.getString("nombre"));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return dia;
    }
}