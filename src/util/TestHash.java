/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package util;

/**
 * Clase utilitaria utilizada únicamente para generar un hash BCrypt
 * de la contraseña inicial del sistema.
 *
 * <p>
 * En el proyecto FitManage los clientes se crean automáticamente mediante
 * un trigger de base de datos que genera también una cuenta de usuario
 * asociada. Dicho trigger asigna una contraseña inicial común para los
 * nuevos usuarios.
 * </p>
 *
 * <p>
 * Para mantener buenas prácticas de seguridad, las contraseñas no se
 * almacenan en texto plano en la base de datos, sino que se guardan
 * mediante un hash BCrypt.
 * </p>
 *
 * <p>
 * Esta clase se utilizó durante la configuración inicial del sistema para
 * generar el hash de la contraseña por defecto:
 * </p>
 *
 * <pre>
 * 12345678a
 * </pre>
 *
 * <p>
 * El hash generado fue:
 * </p>
 *
 * <pre>
 * $2a$12$TXSKSodaN4.kUR0exWBS3eu3TsxdQU9TmKLFJiBWKAwMGSunWJmx6
 * </pre>
 *
 * <p>
 * Este valor se almacena directamente en el trigger
 * <code>TRG_CLIENTE_USUARIO_BI</code>, permitiendo que los usuarios creados
 * automáticamente puedan iniciar sesión con la contraseña inicial indicada.
 * </p>
 *
 * <p>
 * La verificación de la contraseña durante el login se realiza mediante
 * {@link util.PasswordUtils#checkPassword(String, String)}, que utiliza
 * el algoritmo BCrypt para comparar la contraseña introducida con el
 * hash almacenado en la base de datos.
 * </p>
 *
 * <p>
 * Esta clase no forma parte del funcionamiento normal de la aplicación y
 * puede eliminarse una vez generado el hash necesario.
 * </p>
 *
 * @author Alejandro
 * @version 1.0
 */
public class TestHash {

    /**
     * Método principal utilizado para generar el hash BCrypt
     * de la contraseña inicial del sistema.
     *
     * @param args argumentos de línea de comandos (no utilizados)
     */
    public static void main(String[] args) {
        System.out.println(PasswordUtils.hashPassword("12345678a"));
    }
}