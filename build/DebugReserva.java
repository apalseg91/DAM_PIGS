import support.*;
import Model.*;
import service.*;
import java.time.*;

public class DebugReserva {
    public static void main(String[] args) {
        FakeReservaDAO reservaDAO = new FakeReservaDAO();
        FakeActividadDiaDAO actividadDiaDAO = new FakeActividadDiaDAO();
        ReservaService service = new ReservaService(reservaDAO, actividadDiaDAO);

        Actividad actividad = new Actividad();
        actividad.setIdActividad(1);
        actividad.setNombre("Yoga");
        actividad.setHoraInicio(LocalTime.of(10, 0));
        actividad.setHoraFin(LocalTime.of(11, 0));
        actividad.setAforoMaximo(2);

        ActividadDia ad = new ActividadDia();
        ad.setIdActividadDia(10);
        ad.setActividad(actividad);
        ad.setDia(new Dia(1, "L", "Lunes"));
        actividadDiaDAO.putActividadDia(ad);

        LocalDate fecha = LocalDate.now().plusDays(1);
        Reserva r1 = new Reserva();
        r1.setCliente(cliente(1));
        r1.setActividadDia(ad);
        r1.setFechaClase(fecha);
        r1.setActiva(true);
        reservaDAO.addReserva(r1);

        Reserva r2 = new Reserva();
        r2.setCliente(cliente(2));
        r2.setActividadDia(ad);
        r2.setFechaClase(fecha);
        r2.setActiva(true);
        reservaDAO.addReserva(r2);

        System.out.println("count=" + reservaDAO.countActivasPorActividadDiaYFecha(10, fecha));
        System.out.println("aforo=" + actividadDiaDAO.findById(10).getActividad().getAforoMaximo());
        try {
            service.crearReserva(reserva(cliente(3), ad, fecha));
            System.out.println("NO_THROW");
        } catch (Exception e) {
            System.out.println(e.getClass().getName() + ": " + e.getMessage());
        }
    }

    private static Cliente cliente(int id) {
        Cliente c = new Cliente();
        c.setIdCliente(id);
        c.setNombre("C" + id);
        c.setEmail("c" + id + "@mail.com");
        return c;
    }

    private static Reserva reserva(Cliente cliente, ActividadDia ad, LocalDate fecha) {
        Reserva r = new Reserva();
        r.setCliente(cliente);
        r.setActividadDia(ad);
        r.setFechaClase(fecha);
        r.setActiva(true);
        return r;
    }
}
