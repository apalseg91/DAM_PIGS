package DAO;

import Model.Dia;
import java.util.List;

/**
 * Interfaz DAO para la gestión de la entidad {@link Dia}.
 * 
 * Define las operaciones de consulta relacionadas con los días
 * disponibles en el sistema, utilizados para la planificación
 * de actividades.
 * 
 * Forma parte de la capa de acceso a datos (DAO), permitiendo
 * desacoplar la lógica de negocio del mecanismo de persistencia.
 * 
 * Las implementaciones concretas serán responsables de ejecutar
 * las consultas SQL necesarias para recuperar la información.
 * 
 * @author Alejandro
 * @version 1.0
 */
public interface DiaDAO {

    /**
     * Recupera todos los días registrados en el sistema.
     *
     * @return Lista de objetos Dia.
     */
    List<Dia> findAll();

    /**
     * Obtiene un día concreto a partir de su identificador.
     *
     * @param idDia Identificador único del día.
     * @return Objeto Dia si existe, null en caso contrario.
     */
    Dia findById(int idDia);
}
