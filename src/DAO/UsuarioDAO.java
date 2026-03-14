package DAO;

import Model.Rol;
import Model.Usuario;
import java.util.List;

/**
 * Interfaz DAO para la entidad Usuario. Define las operaciones de acceso a
 * datos relacionadas con la autenticación y gestión completa de usuarios.
 *
 * Se limita exclusivamente a operaciones de persistencia. No contiene lógica de
 * negocio ni seguridad.
 *
 * @author Alejandro
 * @version 1.0
 */
public interface UsuarioDAO {

    /**
     * Busca un usuario por su email.
     *
     * @param email email del usuario
     * @return Usuario o null si no existe
     */
    Usuario findByEmail(String email);

    /**
     * Busca un usuario por su ID.
     *
     * @param id identificador del usuario
     * @return Usuario o null si no existe
     */
    Usuario findById(int id);

    /**
     * Devuelve todos los usuarios de un determinado rol.
     *
     * @param nombreRol nombre del rol (ADMINISTRADOR, USUARIO)
     * @return lista de usuarios
     */
    List<Usuario> findAllByRol(String nombreRol);

    /**
     * Inserta un nuevo usuario en base de datos.
     *
     * @param usuario usuario a guardar
     */
    void save(Usuario usuario);

    /**
     * Actualiza un usuario existente.
     *
     * @param usuario usuario con los datos actualizados
     */
    void update(Usuario usuario);

    /**
     * Elimina un usuario por su ID.
     *
     * @param id identificador del usuario
     */
    void delete(int id);

    /**
     * Comprueba si el usuario no tiene cliente asociado.
     *
     * @param idUsuario id del usuario
     * @return true si no tiene cliente asociado
     */
    boolean noTieneClienteAsociado(int idUsuario);
    
    int contarAdministradores();
    
    /**
 * Obtiene un rol por su nombre.
 *
 * @param nombreRol nombre del rol
 * @return objeto Rol o null si no existe
 */
Rol obtenerRolPorNombre(String nombreRol);


}
