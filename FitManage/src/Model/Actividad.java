package Model;

import java.time.LocalTime;
import java.util.Objects;

/**
 * Entidad que representa una actividad o clase colectiva del gimnasio.
 *
 * @author Alejandro
 */
public class Actividad {

    private int idActividad;
    private String nombre;
    private String descripcion;
    private LocalTime horaInicio;
    private LocalTime horaFin;
    private int aforoMaximo;

    public Actividad() {
    }

    public Actividad(int idActividad, String nombre, String descripcion,
            LocalTime horaInicio, LocalTime horaFin, int aforoMaximo) {
        this.idActividad = idActividad;
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.horaInicio = horaInicio;
        this.horaFin = horaFin;
        this.aforoMaximo = aforoMaximo;
    }

    public int getIdActividad() {
        return idActividad;
    }

    public void setIdActividad(int idActividad) {
        this.idActividad = idActividad;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public LocalTime getHoraInicio() {
        return horaInicio;
    }

    public void setHoraInicio(LocalTime horaInicio) {
        this.horaInicio = horaInicio;
    }

    public LocalTime getHoraFin() {
        return horaFin;
    }

    public void setHoraFin(LocalTime horaFin) {
        this.horaFin = horaFin;
    }

    public int getAforoMaximo() {
        return aforoMaximo;
    }

    public void setAforoMaximo(int aforoMaximo) {
        this.aforoMaximo = aforoMaximo;
    }

    /**
     * Dos actividades son iguales si comparten el mismo id.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Actividad)) {
            return false;
        }
        Actividad that = (Actividad) o;
        return idActividad == that.idActividad;
    }

    @Override
    public int hashCode() {
        return Objects.hash(idActividad);
    }

    @Override
    public String toString() {
        return nombre + " (" + horaInicio + " - " + horaFin + ")";
    }
}
