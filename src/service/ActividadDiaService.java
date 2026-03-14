/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package service;

import DAO.ActividadDiaDAO;
import Model.Actividad;
import Model.ActividadDia;
import Model.Dia;
import java.util.List;

/**
 * Servicio encargado de gestionar la relación entre
 * {@link Actividad} y {@link Dia}.
 *
 * <p>
 * Permite asignar los días en los que se imparte una actividad,
 * consultar las asociaciones existentes y generar representaciones
 * en texto para mostrar en la interfaz gráfica.
 * </p>
 *
 * <p>
 * Actúa como capa intermedia entre los controladores y el
 * {@link ActividadDiaDAO}, centralizando la lógica de asociación.
 * </p>
 *
 * @author Alejandro
 * @version 1.0

 */
public class ActividadDiaService {

    /** DAO encargado de la persistencia de la relación actividad-día */
    private final ActividadDiaDAO actividadDiaDAO;

    /**
     * Constructor del servicio.
     *
     * @param actividadDiaDAO DAO de relación actividad-día
     */
    public ActividadDiaService(ActividadDiaDAO actividadDiaDAO) {
        this.actividadDiaDAO = actividadDiaDAO;
    }

    /**
     * Asigna una lista de días a una actividad.
     *
     * <p>
     * Primero elimina las asociaciones previas para evitar duplicidades,
     * y posteriormente crea las nuevas relaciones.
     * </p>
     *
     * @param idActividad identificador de la actividad
     * @param dias lista de días seleccionados
     */
    public void asignarDiasActividad(int idActividad, List<Dia> dias) {

        // Limpia asociaciones previas
        actividadDiaDAO.deleteByActividad(idActividad);

        for (Dia dia : dias) {

            ActividadDia ad = new ActividadDia();

            Actividad act = new Actividad();
            act.setIdActividad(idActividad);

            ad.setActividad(act);
            ad.setDia(dia);

            actividadDiaDAO.create(ad);
        }
    }

    /**
     * Obtiene la lista de días asociados a una actividad.
     *
     * @param idActividad identificador de la actividad
     * @return lista de relaciones {@link ActividadDia}
     */
    public List<ActividadDia> obtenerDiasActividad(int idActividad) {
        return actividadDiaDAO.findByActividad(idActividad);
    }

    /**
     * Devuelve los días asociados a una actividad en formato texto.
     *
     * <p>
     * Genera una cadena con los códigos de los días ordenados
     * y separados por guiones.
     * </p>
     *
     * <p>
     * Ejemplo: {@code L-M-X}
     * </p>
     *
     * @param idActividad identificador de la actividad
     * @return cadena con los días formateados o cadena vacía si no tiene días
     */
    public String obtenerDiasComoTexto(int idActividad) {

        List<ActividadDia> lista = actividadDiaDAO.findByActividad(idActividad);

        return lista.stream()
                .map(ad -> ad.getDia().getCodigo())
                .sorted()
                .reduce((a, b) -> a + "-" + b)
                .orElse("");
    }
}