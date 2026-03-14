package Model;

import java.time.LocalDate;
import java.util.Objects;

/**
 * Entidad que representa una reserva de un cliente para una actividad en una
 * fecha concreta.
 *
 * @author Alejandro
 */
public class Reserva {

    private int idReserva;
    private Cliente cliente;
    private ActividadDia actividadDia;
    private LocalDate fechaClase;
    private LocalDate fechaReserva;
    private boolean activa;

    public Reserva() {
    }

    public Reserva(int idReserva, Cliente cliente, ActividadDia actividadDia,
            LocalDate fechaClase, LocalDate fechaReserva, boolean activa) {
        this.idReserva = idReserva;
        this.cliente = cliente;
        this.actividadDia = actividadDia;
        this.fechaClase = fechaClase;
        this.fechaReserva = fechaReserva;
        this.activa = activa;
    }

    public int getIdReserva() {
        return idReserva;
    }

    public void setIdReserva(int idReserva) {
        this.idReserva = idReserva;
    }

    public Cliente getCliente() {
        return cliente;
    }

    public void setCliente(Cliente cliente) {
        this.cliente = cliente;
    }

    public ActividadDia getActividadDia() {
        return actividadDia;
    }

    public void setActividadDia(ActividadDia actividadDia) {
        this.actividadDia = actividadDia;
    }

    public LocalDate getFechaClase() {
        return fechaClase;
    }

    public void setFechaClase(LocalDate fechaClase) {
        this.fechaClase = fechaClase;
    }

    public LocalDate getFechaReserva() {
        return fechaReserva;
    }

    public void setFechaReserva(LocalDate fechaReserva) {
        this.fechaReserva = fechaReserva;
    }

    public boolean isActiva() {
        return activa;
    }

    public void setActiva(boolean activa) {
        this.activa = activa;
    }

    /**
     * Dos reservas son iguales si tienen el mismo id.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Reserva)) {
            return false;
        }
        Reserva reserva = (Reserva) o;
        return idReserva == reserva.idReserva;
    }

    @Override
    public int hashCode() {
        return Objects.hash(idReserva);
    }

    @Override
    public String toString() {

        String estadoTexto = activa ? "ACTIVA" : "CANCELADA";

        return actividadDia.getActividad().getNombre()
                + " · "
                + actividadDia.getDia().getNombre()
                + " · "
                + actividadDia.getActividad().getHoraInicio()
                + "-" + actividadDia.getActividad().getHoraFin()
                + " · "
                + fechaClase
                + " · "
                + estadoTexto;
    }
}
