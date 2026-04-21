/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package DAO;

import Model.Cliente;
import java.util.List;

/**
 * Interfaz DAO para la gestión de la entidad {@link Cliente}.
 *
 * Define las operaciones de persistencia y consulta relacionadas con los
 * clientes del sistema.
 *
 * Incluye soporte tanto para borrado físico como borrado lógico, así como
 * consultas específicas por estado (activo/inactivo) y búsquedas por email.
 *
 * Forma parte de la capa de acceso a datos (DAO), permitiendo desacoplar la
 * lógica de negocio del mecanismo de almacenamiento.
 *
 * Las implementaciones concretas serán responsables de ejecutar las consultas
 * SQL correspondientes.
 *
 * @author Alejandro
 * @version 1.0
 */
public interface ClienteDAO {

    /**
     * Persiste un nuevo cliente en la base de datos.
     *
     * @param cliente Objeto Cliente con los datos a almacenar.
     */
    void create(Cliente cliente);

    /**
     * Actualiza los datos de un cliente existente.
     *
     * @param cliente Objeto Cliente con la información modificada.
     */
    void update(Cliente cliente);

    /**
     * Elimina físicamente un cliente de la base de datos.
     *
     * @param idCliente Identificador único del cliente.
     */
    void delete(int idCliente); // borrado físico

    /**
     * Marca un cliente como inactivo (borrado lógico).
     *
     * @param idCliente Identificador único del cliente.
     */
    void setInactivo(int idCliente); // borrado lógico

    /**
     * Recupera un cliente mediante su identificador.
     *
     * @param idCliente Identificador único del cliente.
     * @return Objeto Cliente si existe, null en caso contrario.
     */
    Cliente findById(int idCliente);

    /**
     * Busca un cliente por su dirección de correo electrónico.
     *
     * @param email Email del cliente.
     * @return Objeto Cliente si existe, null en caso contrario.
     */
    Cliente findByEmail(String email);

    /**
     * Recupera el detalle completo de un cliente mediante su email. Puede
     * incluir información adicional obtenida mediante joins.
     *
     * @param email Email del cliente.
     * @return Objeto Cliente con información ampliada.
     */
    Cliente findDetalleByEmail(String email);

    /**
     * Obtiene todos los clientes registrados.
     *
     * @return Lista de objetos Cliente.
     */
    List<Cliente> findAll();

    /**
     * Verifica si un cliente tiene dependencias asociadas (por ejemplo reservas
     * o pagos), impidiendo su eliminación.
     *
     * @param idCliente Identificador único del cliente.
     * @return true si existen dependencias, false en caso contrario.
     */
    boolean tieneDependencias(int idCliente);

    /**
     * Comprueba si un cliente se encuentra marcado como inactivo.
     *
     * @param idCliente Identificador único del cliente.
     * @return true si está inactivo, false en caso contrario.
     */
    boolean estaInactivo(int idCliente);

    /**
     * Marca un cliente como activo nuevamente.
     *
     * @param idCliente Identificador único del cliente.
     */
    void setActivo(int idCliente);

    /**
     * Recupera únicamente los clientes activos.
     *
     * @return Lista de clientes con estado activo.
     */
    List<Cliente> findActivos();

    /**
     * Recupera únicamente los clientes inactivos.
     *
     * @return Lista de clientes con estado inactivo.
     */
    List<Cliente> findInactivos();

    /**
     * Obtiene todos los clientes con información detallada. Puede incluir datos
     * ampliados mediante joins.
     *
     * @return Lista de clientes con información completa.
     */
    List<Cliente> findAllDetalle();
}
