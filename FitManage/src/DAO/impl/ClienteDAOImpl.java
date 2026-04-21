/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package DAO.impl;

import DAO.ClienteDAO;
import Model.Cliente;
import util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Implementación JDBC del DAO de Cliente.
 *
 * Gestiona las operaciones CRUD y consultas específicas
 * sobre la entidad CLIENTE.
 *
 * Compatible con Java 8 para mantener integración
 * con iReport 5.6.0.
 *
 * @author Alejandro
 */
public class ClienteDAOImpl implements ClienteDAO {

    /**
 * Inserta un nuevo cliente en la base de datos.
 *
 * Utiliza la secuencia SEQ_CLIENTE para generar el ID automáticamente.
 * El cliente se crea con estado activo por defecto (activo = 1).
 */
private static final String SQL_INSERT =
        "INSERT INTO CLIENTE ( " +
        "id_cliente, nombre, apellidos, email, dni, telefono, direccion, activo " +
        ") VALUES ( " +
        "SEQ_CLIENTE.NEXTVAL, ?, ?, ?, ?, ?, ?, 1 " +
        ")";

/**
 * Actualiza los datos básicos de un cliente existente.
 *
 * No modifica el estado activo ni las fechas automáticas.
 */
private static final String SQL_UPDATE =
        "UPDATE CLIENTE SET " +
        "nombre = ?, " +
        "apellidos = ?, " +
        "email = ?, " +
        "dni = ?, " +
        "telefono = ?, " +
        "direccion = ? " +
        "WHERE id_cliente = ?";

/**
 * Elimina físicamente un cliente de la base de datos.
 *
 * Debe utilizarse únicamente cuando el cliente no tenga
 * dependencias asociadas.
 */
private static final String SQL_DELETE =
        "DELETE FROM CLIENTE " +
        "WHERE id_cliente = ?";

/**
 * Realiza la baja lógica de un cliente.
 *
 * Cambia el campo activo a 0 sin eliminar el registro.
 */
private static final String SQL_SET_INACTIVO =
        "UPDATE CLIENTE SET activo = 0 " +
        "WHERE id_cliente = ?";

/**
 * Reactiva un cliente previamente dado de baja lógica.
 *
 * Cambia el campo activo a 1.
 */
private static final String SQL_SET_ACTIVO =
        "UPDATE CLIENTE SET activo = 1 " +
        "WHERE id_cliente = ?";

/**
 * Obtiene el listado completo de clientes con información detallada.
 *
 * Incluye fechas de alta y próximo pago.
 * Se utiliza principalmente en el dashboard del administrador.
 */
private static final String SQL_FIND_ALL_DETALLE =
        "SELECT id_cliente, nombre, apellidos, email, " +
        "dni, telefono, direccion, " +
        "fecha_alta, fecha_proximo_pago, activo, " +
        "cuota_mensual " +
        "FROM CLIENTE " +
        "ORDER BY apellidos";

/**
 * Obtiene todos los clientes activos.
 *
 * Filtra por activo = 1.
 */
private static final String SQL_FIND_ACTIVOS =
        "SELECT id_cliente, nombre, apellidos, email, " +
        "dni, telefono, direccion, " +
        "fecha_alta, fecha_proximo_pago, activo, " +  "cuota_mensual " +
        "FROM CLIENTE " +
        "WHERE activo = 1 " +
        "ORDER BY apellidos";

/**
 * Obtiene todos los clientes inactivos.
 *
 * Filtra por activo = 0.
 */
private static final String SQL_FIND_INACTIVOS =
        "SELECT id_cliente, nombre, apellidos, email, " +
        "dni, telefono, direccion, " +
        "fecha_alta, fecha_proximo_pago, activo, " +  "cuota_mensual " +
        "FROM CLIENTE " +
        "WHERE activo = 0 " +
        "ORDER BY apellidos";

/**
 * Busca un cliente por su identificador único.
 */
private static final String SQL_FIND_BY_ID =
        "SELECT id_cliente, nombre, apellidos, email, " +
        "dni, telefono, direccion, " +
        "fecha_alta, fecha_proximo_pago, activo, " +  "cuota_mensual " +
        "FROM CLIENTE " +
        "WHERE id_cliente = ?";

/**
 * Busca un cliente por su dirección de email.
 *
 * La comparación se realiza en modo case-insensitive.
 */
private static final String SQL_FIND_BY_EMAIL =
        "SELECT id_cliente, nombre, apellidos, email, " +
        "dni, telefono, direccion, " +
        "fecha_alta, fecha_proximo_pago, activo, " +  "cuota_mensual " +
        "FROM CLIENTE " +
        "WHERE LOWER(email) = LOWER(?)";

/**
 * Comprueba si el cliente tiene registros asociados
 * en la tabla CLIENTE_ESTADO.
 *
 * Se utiliza para validar si puede eliminarse físicamente.
 */
private static final String SQL_TIENE_DEPENDENCIAS =
        "SELECT COUNT(*) " +
        "FROM CLIENTE_ESTADO " +
        "WHERE id_cliente = ?";

/**
 * Verifica si un cliente está actualmente inactivo.
 *
 * Devuelve el valor del campo activo.
 */
private static final String SQL_ESTA_INACTIVO =
        "SELECT activo " +
        "FROM CLIENTE " +
        "WHERE id_cliente = ?";

