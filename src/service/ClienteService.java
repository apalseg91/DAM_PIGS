/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package service;

import DAO.ClienteDAO;
import Model.Cliente;
import java.util.List;

/**
 * Servicio encargado de gestionar la lógica de negocio relacionada con los
 * clientes.
 *
 * <p>
 * Actúa como intermediario entre la capa de presentación (controladores y
 * vistas) y la capa de acceso a datos ({@link ClienteDAO}), encapsulando todas
 * las operaciones sobre clientes y evitando que la interfaz gráfica acceda
 * directamente a la base de datos.
 * </p>
 *
 * <h2>Responsabilidades principales:</h2>
 * <ul>
 * <li>Alta de nuevos clientes</li>
 * <li>Modificación de datos</li>
 * <li>Baja lógica y eliminación física</li>
 * <li>Control de dependencias antes de eliminar</li>
 * <li>Gestión de estados (activo/inactivo)</li>
 * <li>Comprobación de disponibilidad de email</li>
 * <li>Consultas generales y detalladas</li>
 * </ul>
 *
 * @author Alejandro
 * @version 1.0
 *
 */
public class ClienteService {

    /**
     * DAO encargado de la persistencia de clientes
     */
    private final ClienteDAO clienteDAO;

    /**
     * Constructor del servicio de clientes.
     *
     * @param clienteDAO DAO de acceso a datos de clientes
     */
    public ClienteService(ClienteDAO clienteDAO) {
        this.clienteDAO = clienteDAO;
    }

    /**
     * Obtiene todos los clientes (consulta básica).
     *
     * @return lista completa de clientes
     */
    public List<Cliente> obtenerClientes() {
        return clienteDAO.findAll();
    }

    /**
     * Crea un nuevo cliente en el sistema.
     *
     * @param cliente objeto Cliente a persistir
     */
    public void crearCliente(Cliente cliente) {
        clienteDAO.create(cliente);
    }

    /**
     * Realiza una baja lógica del cliente (lo marca como inactivo).
     *
     * @param idCliente identificador del cliente
     */
    public void setInactivoCliente(int idCliente) {
        clienteDAO.setInactivo(idCliente);
    }

    /**
     * Elimina físicamente un cliente de la base de datos.
     *
     * @param idCliente identificador del cliente
     */
    public void eliminarCliente(int idCliente) {
        clienteDAO.delete(idCliente);
    }

    /**
     * Comprueba si un cliente puede eliminarse (sin considerar si está activo o
     * no).
     *
     * @param idCliente identificador del cliente
     * @return {@code true} si no tiene dependencias asociadas
     */
    public boolean puedeEliminarCliente(int idCliente) {
        return !clienteDAO.tieneDependencias(idCliente);
    }

    /**
     * Comprueba si un cliente puede eliminarse físicamente.
     *
     * <p>
     * Condiciones:</p>
     * <ul>
     * <li>Debe estar inactivo</li>
     * <li>No debe tener dependencias asociadas</li>
     * </ul>
     *
     * @param idCliente identificador del cliente
     * @return {@code true} si cumple las condiciones
     */
    public boolean puedeEliminarFisicamente(int idCliente) {
        return clienteDAO.estaInactivo(idCliente)
                && !clienteDAO.tieneDependencias(idCliente);
    }

    /**
     * Actualiza los datos de un cliente existente.
     *
     * @param cliente objeto Cliente con los nuevos datos
     */
    public void actualizarCliente(Cliente cliente) {
        clienteDAO.update(cliente);
    }

    /**
     * Reactiva un cliente previamente dado de baja.
     *
     * @param idCliente identificador del cliente
     */
    public void setActivoCliente(int idCliente) {
        clienteDAO.setActivo(idCliente);
    }

    /**
     * Comprueba si un email está disponible para su uso.
     *
     * <p>
     * Se utiliza tanto en altas como en modificaciones. Permite reutilizar el
     * mismo email si pertenece al mismo cliente.
     * </p>
     *
     * @param email email a comprobar
     * @param idClienteActual identificador del cliente actual (null en altas)
     * @return {@code true} si el email puede utilizarse
     */
    public boolean emailDisponible(String email, Integer idClienteActual) {

        Cliente existente = clienteDAO.findByEmail(email);

        if (existente == null) {
            return true; // no existe -> es un alta nueva
        }

        if (idClienteActual != null
                && existente.getIdCliente() == idClienteActual) {
            return true;
        }

        return false; // email usado por otro cliente
    }

    /**
     * Obtiene todos los clientes activos.
     *
     * @return lista de clientes activos
     */
    public List<Cliente> obtenerClientesActivos() {
        return clienteDAO.findActivos();
    }

    /**
     * Obtiene todos los clientes inactivos.
     *
     * @return lista de clientes inactivos
     */
    public List<Cliente> obtenerClientesInactivos() {
        return clienteDAO.findInactivos();
    }

    /**
     * Busca un cliente por su identificador.
     *
     * @param idCliente identificador del cliente
     * @return cliente si existe, o {@code null}
     */
    public Cliente buscarPorId(int idCliente) {
        return clienteDAO.findById(idCliente);
    }

    /**
     * Busca un cliente por su email.
     *
     * @param email email del cliente
     * @return cliente si existe, o {@code null}
     */
    public Cliente findByEmail(String email) {
        return clienteDAO.findByEmail(email);
    }

    /**
     * Obtiene los datos completos de un cliente a partir de su email.
     *
     * @param email email del cliente
     * @return cliente con información detallada
     */
    public Cliente obtenerDetalleClientePorEmail(String email) {

        return clienteDAO.findDetalleByEmail(email);
    }

    /**
     * Obtiene el listado completo de clientes con todos los campos detallados.
     *
     * @return lista de clientes con información completa
     */
    public List<Cliente> obtenerClientesDetalle() {
        return clienteDAO.findAllDetalle();
    }

    /**
     * Busca los datos completos de un cliente por su email.
     *
     * @param email email del cliente
     * @return cliente con información completa
     */
    public Cliente buscarDetallePorEmail(String email) {
        return clienteDAO.findDetalleByEmail(email);
    }
}
