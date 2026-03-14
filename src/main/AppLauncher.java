/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package main;

import DAO.UsuarioDAO;
import DAO.impl.UsuarioDAOImpl;
import controller.LoginController;
import javax.swing.SwingUtilities;
import service.AuthService;
import service.AuthServiceImpl;
import view.LoginJFrame;

/**
 * Clase encargada de centralizar el arranque de la aplicación
 * y la inicialización de las vistas principales junto con sus
 * controladores.
 *
 * <p>
 * Garantiza que cada interfaz gráfica se cree siempre asociada
 * a su controlador correspondiente, evitando:
 * </p>
 * <ul>
 *   <li>Duplicación de código</li>
 *   <li>Vistas sin lógica asociada</li>
 *   <li>Errores tras operaciones como cierre de sesión</li>
 * </ul>
 *
 * <p>
 * También asegura que la interfaz gráfica se ejecute dentro
 * del hilo de eventos de Swing (EDT), cumpliendo buenas prácticas
 * de desarrollo en aplicaciones Java Swing.
 * </p>
 *
 * @author Alejandro  
 * @version 1.0

 */
public class AppLauncher {

    /** DAO de acceso a datos de usuarios */
    private static final UsuarioDAO usuarioDAO = new UsuarioDAOImpl();

    /** Servicio de autenticación del sistema */
    private static final AuthService authService =
            new AuthServiceImpl(usuarioDAO);

    /**
     * Constructor privado para evitar instanciación.
     *
     * <p>
     * Esta clase funciona como lanzador estático.
     * </p>
     */
    private AppLauncher() {
    }

    /**
     * Muestra la pantalla de login inicializando
     * su controlador correspondiente.
     *
     * <p>
     * La vista se crea dentro del Event Dispatch Thread
     * utilizando {@link SwingUtilities#invokeLater(Runnable)}.
     * </p>
     */
    public static void showLogin() {

        SwingUtilities.invokeLater(() -> {

            LoginJFrame loginView = new LoginJFrame();

            new LoginController(
                    loginView,
                    authService
            );

            loginView.setVisible(true);
        });
    }
}
