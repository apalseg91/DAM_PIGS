/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package service;

import java.io.File;
import java.io.InputStream;
import java.sql.Connection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.util.JRLoader;

/**
 * Servicio encargado de la generación de informes mediante JasperReports.
 *
 * Esta clase centraliza toda la lógica relacionada con: - Compilación de
 * plantillas .jrxml - Paso de parámetros - Rellenado del informe - Generado de
 * PDF
 *
 * Se mantiene separada del controlador para respetar el patrón MVC.
 *
 * @author Alejandro
 * @version 1.0
 *
 */
public class InformeService {

    /**
     * Genera un informe Jasper y lo exporta a PDF en la ubicación seleccionada
     * por el usuario.
     *
     * @param rutaJasper Ruta del archivo .jasper
     * @param fechaDesde Fecha inicial del período (obligatoria)
     * @param fechaHasta Fecha final del período (obligatoria)
     * @param dni DNI del cliente (puede ser null o vacío)
     * @param connection Conexión activa a base de datos
     * @throws Exception Si ocurre un error durante la generación
     */
    public static boolean generarInforme(
            String rutaJasper,
            Date fechaDesde,
            Date fechaHasta,
            String dni,
            Connection connection) throws Exception {

        if (fechaDesde == null || fechaHasta == null) {
            throw new IllegalArgumentException("Las fechas no pueden ser nulas.");
        }
        //InputStream reportStream
        // = InformeService.class.getResourceAsStream("/reportes/" + rutaJasper);
        java.net.URL reportUrl
                = InformeService.class.getResource("/reportes/" + rutaJasper);

        InputStream logoStream
                = InformeService.class.getResourceAsStream("/img/Logo_FitManage.png");

        // if (reportStream == null) {
        // throw new RuntimeException("No se encontró el reporte: " + rutaJasper);
        //}
        if (reportUrl == null) {
            throw new RuntimeException("No se encontró el reporte: " + rutaJasper);
        }

        if (logoStream == null) {
            throw new RuntimeException("No se encontró el logo");
        }
        JasperReport jasperReport
                = (JasperReport) JRLoader.loadObject(reportUrl);
        //JasperReport jasperReport
        //= (JasperReport) JRLoader.loadObject(reportStream);

        Map<String, Object> parametros = new HashMap<>();

        parametros.put("P_LOGO", logoStream);

        parametros.put("P_DNI",
                (dni == null || dni.trim().isEmpty()) ? "%" : dni.trim());

        parametros.put("P_DESDE",
                new java.sql.Timestamp(fechaDesde.getTime()));

        parametros.put("P_HASTA",
                new java.sql.Timestamp(fechaHasta.getTime()));

        JasperPrint jasperPrint = JasperFillManager.fillReport(
                jasperReport,
                parametros,
                connection
        );

        if (jasperPrint.getPages().isEmpty()) {
            throw new IllegalStateException(
                    "El informe no contiene datos para los criterios seleccionados."
            );
        }

        javax.swing.JFileChooser fileChooser = new javax.swing.JFileChooser();

        fileChooser.setDialogTitle("Guardar informe como PDF");

        String nombreArchivo
                = (dni == null || dni.trim().isEmpty())
                ? "Informe_general.pdf"
                : "Informe_" + dni.trim() + ".pdf";

        fileChooser.setSelectedFile(new File(nombreArchivo));

        int userSelection = fileChooser.showSaveDialog(null);

        if (userSelection == javax.swing.JFileChooser.APPROVE_OPTION) {

            File fileToSave = fileChooser.getSelectedFile();

            if (!fileToSave.getAbsolutePath().toLowerCase().endsWith(".pdf")) {
                fileToSave = new File(fileToSave.getAbsolutePath() + ".pdf");
            }

            JasperExportManager.exportReportToPdfFile(
                    jasperPrint,
                    fileToSave.getAbsolutePath()
            );
            return true;
        }
        return false;
    }

}
