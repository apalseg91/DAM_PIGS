/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package controller;

import Model.Actividad;
import Model.ActividadDia;
import Model.Cliente;
import Model.Dia;
import Model.Reserva;
import Model.Usuario;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JOptionPane;
import javax.swing.JSpinner;
import javax.swing.SpinnerDateModel;
import javax.swing.table.DefaultTableModel;
import service.ActividadService;
import service.ActividadDiaService;
import service.ClienteService;
import service.ReservaService;
import util.Session;
import view.FormReservarActividadJFrame;

/**
 * Controlador encargado del proceso de reserva de actividades por parte del
 * cliente.
 * <p>
 * Gestiona la interacción entre la vista {@link FormReservarActividadJFrame} y
 * la capa de servicios, siguiendo el patrón MVC.
 * </p>
 *
 * Funcionalidades principales:
 * <ul>
 * <li>Listado de actividades disponibles</li>
 * <li>Selección de día de impartición</li>
 * <li>Validación de fecha, día y aforo</li>
 * <li>Creación de reservas</li>
 * <li>Refresco del listado de reservas tras una reserva exitosa</li>
 * </ul>
 *
 * El cliente autenticado se obtiene a través de la sesión actual gestionada por
 * {@link Session}.
 *
 * @author Alejandro
 * @version 1.0
 */
public class ReservarActividadController {

    /**
     * Vista del formulario de reserva de actividades
     */
    private final FormReservarActividadJFrame view;

    /**
     * Servicio de gestión de actividades
     */
    private final ActividadService actividadService;

    /**
     * Servicio de relación actividad-día
     */
    private final ActividadDiaService actividadDiaService;

    /**
     * Servicio de gestión de reservas
     */
    private final ReservaService reservaService;

    /**
     * Servicio de gestión de clientes
     */
    private final ClienteService clienteService;

    /**
     * Acción a ejecutar tras crear una reserva correctamente
     */
    private final Runnable onReservaCreada;

    /**
     * Constructor del controlador de reservas de actividades.
     *
     * @param view vista del formulario de reserva
     * @param actividadService servicio de actividades
     * @param actividadDiaService servicio de relación actividad-día
     * @param reservaService servicio de reservas
     * @param clienteService servicio de clientes
     * @param onReservaCreada callback para refrescar datos tras la reserva
     */
    public ReservarActividadController(
            FormReservarActividadJFrame view,
            ActividadService actividadService,
            ActividadDiaService actividadDiaService,
            ReservaService reservaService,
            ClienteService clienteService,
            Runnable onReservaCreada
    ) {
        this.view = view;
        this.actividadService = actividadService;
        this.actividadDiaService = actividadDiaService;
        this.reservaService = reservaService;
        this.clienteService = clienteService;
        this.onReservaCreada = onReservaCreada;

        initController();
        cargarActividades();
        configurarSpinnerFecha();
    }

    /**
     * Inicializa los listeners y el estado inicial de los componentes de la
     * vista.
     */
    private void initController() {

        view.getjTableActividades()
                .getSelectionModel()
                .addListSelectionListener(e -> {
                    if (!e.getValueIsAdjusting()) {
                        cargarDiasActividadSeleccionada();
                        actualizarEstadoBotonReservar();
                        actualizarPlazasTabla();
                    }
                });

        view.getJComboBoxDia().addActionListener(e -> {
            corregirFechaSiNoCoincide();
            actualizarEstadoBotonReservar();
            actualizarPlazasTabla();
        });

        view.getjSpinnerFecha().addChangeListener(e -> actualizarEstadoBotonReservar());
        view.getjButtonAceptarReserva().addActionListener(e -> validarFormulario());
        view.getjButtonCancelar().addActionListener(e -> view.dispose());
        inicializarComboDias();
        view.getjButtonAceptarReserva().setEnabled(false);
    }

    /**
     * Carga los días disponibles de la actividad seleccionada en el combo.
     */
    private void cargarDiasActividadSeleccionada() {

        int fila = view.getjTableActividades().getSelectedRow();
        DefaultComboBoxModel<ActividadDia> model = new DefaultComboBoxModel<>();

        if (fila == -1) {
            model.addElement(crearPlaceholderDia());
            view.getJComboBoxDia().setModel(model);
            return;
        }

        int idActividad = (int) view.getjTableActividades()
                .getModel()
                .getValueAt(fila, 0);

        List<ActividadDia> actividadesDia
                = actividadDiaService.obtenerDiasActividad(idActividad);

        if (actividadesDia.isEmpty()) {
            model.addElement(crearPlaceholderDia());
        } else {
            actividadesDia.forEach(model::addElement);
        }

        view.getJComboBoxDia().setModel(model);
        view.getJComboBoxDia().setSelectedIndex(0);
        actualizarPlazasTabla();
    }

