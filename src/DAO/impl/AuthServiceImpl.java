/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package DAO.impl;

import DAO.UsuarioDAO;
import Model.Usuario;
import service.AuthService;
import util.PasswordUtils;

/**
 * Implementación del servicio de autenticación.
 *
 * Se encarga de validar las credenciales de un usuario comparando la contraseña
 * introducida con el hash almacenado en la base de datos.
 *
 * Aplica separación de responsabilidades delegando el acceso a datos en
 * {@link UsuarioDAO}.
 *
 * Compatible con Java 8.
 *
 * @author Alejandro
 */
public class AuthServiceImpl implements AuthService {

    /**
     * DAO de acceso a datos de usuario.
     */
    private final UsuarioDAO usuarioDAO;

    /**
     * Constructor del servicio de autenticación.
     *
     * @param usuarioDAO DAO encargado de la consulta de usuarios
     */
    public AuthServiceImpl(UsuarioDAO usuarioDAO) {
        this.usuarioDAO = usuarioDAO;
    }

    /**
     * Valida las credenciales del usuario.
     *
     * @param email dirección de correo introducida
     * @param password contraseña en texto plano
     * @return Usuario autenticado si las credenciales son correctas, o null en
     * caso contrario
     */
    @Override
    public Usuario login(String email, String password) {

        // Validación básica de campos
        if (email == null || email.trim().isEmpty()
                || password == null || password.trim().isEmpty()) {
            return null;
        }

        // Buscar usuario por email
        Usuario usuario = usuarioDAO.findByEmail(email);

        if (usuario == null) {
            return null;
        }

        // Verificar contraseña contra hash almacenado
        boolean ok = PasswordUtils.checkPassword(
                password,
                usuario.getContrasenaHash()
        );

        return ok ? usuario : null;
    }
}
