package controller;

import DAO.ActividadDAO;
import DAO.ActividadDiaDAO;
import DAO.impl.ActividadDAOImpl;
import DAO.impl.ActividadDiaDAOImpl;
import Model.Usuario;
import service.AuthService;
import service.ClienteService;
import view.LoginJFrame;
import view.AdminJFrame;
import util.Session;
import service.PagoService;
import DAO.impl.PagoDAOImpl;
import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import DAO.impl.ClienteDAOImpl;
import DAO.impl.MetodoPagoDAOImpl;
import DAO.impl.ReservaDAOImpl;
import service.ActividadDiaService;
import service.ActividadService;
import service.MetodoPagoService;
import service.ReservaService;
import view.ClienteJFrame;

/**
 * Controlador encargado del proceso de autenticación del sistema.
 *
 * <p>
 * Responsabilidades:
 * </p>
 * <ul>
 * <li>Validar credenciales introducidas en la vista</li>
 * <li>Delegar autenticación en {@link AuthService}</li>
 * <li>Inicializar sesión de usuario</li>
 * <li>Redirigir según rol</li>
 * </ul>
 *
 * <p>
 * Actúa como punto de entrada funcional tras el login. Compatible con Java 8.
 * </p>
 *
 * @author Alejandro
 * @version 1.0
 *
 */
public class LoginController {

    /**
     * Vista de inicio de sesión
     */
    private final LoginJFrame view;

    /**
     * Servicio de autenticación
     */
    private final AuthService authService;

    /**
     * Constructor del controlador de login.
     *
     * @param view vista de login
     * @param authService servicio de autenticación
     */
    public LoginController(LoginJFrame view, AuthService authService) {
        this.view = view;
        this.authService = authService;
        initController();
    }

    /**
     * Inicializa los listeners de los componentes de la vista de login.
     */
    private void initController() {

        view.setLoginAction(e -> login());
        view.getJButtonSalir().addActionListener(e -> salirAplicacion());
    }

    /**
     * Gestiona el proceso de autenticación del usuario.
     * <p>
     * Valida los datos introducidos, realiza el login y redirige al usuario a
     * la interfaz correspondiente según su rol.
     * </p>
     */
    private void login() {

        String email = view.getEmail();
        String password = view.getPassword();

        if (email == null || email.trim().isEmpty()
                || password == null || password.trim().isEmpty()) {
            JOptionPane.showMessageDialog(
                    view,
                    "Debe introducir email y contraseña",
                    "Error",
                    JOptionPane.ERROR_MESSAGE
            );
            return;
        }

        Usuario usuario = authService.login(email, password);

        if (usuario == null) {
            JOptionPane.showMessageDialog(
                    view,
                    "Credenciales incorrectas",
                    "Error",
                    JOptionPane.ERROR_MESSAGE
            );
            return;
        }

        // ======================
        // LOGIN CORRECTO
        // ======================
        Session.login(usuario);

        JOptionPane.showMessageDialog(
                view,
                "Bienvenido " + usuario.getEmail()
                + "\nRol: " + usuario.getRol().getNombreRol(),
                "Login correcto",
                JOptionPane.INFORMATION_MESSAGE
        );

        view.dispose();

        // ======================
        // REDIRECCIÓN POR ROL
        // ======================
        redirigirPorRol(usuario);
    }

    /**
     * Redirige al usuario autenticado a la vista correspondiente según su rol.
     *
     * @param usuario usuario autenticado
     */
    private void redirigirPorRol(Usuario usuario) {

        String rol = usuario.getRol().getNombreRol();

        if ("ADMINISTRADOR".equalsIgnoreCase(rol)) {
            abrirVistaAdministrador();
        } else if ("USUARIO".equalsIgnoreCase(rol)) {
            abrirVistaCliente();
        } else {
            JOptionPane.showMessageDialog(
                    view,
                    "Rol no reconocido: " + rol,
                    "Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    /**
     * Inicializa y muestra la vista del administrador.
     */
    private void abrirVistaAdministrador() {

        AdminJFrame adminFrame = new AdminJFrame();

        ClienteService clienteService
                = new ClienteService(new ClienteDAOImpl());

        PagoService pagoService
                = new PagoService(new PagoDAOImpl());

        MetodoPagoService metodoPagoService
                = new MetodoPagoService(new MetodoPagoDAOImpl());

        new AdminController(
                adminFrame,
                clienteService,
                pagoService,
                metodoPagoService
        );

        adminFrame.setVisible(true);
    }

    /**
     * Inicializa y muestra la vista del cliente.
     */
    private void abrirVistaCliente() {

        ClienteJFrame clienteFrame = new ClienteJFrame();

        ClienteService clienteService
                = new ClienteService(new ClienteDAOImpl());

        ActividadDAO actividadDAO = new ActividadDAOImpl();
        ActividadDiaDAO actividadDiaDAO = new ActividadDiaDAOImpl();

        ActividadService actividadService
                = new ActividadService(actividadDAO, actividadDiaDAO);

        ActividadDiaService actividadDiaService
                = new ActividadDiaService(actividadDiaDAO);

        ReservaService reservaService
                = new ReservaService(
                        new ReservaDAOImpl(),
                        new ActividadDiaDAOImpl()
                );

        new ClienteController(
                clienteFrame,
                reservaService,
                clienteService,
                actividadService,
                actividadDiaService
        );

        clienteFrame.setVisible(true);
    }

    /**
     * Cierra la aplicación previa confirmación del usuario.
     */
    private void salirAplicacion() {

        int confirm = JOptionPane.showConfirmDialog(
                view,
                "¿Desea cerrar la aplicación?",
                "Salir",
                JOptionPane.YES_NO_OPTION
        );

        if (confirm == JOptionPane.YES_OPTION) {
            System.exit(0);
        }
    }
}
