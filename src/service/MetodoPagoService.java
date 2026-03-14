package service;

import DAO.MetodoPagoDAO;
import Model.MetodoPago;
import java.util.List;

/**
 * Servicio encargado de gestionar los métodos de pago disponibles.
 * <p>
 * Permite consultar los métodos de pago registrados en el sistema,
 * actuando como intermediario entre la capa de presentación y el DAO.
 * </p>
 *
 * <h2>Responsabilidades:</h2>
 * <ul>
 *     <li>Obtener todos los métodos de pago</li>
 *     <li>Buscar un método de pago por su identificador</li>
 * </ul>
 *
 * @author Alejandro  
 * @version 1.0

 */
public class MetodoPagoService {

    /** DAO encargado del acceso a datos de métodos de pago */
    private final MetodoPagoDAO metodoPagoDAO;

    /**
     * Constructor del servicio de métodos de pago.
     *
     * @param metodoPagoDAO DAO responsable de la persistencia
     */
    public MetodoPagoService(MetodoPagoDAO metodoPagoDAO) {
        this.metodoPagoDAO = metodoPagoDAO;
    }

    /**
     * Obtiene todos los métodos de pago disponibles.
     *
     * @return lista completa de métodos de pago
     */
    public List<MetodoPago> obtenerTodos() {
        return metodoPagoDAO.findAll();
    }

    /**
     * Busca un método de pago por su identificador.
     *
     * @param id identificador del método de pago
     * @return método de pago correspondiente o {@code null} si no existe
     */
    public MetodoPago buscarPorId(int id) {
        return metodoPagoDAO.findById(id);
    }
}
