package controller;

import DAO.impl.ActividadDiaDAOImpl;
import DAO.impl.ActividadDAOImpl;
import DAO.impl.DiaDAOImpl;
import DAO.impl.UsuarioDAOImpl;
import Model.Cliente;
import Model.MetodoPago;
import Model.Pago;
import Model.Usuario;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.List;
import javax.swing.ButtonGroup;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
import javax.swing.text.DateFormatter;
import javax.swing.text.DefaultFormatterFactory;
import service.ActividadDiaService;
import service.ActividadService;
import service.ClienteService;
import service.DiaService;
import service.MetodoPagoService;
import service.PagoService;
import service.UsuarioService;
import view.AdminJFrame;
import view.FormAltaJDialog;
import view.FormListadoPagosJDialog;
import view.FormPagoJDialog;
import util.ValidadorCliente;
import view.GenerarInformeJDialog;
import view.GestionAdminSistemaJDialog;
import view.GestionCuentaUsuarioJDialog;
import view.ListadoClientesJDialog;
import view.ModificarDatosAdminSistemaJDialog;
import view.ModificarDatosUsuariosJDialog;
import controller.InformeController;
import util.Session;

/**
 * Controlador principal del dashboard del administrador.
 * <p>
 * Gestiona:
 * <ul>
 * <li>Listado y filtrado de clientes</li>
 * <li>Alta, modificación, baja lógica y eliminación definitiva de clientes</li>
 * <li>Registro y consulta de pagos</li>
 * <li>Gestión de actividades (embebida en el dashboard)</li>
 * </ul>
 *
 * Sigue el patrón MVC, actuando como intermediario entre la vista
 * {@link AdminJFrame} y la capa de servicios.
 *
 * @author Alejandro
 * @version 1.0
 *
 */
public class AdminController {

    /**
     * Vista principal del administrador
     */
    private final AdminJFrame view;

    /**
     * Servicio de gestión de clientes
     */
    private final ClienteService clienteService;

    /**
     * Servicio de gestión de pagos
     */
    private final PagoService pagoService;

    /**
     * Servicio de métodos de pago
     */
    private final MetodoPagoService metodoPagoService;
    /**
     * Servicio de gestión de usuarios del sistema.
     */
    private final UsuarioService usuarioService;

    /**
     * Constructor del controlador del administrador.
     *
     * @param view vista principal del administrador
     * @param clienteService servicio de clientes
     * @param pagoService servicio de pagos
     * @param metodoPagoService servicio de métodos de pago
     */
    public AdminController(
            AdminJFrame view,
            ClienteService clienteService,
            PagoService pagoService,
            MetodoPagoService metodoPagoService
    ) {
        this.view = view;
        this.clienteService = clienteService;
        this.pagoService = pagoService;
        this.metodoPagoService = metodoPagoService;

        this.usuarioService = new UsuarioService(new UsuarioDAOImpl());

        initController();
        cargarClientes();
        initActividades();
        cargarClienteSesion();
    }

    /**
     * Inicializa los listeners de los componentes del dashboard.
     */
    private void initController() {

        //view.getBtnRefrescar().addActionListener(e -> cargarClientes());
        view.getBtnNuevoCliente().addActionListener(e -> abrirAltaCliente());
        view.getJButtonInactivo().addActionListener(e -> setInactivo());
        //view.getJButtonDelete().addActionListener(e -> eliminarCliente());
        view.getJButtonUpdate().addActionListener(e -> modificarCliente());
        view.getJButtonSetActivo().addActionListener(e -> setActivo());
        view.getJComboBoxFiltros().addActionListener(e -> setFiltro());
        view.getJButtonRegistrarPago().addActionListener(e -> registrarPago());
        view.getJButtonListadoPagos().addActionListener(e -> verPagosCliente());
        view.getJButtonCuentasUsuario().addActionListener(e -> abrirGestionUsuarios());
        view.getJButtonAdminSistema().addActionListener(e -> abrirGestionAdmins());
        view.getJButtonInformes().addActionListener(e -> abrirGenerarInforme());

    }

    /**
     * Inicializa la gestión de actividades dentro del dashboard del
     * administrador. Las actividades se cargan directamente en la vista sin
     * abrir diálogos adicionales.
     */
    private void initActividades() {

        ActividadService actividadService
                = new ActividadService(
                        new ActividadDAOImpl(),
                        new ActividadDiaDAOImpl()
                );

        ActividadDiaService actividadDiaService
                = new ActividadDiaService(new ActividadDiaDAOImpl());

        DiaService diaService
                = new DiaService(new DiaDAOImpl());

        new ActividadesController(
                view,
                actividadService,
                actividadDiaService,
                diaService
        );
    }

