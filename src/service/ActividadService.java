/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package service;

import DAO.ActividadDAO;
import DAO.ActividadDiaDAO;
import Model.Actividad;
import Model.Dia;
import java.util.List;

/**
 * Servicio encargado de gestionar la lógica de negocio relacionada con
 * las actividades del gimnasio.
 *
 * <p>
 * Actúa como intermediario entre la capa de presentación (controladores)
 * y la capa de acceso a datos (DAO), asegurando que:
 * </p>
 * <ul>
 *     <li>Los datos obligatorios estén correctamente informados</li>
 *     <li>Los horarios sean coherentes</li>
 *     <li>No existan solapamientos entre actividades</li>
 *     <li>El aforo máximo sea válido</li>
 * </ul>
 *
 * <p>
 * Delega la persistencia en {@link ActividadDAO} y la gestión
 * de relaciones actividad-día en {@link ActividadDiaDAO}.
 * </p>
 *
 * @author Alejandro  
 * @version 1.0

 */
public class ActividadService {

    private static final int MAX_NOMBRE_LENGTH = 50;

    /** DAO principal de actividades */
    private final ActividadDAO actividadDAO;

    /** DAO para la relación entre actividad y día */
    private final ActividadDiaDAO actividadDiaDAO;

    /**
     * Constructor del servicio de actividades.
     *
     * @param actividadDAO DAO de actividades
     * @param actividadDiaDAO DAO de relación actividad-día
     */
    public ActividadService(
            ActividadDAO actividadDAO,
            ActividadDiaDAO actividadDiaDAO
    ) {
        this.actividadDAO = actividadDAO;
        this.actividadDiaDAO = actividadDiaDAO;
    }

    /**
     * Crea una nueva actividad en el sistema.
     *
     * <p>
     * Antes de persistirla, valida que los datos sean correctos.
     * </p>
     *
     * @param actividad actividad a crear
     * @throws IllegalArgumentException si los datos no son válidos
     */
    public void crearActividad(Actividad actividad) {

        validarActividad(actividad);
        actividadDAO.create(actividad);
    }

    /**
     * Actualiza una actividad existente.
     *
     * <p>
     * Comprueba que el identificador sea válido y que los datos
     * cumplan las reglas de negocio antes de delegar en el DAO.
     * </p>
     *
     * @param actividad actividad con los nuevos datos
     * @throws IllegalArgumentException si el id es inválido o los datos no son correctos
     */
    public void actualizarActividad(Actividad actividad) {

        if (actividad.getIdActividad() <= 0) {
            throw new IllegalArgumentException("Actividad no válida");
        }

        validarActividad(actividad);
        actividadDAO.update(actividad);
    }

    /**
     * Elimina una actividad del sistema.
     *
     * <p>
     * Primero elimina las relaciones actividad-día asociadas,
     * y posteriormente elimina la actividad.
     * </p>
     *
     * @param idActividad identificador de la actividad a eliminar
     */
    public void eliminarActividad(int idActividad) {

        // 1. borrar relaciones actividad-día
        actividadDiaDAO.deleteByActividad(idActividad);

        // 2. borrar actividad
        actividadDAO.delete(idActividad);
    }

    /**
     * Busca una actividad por su identificador.
     *
     * @param idActividad identificador de la actividad
     * @return la actividad encontrada o {@code null} si no existe
     */
    public Actividad buscarPorId(int idActividad) {

        return actividadDAO.findById(idActividad);
    }

    /**
     * Obtiene el listado completo de actividades.
     *
     * @return lista de actividades registradas
     */
    public List<Actividad> obtenerActividades() {

        return actividadDAO.findAll();
    }

    /**
     * Valida que una nueva actividad no solape su horario con otras
     * actividades ya existentes en los mismos días.
     *
     * <p>
     * Se utiliza tanto en creación como en edición.
     * En modo edición se ignora la propia actividad.
     * </p>
     *
     * @param nueva actividad que se desea crear o modificar
     * @param diasSeleccionados lista de días en los que se impartirá
     * @throws IllegalArgumentException si existe solapamiento
     */
    public void validarSolapamiento(Actividad nueva, List<Dia> diasSeleccionados) {

        for (Dia dia : diasSeleccionados) {

            List<Actividad> existentes
                    = actividadDAO.findByDia(dia.getIdDia());

            for (Actividad existente : existentes) {

                // Evitar compararse consigo misma (modo edición)
                if (nueva.getIdActividad() > 0
                        && nueva.getIdActividad() == existente.getIdActividad()) {
                    continue;
                }

                boolean solapa =
                        nueva.getHoraInicio().isBefore(existente.getHoraFin())
                        && nueva.getHoraFin().isAfter(existente.getHoraInicio());

                if (solapa) {
                    throw new IllegalArgumentException(
                            "Solapamiento con " + existente.getNombre()
                            + " el " + dia.getNombre()
                            + " (" + existente.getHoraInicio()
                            + "–" + existente.getHoraFin() + ")"
                    );
                }
            }
        }
    }

    /**
     * Valida los datos obligatorios y reglas de negocio de una actividad.
     *
     * @param actividad actividad a validar
     * @throws IllegalArgumentException si no cumple las reglas
     */
    private void validarActividad(Actividad actividad) {

        if (actividad == null) {
            throw new IllegalArgumentException("La actividad no puede ser nula");
        }

        if (actividad.getNombre() == null || actividad.getNombre().trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre es obligatorio");
        }

        if (actividad.getNombre().trim().length() > MAX_NOMBRE_LENGTH) {
            throw new IllegalArgumentException(
                    "El nombre no puede superar " + MAX_NOMBRE_LENGTH + " caracteres"
            );
        }

        if (actividad.getHoraInicio() == null || actividad.getHoraFin() == null) {
            throw new IllegalArgumentException("La actividad debe tener horario");
        }

        if (!actividad.getHoraInicio().isBefore(actividad.getHoraFin())) {
            throw new IllegalArgumentException(
                    "La hora de inicio debe ser anterior a la hora de fin"
            );
        }

        if (actividad.getAforoMaximo() <= 0) {
            throw new IllegalArgumentException(
                    "El aforo máximo debe ser mayor que 0"
            );
        }
    }
}
