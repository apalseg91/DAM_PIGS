package main;

/**
 * Punto de entrada principal de la aplicación.
 *
 * <p>
 * Inicia el sistema delegando la responsabilidad de arranque
 * a {@link AppLauncher}, que centraliza la creación de vistas
 * y controladores.
 * </p>
 *
 * <p>
 * Esta clase no contiene lógica de negocio ni de presentación,
 * únicamente arranca la aplicación.
 * </p>
 *
 * @author Alejandro
 * @version 1.0

 */
public class Main {

    /**
     * Método principal de ejecución.
     *
     * @param args argumentos de línea de comandos (no utilizados)
     */
    public static void main(String[] args) {
        AppLauncher.showLogin();
    }
}