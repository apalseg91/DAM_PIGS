/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package service;

import DAO.DiaDAO;
import Model.Dia;
import java.util.List;

/**
 * Servicio encargado de gestionar los días de la semana
 * asociados a las actividades del gimnasio.
 *
 * <p>
 * Permite recuperar los días disponibles para asignación
 * en la planificación de actividades.
 * </p>
 *
 * Actúa como intermediario entre los controladores
 * y la capa de acceso a datos (DiaDAO).
 *
 * @author Alejandro
 * @version 1.0
 */
public class DiaService {

    /** DAO de acceso a datos de días */
    private final DiaDAO diaDAO;

    /**
     * Constructor del servicio de días.
     *
     * @param diaDAO DAO encargado de la persistencia de los días
     */
    public DiaService(DiaDAO diaDAO) {
        this.diaDAO = diaDAO;
    }

    /**
     * Obtiene todos los días registrados en el sistema.
     *
     * @return lista de objetos Dia
     */
    public List<Dia> obtenerDias() {
        return diaDAO.findAll();
    }
}