/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package controller;

import Model.Cliente;
import Model.Usuario;
import java.sql.Connection;
import javax.swing.JOptionPane;
import service.ClienteService;
import service.FacturaService;
import util.DBConnection;
import util.Session;
import view.ClienteJFrame;

/**
 * Controlador encargado de gestionar las operaciones
 * relacionadas con la facturación del cliente.
 *
 * Actúa como intermediario entre la vista ClienteJFrame
 * y el servicio FacturaService.
 *
 * Sigue el patrón MVC delegando la lógica de negocio
 * en la capa de servicio.
 *
 * @author Alejandro
  * @version 1.0

 */
public class FacturaController {
    private final ClienteJFrame view;
    private final FacturaService facturaService;
    private final ClienteService clienteService;

    /**
     * Constructor del controlador de facturación.
     *
     * @param view Vista del cliente
     * @param facturaService Servicio de facturación
     * @param clienteService Servicio de clientes
     */
    public FacturaController(
            ClienteJFrame view,
            FacturaService facturaService,
            ClienteService clienteService
    ) {
        this.view = view;
        this.facturaService = facturaService;
        this.clienteService = clienteService;

        initController();
    }

    /**
     * Inicializa el listener del botón de descarga
     * de la última factura.
     */
    private void initController() {

        view.getJButtonDescargarFactura()
                .addActionListener(e -> descargarUltimaFactura());
    }

    /**
     * Genera y descarga la última factura
     * del cliente autenticado.
     */
    private void descargarUltimaFactura() {

        try {

            Usuario usuario = Session.getUsuarioActual();
            Cliente cliente =
                    clienteService.findByEmail(usuario.getEmail());

            if (cliente == null) {
                JOptionPane.showMessageDialog(
                        view,
                        "No se ha encontrado el cliente asociado.",
                        "Error",
                        JOptionPane.ERROR_MESSAGE
                );
                return;
            }

            Connection conn = DBConnection.getConnection();
            facturaService.generarUltimaFacturaPDF(
                    cliente.getDni(),
                    conn
            );
            JOptionPane.showMessageDialog(
                    view,
                    "Factura generada correctamente.",
                    "Descarga completada",
                    JOptionPane.INFORMATION_MESSAGE
            );

        } catch (Exception ex) {

            ex.printStackTrace();

            JOptionPane.showMessageDialog(
                    view,
                    "Error al generar la factura.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }
}
