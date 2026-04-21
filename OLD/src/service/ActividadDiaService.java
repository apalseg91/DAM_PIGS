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
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Servicio encargado de gestionar la relación entre {@link Actividad} y
 * {@link Dia}.
 *
 * <p>
 * Permite asignar los días en los que se imparte una actividad, consultar las
 * asociaciones existentes y generar representaciones en texto para mostrar en
 * la interfaz gráfica.
 * </p>
 *
 * <p>
 * Actúa como capa intermedia entre los controladores y el
 * {@link ActividadDiaDAO}, centralizando la lógica de asociación.
 * </p>
 *
 * @author Alejandro
 * @version 1.0
 *
 */
public class ActividadDiaService {

    /**
     * DAO encargado de la persistencia de la relación actividad-día
     */
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
     * Actualiza los días asociados a una actividad de forma inteligente.
     *
     * <p>
     * En lugar de eliminar todas las relaciones (lo que provocaría errores de
     * integridad referencial), se realiza una sincronización:
     * </p>
     *
     * <ul>
     * <li>Se insertan únicamente los días nuevos</li>
     * <li>Se eliminan únicamente los días que ya no están seleccionados</li>
     * </ul>
     *
     * <p>
     * Este enfoque evita:
     * </p>
     * <ul>
     * <li>Errores ORA-02292 (FK con reservas)</li>
     * <li>Errores ORA-00001 (duplicados por UNIQUE)</li>
     * </ul>
     *
     * @param idActividad identificador de la actividad
     * @param diasNuevos lista de días seleccionados en la UI
     */
    public void asignarDiasActividad(int idActividad, List<Dia> diasNuevos) {

        // 🔹 Obtener días actuales de BD
        List<ActividadDia> actuales = actividadDiaDAO.findByActividad(idActividad);

        // 🔹 Convertir a sets de IDs para comparar
        Set<Integer> actualesIds = actuales.stream()
                .map(ad -> ad.getDia().getIdDia())
                .collect(Collectors.toSet());

        Set<Integer> nuevosIds = diasNuevos.stream()
                .map(Dia::getIdDia)
                .collect(Collectors.toSet());

        for (ActividadDia ad : actuales) {

            if (!nuevosIds.contains(ad.getDia().getIdDia())) {

                try {
                    actividadDiaDAO.delete(ad.getIdActividadDia());
                } catch (Exception e) {

                    /**
                     * Lanza excepción de negocio para que la capa superior
                     * (controller) pueda gestionarla y mostrar un mensaje al
                     * usuario.
                     */
                    throw new IllegalStateException(
                            "No se puede eliminar el día " + ad.getDia().getNombre()
                            + " porque tiene reservas activas"
                    );
                }
            }
        }
        for (Dia dia : diasNuevos) {

            if (!actualesIds.contains(dia.getIdDia())) {

                ActividadDia ad = new ActividadDia();

                Actividad act = new Actividad();
                act.setIdActividad(idActividad);

                ad.setActividad(act);
                ad.setDia(dia);

                actividadDiaDAO.create(ad);
            }
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
     * Devuelve una representación en texto de los días asociados a una
     * actividad.
     *
     * <p>
     * Los días se muestran ordenados y sin duplicados, separados por guiones.
     * Ejemplo: "L-M-X".
     * </p>
     *
     * @param idActividad identificador de la actividad
     * @return cadena con los códigos de los días
     */
    public String obtenerDiasComoTexto(int idActividad) {

        List<ActividadDia> lista = actividadDiaDAO.findByActividad(idActividad);

        return lista.stream()
                .map(ad -> ad.getDia().getCodigo())
                .distinct() 
                .sorted()
                .reduce((a, b) -> a + "-" + b)
                .orElse("");
    }
}