    /**
     * Carga el listado completo de clientes con toda la información detallada.
     */
    private void cargarClientes() {
        cargarClientes(clienteService.obtenerClientesDetalle());
    }

    /**
     * Carga en la tabla de clientes una lista concreta de clientes, manteniendo
     * siempre el mismo modelo de columnas.
     *
     * @param clientes lista de clientes a mostrar
     */
    private void cargarClientes(List<Cliente> clientes) {

        String[] columnas = {
            "ID", "Nombre", "Apellidos", "Email",
            "DNI", "Teléfono", "Dirección",
            "Fecha alta", "Próx. pago", "Activo"
        };

        DefaultTableModel model = new DefaultTableModel(columnas, 0);

        java.time.format.DateTimeFormatter formatter
                = java.time.format.DateTimeFormatter.ofPattern("dd-MM-yyyy");

        for (Cliente c : clientes) {

            String fechaAltaFormateada = c.getFechaAlta() != null
                    ? c.getFechaAlta().format(formatter)
                    : "";

            String fechaProxPagoFormateada = c.getFechaProximoPago() != null
                    ? c.getFechaProximoPago().format(formatter)
                    : "";

            model.addRow(new Object[]{
                c.getIdCliente(),
                c.getNombre(),
                c.getApellidos(),
                c.getEmail(),
                c.getDni(),
                c.getTelefono(),
                c.getDireccion(),
                fechaAltaFormateada,
                fechaProxPagoFormateada,
                c.isActivo() ? "Sí" : "No"
            });
        }

        view.getTableClientes().setModel(model);
        ocultarColumna(view.getTableClientes(), 0);
    }

    /**
     * Aplica el filtro seleccionado en el combo de filtros (activos, inactivos
     * o todos).
     */
    private void setFiltro() {

        String filtro = (String) view.getJComboBoxFiltros().getSelectedItem();
        List<Cliente> clientes;

        switch (filtro) {
            case "Activos":
                clientes = clienteService.obtenerClientesActivos();
                break;
            case "Inactivos":
                clientes = clienteService.obtenerClientesInactivos();
                break;
            default:
                clientes = clienteService.obtenerClientesDetalle();
        }

        cargarClientes(clientes);
    }

