/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package service;

import DAO.UsuarioDAO;
import Model.Rol;
import Model.Usuario;
import util.PasswordUtils;
import java.util.List;
import util.Session;

/**
 * Implementación del servicio de gestión de usuarios.
 *
 * Aplica las reglas de seguridad necesarias, garantizando que las contraseñas
 * siempre se almacenen hasheadas.
 *
 * @author Alejandro
 * @version 1.0
 */
public class UsuarioService {

    private final UsuarioDAO usuarioDAO;

    /**
     * Email del superadministrador del sistema.
     */
    private static final String SUPER_ADMIN_EMAIL = "admin@fitmanage.com";

    public UsuarioService(UsuarioDAO usuarioDAO) {
        this.usuarioDAO = usuarioDAO;
    }

    public List<Usuario> listarUsuariosPorRol(String nombreRol) {
        return usuarioDAO.findAllByRol(nombreRol);
    }

    public void actualizarUsuario(int idUsuario, String email, String nuevaPassword) {

        Usuario usuario = usuarioDAO.findById(idUsuario);

        if (usuario == null) {
            return;
        }

        usuario.setEmail(email);

        if (nuevaPassword != null && !nuevaPassword.trim().isEmpty()) {
            String hash = PasswordUtils.hashPassword(nuevaPassword);
            usuario.setContrasenaHash(hash);
        }

        usuarioDAO.update(usuario);
    }

    public void eliminarUsuario(int idUsuario) {
        usuarioDAO.delete(idUsuario);
    }

    /**
     * Indica si un usuario puede eliminarse físicamente.
     *
     * Un usuario no puede eliminarse si tiene un cliente asociado.
     *
     * @param idUsuario identificador del usuario
     * @return true si puede eliminarse
     */
    /*public boolean puedeEliminarUsuario(int idUsuario) {
        return usuarioDAO.noTieneClienteAsociado(idUsuario);
    }*/

    /**
     * Indica si el usuario es el superadministrador.
     */
    public boolean esSuperAdmin(int idUsuario) {

        Usuario usuario = usuarioDAO.findById(idUsuario);

        return usuario != null
                && SUPER_ADMIN_EMAIL.equalsIgnoreCase(usuario.getEmail());
    }

    /**
     * Determina si un administrador puede ser eliminado.
     */
    public boolean puedeEliminarAdministrador(int idUsuario) {

        // No se puede borrar el superadmin
        if (esSuperAdmin(idUsuario)) {
            return false;
        }

        // No puede eliminarse a sí mismo
        if (Session.getUsuarioActual().getIdUsuario() == idUsuario) {
            return false;
        }

        // Debe existir al menos un admin
        if (usuarioDAO.contarAdministradores() <= 1) {
            return false;
        }

        return true;
    }

    /**
     * Busca un usuario por su identificador.
     *
     * @param idUsuario identificador del usuario
     * @return objeto Usuario o null si no existe
     */
    public Usuario buscarPorId(int idUsuario) {
        return usuarioDAO.findById(idUsuario);
    }

    /**
     * Crea un nuevo administrador del sistema.
     *
     * La contraseña se almacena siempre hasheada mediante BCrypt.
     *
     * @param email email del nuevo administrador
     * @param password contraseña en texto plano
     */
    public void crearAdministrador(String email, String password) {

        if (email == null || email.trim().isEmpty()
                || password == null || password.trim().isEmpty()) {
            return;
        }

        // Hashear contraseña
        String hash = PasswordUtils.hashPassword(password);

        // Obtener rol ADMINISTRADOR
        Rol rolAdmin = usuarioDAO.obtenerRolPorNombre("ADMINISTRADOR");

        Usuario admin = new Usuario();
        admin.setEmail(email.trim());
        admin.setContrasenaHash(hash);
        admin.setFechaCreacion(java.time.LocalDate.now());
        admin.setRol(rolAdmin);

        usuarioDAO.save(admin);
    }

    /**
     * Actualiza los datos de un administrador.
     *
     * Reglas: - El superadministrador no puede cambiar su email. - La
     * contraseña siempre se hashea si se modifica.
     *
     * @param idUsuario identificador del administrador
     * @param nuevoEmail nuevo email
     * @param nuevaPassword nueva contraseña en texto plano (opcional)
     */
    public void actualizarAdministrador(
            int idUsuario,
            String nuevoEmail,
            String nuevaPassword
    ) {

        Usuario admin = usuarioDAO.findById(idUsuario);

        if (admin == null) {
            return;
        }

        // Si no es superadmin, puede modificar email
        if (!esSuperAdmin(idUsuario)) {
            if (nuevoEmail != null && !nuevoEmail.trim().isEmpty()) {
                admin.setEmail(nuevoEmail.trim());
            }
        }

        // Si hay nueva contraseña → hashear
        if (nuevaPassword != null && !nuevaPassword.trim().isEmpty()) {

            // Evita doble hash si viene ya hasheada
            if (!nuevaPassword.startsWith("$2a$")) {
                String hash = PasswordUtils.hashPassword(nuevaPassword);
                admin.setContrasenaHash(hash);
            }
        }

        usuarioDAO.update(admin);
    }

}
