package Model;

import java.util.Objects;

/**
 * Entidad que representa un día de la semana.
 *
 * @author Alejandro
 */
public class Dia {

    private int idDia;
    private String codigo;
    private String nombre;

    // Constructor vacío
    public Dia() {
    }

    // Constructor completo
    public Dia(int idDia, String codigo, String nombre) {
        this.idDia = idDia;
        this.codigo = codigo;
        this.nombre = nombre;
    }

    public int getIdDia() {
        return idDia;
    }

    public void setIdDia(int idDia) {
        this.idDia = idDia;
    }

    public String getCodigo() {
        return codigo != null ? codigo.trim() : null;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    /**
     * Dos días son iguales si tienen el mismo id.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Dia)) {
            return false;
        }
        Dia dia = (Dia) o;
        return idDia == dia.idDia;
    }

    @Override
    public int hashCode() {
        return Objects.hash(idDia);
    }

    @Override
    public String toString() {
        return nombre;
    }
}
