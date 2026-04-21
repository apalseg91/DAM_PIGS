package Model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Objects;

/**
 * Entidad que representa una factura generada por el sistema.
 * @author Alejandro
 */
public class Factura {

    private int idFactura;
    private LocalDate fechaEmision;
    private BigDecimal total;

    // Relaciones cono otros POJOS
    private MetodoPago metodoPago; // OJO puede ser null
    private Pago pago;             // OJO puede ser null

    public Factura() {
    }

    public Factura(int idFactura, LocalDate fechaEmision, BigDecimal total,
            MetodoPago metodoPago, Pago pago) {
        this.idFactura = idFactura;
        this.fechaEmision = fechaEmision;
        this.total = total;
        this.metodoPago = metodoPago;
        this.pago = pago;
    }

    public int getIdFactura() {
        return idFactura;
    }

    public void setIdFactura(int idFactura) {
        this.idFactura = idFactura;
    }

    public LocalDate getFechaEmision() {
        return fechaEmision;
    }

    public void setFechaEmision(LocalDate fechaEmision) {
        this.fechaEmision = fechaEmision;
    }

    public BigDecimal getTotal() {
        return total;
    }

    public void setTotal(BigDecimal total) {
        this.total = total;
    }

    public MetodoPago getMetodoPago() {
        return metodoPago;
    }

    public void setMetodoPago(MetodoPago metodoPago) {
        this.metodoPago = metodoPago;
    }

    public Pago getPago() {
        return pago;
    }

    public void setPago(Pago pago) {
        this.pago = pago;
    }

    /**
     * Dos facturas son iguales si tienen el mismo id.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Factura)) {
            return false;
        }
        Factura factura = (Factura) o;
        return idFactura == factura.idFactura;
    }

    @Override
    public int hashCode() {
        return Objects.hash(idFactura);
    }

    @Override
    public String toString() {
        return "Factura #" + idFactura + " - " + total + " €";
    }
}
