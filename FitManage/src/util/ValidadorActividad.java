package util;

import Model.Actividad;
import java.time.Duration;
import java.time.LocalTime;

/**
 * Clase utilitaria encargada de validar los datos de una actividad.
 *
 * <p>
 * Centraliza todas las reglas de validación para evitar duplicidad de código en
 * controladores o vistas, facilitando el mantenimiento y la reutilización.
 * </p>
 *
 * <p>
 * Todas las validaciones lanzan {@link IllegalArgumentException} con un mensaje
 * claro para el usuario, que será mostrado en la interfaz gráfica.
 * </p>
 *
 * @author Alejandro
 */
public class ValidadorActividad {

    private static final int MAX_NOMBRE_LENGTH = 50;

    /**
     * Valida el nombre de la actividad.
     *
     * @param nombre nombre introducido por el usuario
     * @throws IllegalArgumentException si es nulo, vacío o demasiado largo
     */
    public static void validarNombre(String nombre) {

        if (nombre == null || nombre.trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre es obligatorio");
        }

        if (nombre.trim().length() > MAX_NOMBRE_LENGTH) {
            throw new IllegalArgumentException(
                    "El nombre no puede superar " + MAX_NOMBRE_LENGTH + " caracteres"
            );
        }
    }

    /**
     * Valida la descripción de la actividad.
     *
     * @param descripcion descripción introducida
     * @throws IllegalArgumentException si está vacía
     */
    public static void validarDescripcion(String descripcion) {

        if (descripcion == null || descripcion.trim().isEmpty()) {
            throw new IllegalArgumentException("La descripción es obligatoria");
        }
    }

    /**
     * Valida el rango horario de la actividad.
     *
     * @param inicio hora de inicio
     * @param fin hora de fin
     * @throws IllegalArgumentException si el rango no es válido
     */
    public static void validarHorario(LocalTime inicio, LocalTime fin) {

        if (inicio == null || fin == null) {
            throw new IllegalArgumentException("Debe indicar hora de inicio y fin");
        }

        if (!inicio.isBefore(fin)) {
            throw new IllegalArgumentException(
                    "La hora de inicio debe ser anterior a la de fin"
            );
        }

        long minutos = Duration.between(inicio, fin).toMinutes();

        if (minutos < 15) {
            throw new IllegalArgumentException(
                    "La actividad debe durar al menos 15 minutos"
            );
        }

        LocalTime apertura = LocalTime.of(7, 0);
        LocalTime cierre = LocalTime.of(23, 0);

        if (inicio.isBefore(apertura) || fin.isAfter(cierre)) {
            throw new IllegalArgumentException(
                    "El horario debe estar entre 07:00 y 23:00"
            );
        }
    }

    /**
     * Valida el aforo máximo.
     *
     * @param aforo valor introducido
     * @throws IllegalArgumentException si es menor o igual a cero
     */
    public static void validarAforo(int aforo) {

        if (aforo <= 0) {
            throw new IllegalArgumentException(
                    "El aforo máximo debe ser mayor que 0"
            );
        }
    }

    /**
     * Valida todos los campos de una actividad de forma conjunta.
     *
     * @param actividad actividad a validar
     * @throws IllegalArgumentException si algún campo no es válido
     */
    public static void validarActividadCompleta(Actividad actividad) {

        if (actividad == null) {
            throw new IllegalArgumentException("La actividad no puede ser nula");
        }

        validarNombre(actividad.getNombre());
        validarDescripcion(actividad.getDescripcion());
        validarHorario(actividad.getHoraInicio(), actividad.getHoraFin());
        validarAforo(actividad.getAforoMaximo());
    }
}
