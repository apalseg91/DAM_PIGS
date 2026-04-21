/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package view;

import javax.swing.JButton;
import javax.swing.JTable;

/**
 * Interfaz que define el contrato de la vista para la gestión
 * de actividades dentro del sistema.
 *
 * Forma parte de la capa de presentación en la arquitectura MVC.
 * Permite que el controlador interactúe con los componentes de
 * la interfaz gráfica sin depender de una implementación concreta
 * de la vista.
 *
 * De esta manera se consigue desacoplar la lógica de control de
 * la implementación específica basada en Swing.
 *
 * @author Alejandro
 * @version 1.0
 */
public interface ActividadesView {

    /**
     * Devuelve la tabla que muestra el listado de actividades.
     *
     * @return JTable utilizada para visualizar las actividades.
     */
    JTable getJTableActividades();

    /**
     * Devuelve el botón utilizado para crear una nueva actividad.
     *
     * @return JButton para iniciar la creación de una actividad.
     */
    JButton getJButtonNuevaActividad();

    /**
     * Devuelve el botón utilizado para editar la actividad seleccionada.
     *
     * @return JButton para editar una actividad existente.
     */
    JButton getJButtonEditarActividad();

    /**
     * Devuelve el botón utilizado para eliminar la actividad seleccionada.
     *
     * @return JButton para eliminar una actividad.
     */
    JButton getJButtonEliminarActividad();
}
