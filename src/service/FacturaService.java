/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package service;

import java.io.File;
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

        String rutaPlantilla = "resources/informe_factura.jasper";

        String rutaLogo = new File(
                "resources"
                + File.separator
                + "img"
                + File.separator
                + "Logo_FitManage.png"
        ).getAbsolutePath();

        JasperReport jasperReport
                = (JasperReport) JRLoader.loadObjectFromFile(rutaPlantilla);

        Map<String, Object> parametros = new HashMap<>();
    
        parametros.put("P_LOGO", rutaLogo);
        parametros.put("P_DNI", dni);

        JasperPrint jasperPrint = JasperFillManager.fillReport(
                jasperReport,
                parametros,
                connection
        );
        // Selector de ubicación
        javax.swing.JFileChooser fileChooser
                = new javax.swing.JFileChooser();

        fileChooser.setDialogTitle("Guardar factura como PDF");
        fileChooser.setSelectedFile(
                new File("Factura_" + dni + ".pdf")
        );

        int userSelection = fileChooser.showSaveDialog(null);

        if (userSelection == javax.swing.JFileChooser.APPROVE_OPTION) {

            File fileToSave = fileChooser.getSelectedFile();

            if (!fileToSave.getAbsolutePath()
                    .toLowerCase()
                    .endsWith(".pdf")) {

                fileToSave = new File(
                        fileToSave.getAbsolutePath() + ".pdf"
                );
            }

            JasperExportManager.exportReportToPdfFile(
                    jasperPrint,
                    fileToSave.getAbsolutePath()
            );
        }
    }
}
