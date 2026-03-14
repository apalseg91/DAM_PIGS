/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package controller;

import service.InformeService;
import view.GenerarInformeJDialog;

import javax.swing.*;
import java.sql.Connection;
import java.text.SimpleDateFormat;
import java.util.Date;
import util.DBConnection;

/**
 * Controlador encargado de gestionar la generación de informes desde la vista
 * GenerarInformeJDialog.
 *
 * Aplica patrón MVC delegando la lógica de negocio en el servicio
 * InformeService.
 *
 * @author Alejandro
  * @version 1.0

 */
public class InformeController {

    private GenerarInformeJDialog view;

    /**
     * Constructor del controlador.
     *
     * @param view Vista asociada al diálogo de generación de informes.
     */
    public InformeController(GenerarInformeJDialog view) {
        this.view = view;
        initController();
    }

    /**
     * Inicializa los listeners de la vista.
     */
    private void initController() {

        view.getJCheckBoxPago().addActionListener(e
                -> view.getJCheckBoxAsistenciaClases().setSelected(false)
        );

        view.getJCheckBoxAsistenciaClases().addActionListener(e
                -> view.getJCheckBoxPago().setSelected(false)
        );
        view.getJButtonGenerarInforme().addActionListener(e -> generarInforme());
    }

    /**
     * Recoge los datos introducidos en la vista y genera el informe
     * correspondiente según el tipo seleccionado.
     */
    private void generarInforme() {

    try {

        Date fechaDesde =
                (Date) view.getJFormattedTextFieldInicio().getValue();
        Date fechaHasta =
                (Date) view.getJFormattedTextFieldFin().getValue();

        String dni = view.getJTextFieldDNI().getText();

        if (fechaDesde == null || fechaHasta == null) {
            JOptionPane.showMessageDialog(
                    view,
                    "Debe indicar el período.",
                    "Aviso",
                    JOptionPane.WARNING_MESSAGE
            );
            return;
        }

        String rutaPlantilla;

        if (view.getJCheckBoxPago().isSelected()) {
            rutaPlantilla = "resources/informe_pagos.jasper";
        } else if (view.getJCheckBoxAsistenciaClases().isSelected()) {
            rutaPlantilla = "resources/informe_reservas.jasper";
        } else {
            JOptionPane.showMessageDialog(
                    view,
                    "Seleccione un tipo de informe.",
                    "Aviso",
                    JOptionPane.WARNING_MESSAGE
            );
            return;
        }

        try (Connection conn = DBConnection.getConnection()) {

            InformeService.generarInforme(
                    rutaPlantilla,
                    fechaDesde,
                    fechaHasta,
                    dni,
                    conn
            );
        }

        JOptionPane.showMessageDialog(
                view,
                "Informe generado correctamente.",
                "Descarga completada",
                JOptionPane.INFORMATION_MESSAGE
        );

    } catch (IllegalStateException ex) {

        JOptionPane.showMessageDialog(
                view,
                ex.getMessage(),
                "Sin datos",
                JOptionPane.WARNING_MESSAGE
        );

    } catch (Exception ex) {

        ex.printStackTrace();

        JOptionPane.showMessageDialog(
                view,
                "Error al generar el informe.",
                "Error",
                JOptionPane.ERROR_MESSAGE
        );
    }
}
}
