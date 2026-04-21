package controller;

import Model.Actividad;
import Model.ActividadDia;
import Model.Dia;
import java.awt.Window;
import java.time.LocalTime;
import java.util.List;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableModel;
import service.ActividadDiaService;
import service.ActividadService;
import service.DiaService;
import view.ActividadesView;
import view.FormActividadJDialog;
import java.util.stream.Collectors;
import util.ValidadorActividad;

/**
 * Controlador encargado de la gestión de actividades.
 *
 * <p>
 * Implementa la lógica de coordinación entre la vista (ActividadesView) y la
 * capa de servicios siguiendo el patrón MVC.
 * </p>
 *
 * <p>
 * Responsabilidades principales:
 * </p>
 * <ul>
 * <li>Cargar actividades en la tabla</li>
 * <li>Gestionar alta de actividades</li>
 * <li>Gestionar edición</li>
 * <li>Gestionar eliminación</li>
 * <li>Validar datos antes de delegar en la capa de servicio</li>
 * </ul>
 *
 * Compatible con Java 8.
 *
 * @author Alejandro
 * @version 1.0
 *
 */
public class ActividadesController {

    /**
     * Vista que muestra y gestiona las actividades
     */
    private final ActividadesView view;

    /**
     * Servicio de gestión de actividades
     */
    private final ActividadService actividadService;

    /**
     * Servicio de relación actividad-día
     */
    private final ActividadDiaService actividadDiaService;

    /**
     * Servicio de gestión de días
     */
    private final DiaService diaService;

    /**
     * Constructor del controlador de actividades.
     *
     * @param view vista que implementa {@link ActividadesView}
     * @param actividadService servicio de actividades
     * @param actividadDiaService servicio de relación actividad-día
     * @param diaService servicio de días
     */
    public ActividadesController(
            ActividadesView view,
            ActividadService actividadService,
            ActividadDiaService actividadDiaService,
            DiaService diaService
    ) {
        this.view = view;
        this.actividadService = actividadService;
        this.actividadDiaService = actividadDiaService;
        this.diaService = diaService;

        initController();
        cargarActividades();
    }

    /**
     * Inicializa los listeners de los botones de la vista.
     */
    private void initController() {

        view.getJButtonNuevaActividad()
                .addActionListener(e -> abrirAltaActividad());

        view.getJButtonEditarActividad()
                .addActionListener(e -> editarActividad());

        view.getJButtonEliminarActividad()
                .addActionListener(e -> eliminarActividad());
    }

    /**
     * Carga el listado de actividades en la tabla de la vista. Mantiene siempre
     * el mismo modelo de columnas.
     */
    private void cargarActividades() {

        List<Actividad> actividades = actividadService.obtenerActividades();

        String[] columnas = {
            "ID", "Nombre", "Hora inicio", "Hora fin", "Aforo máx.", "Días"
        };

        DefaultTableModel model = new DefaultTableModel(columnas, 0);

        for (Actividad a : actividades) {
            String dias = actividadDiaService.obtenerDiasComoTexto(a.getIdActividad());
            model.addRow(new Object[]{
                a.getIdActividad(),
                a.getNombre(),
                a.getHoraInicio(),
                a.getHoraFin(),
                a.getAforoMaximo(),
                dias
            });
        }

        view.getJTableActividades().setModel(model);
        ocultarColumna(view.getJTableActividades(), 0);
    }

    /**
     * Abre el formulario para crear una nueva actividad. Valida los datos
     * introducidos y refresca la tabla al finalizar.
     */
    private void abrirAltaActividad() {
        Window parent = SwingUtilities.getWindowAncestor(
                view.getJTableActividades()
        );

        FormActividadJDialog dialog = new FormActividadJDialog(parent);

        dialog.getJButtonActAceptar().addActionListener(e -> {

            try {
                String nombre = dialog.getJTextFieldActNombre();
                String descripcion = dialog.getJTextAreaActDesc();
                LocalTime inicio = dialog.getHoraInicio();
                LocalTime fin = dialog.getHoraFin();
                int aforo = dialog.getJSpinnerActAforo();

                ValidadorActividad.validarNombre(nombre);
                ValidadorActividad.validarDescripcion(descripcion);
                ValidadorActividad.validarHorario(inicio, fin);
                ValidadorActividad.validarAforo(aforo);

                Actividad actividad = new Actividad();
                actividad.setNombre(nombre);
                actividad.setDescripcion(descripcion);
                actividad.setHoraInicio(inicio);
                actividad.setHoraFin(fin);
                actividad.setAforoMaximo(aforo);

                List<Dia> diasSeleccionados
                        = dialog.getDiasSeleccionados(diaService.obtenerDias());

                if (diasSeleccionados.isEmpty()) {
                    throw new IllegalArgumentException(
                            "Debe seleccionar al menos un día"
                    );
                }

                actividadService.validarSolapamiento(
                        actividad, diasSeleccionados
                );

                actividadService.crearActividad(actividad);

                actividadDiaService.asignarDiasActividad(
                        actividad.getIdActividad(),
                        diasSeleccionados
                );

                dialog.dispose();
                cargarActividades();
                JOptionPane.showMessageDialog(
                        dialog,
                        "Actividad creada correctamente",
                        "Nueva actividad",
                        JOptionPane.INFORMATION_MESSAGE
                );

            } catch (IllegalArgumentException ex) {

                JOptionPane.showMessageDialog(
                        dialog,
                        ex.getMessage(),
                        "Error de validación",
                        JOptionPane.ERROR_MESSAGE
                );

                String msg = ex.getMessage().toLowerCase();

                if (msg.contains("nombre")) {
                    dialog.limpiarNombre();
                } else if (msg.contains("descripción")) {
                    dialog.limpiarDescripcion();
                } else if (msg.contains("hora") || msg.contains("horario")) {
                    dialog.limpiarHoras();
                } else if (msg.contains("aforo")) {
                    dialog.limpiarAforo();
                }

            }
        });

        dialog.setVisible(true);
    }

