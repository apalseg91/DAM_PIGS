package Model;

import java.util.Objects;

/**
 *Entidad que representa un método de pago del sistema: EFECTIVO, TARJETA, BIZUM
 * @author Alejandro
 */
public class MetodoPago {
    private int idMetodoPago;
    private String nombreMetodo;
    private String descripcion;

    public MetodoPago() {
    }

    public MetodoPago(int idMetodoPago, String nombreMetodo, String descripcion) {
        this.idMetodoPago = idMetodoPago;
        this.nombreMetodo = nombreMetodo;
        this.descripcion = descripcion;
    }

    public int getIdMetodoPago() {
        return idMetodoPago;
    }

    public void setIdMetodoPago(int idMetodoPago) {
        this.idMetodoPago = idMetodoPago;
    }

    public String getNombreMetodo() {
        return nombreMetodo;
    }

    public void setNombreMetodo(String nombreMetodo) {
        this.nombreMetodo = nombreMetodo;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    /**
     * Dos métodos de pago son iguales si comparten el mismo id.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof MetodoPago)) return false;
        MetodoPago that = (MetodoPago) o;
        return idMetodoPago == that.idMetodoPago;
    }

    @Override
    public int hashCode() {
        return Objects.hash(idMetodoPago);
    }

    @Override
    public String toString() {
        return nombreMetodo;
    }
}
