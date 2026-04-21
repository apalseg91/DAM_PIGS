/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package DAO.impl;

import DAO.UsuarioDAO;
import Model.Rol;
import Model.Usuario;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import util.DBConnection;

/**
 * Implementación JDBC del DAO de Usuario.
 *
 * Gestiona: - Autenticación por email - Búsqueda por ID - Listado por rol -
 * Alta, modificación y eliminación - Validaciones de integridad (cliente
 * asociado) - Control de número de administradores
 *
 * Compatible con Java 8 para mantener integración con iReport 5.6.0.
 *
 * Sigue el patrón DAO dentro de la arquitectura MVC del proyecto.
 *
 * @author Alejandro
 */
public class UsuarioDAOImpl implements UsuarioDAO {

    /**
     * Busca un usuario por email (case-insensitive).
     */
    private static final String SQL_FIND_BY_EMAIL
            = "SELECT u.id_usuario, u.email, u.contrasena_hash, u.fecha_creacion, "
            + "r.id_rol, r.nombre_rol, r.descripcion "
            + "FROM USUARIO u "
            + "JOIN ROL r ON u.id_rol = r.id_rol "
            + "WHERE LOWER(u.email) = LOWER(?)";

    /**
     * Busca un usuario por ID.
     */
    private static final String SQL_FIND_BY_ID
            = "SELECT u.id_usuario, u.email, u.contrasena_hash, u.fecha_creacion, "
            + "r.id_rol, r.nombre_rol, r.descripcion "
            + "FROM USUARIO u "
            + "JOIN ROL r ON u.id_rol = r.id_rol "
            + "WHERE u.id_usuario = ?";

    /**
     * Obtiene todos los usuarios con un rol determinado.
     */
    private static final String SQL_FIND_ALL_BY_ROL
            = "SELECT u.id_usuario, u.email, u.contrasena_hash, u.fecha_creacion, "
            + "r.id_rol, r.nombre_rol, r.descripcion "
            + "FROM USUARIO u "
            + "JOIN ROL r ON u.id_rol = r.id_rol "
            + "WHERE UPPER(r.nombre_rol) = UPPER(?) "
            + "ORDER BY u.id_usuario";

    /**
     * Inserta un nuevo usuario.
     */
    private static final String SQL_INSERT
            = "INSERT INTO USUARIO (email, contrasena_hash, id_rol) "
            + "VALUES (?, ?, ?)";

    /**
     * Actualiza email y contraseña de un usuario.
     */
    private static final String SQL_UPDATE
            = "UPDATE USUARIO SET email = ?, contrasena_hash = ? "
            + "WHERE id_usuario = ?";

    /**
     * Elimina un usuario por ID.
     */
    private static final String SQL_DELETE
            = "DELETE FROM USUARIO WHERE id_usuario = ?";

    /**
     * Comprueba si un usuario tiene cliente asociado.
     */
    private static final String SQL_TIENE_CLIENTE
            = "SELECT COUNT(*) FROM CLIENTE WHERE id_usuario = ?";

    /**
     * Cuenta el número total de administradores.
     */
    private static final String SQL_CONTAR_ADMINS
            = "SELECT COUNT(*) "
            + "FROM USUARIO u "
            + "JOIN ROL r ON u.id_rol = r.id_rol "
            + "WHERE UPPER(r.nombre_rol) = 'ADMINISTRADOR'";

    /**
     * Obtiene un rol por nombre.
     */
    private static final String SQL_OBTENER_ROL
            = "SELECT id_rol, nombre_rol, descripcion "
            + "FROM ROL "
            + "WHERE UPPER(nombre_rol) = UPPER(?)";

    /**
     * Busca usuario por email.
     *
     * @param email correo electrónico
     * @return Usuario completo o null si no existe
     */
    @Override
    public Usuario findByEmail(String email) {

        try (Connection con = DBConnection.getConnection(); PreparedStatement ps = con.prepareStatement(SQL_FIND_BY_EMAIL)) {

            ps.setString(1, email);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return mapUsuario(rs);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * Busca usuario por ID.
     */
    @Override
    public Usuario findById(int id) {

        try (Connection con = DBConnection.getConnection(); PreparedStatement ps = con.prepareStatement(SQL_FIND_BY_ID)) {

            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return mapUsuario(rs);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * Devuelve todos los usuarios con un rol determinado.
     */
    @Override
    public List<Usuario> findAllByRol(String nombreRol) {

        List<Usuario> lista = new ArrayList<>();

        try (Connection con = DBConnection.getConnection(); PreparedStatement ps = con.prepareStatement(SQL_FIND_ALL_BY_ROL)) {

            ps.setString(1, nombreRol);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                lista.add(mapUsuario(rs));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return lista;
    }

    /**
     * Guarda un nuevo usuario.
     */
    @Override
    public void save(Usuario usuario) {

        try (Connection con = DBConnection.getConnection(); PreparedStatement ps = con.prepareStatement(SQL_INSERT)) {

            ps.setString(1, usuario.getEmail());
            ps.setString(2, usuario.getContrasenaHash());
            ps.setInt(3, usuario.getRol().getIdRol());

            ps.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Actualiza un usuario existente.
     */
    @Override
    public void update(Usuario usuario) {

        try (Connection con = DBConnection.getConnection(); PreparedStatement ps = con.prepareStatement(SQL_UPDATE)) {

            ps.setString(1, usuario.getEmail());
            ps.setString(2, usuario.getContrasenaHash());
            ps.setInt(3, usuario.getIdUsuario());

            ps.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Elimina un usuario.
     */
    @Override
    public void delete(int id) {

        try (Connection con = DBConnection.getConnection(); PreparedStatement ps = con.prepareStatement(SQL_DELETE)) {

            ps.setInt(1, id);
            ps.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Comprueba que el usuario no tenga cliente asociado.
     */
    @Override
    public boolean noTieneClienteAsociado(int idUsuario) {

        try (Connection con = DBConnection.getConnection(); PreparedStatement ps = con.prepareStatement(SQL_TIENE_CLIENTE)) {

            ps.setInt(1, idUsuario);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return rs.getInt(1) == 0;
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

    /**
     * Devuelve el número total de administradores.
     */
    @Override
    public int contarAdministradores() {

        try (Connection con = DBConnection.getConnection(); PreparedStatement ps = con.prepareStatement(SQL_CONTAR_ADMINS); ResultSet rs = ps.executeQuery()) {

            if (rs.next()) {
                return rs.getInt(1);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return 0;
    }

    /**
     * Obtiene un rol por nombre.
     */
    @Override
    public Rol obtenerRolPorNombre(String nombreRol) {

        try (Connection con = DBConnection.getConnection(); PreparedStatement ps = con.prepareStatement(SQL_OBTENER_ROL)) {

            ps.setString(1, nombreRol);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return new Rol(
                        rs.getInt("id_rol"),
                        rs.getString("nombre_rol"),
                        rs.getString("descripcion")
                );
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * Mapea una fila del ResultSet a un objeto Usuario completo.
     */
    private Usuario mapUsuario(ResultSet rs) throws SQLException {

        Rol rol = new Rol(
                rs.getInt("id_rol"),
                rs.getString("nombre_rol"),
                rs.getString("descripcion")
        );

        return new Usuario(
                rs.getInt("id_usuario"),
                rs.getString("email"),
                rs.getString("contrasena_hash"),
                rs.getDate("fecha_creacion").toLocalDate(),
                rol
        );
    }
}
