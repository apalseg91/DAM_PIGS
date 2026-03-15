/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package controller;

import Model.Cliente;
import Model.Reserva;
import Model.Usuario;
import java.util.List;
import javax.swing.JOptionPane;
import service.ActividadDiaService;
import service.ActividadService;
import service.ClienteService;
import service.FacturaService;
import service.ReservaService;
import util.Session;
import view.ClienteJFrame;
import view.FormReservarActividadJFrame;
import view.MisDatosClienteJDialog;

/**
 * Controlador encargado de la gestión de la zona privada del cliente.
 * <p>
 * Actúa como intermediario entre la vista {@link ClienteJFrame} y la capa de
 * servicios, siguiendo el patrón MVC.
 * </p>
 *
 * Funcionalidades principales:
 * <ul>
 * <li>Listado de reservas del cliente autenticado</li>
 * <li>Creación de nuevas reservas de actividades</li>
 * <li>Cancelación de reservas existentes</li>
 * <li>Consulta de los datos personales del cliente</li>
 * </ul>
 *
 * El cliente autenticado se obtiene a través de la sesión actual gestionada por
 * {@link Session}.
 *
 * @author Alejandro
 * @version 1.0
 *
 */
public class ClienteController {

    /**
     * Vista principal del cliente
     */
    private final ClienteJFrame view;

    /**
     * Servicio de gestión de reservas
     */
    private final ReservaService reservaService;

    /**
     * Servicio de gestión de clientes
     */
    private final ClienteService clienteService;

    /**
     * Servicio de gestión de actividades
     */
    private final ActividadService actividadService;

    /**
     * Servicio de relación actividad-día
     */
    private final ActividadDiaService actividadDiaService;

    /**
     * Constructor del controlador del cliente.
     *
     * @param view vista principal del cliente
     * @param reservaService servicio de reservas
     * @param clienteService servicio de clientes
     * @param actividadService servicio de actividades
     * @param actividadDiaService servicio de relación actividad-día
     */
    public ClienteController(
            ClienteJFrame view,
            ReservaService reservaService,
            ClienteService clienteService,
            ActividadService actividadService,
            ActividadDiaService actividadDiaService
    ) {
        this.view = view;
        this.reservaService = reservaService;
        this.clienteService = clienteService;
        this.actividadService = actividadService;
        this.actividadDiaService = actividadDiaService;

        initController();
        cargarReservas();
        cargarClienteSesion();
        new FacturaController(view, new FacturaService(), clienteService);

    }

    /**
     * Inicializa los listeners de los botones de la vista del cliente.
     */
    private void initController() {
        view.getJButtonNuevaReserva().addActionListener(e -> nuevaReserva());
        view.getJButtonCancelarReserva().addActionListener(e -> cancelarReserva());
        view.getJButtonMisDatos().addActionListener(e -> verMisDatos());
    }

    /**
     * Carga las reservas del cliente autenticado y las muestra en la vista.
     *
     * @throws IllegalStateException si no existe un cliente asociado al usuario
     * autenticado
     */
    private void cargarReservas() {

        Usuario usuario = Session.getUsuarioActual();
        Cliente cliente = clienteService.findByEmail(usuario.getEmail());

        if (cliente == null) {
            throw new IllegalStateException(
                    "No existe cliente asociado al usuario logueado");
        }

        List<Reserva> reservas
                = reservaService.obtenerReservasCliente(cliente.getIdCliente());
                view.getJTableReservas().removeColumn(
                view.getJTableReservas().getColumnModel().getColumn(0)
        );

        view.setReservas(reservas);
    }

    /**
     * Abre el formulario para realizar una nueva reserva de actividad.
     * <p>
     * Tras completar la reserva, se refresca automáticamente el listado de
     * reservas del cliente.
     * </p>
     */
    private void nuevaReserva() {

        FormReservarActividadJFrame form
                = new FormReservarActividadJFrame();

        new ReservarActividadController(
                form,
                actividadService,
                actividadDiaService,
                reservaService,
                clienteService,
                this::cargarReservas // callback de refresco
        );

        form.setVisible(true);
    }

    /**
     * Cancela la reserva seleccionada en la tabla de reservas, previa
     * confirmación del usuario.
     */
    private void cancelarReserva() {

        int fila = view.getJTableReservas().getSelectedRow();

        if (fila == -1) {
            JOptionPane.showMessageDialog(
                    view,
                    "Seleccione una reserva",
                    "Aviso",
                    JOptionPane.WARNING_MESSAGE
            );
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(
                view,
                "¿Desea cancelar la reserva seleccionada?",
                "Confirmar cancelación",
                JOptionPane.YES_NO_OPTION
        );

        if (confirm != JOptionPane.YES_OPTION) {
            return;
        }

        // El ID de la reserva se obtiene del modelo de la tabla (columna oculta)
        int idReserva = (int) view.getJTableReservas()
                .getModel()
                .getValueAt(fila, 0);

        reservaService.cancelarReserva(idReserva);
        cargarReservas();

        JOptionPane.showMessageDialog(
                view,
                "Reserva cancelada correctamente",
                "Cancelación",
                JOptionPane.INFORMATION_MESSAGE
        );
    }

    /**
     * Muestra el diálogo con los datos personales del cliente autenticado.
     */
    private void verMisDatos() {

        Usuario usuario = Session.getUsuarioActual();
        Cliente cliente
                = clienteService.obtenerDetalleClientePorEmail(usuario.getEmail());

        if (cliente == null) {
            JOptionPane.showMessageDialog(
                    view,
                    "No se han podido cargar los datos del cliente",
                    "Error",
                    JOptionPane.ERROR_MESSAGE
            );
            return;
        }
        MisDatosClienteJDialog dialog = new MisDatosClienteJDialog(view, true);
        dialog.setCliente(cliente);
        dialog.setLocationRelativeTo(view);
        dialog.setVisible(true);
    }

    /**
     * Carga el nombre del cliente autenticado en la cabecera del panel.
     */
    private void cargarClienteSesion() {
        Usuario usuario = Session.getUsuarioActual();
        if (usuario == null) {
            return;
        }
        Cliente cliente = clienteService.obtenerDetalleClientePorEmail(usuario.getEmail());
        if (cliente != null) {
            String nombreCompleto = cliente.getNombre() + " " + cliente.getApellidos();
            view.getJLabelSaludo()
                    .setText("¡Bienvenid@! | " + nombreCompleto);
        }
    }
}
