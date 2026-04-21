package integration;

import DAO.ActividadDAO;
import DAO.ActividadDiaDAO;
import DAO.impl.ActividadDAOImpl;
import DAO.impl.ActividadDiaDAOImpl;
import Model.Actividad;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.Assert;
import service.ActividadService;
import util.DBConnection;

import java.sql.Connection;
import java.time.LocalTime;
import java.util.UUID;

import static org.junit.Assert.*;

public class ActividadServiceITTest {

    private static final int MAX_NOMBRE_LENGTH = 50;

    private ActividadService actividadService;
    private Actividad actividadTest;

    @BeforeClass
    public static void checkOracleConnection() {
        try (Connection ignored = DBConnection.getConnection()) {
            // OK
        } catch (Exception e) {
            AssertionError ae = new AssertionError(
                    "Oracle no está disponible en jdbc:oracle:thin:@//localhost:1521/xe (usuario FITMANAGE)."
            );
            ae.initCause(e);
            throw ae;
        }
    }

    @Before
    public void setUp() {
        ActividadDAO actividadDAO = new ActividadDAOImpl();
        ActividadDiaDAO actividadDiaDAO = new ActividadDiaDAOImpl();
        actividadService = new ActividadService(actividadDAO, actividadDiaDAO);

        actividadTest = new Actividad();
        actividadTest.setNombre(buildNombre("ActIT_"));
        actividadTest.setDescripcion("Actividad creada para test integración");
        actividadTest.setHoraInicio(LocalTime.of(10, 0));
        actividadTest.setHoraFin(LocalTime.of(11, 0));
        actividadTest.setAforoMaximo(20);

        assertTrue(actividadTest.getNombre().length() <= MAX_NOMBRE_LENGTH);
        actividadService.crearActividad(actividadTest);
        assertTrue(actividadTest.getIdActividad() > 0);
    }

    @After
    public void tearDown() {
        try {
            if (actividadTest != null && actividadTest.getIdActividad() > 0) {
                actividadService.eliminarActividad(actividadTest.getIdActividad());
            }
        } catch (Exception ignored) {
            // Limpieza best-effort para no enmascarar fallos del test principal
        }
    }

    @Test
    public void testBuscarActividad() {
        Actividad encontrada = actividadService.buscarPorId(actividadTest.getIdActividad());
        assertNotNull(encontrada);
        assertEquals(actividadTest.getNombre(), encontrada.getNombre());
    }

    @Test
    public void testActualizarActividad() {
        actividadTest.setNombre(buildNombre("ActUpd_"));
        assertTrue(actividadTest.getNombre().length() <= MAX_NOMBRE_LENGTH);
        actividadService.actualizarActividad(actividadTest);

        Actividad actualizada = actividadService.buscarPorId(actividadTest.getIdActividad());
        assertNotNull(actualizada);
        assertEquals(actividadTest.getNombre(), actualizada.getNombre());
    }

    @Test
    public void testEliminarActividad() {
        int id = actividadTest.getIdActividad();

        actividadService.eliminarActividad(id);
        actividadTest.setIdActividad(0); // evitar doble borrado en tearDown

        Actividad eliminada = actividadService.buscarPorId(id);
        assertNull(eliminada);
    }

    private static String buildNombre(String prefix) {
        if (prefix.length() >= MAX_NOMBRE_LENGTH) {
            return prefix.substring(0, MAX_NOMBRE_LENGTH);
        }

        String suffix = UUID.randomUUID().toString();
        int maxSuffixLength = Math.min(
                MAX_NOMBRE_LENGTH - prefix.length(),
                suffix.length()
        );
        return prefix + suffix.substring(0, maxSuffixLength);
    }
}
