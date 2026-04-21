package service;

import DAO.UsuarioDAO;
import Model.Usuario;
import util.PasswordUtils;

/**
 * Implementación del servicio de autenticación del sistema.
 *
 * <p>
 * Se encarga de validar las credenciales introducidas por el usuario
 * durante el proceso de login, comprobando que:
 * </p>
 * <ul>
 *     <li>El email existe en el sistema</li>
 *     <li>La contraseña introducida coincide con el hash almacenado</li>
 * </ul>
 *
 * <p>
 * Utiliza {@link UsuarioDAO} para acceder a los datos del usuario
 * y {@link PasswordUtils} para verificar la contraseña de forma segura.
 * </p>
 *
 * <p>
 * No almacena contraseñas en texto plano, sino que compara hashes,
 * siguiendo buenas prácticas de seguridad.
 * </p>
 *
 * @author Alejandro
 * @version 1.0

 */
public class AuthServiceImpl implements AuthService {
    
    /** DAO encargado de la persistencia de usuarios */
    private final UsuarioDAO usuarioDAO;

    /**
     * Constructor del servicio de autenticación.
     *
     * @param usuarioDAO DAO de acceso a datos de usuarios
     */
    public AuthServiceImpl(UsuarioDAO usuarioDAO) {
        this.usuarioDAO = usuarioDAO;
    }

    /**
     * Intenta autenticar a un usuario con las credenciales proporcionadas.
     *
     * <p>
     * El proceso de autenticación sigue los siguientes pasos:
     * </p>
     * <ol>
     *     <li>Verifica que email y contraseña no estén vacíos</li>
     *     <li>Busca el usuario por email</li>
     *     <li>Comprueba que la contraseña coincide con el hash almacenado</li>
     * </ol>
     *
     * @param email email del usuario
     * @param password contraseña introducida en texto plano
     * @return objeto {@link Usuario} si la autenticación es correcta,
     *         o {@code null} si las credenciales no son válidas
     */
    @Override
    public Usuario login(String email, String password) {

        if (email == null || email.trim().isEmpty()
                || password == null || password.trim().isEmpty()) {
            return null;
        }

        Usuario usuario = usuarioDAO.findByEmail(email);

        if (usuario == null) {
            return null;
        }

        boolean ok = PasswordUtils.checkPassword(
                password,
                usuario.getContrasenaHash()
        );

        return ok ? usuario : null;
    } 
}
