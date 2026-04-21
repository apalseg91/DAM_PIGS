/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package DAO.impl;

import DAO.PagoDAO;
import Model.Pago;
import java.util.List;
import util.DBConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

/**
 * Implementación JDBC del DAO de Pago.
 *
 * Gestiona las operaciones de persistencia y consulta
 * sobre la entidad PAGO.
 *
 * Se utiliza en el módulo de gestión de cobros del sistema.
 *
 * Compatible con Java 8 para mantener integración con iReport 5.6.0.
 *
 * @author Alejandro
 */
public class PagoDAOImpl implements PagoDAO {

    /**
     * Inserta un nuevo pago en la base de datos.
     *
     * Utiliza la secuencia SEQ_PAGO para generar el ID automáticamente.
     * La fecha de pago se establece con SYSDATE.
     */
    private static final String SQL_INSERT =
            "INSERT INTO PAGO (id_pago, id_cliente, fecha_pago, importe, id_metodo_pago, concepto) " +
            "VALUES (SEQ_PAGO.NEXTVAL, ?, SYSDATE, ?, ?, ?)";

    /**
     * Obtiene todos los pagos de un cliente concreto.
     *
     * Se ordenan por fecha descendente.
     */
    private static final String SQL_FIND_BY_CLIENTE =
            "SELECT id_pago, fecha_pago, importe " +
            "FROM PAGO " +
            "WHERE id_cliente = ? " +
            "ORDER BY fecha_pago DESC";

    /**
     * Obtiene los pagos realizados en un mes y año específicos.
     *
     * Utiliza EXTRACT para filtrar por mes y año.
     */
    private static final String SQL_FIND_BY_FECHA =
            "SELECT id_pago, id_cliente, fecha_pago, importe, id_metodo_pago " +
            "FROM PAGO " +
            "WHERE EXTRACT(MONTH FROM fecha_pago) = ? " +
            "AND EXTRACT(YEAR FROM fecha_pago) = ? " +
            "ORDER BY fecha_pago DESC";

    /**
     * Inserta un nuevo pago.
     *
     * @param pago objeto Pago a persistir
     */
    @Override
    public void create(Pago pago) {

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(SQL_INSERT)) {

            ps.setInt(1, pago.getCliente().getIdCliente());
            ps.setBigDecimal(2, pago.getImporte());
            ps.setInt(3, pago.getMetodoPago().getIdMetodoPago());
            ps.setString(4, pago.getConcepto());

            ps.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Obtiene los pagos asociados a un cliente.
     *
     * @param idCliente identificador del cliente
     * @return lista de pagos
     */
    @Override
    public List<Pago> findByCliente(int idCliente) {

        List<Pago> pagos = new ArrayList<>();

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(SQL_FIND_BY_CLIENTE)) {

            ps.setInt(1, idCliente);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                Pago p = new Pago();
                p.setIdPago(rs.getInt("id_pago"));
                p.setFechaPago(rs.getDate("fecha_pago").toLocalDate());
                p.setImporte(rs.getBigDecimal("importe"));
                pagos.add(p);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return pagos;
    }

    /**
     * Obtiene los pagos realizados en un mes y año determinados.
     *
     * @param mes  mes (1-12)
     * @param anio año completo (ej. 2025)
     * @return lista de pagos filtrados
     */
    @Override
    public List<Pago> findByFecha(int mes, int anio) {

        List<Pago> pagos = new ArrayList<>();

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(SQL_FIND_BY_FECHA)) {

            ps.setInt(1, mes);
            ps.setInt(2, anio);

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                Pago p = new Pago();
                p.setIdPago(rs.getInt("id_pago"));
                p.setFechaPago(rs.getDate("fecha_pago").toLocalDate());
                p.setImporte(rs.getBigDecimal("importe"));
                pagos.add(p);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return pagos;
    }
}
