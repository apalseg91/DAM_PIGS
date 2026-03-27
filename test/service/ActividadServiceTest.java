package service;

import Model.Actividad;
import Model.Dia;
import org.junit.Before;
import org.junit.Test;
import support.FakeActividadDAO;
import support.FakeActividadDiaDAO;
import support.AssertEx;

import java.time.LocalTime;
import java.util.Arrays;
import java.util.Collections;

import static org.junit.Assert.*;

public class ActividadServiceTest {

    private FakeActividadDAO actividadDAO;
    private FakeActividadDiaDAO actividadDiaDAO;
    private ActividadService actividadService;

    @Before
    public void setUp() {
        actividadDAO = new FakeActividadDAO();
        actividadDiaDAO = new FakeActividadDiaDAO();
        actividadService = new ActividadService(actividadDAO, actividadDiaDAO);
    }

    private static Actividad actividadValida() {
        Actividad a = new Actividad();
        a.setNombre("Spinning");
        a.setDescripcion("Clase");
        a.setHoraInicio(LocalTime.of(10, 0));
        a.setHoraFin(LocalTime.of(11, 0));
        a.setAforoMaximo(10);
        return a;
    }

    @Test
    public void crearActividad_validaActividadNula() {
        AssertEx.assertThrows(IllegalArgumentException.class, () -> actividadService.crearActividad(null));
    }

    @Test
    public void crearActividad_validaNombreObligatorio() {
        Actividad a = actividadValida();
        a.setNombre("   ");
        AssertEx.assertThrows(IllegalArgumentException.class, () -> actividadService.crearActividad(a));
    }

    @Test
    public void crearActividad_validaLongitudMaximaNombre() {
        Actividad a = actividadValida();
        a.setNombre("123456789012345678901234567890123456789012345678901");
        AssertEx.assertThrows(IllegalArgumentException.class, () -> actividadService.crearActividad(a));
    }

    @Test
    public void crearActividad_validaHorarioObligatorio() {
        Actividad a = actividadValida();
        a.setHoraInicio(null);
        AssertEx.assertThrows(IllegalArgumentException.class, () -> actividadService.crearActividad(a));
    }

    @Test
    public void crearActividad_validaHoraInicioAntesQueHoraFin() {
        Actividad a = actividadValida();
        a.setHoraInicio(LocalTime.of(11, 0));
        a.setHoraFin(LocalTime.of(11, 0));
        AssertEx.assertThrows(IllegalArgumentException.class, () -> actividadService.crearActividad(a));
    }

    @Test
    public void crearActividad_validaAforoMayorQueCero() {
        Actividad a = actividadValida();
        a.setAforoMaximo(0);
        AssertEx.assertThrows(IllegalArgumentException.class, () -> actividadService.crearActividad(a));
    }

    @Test
    public void actualizarActividad_lanzaSiIdInvalido() {
        Actividad a = actividadValida();
        a.setIdActividad(0);
        AssertEx.assertThrows(IllegalArgumentException.class, () -> actividadService.actualizarActividad(a));
    }

    @Test
    public void actualizarActividad_delegaEnDAO() {
        Actividad a = actividadValida();
        a.setIdActividad(1);
        actividadService.actualizarActividad(a);
        assertEquals(1, actividadDAO.updateCallCount);
        assertSame(a, actividadDAO.lastUpdated);
    }

    @Test
    public void actualizarActividad_validaLongitudMaximaNombre() {
        Actividad a = actividadValida();
        a.setIdActividad(1);
        a.setNombre("123456789012345678901234567890123456789012345678901");
        AssertEx.assertThrows(IllegalArgumentException.class, () -> actividadService.actualizarActividad(a));
    }

    @Test
    public void validarSolapamiento_noSolapa() {
        Dia lunes = new Dia(1, "L", "Lunes");

        Actividad existente = actividadValida();
        existente.setIdActividad(100);
        existente.setHoraInicio(LocalTime.of(9, 0));
        existente.setHoraFin(LocalTime.of(10, 0));
        actividadDAO.setActividadesForDia(lunes.getIdDia(), Collections.singletonList(existente));

        Actividad nueva = actividadValida();
        nueva.setHoraInicio(LocalTime.of(10, 0));
        nueva.setHoraFin(LocalTime.of(11, 0));

        AssertEx.assertDoesNotThrow(() -> actividadService.validarSolapamiento(nueva, Arrays.asList(lunes)));
    }

    @Test
    public void validarSolapamiento_solapaYLanza() {
        Dia lunes = new Dia(1, "L", "Lunes");

        Actividad existente = actividadValida();
        existente.setIdActividad(100);
        existente.setHoraInicio(LocalTime.of(10, 0));
        existente.setHoraFin(LocalTime.of(11, 0));
        actividadDAO.setActividadesForDia(lunes.getIdDia(), Collections.singletonList(existente));

        Actividad nueva = actividadValida();
        nueva.setHoraInicio(LocalTime.of(10, 30));
        nueva.setHoraFin(LocalTime.of(11, 30));

        AssertEx.assertThrows(IllegalArgumentException.class, () -> actividadService.validarSolapamiento(nueva, Arrays.asList(lunes)));
    }

    @Test
    public void validarSolapamiento_mismoIdSeIgnoraEnEdicion() {
        Dia lunes = new Dia(1, "L", "Lunes");

        Actividad existente = actividadValida();
        existente.setIdActividad(5);
        existente.setHoraInicio(LocalTime.of(10, 0));
        existente.setHoraFin(LocalTime.of(11, 0));
        actividadDAO.setActividadesForDia(lunes.getIdDia(), Collections.singletonList(existente));

        Actividad nueva = actividadValida();
        nueva.setIdActividad(5);
        nueva.setHoraInicio(LocalTime.of(10, 30));
        nueva.setHoraFin(LocalTime.of(11, 30));

        AssertEx.assertDoesNotThrow(() -> actividadService.validarSolapamiento(nueva, Arrays.asList(lunes)));
    }

    @Test
    public void eliminarActividad_llamaADaosEnOrdenLogico() {
        actividadService.eliminarActividad(7);
        assertEquals(1, actividadDiaDAO.cancelarReservasPorActividadCallCount);
        assertEquals(Integer.valueOf(7), actividadDiaDAO.lastCancelledReservasActividadId);
        assertEquals(1, actividadDiaDAO.deleteByActividadCallCount);
        assertEquals(Integer.valueOf(7), actividadDiaDAO.lastDeletedByActividadId);
        assertEquals(1, actividadDAO.deleteCallCount);
        assertEquals(Integer.valueOf(7), actividadDAO.lastDeletedId);
    }
}
