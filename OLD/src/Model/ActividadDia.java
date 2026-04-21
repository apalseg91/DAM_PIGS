package Model;

import java.util.Objects;

/**
 * Entidad que representa la relación entre una actividad y un día.
 *
 * @author Alejandro
 */
public class ActividadDia {

    private int idActividadDia;
    private Actividad actividad;
    private Dia dia;

    public ActividadDia() {
    }

    public ActividadDia(int idActividadDia, Actividad actividad, Dia dia) {
        this.idActividadDia = idActividadDia;
        this.actividad = actividad;
        this.dia = dia;
    }

    public int getIdActividadDia() {
        return idActividadDia;
    }

    public void setIdActividadDia(int idActividadDia) {
        this.idActividadDia = idActividadDia;
    }

    public Actividad getActividad() {
        return actividad;
    }

    public void setActividad(Actividad actividad) {
        this.actividad = actividad;
    }

    public Dia getDia() {
        return dia;
    }

    public void setDia(Dia dia) {
        this.dia = dia;
    }
    /**
     * Dos combinaciones actividad-día son iguales si tienen el mismo id.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ActividadDia)) {
            return false;
        }
        ActividadDia that = (ActividadDia) o;
        return idActividadDia == that.idActividadDia;
    }

    @Override
    public int hashCode() {
        return Objects.hash(idActividadDia);
    }

    @Override
    public String toString() {

        String nombreActividad
                = (actividad != null) ? actividad.getNombre() : "Actividad";

        String nombreDia
                = (dia != null) ? dia.getNombre() : "Día";

        return nombreActividad + " - " + nombreDia;
    }
}