    /**
     * Abre el formulario para editar la actividad seleccionada.
     *
     * <p>
     * Carga los datos actuales de la actividad y permite su modificación.
     * Gestiona errores de validación y de integridad mostrando mensajes
     * mediante JOptionPane.
     * </p>
     */
    private void editarActividad() {
        int fila = view.getJTableActividades().getSelectedRow();

        if (fila == -1) {
            JOptionPane.showMessageDialog(
                    view.getJTableActividades(),
                    "Seleccione una actividad",
                    "Aviso",
                    JOptionPane.WARNING_MESSAGE
            );
            return;
        }

        int id = (int) view.getJTableActividades().getValueAt(fila, 0);

        Actividad actividad = actividadService.buscarPorId(id);

        Window parent = SwingUtilities.getWindowAncestor(
                view.getJTableActividades()
        );

        FormActividadJDialog dialog = new FormActividadJDialog(parent);
        dialog.setActividad(actividad);

        List<Dia> dias = actividadDiaService.obtenerDiasActividad(id)
                .stream()
                .map(ActividadDia::getDia)
                .collect(Collectors.toList());

        dialog.marcarDias(dias);

        dialog.getJButtonActAceptar().addActionListener(e -> {

            try {
                String nombre = dialog.getJTextFieldActNombre();
                String descripcion = dialog.getJTextAreaActDesc();
                LocalTime inicio = dialog.getHoraInicio();
                LocalTime fin = dialog.getHoraFin();
                int aforo = dialog.getJSpinnerActAforo();

                ValidadorActividad.validarNombre(nombre);
                ValidadorActividad.validarDescripcion(descripcion);
                ValidadorActividad.validarHorario(inicio, fin);
                ValidadorActividad.validarAforo(aforo);

                actividad.setNombre(nombre);
                actividad.setDescripcion(descripcion);
                actividad.setHoraInicio(inicio);
                actividad.setHoraFin(fin);
                actividad.setAforoMaximo(aforo);

                List<Dia> diasSeleccionados
                        = dialog.getDiasSeleccionados(diaService.obtenerDias());

                if (diasSeleccionados.isEmpty()) {
                    throw new IllegalArgumentException(
                            "Debe seleccionar al menos un día"
                    );
                }

                actividadService.validarSolapamiento(
                        actividad, diasSeleccionados
                );

                actividadService.actualizarActividad(actividad);

                actividadDiaService.asignarDiasActividad(
                        actividad.getIdActividad(),
                        diasSeleccionados
                );

                dialog.dispose();
                cargarActividades();
                JOptionPane.showMessageDialog(
                        dialog,
                        "Actividad modificada correctamente",
                        "Cambio en actividad",
                        JOptionPane.INFORMATION_MESSAGE
                );

            } catch (IllegalArgumentException ex) {

                JOptionPane.showMessageDialog(dialog, ex.getMessage(),
                        "Error de validación", JOptionPane.ERROR_MESSAGE);

                String msg = ex.getMessage().toLowerCase();

                if (msg.contains("nombre")) {
                    dialog.limpiarNombre();
                } else if (msg.contains("descripción")) {
                    dialog.limpiarDescripcion();
                } else if (msg.contains("hora") || msg.contains("horario")) {
                    dialog.limpiarHoras();
                } else if (msg.contains("aforo")) {
                    dialog.limpiarAforo();
                }

            } catch (IllegalStateException ex) {

                JOptionPane.showMessageDialog(
                        dialog,
                        ex.getMessage(),
                        "Operación no permitida",
                        JOptionPane.WARNING_MESSAGE
                );
            }
        });

        dialog.setVisible(true);
    }

    /**
     * Elimina la actividad seleccionada previa confirmación del usuario.
     */
    /**
     * Elimina la actividad seleccionada previa confirmación del usuario.
     */
    private void eliminarActividad() {

        int fila = view.getJTableActividades().getSelectedRow();

        if (fila == -1) {
            JOptionPane.showMessageDialog(
                    null,
                    "Seleccione una actividad",
                    "Aviso",
                    JOptionPane.WARNING_MESSAGE
            );
            return;
        }

        int id = (int) view.getJTableActividades()
                .getValueAt(fila, 0);

        int confirm = JOptionPane.showConfirmDialog(
                null,
                "¿Eliminar la actividad seleccionada?",
                "Confirmar eliminación",
                JOptionPane.YES_NO_OPTION
        );

        if (confirm == JOptionPane.YES_OPTION) {

            try {

                actividadService.eliminarActividad(id);

                cargarActividades();

                JOptionPane.showMessageDialog(
                        null,
                        "Actividad eliminada correctamente",
                        "Eliminar actividad",
                        JOptionPane.INFORMATION_MESSAGE
                );

            } catch (IllegalStateException ex) {

                JOptionPane.showMessageDialog(
                        null,
                        ex.getMessage(),
                        "Acción no permitida.",
                        JOptionPane.WARNING_MESSAGE
                );

            } catch (Exception ex) {

                JOptionPane.showMessageDialog(
                        null,
                        "Error al eliminar la actividad",
                        "Error",
                        JOptionPane.ERROR_MESSAGE
                );
            }
        }
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
