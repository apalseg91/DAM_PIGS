package service;

import Model.Actividad;
import Model.ActividadDia;
import Model.Cliente;
import Model.Dia;
import Model.Reserva;
import org.junit.Before;
import org.junit.Test;
import support.FakeActividadDiaDAO;
import support.FakeReservaDAO;
import support.AssertEx;

import java.time.LocalDate;
import java.time.LocalTime;

import static org.junit.Assert.*;

public class ReservaServiceTest {

    private FakeReservaDAO reservaDAO;
    private FakeActividadDiaDAO actividadDiaDAO;
    private ReservaService reservaService;

    @Before
    public void setUp() {
        reservaDAO = new FakeReservaDAO();
        actividadDiaDAO = new FakeActividadDiaDAO();
        reservaService = new ReservaService(reservaDAO, actividadDiaDAO);
    }

    private static Cliente clienteConId(int id) {
        Cliente c = new Cliente();
        c.setIdCliente(id);
        c.setNombre("Cliente");
        c.setEmail("c" + id + "@mail.com");
        return c;
    }

    private static Actividad actividadConAforo(int aforo) {
        Actividad a = new Actividad();
        a.setIdActividad(1);
        a.setNombre("Yoga");
        a.setHoraInicio(LocalTime.of(10, 0));
        a.setHoraFin(LocalTime.of(11, 0));
        a.setAforoMaximo(aforo);
        return a;
    }

    private static ActividadDia actividadDiaConId(int idActividadDia, int aforo) {
        ActividadDia ad = new ActividadDia();
        ad.setIdActividadDia(idActividadDia);
        ad.setActividad(actividadConAforo(aforo));
        ad.setDia(new Dia(1, "L", "Lunes"));
        return ad;
    }

    private static Reserva reservaValida(Cliente cliente, ActividadDia ad, LocalDate fechaClase) {
        Reserva r = new Reserva();
        r.setCliente(cliente);
        r.setActividadDia(ad);
        r.setFechaClase(fechaClase);
        r.setActiva(true);
        return r;
    }

    @Test
    public void crearReserva_validaReservaNula() {
        AssertEx.assertThrows(IllegalArgumentException.class, () -> reservaService.crearReserva(null));
    }

    @Test
    public void crearReserva_validaClienteObligatorio() {
        ActividadDia ad = actividadDiaConId(10, 5);
        actividadDiaDAO.putActividadDia(ad);

        Reserva r = new Reserva();
        r.setActividadDia(ad);
        r.setFechaClase(LocalDate.now().plusDays(1));

        AssertEx.assertThrows(IllegalArgumentException.class, () -> reservaService.crearReserva(r));
    }

    @Test
    public void crearReserva_validaActividadDiaObligatoria() {
        Reserva r = new Reserva();
        r.setCliente(clienteConId(1));
        r.setFechaClase(LocalDate.now().plusDays(1));

        AssertEx.assertThrows(IllegalArgumentException.class, () -> reservaService.crearReserva(r));
    }

    @Test
    public void crearReserva_validaFechaClaseObligatoria() {
        ActividadDia ad = actividadDiaConId(10, 5);
        actividadDiaDAO.putActividadDia(ad);

        Reserva r = new Reserva();
        r.setCliente(clienteConId(1));
        r.setActividadDia(ad);

        AssertEx.assertThrows(IllegalArgumentException.class, () -> reservaService.crearReserva(r));
    }

    @Test
    public void crearReserva_noPermiteFechaPasada() {
        ActividadDia ad = actividadDiaConId(10, 5);
        actividadDiaDAO.putActividadDia(ad);

        Reserva r = reservaValida(clienteConId(1), ad, LocalDate.now().minusDays(1));
        AssertEx.assertThrows(IllegalArgumentException.class, () -> reservaService.crearReserva(r));
    }

    @Test
    public void crearReserva_lanzaSiActividadDiaNoExiste() {
        ActividadDia ad = actividadDiaConId(99, 5);
        // No se inserta en el DAO fake, por lo que findById() devolverá null
        Reserva r = reservaValida(clienteConId(1), ad, LocalDate.now().plusDays(1));
        AssertEx.assertThrows(IllegalArgumentException.class, () -> reservaService.crearReserva(r));
    }

    @Test
    public void crearReserva_lanzaSiAforoLleno() {
        int aforo = 2;
        LocalDate fecha = LocalDate.now().plusDays(1);
        ActividadDia ad = actividadDiaConId(10, aforo);
        actividadDiaDAO.putActividadDia(ad);

        // El servicio cuenta únicamente reservas activas para controlar el aforo.
        Reserva r1 = reservaValida(clienteConId(1), ad, fecha);
        r1.setActiva(true);
        Reserva r2 = reservaValida(clienteConId(2), ad, fecha);
        r2.setActiva(true);
        reservaDAO.addReserva(r1);
        reservaDAO.addReserva(r2);

        Reserva nueva = reservaValida(clienteConId(3), ad, fecha);
        AssertEx.assertThrows(IllegalArgumentException.class, () -> reservaService.crearReserva(nueva));
    }

    @Test
    public void crearReserva_lanzaSiDobleReservaActiva() {
        int aforo = 5;
        LocalDate fecha = LocalDate.now().plusDays(1);
        ActividadDia ad = actividadDiaConId(10, aforo);
        actividadDiaDAO.putActividadDia(ad);

        Cliente cliente = clienteConId(1);
        Reserva existente = reservaValida(cliente, ad, fecha);
        existente.setActiva(true);
        reservaDAO.addReserva(existente);

        Reserva nueva = reservaValida(cliente, ad, fecha);
        AssertEx.assertThrows(IllegalArgumentException.class, () -> reservaService.crearReserva(nueva));
    }

    @Test
    public void hayAforoDisponible_devuelveSegunCountActivas() {
        int aforo = 2;
        LocalDate fecha = LocalDate.now().plusDays(1);
        ActividadDia ad = actividadDiaConId(10, aforo);
        actividadDiaDAO.putActividadDia(ad);

        Reserva r1 = reservaValida(clienteConId(1), ad, fecha);
        r1.setActiva(true);
        reservaDAO.addReserva(r1);

        assertTrue(reservaService.hayAforoDisponible(ad, fecha));

        Reserva r2 = reservaValida(clienteConId(2), ad, fecha);
        r2.setActiva(true);
        reservaDAO.addReserva(r2);

        assertFalse(reservaService.hayAforoDisponible(ad, fecha));
    }
}
