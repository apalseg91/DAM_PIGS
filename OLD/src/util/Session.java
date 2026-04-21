/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

package util;

import Model.Usuario;

/**
 * Clase utilitaria que gestiona la sesión del usuario autenticado.
 *
 * @author Alejandro
 * @version 1.0
 */
public class Session {

    private static Usuario usuarioActual;

    // Evita instanciación
    private Session() {

    }

    /**
     * Inicia sesión almacenando el usuario autenticado.
     */
    public static void login(Usuario usuario) {
        usuarioActual = usuario;
    }

    /**
     * Cierra la sesión actual.
     */
    public static void logout() {
        usuarioActual = null;
    }

    /**
     * Devuelve el usuario autenticado o null si no hay sesión.
     */
    public static Usuario getUsuarioActual() {
        return usuarioActual;
    }

    /**
     * Indica si hay un usuario autenticado.
     */
    public static boolean isAuthenticated() {
        return usuarioActual != null;
    }
}
