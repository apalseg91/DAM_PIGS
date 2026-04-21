/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package util;

import java.util.regex.Pattern;

/**
 * Clase utilitaria encargada de validar los datos
 * relacionados con la entidad Cliente.
 *
 * Centraliza todas las reglas de validación
 * para mantener los controladores limpios.
 * @author Alejandro
 * @version 1.0
 */
public final class ValidadorCliente {
    private ValidadorCliente() {
        // Evita instanciación
    }

    private static final Pattern REGEX_NOMBRE =
            Pattern.compile("^[A-Za-zÁÉÍÓÚáéíóúÑñ ]{2,30}$");

    private static final Pattern REGEX_APELLIDOS =
            Pattern.compile("^[A-Za-zÁÉÍÓÚáéíóúÑñ ]{2,50}$");

    private static final Pattern REGEX_DNI =
            Pattern.compile("^[0-9]{8}[A-Z]$");

    private static final Pattern REGEX_TELEFONO =
            Pattern.compile("^[0-9]{9}$");

    private static final Pattern REGEX_EMAIL =
            Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");

    private static final Pattern REGEX_DIRECCION =
            Pattern.compile("^[A-Za-z0-9ÁÉÍÓÚáéíóúÑñºª.,\\- ]{5,100}$");

    /**
     * Valida nombre.
     */
    public static boolean validarNombre(String nombre) {
        return nombre != null && REGEX_NOMBRE.matcher(nombre).matches();
    }

    /**
     * Valida apellidos.
     */
    public static boolean validarApellidos(String apellidos) {
        return apellidos != null && REGEX_APELLIDOS.matcher(apellidos).matches();
    }

    /**
     * Valida DNI.
     */
    public static boolean validarDni(String dni) {
        return dni != null && REGEX_DNI.matcher(dni).matches();
    }

    /**
     * Valida teléfono.
     */
    public static boolean validarTelefono(String telefono) {
        return telefono != null && REGEX_TELEFONO.matcher(telefono).matches();
    }

    /**
     * Valida email.
     */
    public static boolean validarEmail(String email) {
        return email != null && REGEX_EMAIL.matcher(email).matches();
    }

    /**
     * Valida dirección.
     */
    public static boolean validarDireccion(String direccion) {
        return direccion != null && REGEX_DIRECCION.matcher(direccion).matches();
    }
}
