package support;

import DAO.ReservaDAO;
import Model.Reserva;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class FakeReservaDAO implements ReservaDAO {

    private int nextId = 1;
    private final List<Reserva> reservas = new ArrayList<>();

    public int createCallCount = 0;

    public void addReserva(Reserva reserva) {
        if (reserva.getIdReserva() <= 0) {
            reserva.setIdReserva(nextId++);
        }
        reservas.add(reserva);
    }

    @Override
    public void create(Reserva reserva) {
        createCallCount++;
        addReserva(reserva);
    }

    @Override
    public void cancel(int idReserva) {
        for (Reserva r : reservas) {
            if (r.getIdReserva() == idReserva) {
                r.setActiva(false);
                return;
            }
        }
    }

    @Override
    public List<Reserva> findByCliente(int idCliente) {
        List<Reserva> out = new ArrayList<>();
        for (Reserva r : reservas) {
            if (r.getCliente() != null && r.getCliente().getIdCliente() == idCliente) {
                out.add(r);
            }
        }
        return out;
    }

    @Override
    public List<Reserva> findByFecha(LocalDate fecha) {
        List<Reserva> out = new ArrayList<>();
        for (Reserva r : reservas) {
            if (fecha != null && fecha.equals(r.getFechaClase())) {
                out.add(r);
            }
        }
        return out;
    }

    @Override
    public List<Reserva> findActivasByCliente(int idCliente) {
        List<Reserva> out = new ArrayList<>();
        for (Reserva r : reservas) {
            if (r.getCliente() != null
                    && r.getCliente().getIdCliente() == idCliente
                    && r.isActiva()) {
                out.add(r);
            }
        }
        return out;
    }

    @Override
    public int countActivasPorActividadDiaYFecha(int idActividadDia, LocalDate fecha) {
        int count = 0;
        for (Reserva r : reservas) {
            if (r.isActiva()
                    && r.getActividadDia() != null
                    && r.getActividadDia().getIdActividadDia() == idActividadDia
                    && fecha != null
                    && fecha.equals(r.getFechaClase())) {
                count++;
            }
        }
        return count;
    }
}

