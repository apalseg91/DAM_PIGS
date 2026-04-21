package DAO;

import java.util.List;
import Model.Factura;

/**
 * Interfaz DAO para la gestión de la entidad {@link Factura}.
 * 
 * Define las operaciones de persistencia y consulta relacionadas
 * con las facturas generadas en el sistema.
 * 
 * Forma parte de la capa de acceso a datos (DAO), permitiendo
 * desacoplar la lógica de negocio del mecanismo de almacenamiento.
 * 
 * Las implementaciones concretas serán responsables de ejecutar
 * las consultas SQL necesarias para almacenar y recuperar
 * información de facturación.
 * 
 * Esta entidad está vinculada al módulo de generación de informes
 * mediante JasperReports para la exportación en formato PDF.
 * 
 * @author Alejandro
 * @version 1.0
 */
public interface FacturaDAO {

    /**
     * Persiste una nueva factura en la base de datos.
     *
     * @param factura Objeto Factura con los datos a almacenar.
     */
    void create(Factura factura);

    /**
     * Recupera una factura a partir de su identificador.
     *
     * @param idFactura Identificador único de la factura.
     * @return Objeto Factura si existe, null en caso contrario.
     */
    Factura findById(int idFactura);

    /**
     * Obtiene todas las facturas asociadas a un cliente concreto.
     *
     * @param idCliente Identificador único del cliente.
     * @return Lista de facturas correspondientes al cliente.
     */
    List<Factura> findByCliente(int idCliente);
    
}