    /**
     * Abre el formulario para dar de alta un nuevo cliente.
     */
    private void abrirAltaCliente() {

        FormAltaJDialog dialog = new FormAltaJDialog(view, true);

        dialog.getButtonGuardar().addActionListener(e -> {

            // Campos obligatorios
            if ((dialog.getNombre() == null || dialog.getNombre().trim().isEmpty())
                    || (dialog.getApellidos() == null || dialog.getApellidos().trim().isEmpty())
                    || (dialog.getEmail() == null || dialog.getEmail().trim().isEmpty())
                    || (dialog.getDni() == null || dialog.getDni().trim().isEmpty())
                    || (dialog.getTelefono() == null || dialog.getTelefono().trim().isEmpty())
                    || (dialog.getDireccion() == null || dialog.getDireccion().trim().isEmpty())) {

                JOptionPane.showMessageDialog(
                        dialog,
                        "Todos los campos son obligatorios.",
                        "Error",
                        JOptionPane.ERROR_MESSAGE
                );
                return;
            }

            // Formato
            if (!ValidadorCliente.validarNombre(dialog.getNombre())) {
                JOptionPane.showMessageDialog(dialog,
                        "El nombre solo puede contener letras y espacios (2-30 caracteres).",
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (!ValidadorCliente.validarApellidos(dialog.getApellidos())) {
                JOptionPane.showMessageDialog(dialog,
                        "Los apellidos solo pueden contener letras y espacios (2-50 caracteres).",
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (!ValidadorCliente.validarEmail(dialog.getEmail())) {
                JOptionPane.showMessageDialog(dialog,
                        "El email no tiene un formato válido.",
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (!ValidadorCliente.validarDni(dialog.getDni().toUpperCase())) {
                JOptionPane.showMessageDialog(dialog,
                        "El DNI debe tener 8 dígitos y una letra mayúscula (ej: 12345678A).",
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (!ValidadorCliente.validarTelefono(dialog.getTelefono())) {
                JOptionPane.showMessageDialog(dialog,
                        "El teléfono debe contener exactamente 9 dígitos.",
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (!ValidadorCliente.validarDireccion(dialog.getDireccion())) {
                JOptionPane.showMessageDialog(dialog,
                        "La dirección contiene caracteres no válidos (5-100 caracteres).",
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Crear cliente
            Cliente cliente = new Cliente();
            cliente.setNombre(dialog.getNombre().trim());
            cliente.setApellidos(dialog.getApellidos().trim());
            cliente.setEmail(dialog.getEmail().trim());
            cliente.setDni(dialog.getDni().toUpperCase().trim());
            cliente.setTelefono(dialog.getTelefono().trim());
            cliente.setDireccion(dialog.getDireccion().trim());
            cliente.setActivo(true);

            clienteService.crearCliente(cliente);

            dialog.dispose();
            cargarClientes();
            JOptionPane.showMessageDialog(
                    view,
                    "Cliente registrado correctamente.",
                    "Alta nuevo cliente",
                    JOptionPane.INFORMATION_MESSAGE
            );
        });
        dialog.setVisible(true);

    }

    /**
     * Da de baja lógica a un cliente seleccionado.
     */
    private void setInactivo() {

        int fila = view.getTableClientes().getSelectedRow();

        if (fila == -1) {
            JOptionPane.showMessageDialog(view, "Debe seleccionar un cliente.");
            return;
        }

        int idCliente = (int) view.getTableClientes().getValueAt(fila, 0);

        if (JOptionPane.showConfirmDialog(
                view,
                "¿Desea dar de baja lógicamente al cliente?",
                "Confirmar baja",
                JOptionPane.YES_NO_OPTION
        ) == JOptionPane.YES_OPTION) {

            clienteService.setInactivoCliente(idCliente);
            cargarClientes();
        }
        JOptionPane.showMessageDialog(
                view,
                "Estado de cliente cambiado a inactivo.",
                "Cambio a inactivo",
                JOptionPane.INFORMATION_MESSAGE
        );
    }

    /**
     * Reactiva un cliente previamente dado de baja.
     */
    private void setActivo() {

        int fila = view.getTableClientes().getSelectedRow();

        if (fila == -1) {
            JOptionPane.showMessageDialog(view, "Debe seleccionar un cliente.");
            return;
        }

        String activo = (String) view.getTableClientes().getValueAt(fila, 9);

        if ("Sí".equalsIgnoreCase(activo)) {
            JOptionPane.showMessageDialog(view, "El cliente ya está activo.");
            return;
        }

        int idCliente = (int) view.getTableClientes().getValueAt(fila, 0);

        if (JOptionPane.showConfirmDialog(
                view,
                "¿Desea reactivar el cliente?",
                "Confirmar activación",
                JOptionPane.YES_NO_OPTION
        ) == JOptionPane.YES_OPTION) {

            clienteService.setActivoCliente(idCliente);
            cargarClientes();
        }
        JOptionPane.showMessageDialog(
                view,
                "Estado de cliente cambiado a activo.",
                "Cambio a activo",
                JOptionPane.INFORMATION_MESSAGE
        );
    }

    /**
     * Modifica los datos básicos de un cliente seleccionado en la tabla.
     *
     * Abre el formulario de alta reutilizándolo como formulario de edición,
     * precargando los datos actuales del cliente.
     *
     * Si el usuario confirma, valida los campos y actualiza el cliente.
     */
    private void modificarCliente() {

        int fila = view.getTableClientes().getSelectedRow();

        if (fila == -1) {
            JOptionPane.showMessageDialog(
                    view,
                    "Debe seleccionar un cliente.",
                    "Aviso",
                    JOptionPane.WARNING_MESSAGE
            );
            return;
        }

        int idCliente = (int) view.getTableClientes().getValueAt(fila, 0);
        String nombre = (String) view.getTableClientes().getValueAt(fila, 1);
        String apellidos = (String) view.getTableClientes().getValueAt(fila, 2);
        String email = (String) view.getTableClientes().getValueAt(fila, 3);
        String dni = (String) view.getTableClientes().getValueAt(fila, 4);
        String telefono = (String) view.getTableClientes().getValueAt(fila, 5);
        String direccion = (String) view.getTableClientes().getValueAt(fila, 6);

        FormAltaJDialog dialog = new FormAltaJDialog(view, true);

        dialog.setIdCliente(idCliente);
        dialog.setNombre(nombre);
        dialog.setApellidos(apellidos);
        dialog.setEmail(email);
        dialog.setDni(dni);
        dialog.setTelefono(telefono);
        dialog.setDireccion(direccion);

        dialog.getButtonGuardar().addActionListener(e -> {

            // Validar campos obligatorios
            if ((dialog.getNombre() == null || dialog.getNombre().trim().isEmpty())
                    || (dialog.getApellidos() == null || dialog.getApellidos().trim().isEmpty())
                    || (dialog.getEmail() == null || dialog.getEmail().trim().isEmpty())
                    || (dialog.getDni() == null || dialog.getDni().trim().isEmpty())
                    || (dialog.getTelefono() == null || dialog.getTelefono().trim().isEmpty())
                    || (dialog.getDireccion() == null || dialog.getDireccion().trim().isEmpty())) {

                JOptionPane.showMessageDialog(
                        dialog,
                        "Todos los campos son obligatorios.",
                        "Error",
                        JOptionPane.ERROR_MESSAGE
                );
                return;
            }

            // Validaciones de formato
            if (!ValidadorCliente.validarNombre(dialog.getNombre())) {
                JOptionPane.showMessageDialog(dialog,
                        "El nombre solo puede contener letras y espacios (2-30 caracteres).",
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (!ValidadorCliente.validarApellidos(dialog.getApellidos())) {
                JOptionPane.showMessageDialog(dialog,
                        "Los apellidos solo pueden contener letras y espacios (2-50 caracteres).",
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (!ValidadorCliente.validarEmail(dialog.getEmail())) {
                JOptionPane.showMessageDialog(dialog,
                        "El email no tiene un formato válido.",
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (!ValidadorCliente.validarDni(dialog.getDni().toUpperCase())) {
                JOptionPane.showMessageDialog(dialog,
                        "El DNI debe tener 8 dígitos y una letra mayúscula.",
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (!ValidadorCliente.validarTelefono(dialog.getTelefono())) {
                JOptionPane.showMessageDialog(dialog,
                        "El teléfono debe contener exactamente 9 dígitos.",
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (!ValidadorCliente.validarDireccion(dialog.getDireccion())) {
                JOptionPane.showMessageDialog(dialog,
                        "La dirección contiene caracteres no válidos.",
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }

            Cliente cliente = new Cliente();
            cliente.setIdCliente(dialog.getIdCliente());
            cliente.setNombre(dialog.getNombre().trim());
            cliente.setApellidos(dialog.getApellidos().trim());
            cliente.setEmail(dialog.getEmail().trim());
            cliente.setDni(dialog.getDni().toUpperCase().trim());
            cliente.setTelefono(dialog.getTelefono().trim());
            cliente.setDireccion(dialog.getDireccion().trim());

            clienteService.actualizarCliente(cliente);

            dialog.dispose();
            cargarClientes();
        JOptionPane.showMessageDialog(
                view,
                "Datos de cliente actualizados correctamente.",
                "Datos modificados",
                JOptionPane.INFORMATION_MESSAGE
        );
        });
        dialog.setLocationRelativeTo(view);
        dialog.setVisible(true);
    }

    /**
     * Registra un pago para el cliente seleccionado.
     */
    private void registrarPago() {
        int fila = view.getTableClientes().getSelectedRow();

        if (fila == -1) {
            JOptionPane.showMessageDialog(view, "Seleccione un cliente");
            return;
        }

        int idCliente = (int) view.getTableClientes().getValueAt(fila, 0);

        List<MetodoPago> metodosPago = metodoPagoService.obtenerTodos();

        FormPagoJDialog dialog
                = new FormPagoJDialog(view, true, idCliente, metodosPago);

        dialog.getComboConcepto().addActionListener(ev -> {

            String concepto = dialog.getConcepto();

            Cliente cliente = clienteService.buscarPorId(idCliente);

            BigDecimal importe = pagoService.calcularImporte(concepto, cliente);

            dialog.setImporte(importe);
        });

        dialog.getJButtonAceptar().addActionListener(e -> {

            BigDecimal importe = dialog.getjTextFieldImporte();

            if (importe.compareTo(BigDecimal.ZERO) <= 0) {
                JOptionPane.showMessageDialog(
                        dialog,
                        "El importe debe ser mayor que 0",
                        "Error",
                        JOptionPane.ERROR_MESSAGE
                );
                return;
            }

            String concepto = dialog.getConcepto();

            if (concepto == null || concepto.trim().isEmpty()) {
                JOptionPane.showMessageDialog(
                        dialog,
                        "Debe introducir un concepto",
                        "Error",
                        JOptionPane.ERROR_MESSAGE
                );
                return;
            }

            MetodoPago metodoPago = dialog.getMetodoPago();

            if (metodoPago == null) {
                JOptionPane.showMessageDialog(
                        dialog,
                        "Debe seleccionar un método de pago",
                        "Error",
                        JOptionPane.ERROR_MESSAGE
                );
                return;
            }

            Pago pago = new Pago();
            pago.setCliente(clienteService.buscarPorId(idCliente));
            pago.setImporte(importe);
            pago.setMetodoPago(metodoPago);
            pago.setConcepto(concepto);

            pagoService.registrarPago(pago);
            cargarClientes();
            dialog.dispose();

            JOptionPane.showMessageDialog(
                    view,
                    "Pago registrado correctamente",
                    "OK",
                    JOptionPane.INFORMATION_MESSAGE
            );
        });

        dialog.setVisible(true);
    }

    /**
     * Muestra el listado de pagos del cliente seleccionado.
     */
    private void verPagosCliente() {

        int fila = view.getTableClientes().getSelectedRow();

        if (fila == -1) {
            JOptionPane.showMessageDialog(view, "Seleccione un cliente");
            return;
        }

        int idCliente = (int) view.getTableClientes().getValueAt(fila, 0);
        List<Pago> pagos = pagoService.obtenerPagosCliente(idCliente);

        if (pagos.isEmpty()) {
            JOptionPane.showMessageDialog(
                    view,
                    "El cliente no tiene pagos registrados",
                    "Información",
                    JOptionPane.INFORMATION_MESSAGE
            );
            return;
        }

        FormListadoPagosJDialog dialog
                = new FormListadoPagosJDialog(view, true, pagos);

        dialog.setVisible(true);
    }

    /**
     * Carga en la tabla del diálogo todos los usuarios con rol USUARIO.
     *
     * @param dialog diálogo de gestión
     */
    private void cargarTablaUsuarios(GestionCuentaUsuarioJDialog dialog) {

        List<Usuario> usuarios
                = usuarioService.listarUsuariosPorRol("USUARIO");

        String[] columnas = {
            "ID", "Email", "Fecha creación"
        };

        DefaultTableModel model = new DefaultTableModel(columnas, 0);
        java.time.format.DateTimeFormatter formatter
                = java.time.format.DateTimeFormatter.ofPattern("dd-MM-yyyy");

        for (Usuario u : usuarios) {
            String fechaCreacion = u.getFechaCreacion() != null
                    ? u.getFechaCreacion().format(formatter)
                    : "";
            model.addRow(new Object[]{
                u.getIdUsuario(),
                u.getEmail(),
                fechaCreacion
            });
        }

        dialog.getJTableGestionUsuarios().setModel(model);
        ocultarColumna(dialog.getJTableGestionUsuarios(), 0);
    }

    /**
     * Elimina el usuario seleccionado en la tabla.
     *
     * @param dialog diálogo actual
     */
    private void eliminarUsuario(GestionCuentaUsuarioJDialog dialog) {

        int fila = dialog.getJTableGestionUsuarios().getSelectedRow();

        if (fila == -1) {
            JOptionPane.showMessageDialog(dialog,
                    "Debe seleccionar un usuario");
            return;
        }

        int idUsuario = (int) dialog.getJTableGestionUsuarios().getValueAt(fila, 0);

        if (JOptionPane.showConfirmDialog(dialog,
                "¿Desea eliminar la cuenta de usuario?",
                "Confirmar eliminación",
                JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {

            usuarioService.eliminarUsuario(idUsuario);
            cargarTablaUsuarios(dialog);

            JOptionPane.showMessageDialog(view,
                    "Cuenta de usuario eliminada correctamente.",
                    "Cuenta eliminada",
                    JOptionPane.INFORMATION_MESSAGE);
        }
    }

    /**
     * Abre el diálogo para modificar los datos del usuario seleccionado.
     *
     * @param dialog diálogo principal
     */
    private void modificarUsuario(GestionCuentaUsuarioJDialog dialog) {

        int fila = dialog.getJTableGestionUsuarios().getSelectedRow();

        if (fila == -1) {
            JOptionPane.showMessageDialog(dialog,
                    "Debe seleccionar un usuario");
            return;
        }

        int idUsuario = (int) dialog.getJTableGestionUsuarios().getValueAt(fila, 0);

        String email = (String) dialog.getJTableGestionUsuarios().getValueAt(fila, 1);

        ModificarDatosUsuariosJDialog dialogModificar
                = new ModificarDatosUsuariosJDialog(view, true);

        dialogModificar.setEmail(email);

        dialogModificar.getJButtonCancelar()
                .addActionListener(e -> dialogModificar.dispose());

        dialogModificar.getJButtonAceptar()
                .addActionListener(e -> {

                    String nuevoEmail = dialogModificar.getEmail();
                    String nuevaPassword = dialogModificar.getPassword();

                    if ((nuevoEmail == null || nuevoEmail.trim().isEmpty())) {
                        JOptionPane.showMessageDialog(dialogModificar,
                                "El email no puede estar vacío");
                        return;
                    }

                    usuarioService.actualizarUsuario(
                            idUsuario,
                            nuevoEmail,
                            nuevaPassword
                    );

                    dialogModificar.dispose();
                    cargarTablaUsuarios(dialog);
            JOptionPane.showMessageDialog(
                    view,
                    "Modificación de datos de usuario exitosa.",
                    "Cambio de datos",
                    JOptionPane.INFORMATION_MESSAGE
            );
                });

        dialogModificar.setLocationRelativeTo(dialog);
        dialogModificar.setVisible(true);
    }

    /**
     * Abre el diálogo de gestión de cuentas de usuario (rol USUARIO). Carga los
     * usuarios en la tabla y configura los eventos.
     */
    private void abrirGestionUsuarios() {

        GestionCuentaUsuarioJDialog dialog
                = new GestionCuentaUsuarioJDialog(view, true);

        cargarTablaUsuarios(dialog);

        dialog.getJButtonCancelar()
                .addActionListener(e -> dialog.dispose());

        dialog.getJButtonEliminar()
                .addActionListener(e -> eliminarUsuario(dialog));
        dialog.getJButtonModificar()
                .addActionListener(e -> modificarUsuario(dialog));

        dialog.getJButtonAceptar()
                .addActionListener(e -> {
                    cargarTablaUsuarios(dialog);
                    dialog.dispose();
                });

        dialog.setLocationRelativeTo(view);
        dialog.setVisible(true);
    }

    /**
     * Abre el diálogo de gestión de administradores.
     */
    private void abrirGestionAdmins() {

        GestionAdminSistemaJDialog dialog
                = new GestionAdminSistemaJDialog(view, true);

        cargarTablaAdmins(dialog);

        dialog.getJButtonCancelar()
                .addActionListener(e -> dialog.dispose());

        dialog.getJButtonAceptar()
                .addActionListener(e -> {
                    cargarTablaAdmins(dialog);
                    dialog.dispose();
                });

        dialog.getJButtonEliminar()
                .addActionListener(e -> eliminarAdmin(dialog));

        dialog.getJButtonNuevoAdmin()
                .addActionListener(e -> crearNuevoAdmin(dialog));

        dialog.getJButtonModificar()
                .addActionListener(e -> modificarAdmin(dialog));

        dialog.setLocationRelativeTo(view);
        dialog.setVisible(true);
    }

    /**
     * Carga en la tabla del diálogo todos los usuarios con rol ADMINISTRADOR.
     *
     * <p>
     * Recupera la lista de administradores desde la capa de servicio y
     * construye dinámicamente el modelo de la tabla con las columnas ID, email
     * y fecha de creación. La fecha se formatea a "dd-MM-yyyy" para su
     * visualización.
     * </p>
     *
     * <p>
     * La columna ID se oculta visualmente, pero se mantiene en el modelo para
     * poder recuperar el identificador interno en operaciones posteriores
     * (modificar/eliminar).
     * </p>
     *
     * @param dialog diálogo de gestión de administradores donde se mostrará la
     * tabla
     */
    private void cargarTablaAdmins(GestionAdminSistemaJDialog dialog) {

        List<Usuario> admins
                = usuarioService.listarUsuariosPorRol("ADMINISTRADOR");

        String[] columnas = {
            "ID", "Email", "Fecha creación"
        };

        DefaultTableModel model = new DefaultTableModel(columnas, 0);
        java.time.format.DateTimeFormatter formatter
                = java.time.format.DateTimeFormatter.ofPattern("dd-MM-yyyy");
        for (Usuario u : admins) {
            String fechaCreacion = u.getFechaCreacion() != null
                    ? u.getFechaCreacion().format(formatter)
                    : "";
            model.addRow(new Object[]{
                u.getIdUsuario(),
                u.getEmail(),
                fechaCreacion});
        }

        dialog.getJTableAdminSistema().setModel(model);
        ocultarColumna(dialog.getJTableAdminSistema(), 0);
    }

    /**
     * Elimina el administrador seleccionado en la tabla del diálogo.
     *
     * <p>
     * Valida que exista una fila seleccionada y delega la operación en la capa
     * de servicio, que aplica las reglas de negocio (no eliminar
     * superadministrador ni al usuario actual).
     * </p>
     *
     * <p>
     * En caso de incumplir alguna regla, se captura la excepción lanzada por el
     * servicio y se muestra un mensaje informativo al usuario.
     * </p>
     *
     * @param dialog diálogo de gestión de administradores
     */
    private void eliminarAdmin(GestionAdminSistemaJDialog dialog) {

        int fila = dialog.getJTableAdminSistema().getSelectedRow();

        if (fila == -1) {
            JOptionPane.showMessageDialog(dialog,
                    "Debe seleccionar un administrador");
            return;
        }

        int idUsuario = (int) dialog.getJTableAdminSistema().getValueAt(fila, 0);

        try {

            usuarioService.eliminarUsuario(idUsuario);
            cargarTablaAdmins(dialog);

            JOptionPane.showMessageDialog(
                    view,
                    "Cuenta de administrador eliminada correctamente.",
                    "Cuenta eliminada",
                    JOptionPane.INFORMATION_MESSAGE
            );

        } catch (IllegalStateException ex) {

            JOptionPane.showMessageDialog(
                    dialog,
                    ex.getMessage(),
                    "Eliminación no permitida",
                    JOptionPane.WARNING_MESSAGE
            );
        }
        JOptionPane.showMessageDialog(
                view,
                "Cuenta de administrador eliminada correctamente.",
                "Cuenta eliminada",
                JOptionPane.INFORMATION_MESSAGE
        );
    }

    /**
     * Abre el formulario para crear un nuevo administrador del sistema.
     *
     * <p>
     * Valida que los campos obligatorios (email y contraseña) estén informados
     * antes de delegar la creación en la capa de servicio. Tras la creación, se
     * actualiza la tabla de administradores.
     * </p>
     *
     * @param dialog diálogo principal de gestión de administradores
     */
    private void crearNuevoAdmin(GestionAdminSistemaJDialog dialog) {

        ModificarDatosAdminSistemaJDialog form
                = new ModificarDatosAdminSistemaJDialog(view, true);

        form.getJButtonCancelar()
                .addActionListener(e -> form.dispose());

        form.getJButtonAceptar()
                .addActionListener(e -> {

                    String email = form.getEmail();
                    String password = form.getPassword();

                    if ((email == null || email.trim().isEmpty())
                            || (password == null || password.trim().isEmpty())) {
                        JOptionPane.showMessageDialog(form,
                                "Email y contraseña obligatorios");
                        return;
                    }

                    usuarioService.crearAdministrador(email, password);

                    form.dispose();
                    cargarTablaAdmins(dialog);
            JOptionPane.showMessageDialog(
                    view,
                    "Cuenta de administrador creada correctamente",
                    "Nuevo administrador",
                    JOptionPane.INFORMATION_MESSAGE
            );
                });

        form.setLocationRelativeTo(dialog);
        form.setVisible(true);
    }

    /**
     * Permite modificar los datos de un administrador seleccionado.
     *
     * <p>
     * Precarga el formulario con los datos actuales del administrador. Si el
     * usuario es el superadministrador, se bloquea la edición del email por
     * motivos de seguridad.
     * </p>
     *
     * <p>
     * La actualización se delega en la capa de servicio, que se encarga de
     * aplicar las reglas de negocio y el hash de la contraseña si procede.
     * </p>
     *
     * @param dialog diálogo de gestión de administradores
     */
    private void modificarAdmin(GestionAdminSistemaJDialog dialog) {

        int fila = dialog.getJTableAdminSistema().getSelectedRow();

        if (fila == -1) {
            JOptionPane.showMessageDialog(dialog,
                    "Debe seleccionar un administrador");
            return;
        }

        int idUsuario = (int) dialog.getJTableAdminSistema().getValueAt(fila, 0);

        Usuario admin = usuarioService.buscarPorId(idUsuario);

        ModificarDatosAdminSistemaJDialog form
                = new ModificarDatosAdminSistemaJDialog(view, true);

        form.setEmail(admin.getEmail());
        form.setPassword(admin.getContrasenaHash());

        // Si es superadmin → bloquear edición email
        if (usuarioService.esSuperAdmin(idUsuario)) {
            form.bloquearEmail();
        }

        form.getJButtonCancelar()
                .addActionListener(e -> form.dispose());

        form.getJButtonAceptar()
                .addActionListener(e -> {

                    String nuevoEmail = form.getEmail();
                    String nuevaPassword = form.getPassword();

                    usuarioService.actualizarAdministrador(
                            idUsuario,
                            nuevoEmail,
                            nuevaPassword
                    );

                    form.dispose();
                    cargarTablaAdmins(dialog);
            JOptionPane.showMessageDialog(
                    view,
                    "La cuenta de administrador se ha actualizado correctamente.",
                    "Datos de administrador modificados",
                    JOptionPane.INFORMATION_MESSAGE
            );
                });

        form.setLocationRelativeTo(dialog);
        form.setVisible(true);
    }

    /**
     * Abre el diálogo de generación de informes.
     *
     * <p>
     * Inicializa el controlador asociado al diálogo y configura los componentes
     * necesarios antes de mostrar la ventana al usuario.
     * </p>
     */
    private void abrirGenerarInforme() {
        GenerarInformeJDialog dialog
                = new GenerarInformeJDialog(view, true);
        new InformeController(dialog);
        initGenerarInformeDialog(dialog);

        dialog.setLocationRelativeTo(view);
        dialog.setVisible(true);
    }

    /**
     * Inicializa los componentes y comportamiento del diálogo de generación de
     * informes.
     *
     * <p>
     * Configura los campos de entrada, establece los formatos de fecha, agrupa
     * los checkboxes de tipo de informe y define las acciones de los botones.
     * </p>
     *
     * @param dialog diálogo de generación de informes
     */
    private void initGenerarInformeDialog(GenerarInformeJDialog dialog) {

        dialog.getJTextFieldDNI().setEditable(false);
        dialog.getJTextFieldEmail().setEditable(false);

        DateFormatter df
                = new DateFormatter(new SimpleDateFormat("dd-MM-yyyy"));

        DefaultFormatterFactory factory
                = new DefaultFormatterFactory(df);

        dialog.getJFormattedTextFieldInicio()
                .setFormatterFactory(factory);

        dialog.getJFormattedTextFieldFin()
                .setFormatterFactory(factory);

        ButtonGroup group = new ButtonGroup();
        group.add(dialog.getJCheckBoxPago());
        group.add(dialog.getJCheckBoxAsistenciaClases());

        dialog.getJButtonCancelar()
                .addActionListener(e -> dialog.dispose());

        dialog.getJButtonLimpiarCampos()
                .addActionListener(e -> {

                    dialog.getJTextFieldDNI().setText("");
                    dialog.getJTextFieldEmail().setText("");
                    dialog.getJFormattedTextFieldInicio().setValue(null);
                    dialog.getJFormattedTextFieldFin().setValue(null);
                    group.clearSelection();
                });

        dialog.getJButtonElegirListado().addActionListener(e -> abrirListadoClientes(dialog));
    }

    /**
     * Abre el diálogo de selección de clientes para informes.
     *
     * <p>
     * Permite seleccionar un cliente de la lista y transferir su información
     * (DNI y email) al diálogo padre para la generación del informe.
     * </p>
     *
     * @param parentDialog diálogo principal de generación de informes
     */
    private void abrirListadoClientes(GenerarInformeJDialog parentDialog) {

        ListadoClientesJDialog dialog
                = new ListadoClientesJDialog(view, true);

        cargarClientesEnDialog(dialog);

        dialog.getJButtonCancelar()
                .addActionListener(e -> dialog.dispose());

        dialog.getJButtonAceptar()
                .addActionListener(e -> {

                    int fila = dialog.getJTable().getSelectedRow();

                    if (fila == -1) {
                        JOptionPane.showMessageDialog(
                                dialog,
                                "Debe seleccionar un cliente"
                        );
                        return;
                    }

                    int idCliente
                            = (int) dialog.getJTable()
                                    .getValueAt(fila, 0);

                    Cliente cliente
                            = clienteService.buscarPorId(idCliente);

                    parentDialog.getJTextFieldDNI()
                            .setText(cliente.getDni());

                    parentDialog.getJTextFieldEmail()
                            .setText(cliente.getEmail());

                    dialog.dispose();
                });

        dialog.setLocationRelativeTo(view);
        dialog.setVisible(true);
    }

    /**
     * Carga la lista de clientes en el diálogo de selección.
     *
     * <p>
     * Muestra información básica del cliente (ID, nombre, apellidos, email, DNI
     * y estado). La columna ID se oculta visualmente pero se mantiene en el
     * modelo para su uso interno.
     * </p>
     *
     * @param dialog diálogo de listado de clientes
     */
    private void cargarClientesEnDialog(
            ListadoClientesJDialog dialog) {

        List<Cliente> clientes
                = clienteService.obtenerClientesDetalle();

        String[] columnas = {
            "ID", "Nombre", "Apellidos", "Email",
            "DNI", "Activo"
        };

        DefaultTableModel model
                = new DefaultTableModel(columnas, 0);

        for (Cliente c : clientes) {
            model.addRow(new Object[]{
                c.getIdCliente(),
                c.getNombre(),
                c.getApellidos(),
                c.getEmail(),
                c.getDni(),
                c.isActivo() ? "Sí" : "No"
            });
        }

        dialog.getJTable().setModel(model);
        ocultarColumna(dialog.getJTable(), 0);

    }

    /**
     * Carga el mail del admistrador autenticado en la cabecera del panel.
     */
    private void cargarClienteSesion() {
        Usuario usuario = Session.getUsuarioActual();
        if (usuario == null) {
            return;
        }
        view.getJLabelSaludo().setText("Administrador conectado: " + usuario.getEmail());
    }

    /**
     * Oculta visualmente una columna de una JTable manteniéndola en el modelo.
     * <p>
     * Esta técnica evita problemas al recuperar valores desde el modelo (por
     * ejemplo IDs de base de datos) ya que la columna sigue existiendo
     * internamente aunque no sea visible para el usuario.
     *
     * @param table JTable donde se ocultará la columna
     * @param columnIndex índice de la columna a ocultar
     */
    private void ocultarColumna(javax.swing.JTable table, int columnIndex) {

        table.getColumnModel().getColumn(columnIndex).setMinWidth(0);
        table.getColumnModel().getColumn(columnIndex).setMaxWidth(0);
        table.getColumnModel().getColumn(columnIndex).setWidth(0);
    }
}