    /**
     * Valida los datos del formulario antes de crear la reserva.
     */
    private void validarFormulario() {

        if (view.getjTableActividades().getSelectedRow() == -1) {
            mostrarAviso("Debe seleccionar una actividad");
            return;
        }

        ActividadDia ad = (ActividadDia) view.getJComboBoxDia().getSelectedItem();
        if (ad == null || ad.getIdActividadDia() == 0) {
            mostrarAviso("Debe seleccionar un día válido");
            return;
        }

        LocalDate fecha = obtenerFechaSpinner();

        if (fecha.isBefore(LocalDate.now())) {
            mostrarAviso("No se pueden seleccionar fechas pasadas");
            return;
        }

        if (!coincideDia(fecha.getDayOfWeek(), ad.getDia())) {
            mostrarAviso(
                    "La fecha seleccionada no corresponde a un "
                    + ad.getDia().getNombre()
            );
            return;
        }

        crearReserva(fecha, ad);

        if (onReservaCreada != null) {
            onReservaCreada.run();
        }

        view.dispose();
    }

    /**
     * Comprueba si el día de la semana coincide con el día de impartición.
     *
     * @param dow día de la semana seleccionado
     * @param dia día configurado en la actividad
     * @return true si coinciden, false en caso contrario
     */
    private boolean coincideDia(DayOfWeek dow, Dia dia) {

        if (dia == null || dia.getCodigo() == null) {
            return false;
        }

        String codigo = dia.getCodigo();

        switch (codigo) {
            case "L":
                return dow == DayOfWeek.MONDAY;
            case "M":
                return dow == DayOfWeek.TUESDAY;
            case "X":
                return dow == DayOfWeek.WEDNESDAY;
            case "J":
                return dow == DayOfWeek.THURSDAY;
            case "V":
                return dow == DayOfWeek.FRIDAY;
            case "S":
                return dow == DayOfWeek.SATURDAY;
            case "D":
                return dow == DayOfWeek.SUNDAY;
            default:
                return false;
        }
    }

    /**
     * Crea una reserva para la actividad y fecha indicadas.
     *
     * @param fechaClase fecha de la clase
     * @param ad relación actividad-día
     */
    private void crearReserva(LocalDate fechaClase, ActividadDia ad) {

        Usuario usuario = Session.getUsuarioActual();
        Cliente cliente
                = clienteService.buscarDetallePorEmail(usuario.getEmail());

        if (cliente == null) {
            mostrarError("No existe cliente asociado al usuario");
            return;
        }

        Reserva reserva = new Reserva();
        reserva.setCliente(cliente);
        reserva.setActividadDia(ad);
        reserva.setFechaClase(fechaClase);
        reserva.setFechaReserva(LocalDate.now());
        reserva.setActiva(true);

        try {

            reservaService.crearReserva(reserva);

            JOptionPane.showMessageDialog(
                    view,
                    "Reserva realizada correctamente️",
                    "Reserva creada",
                    JOptionPane.INFORMATION_MESSAGE
            );

        } catch (IllegalArgumentException ex) {

            JOptionPane.showMessageDialog(
                    view,
                    ex.getMessage(),
                    "Error de validación",
                    JOptionPane.WARNING_MESSAGE
            );

        } catch (IllegalStateException ex) {

            JOptionPane.showMessageDialog(
                    view,
                    ex.getMessage(),
                    "Operación no permitida",
                    JOptionPane.WARNING_MESSAGE
            );
        }
    }

    /**
     * Inicializa el combo de días con un elemento placeholder.
     */
    private void inicializarComboDias() {

        DefaultComboBoxModel<ActividadDia> model = new DefaultComboBoxModel<>();
        model.addElement(crearPlaceholderDia());
        view.getJComboBoxDia().setModel(model);
    }

    /**
     * Crea un elemento placeholder para el combo de días.
     *
     * @return actividad-día ficticia
     */
    private ActividadDia crearPlaceholderDia() {

        ActividadDia ad = new ActividadDia();
        ad.setIdActividadDia(0);

        Actividad a = new Actividad();
        a.setNombre("Seleccione una actividad");

        Dia d = new Dia();
        d.setNombre("");

        ad.setActividad(a);
        ad.setDia(d);

        return ad;
    }

    /**
     * Actualiza el estado del botón de reserva según las validaciones actuales.
     */
    private void actualizarEstadoBotonReservar() {

        if (view.getjTableActividades().getSelectedRow() == -1) {
            view.getjButtonAceptarReserva().setEnabled(false);
            return;
        }

        ActividadDia ad = (ActividadDia) view.getJComboBoxDia().getSelectedItem();
        if (ad == null || ad.getIdActividadDia() == 0) {
            view.getjButtonAceptarReserva().setEnabled(false);
            return;
        }

        LocalDate fecha = obtenerFechaSpinner();
        if (fecha.isBefore(LocalDate.now())) {
            view.getjButtonAceptarReserva().setEnabled(false);
            return;
        }

        if (!coincideDia(fecha.getDayOfWeek(), ad.getDia())) {
            view.getjButtonAceptarReserva().setEnabled(false);
            return;
        }

        int aforo = reservaService.obtenerAforoMaximo(
                ad.getIdActividadDia()
        );

        view.getjButtonAceptarReserva().setEnabled(true);
    }

