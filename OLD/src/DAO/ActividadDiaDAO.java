/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package DAO;

import Model.ActividadDia;
import java.util.List;

/**
 * Interfaz DAO para la gestión de la entidad {@link ActividadDia}.
 *
 * Define las operaciones de persistencia relacionadas con la asociación entre
 * actividades y días, permitiendo gestionar la programación de actividades
 * dentro del sistema.
 *
 * Forma parte de la capa de acceso a datos (DAO), garantizando el
 * desacoplamiento entre la lógica de negocio y el mecanismo de almacenamiento
 * en base de datos.
 *
 * Las implementaciones concretas se encargarán de ejecutar las consultas SQL
 * necesarias.
 *
 * @author Alejandro
 * @version 1.0
 */
public interface ActividadDiaDAO {

    /**
     * Persiste una nueva relación entre una actividad y un día.
     *
     * @param actividadDia Objeto ActividadDia que contiene la asociación a
     * almacenar.
     */
    void create(ActividadDia actividadDia);

    /**
     * Elimina todas las asociaciones de días vinculadas a una actividad
     * concreta.
     *
     * @param idActividad Identificador único de la actividad.
     */
    void deleteByActividad(int idActividad);

    /**
     * Recupera todas las asociaciones de días correspondientes a una actividad.
     *
     * @param idActividad Identificador único de la actividad.
     * @return Lista de objetos ActividadDia asociados a la actividad.
     */
    List<ActividadDia> findByActividad(int idActividad);

    /**
     * Recupera una asociación actividad-día mediante su identificador.
     *
     * @param idActividadDia Identificador único de la relación ActividadDia.
     * @return Objeto ActividadDia si existe, null en caso contrario.
     */
    ActividadDia findById(int idActividadDia);

    /**
     * Marca como canceladas (estado_activa = '0') todas las reservas asociadas
     * a una actividad concreta.
     *
     * <p>
     * Este método no elimina las reservas, sino que realiza una cancelación
     * lógica para mantener el histórico del sistema y respetar las constraints
     * de integridad referencial.
     * </p>
     *
     * @param idActividad identificador de la actividad cuyas reservas se desean
     * cancelar
     */
    void cancelarReservasPorActividad(int idActividad);

    /**
     * Elimina una relación concreta entre actividad y día.
     *
     * @param idActividadDia identificador de la relación
     */
    void delete(int idActividadDia);
}
