/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package DAO;

import Model.Actividad;
import java.util.List;

/**
 * Interfaz DAO para la gestión de entidades {@link Actividad}.
 * 
 * Define las operaciones CRUD básicas y consultas específicas
 * relacionadas con la persistencia de actividades en la base de datos.
 * 
 * Forma parte de la capa de acceso a datos (Data Access Object),
 * permitiendo desacoplar la lógica de negocio de la tecnología
 * de almacenamiento utilizada.
 * 
 * Las implementaciones concretas serán responsables de ejecutar
 * las consultas SQL correspondientes.
 * 
 * @author Alejandro
 * @version 1.0
 */
public interface ActividadDAO {

    /**
     * Persiste una nueva actividad en la base de datos.
     *
     * @param actividad Objeto Actividad con los datos a almacenar.
     */
    void create(Actividad actividad);

    /**
     * Actualiza los datos de una actividad existente.
     *
     * @param actividad Objeto Actividad con la información modificada.
     */
    void update(Actividad actividad);

    /**
     * Elimina una actividad de la base de datos a partir de su identificador.
     *
     * @param idActividad Identificador único de la actividad.
     */
    void delete(int idActividad);

    /**
     * Recupera una actividad por su identificador.
     *
     * @param idActividad Identificador único de la actividad.
     * @return Objeto Actividad si existe, null en caso contrario.
     */
    Actividad findById(int idActividad);

    /**
     * Obtiene todas las actividades registradas en la base de datos.
     *
     * @return Lista de objetos Actividad.
     */
    List<Actividad> findAll();

    /**
     * Recupera las actividades asociadas a un día concreto.
     *
     * @param idDia Identificador del día.
     * @return Lista de actividades programadas para ese día.
     */
    List<Actividad> findByDia(int idDia);

}