    /**
     * Obtiene la fecha seleccionada en el spinner como {@link LocalDate}.
     *
     * @return fecha seleccionada
     */
    private LocalDate obtenerFechaSpinner() {

        Date fechaSpinner = (Date) view.getjSpinnerFecha().getValue();

        return fechaSpinner.toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDate();
    }

    /**
     * Configura el spinner de fecha para permitir solo fechas válidas.
     */
    private void configurarSpinnerFecha() {

        SpinnerDateModel model
                = new SpinnerDateModel(new Date(), null, null, Calendar.DAY_OF_MONTH);

        view.getjSpinnerFecha().setModel(model);

        JSpinner.DateEditor editor
                = new JSpinner.DateEditor(view.getjSpinnerFecha(), "dd/MM/yyyy");

        view.getjSpinnerFecha().setEditor(editor);
        model.setStart(new Date());
    }

    /**
     * Corrige automáticamente la fecha si no coincide con el día seleccionado o
     * si no hay aforo disponible.
     */
    private void corregirFechaSiNoCoincide() {

        ActividadDia ad = (ActividadDia) view.getJComboBoxDia().getSelectedItem();
        if (ad == null || ad.getIdActividadDia() == 0) {
            return;
        }

        LocalDate fechaInicial = obtenerFechaSpinner();
        LocalDate fecha = fechaInicial;
        boolean avisoMostrado = false;

        while (true) {

            if (!coincideDia(fecha.getDayOfWeek(), ad.getDia())) {
                fecha = fecha.plusDays(1);
                continue;
            }

            if (!reservaService.hayAforoDisponible(ad, fecha)) {

                if (!avisoMostrado) {
                    JOptionPane.showMessageDialog(
                            view,
                            "La fecha seleccionada no tiene plazas disponibles.\n"
                            + "Se ha buscado la siguiente fecha con aforo libre.",
                            "Aforo completo",
                            JOptionPane.INFORMATION_MESSAGE
                    );
                    avisoMostrado = true;
                }

                fecha = fecha.plusDays(1);
                continue;
            }

            break;
        }

        if (!fecha.equals(fechaInicial)) {
            view.getjSpinnerFecha().setValue(
                    Date.from(
                            fecha.atStartOfDay(ZoneId.systemDefault()).toInstant()
                    )
            );
        }
    }

    /**
     * Carga el listado de actividades disponibles en la tabla.
     */
    private void cargarActividades() {

        List<Actividad> actividades = actividadService.obtenerActividades();

        DefaultTableModel model = new DefaultTableModel(
                new String[]{"ID", "Nombre", "Hora inicio", "Hora fin", "Plazas:Ocupdas/Libres"},
                0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        for (Actividad a : actividades) {
            model.addRow(new Object[]{
                a.getIdActividad(),
                a.getNombre(),
                a.getHoraInicio(),
                a.getHoraFin(),
                "-/-"
            });
        }

        view.getjTableActividades().setModel(model);

        // Ocultar columna ID
        view.getjTableActividades().removeColumn(
                view.getjTableActividades().getColumnModel().getColumn(0)
        );
    }
//Métodos auxiliares para UI

    private void mostrarAviso(String mensaje) {
        JOptionPane.showMessageDialog(
                view,
                mensaje,
                "Aviso",
                JOptionPane.WARNING_MESSAGE
        );
    }

    private void mostrarError(String mensaje) {
        JOptionPane.showMessageDialog(
                view,
                mensaje,
                "Error",
                JOptionPane.ERROR_MESSAGE
        );
    }

    /**
     * Actualiza la columna de plazas disponibles en la tabla según la actividad
     * seleccionada, día y fecha.
     */
    private void actualizarPlazasTabla() {
        int filaVista = view.getjTableActividades().getSelectedRow();
        if (filaVista == -1) {
            return;
        }

        int fila = view.getjTableActividades()
                .convertRowIndexToModel(filaVista);

        ActividadDia ad = (ActividadDia) view.getJComboBoxDia().getSelectedItem();
        if (ad == null || ad.getIdActividadDia() == 0) {
            return;
        }

        LocalDate fecha = obtenerFechaSpinner();

        int ocupadas = reservaService.obtenerPlazasOcupadas(
                ad.getIdActividadDia(),
                fecha
        );

        int aforo = reservaService.obtenerAforoMaximo(
                ad.getIdActividadDia()
        );

        String texto;

        int libres = aforo - ocupadas;

        if (ocupadas >= aforo) {
            texto = "AFORO COMPLETO (" + aforo + "/" + aforo + ")";
        } else {
            texto = ocupadas + "/" + aforo;
        }
        view.getjTableActividades().getModel()
                .setValueAt(texto, fila, 4);
    }

}
