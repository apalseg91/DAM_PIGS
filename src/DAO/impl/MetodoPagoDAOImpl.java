package DAO.impl;

import DAO.MetodoPagoDAO;
import Model.MetodoPago;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import util.DBConnection;

/**
 * Implementación JDBC del DAO de Método de Pago.
 *
 * Gestiona las consultas sobre la entidad METODO_PAGO.
 * Se utiliza principalmente en el registro de pagos
 * dentro del módulo administrativo.
 *
 * Compatible con Java 8 para mantener integración con iReport 5.6.0.
 *
 * @author Alejandro
 */
public class MetodoPagoDAOImpl implements MetodoPagoDAO {

    /**
     * Obtiene todos los métodos de pago disponibles.
     *
     * Se ordenan alfabéticamente por nombre.
     */
    private static final String SQL_FIND_ALL =
            "SELECT id_metodo_pago, nombre_metodo, descripcion " +
            "FROM METODO_PAGO " +
            "ORDER BY nombre_metodo";

    /**
     * Busca un método de pago por su identificador.
     */
    private static final String SQL_FIND_BY_ID =
            "SELECT id_metodo_pago, nombre_metodo, descripcion " +
            "FROM METODO_PAGO " +
            "WHERE id_metodo_pago = ?";

    /**
     * Devuelve el listado completo de métodos de pago.
     *
     * @return lista de objetos MetodoPago
     */
    @Override
    public List<MetodoPago> findAll() {

        List<MetodoPago> metodos = new ArrayList<>();

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(SQL_FIND_ALL);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                MetodoPago mp = new MetodoPago();
                mp.setIdMetodoPago(rs.getInt("id_metodo_pago"));
                mp.setNombreMetodo(rs.getString("nombre_metodo"));
                mp.setDescripcion(rs.getString("descripcion"));
                metodos.add(mp);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return metodos;
    }

    /**
     * Busca un método de pago concreto.
     *
     * @param id identificador del método de pago
     * @return objeto MetodoPago si existe, null en caso contrario
     */
    @Override
    public MetodoPago findById(int id) {

        MetodoPago metodo = null;

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(SQL_FIND_BY_ID)) {

            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                metodo = new MetodoPago();
                metodo.setIdMetodoPago(rs.getInt("id_metodo_pago"));
                metodo.setNombreMetodo(rs.getString("nombre_metodo"));
                metodo.setDescripcion(rs.getString("descripcion"));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return metodo;
    }
}