    /**
     * Inserta un nuevo cliente en la base de datos.
     *
     * @param cliente objeto cliente con los datos a persistir
     */
    @Override
    public void create(Cliente cliente) {

        try (Connection con = DBConnection.getConnection(); PreparedStatement ps = con.prepareStatement(SQL_INSERT)) {

            ps.setString(1, cliente.getNombre());
            ps.setString(2, cliente.getApellidos());
            ps.setString(3, cliente.getEmail());
            ps.setString(4, cliente.getDni());
            ps.setString(5, cliente.getTelefono());
            ps.setString(6, cliente.getDireccion());

            ps.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Actualiza los datos de un cliente existente.
     *
     * @param cliente cliente con los datos modificados
     */
    @Override
    public void update(Cliente cliente) {

        try (Connection con = DBConnection.getConnection(); PreparedStatement ps = con.prepareStatement(SQL_UPDATE)) {

            ps.setString(1, cliente.getNombre());
            ps.setString(2, cliente.getApellidos());
            ps.setString(3, cliente.getEmail());
            ps.setString(4, cliente.getDni());
            ps.setString(5, cliente.getTelefono());
            ps.setString(6, cliente.getDireccion());
            ps.setInt(7, cliente.getIdCliente());

            ps.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Elimina físicamente un cliente.
     *
     * @param idCliente identificador del cliente
     */
    @Override
    public void delete(int idCliente) {

        try (Connection con = DBConnection.getConnection(); PreparedStatement ps = con.prepareStatement(SQL_DELETE)) {

            ps.setInt(1, idCliente);
            ps.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Realiza baja lógica del cliente.
     *
     * @param idCliente identificador del cliente
     */
    @Override
    public void setInactivo(int idCliente) {

        try (Connection con = DBConnection.getConnection(); PreparedStatement ps = con.prepareStatement(SQL_SET_INACTIVO)) {

            ps.setInt(1, idCliente);
            ps.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Reactiva un cliente previamente inactivo.
     *
     * @param idCliente identificador del cliente
     */
    @Override
    public void setActivo(int idCliente) {

        try (Connection con = DBConnection.getConnection(); PreparedStatement ps = con.prepareStatement(SQL_SET_ACTIVO)) {

            ps.setInt(1, idCliente);
            ps.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /* =========================
       MÉTODOS DE CONSULTA
       ========================= */
    @Override
    public Cliente findById(int idCliente) {

        try (Connection con = DBConnection.getConnection(); PreparedStatement ps = con.prepareStatement(SQL_FIND_BY_ID)) {

            ps.setInt(1, idCliente);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return mapClienteDetalle(rs);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    public Cliente findByEmail(String email) {

        try (Connection con = DBConnection.getConnection(); PreparedStatement ps = con.prepareStatement(SQL_FIND_BY_EMAIL)) {

            ps.setString(1, email);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return mapClienteDetalle(rs);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    public Cliente findDetalleByEmail(String email) {
        return findByEmail(email);
    }

    @Override
    public List<Cliente> findAll() {
        return findAllDetalle();
    }

    @Override
    public List<Cliente> findAllDetalle() {

        List<Cliente> lista = new ArrayList<>();

        try (Connection con = DBConnection.getConnection(); PreparedStatement ps = con.prepareStatement(SQL_FIND_ALL_DETALLE); ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                lista.add(mapClienteDetalle(rs));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return lista;
    }

    @Override
    public List<Cliente> findActivos() {

        List<Cliente> lista = new ArrayList<>();

        try (Connection con = DBConnection.getConnection(); PreparedStatement ps = con.prepareStatement(SQL_FIND_ACTIVOS); ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                lista.add(mapClienteDetalle(rs));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return lista;
    }

    @Override
    public List<Cliente> findInactivos() {

        List<Cliente> lista = new ArrayList<>();

        try (Connection con = DBConnection.getConnection(); PreparedStatement ps = con.prepareStatement(SQL_FIND_INACTIVOS); ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                lista.add(mapClienteDetalle(rs));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return lista;
    }

    @Override
    public boolean tieneDependencias(int idCliente) {

        try (Connection con = DBConnection.getConnection(); PreparedStatement ps = con.prepareStatement(SQL_TIENE_DEPENDENCIAS)) {

            ps.setInt(1, idCliente);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return rs.getInt(1) > 0;
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return true;
    }

    @Override
    public boolean estaInactivo(int idCliente) {

        try (Connection con = DBConnection.getConnection(); PreparedStatement ps = con.prepareStatement(SQL_ESTA_INACTIVO)) {

            ps.setInt(1, idCliente);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return !rs.getBoolean("activo");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

    /* =========================
       MÉTODO AUXILIAR
       ========================= */
    /**
     * Mapea un ResultSet a un objeto Cliente completo.
     *
     * @param rs ResultSet posicionado
     * @return Cliente con todos los campos poblados
     * @throws SQLException si ocurre error de acceso
     */
    private Cliente mapClienteDetalle(ResultSet rs) throws SQLException {

        Cliente c = new Cliente();

        c.setIdCliente(rs.getInt("id_cliente"));
        c.setNombre(rs.getString("nombre"));
        c.setApellidos(rs.getString("apellidos"));
        c.setEmail(rs.getString("email"));
        c.setDni(rs.getString("dni"));
        c.setTelefono(rs.getString("telefono"));
        c.setDireccion(rs.getString("direccion"));

        if (rs.getDate("fecha_alta") != null) {
            c.setFechaAlta(rs.getDate("fecha_alta").toLocalDate());
        }

        if (rs.getDate("fecha_proximo_pago") != null) {
            c.setFechaProximoPago(
                    rs.getDate("fecha_proximo_pago").toLocalDate()
            );
        }

        c.setActivo(rs.getBoolean("activo"));
        c.setCuotaMensual(rs.getBigDecimal("cuota_mensual"));

        return c;
    }
}
