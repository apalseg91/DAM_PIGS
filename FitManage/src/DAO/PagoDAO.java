package DAO;

import Model.Pago;
import java.util.List;

/**
 * Interfaz DAO para la gestión de la entidad {@link Pago}.
 * 
 * Define las operaciones de persistencia y consulta relacionadas
 * con los pagos realizados por los clientes.
 * 
 * Forma parte de la capa de acceso a datos (DAO), permitiendo
 * desacoplar la lógica de negocio del mecanismo de almacenamiento.
 * 
 * Esta entidad se encuentra vinculada al módulo de facturación
 * y permite realizar consultas históricas y filtradas por periodo.
 * 
 * Las implementaciones concretas serán responsables de ejecutar
 * las consultas SQL correspondientes.
 * 
 * @author Alejandro
 * @version 1.0
 */
public interface PagoDAO {

    /**
     * Registra un nuevo pago en la base de datos.
     *
     * @param pago Objeto Pago con la información a almacenar.
     */
    void create(Pago pago);

    /**
     * Recupera todos los pagos asociados a un cliente concreto.
     *
     * @param idCliente Identificador único del cliente.
     * @return Lista de pagos realizados por el cliente.
     */
    List<Pago> findByCliente(int idCliente);

    /**
     * Obtiene los pagos correspondientes a un mes y año determinados.
     * 
     * Permite realizar consultas para informes mensuales o control
     * de ingresos por periodo.
     *
     * @param mes Número del mes (1–12).
     * @param anio Año correspondiente.
     * @return Lista de pagos realizados en el periodo indicado.
     */
    List<Pago> findByFecha(int mes, int anio);   
}
