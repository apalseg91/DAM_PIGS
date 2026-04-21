package DAO;

import Model.Rol;
import java.util.List;

/**
 * Interfaz DAO para la gestión de la entidad {@link Rol}.
 * 
 * Define las operaciones de consulta relacionadas con los roles
 * del sistema, utilizados para la gestión de permisos y control
 * de acceso de los usuarios.
 * 
 * Forma parte de la capa de acceso a datos (DAO), permitiendo
 * desacoplar la lógica de negocio del mecanismo de persistencia.
 * 
 * Los roles determinan el nivel de acceso a las funcionalidades
 * del sistema (por ejemplo, administrador o cliente).
 * 
 * Las implementaciones concretas serán responsables de ejecutar
 * las consultas SQL correspondientes.
 * 
 * @author Alejandro
 * @version 1.0
 */
public interface RolDAO {

    /**
     * Recupera un rol mediante su identificador.
     *
     * @param id Identificador único del rol.
     * @return Objeto Rol si existe, null en caso contrario.
     */
    Rol findById(int id);

    /**
     * Recupera un rol a partir de su nombre.
     *
     * @param nombre Nombre del rol.
     * @return Objeto Rol si existe, null en caso contrario.
     */
    Rol findByNombre(String nombre);

    /**
     * Obtiene todos los roles registrados en el sistema.
     *
     * @return Lista de objetos Rol.
     */
    List<Rol> findAll();
}
