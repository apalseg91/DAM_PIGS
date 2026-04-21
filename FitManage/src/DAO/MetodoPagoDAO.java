package DAO;

import Model.MetodoPago;
import java.util.List;

/**
 * Interfaz DAO para la gestión de la entidad {@link MetodoPago}.
 * 
 * Define las operaciones de consulta relacionadas con los métodos
 * de pago disponibles en el sistema.
 * 
 * Forma parte de la capa de acceso a datos (DAO), permitiendo
 * desacoplar la lógica de negocio del mecanismo de persistencia.
 * 
 * Esta entidad se utiliza principalmente en el módulo de facturación
 * para asociar cada factura con un método de pago concreto.
 * 
 * Las implementaciones concretas serán responsables de ejecutar
 * las consultas SQL correspondientes.
 * 
 * @author Alejandro
 * @version 1.0
 */
public interface MetodoPagoDAO {

   /**
    * Recupera un método de pago mediante su identificador.
    *
    * @param id Identificador único del método de pago.
    * @return Objeto MetodoPago si existe, null en caso contrario.
    */
   MetodoPago findById(int id);

   /**
    * Obtiene todos los métodos de pago disponibles en el sistema.
    *
    * @return Lista de objetos MetodoPago.
    */
   List<MetodoPago> findAll();
}
