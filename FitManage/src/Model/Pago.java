package Model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Objects;

/**
 * Entidad que representa un pago realizado por un cliente
 *
 * @author Alejandro
 */
public class Pago {

    private int idPago;
    private LocalDate fechaPago;
    private BigDecimal importe;
    /**
     * Concepto descriptivo de la factura. Por defecto es 'Cuota mensual'
     */
    private String concepto;

    // Relaciones
    private Cliente cliente;
    private MetodoPago metodoPago;
    private Factura factura; // puede ser null

    // Constructor vacío
    public Pago() {
    }

    // Constructor completo
    public Pago(int idPago, LocalDate fechaPago, BigDecimal importe,
            Cliente cliente, MetodoPago metodoPago, Factura factura) {
        this.idPago = idPago;
        this.fechaPago = fechaPago;
        this.importe = importe;
        this.cliente = cliente;
        this.metodoPago = metodoPago;
        this.factura = factura;
    }

    public int getIdPago() {
        return idPago;
    }

    public void setIdPago(int idPago) {
        this.idPago = idPago;
    }

    public LocalDate getFechaPago() {
        return fechaPago;
    }

    public void setFechaPago(LocalDate fechaPago) {
        this.fechaPago = fechaPago;
    }

    public BigDecimal getImporte() {
        return importe;
    }

    public void setImporte(BigDecimal importe) {
        this.importe = importe;
    }

    public Cliente getCliente() {
        return cliente;
    }

    public void setCliente(Cliente cliente) {
        this.cliente = cliente;
    }

    public MetodoPago getMetodoPago() {
        return metodoPago;
    }

    public void setMetodoPago(MetodoPago metodoPago) {
        this.metodoPago = metodoPago;
    }

    public Factura getFactura() {
        return factura;
    }

    public void setFactura(Factura factura) {
        this.factura = factura;
    }
    /**
 * Obtiene el concepto de la factura.
 * 
 * @return concepto descriptivo
 */
public String getConcepto() {
    return concepto;
}

/**
 * Establece el concepto de la factura.
 * 
 * @param concepto descripción asociada a la factura
 */
public void setConcepto(String concepto) {
    this.concepto = concepto;
}

    /**
     * Dos pagos son iguales si tienen el mismo id.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Pago)) {
            return false;
        }
        Pago pago = (Pago) o;
        return idPago == pago.idPago;
    }

    @Override
    public int hashCode() {
        return Objects.hash(idPago);
    }

    @Override
    public String toString() {
        return "Pago " + importe + " € - " + fechaPago;
    }
}
