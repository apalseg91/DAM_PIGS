/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package service;

import java.io.File;
import java.io.InputStream;
import java.sql.Connection;
import java.util.HashMap;
import java.util.Map;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.util.JRLoader;

/**
 * Servicio encargado de la generación y exportación de facturas en formato PDF
 * mediante JasperReports.
 *
 * Esta clase pertenece al dominio de facturación y se encarga exclusivamente de
 * la lógica relacionada con documentos de factura.
 *
 * Aplica el principio de responsabilidad única (SRP), manteniendo separada la
 * lógica de reporting administrativo.
 *
 * @author Alejandro
 * @version 1.0
 *
 */
public class FacturaService {

    /**
     * Genera la última factura de un cliente y la exporta a PDF.
     *
     * @param dni DNI del cliente
     * @param connection Conexión activa a base de datos
     * @throws Exception Si ocurre un error durante la generación
     */
    public void generarUltimaFacturaPDF(
            String dni,
            Connection connection
    ) throws Exception {

        java.net.URL reportUrl
                = getClass().getResource("/reportes/informe_factura.jasper");
        System.out.println(reportUrl);
        /*InputStream reportStream =
                getClass().getResourceAsStream("/reportes/informe_factura.jasper");*/

        InputStream logoStream
                = getClass().getResourceAsStream("/img/Logo_FitManage.png");
        if (reportUrl == null) {
            throw new RuntimeException("No se encontró el reporte de factura");
        }
        /*if (reportStream == null) {
            throw new RuntimeException("No se encontró el reporte de factura");
        }*/

        if (logoStream == null) {
            throw new RuntimeException("No se encontró el logo");
        }
        JasperReport jasperReport
                = (JasperReport) JRLoader.loadObject(reportUrl);
        /*JasperReport jasperReport =
                (JasperReport) JRLoader.loadObject(reportStream);*/

        Map<String, Object> parametros = new HashMap<>();

        parametros.put("P_DNI", dni);
        parametros.put("P_LOGO", logoStream);

        JasperPrint jasperPrint = JasperFillManager.fillReport(
                jasperReport,
                parametros,
                connection
        );

        javax.swing.JFileChooser fileChooser = new javax.swing.JFileChooser();

        fileChooser.setDialogTitle("Guardar factura como PDF");
        fileChooser.setSelectedFile(new File("Factura_" + dni + ".pdf"));

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
        }
    }
}
