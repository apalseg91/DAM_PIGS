package Model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Objects;

/**
 * Entidad que representa a un cliente (socio) del gimnasio.
 *
 * @author Alejandro
 */
public class Cliente {

    private int idCliente;
    private String nombre;
    private String apellidos;
    private String email;
    private String dni;
    private String telefono;
    private String direccion;
    private LocalDate fechaAlta;
    private LocalDate fechaProximoPago;
    private BigDecimal cuotaMensual;
    private boolean activo;

    // Relaciones con otros POJOS
    private Usuario usuario; // OJO puede ser null
    private Rol rol;

    public Cliente() {
    }

    public Cliente(int idCliente, String nombre, String apellidos, String email,
            String dni, String telefono, String direccion,
            LocalDate fechaAlta, LocalDate fechaProximoPago,
            BigDecimal cuotaMensual, boolean activo,
            Usuario usuario, Rol rol) {

        this.idCliente = idCliente;
        this.nombre = nombre;
        this.apellidos = apellidos;
        this.email = email;
        this.dni = dni;
        this.telefono = telefono;
        this.direccion = direccion;
        this.fechaAlta = fechaAlta;
        this.fechaProximoPago = fechaProximoPago;
        this.cuotaMensual = cuotaMensual;
        this.activo = activo;
        this.usuario = usuario;
        this.rol = rol;
    }

    public int getIdCliente() {
        return idCliente;
    }

    public void setIdCliente(int idCliente) {
        this.idCliente = idCliente;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getApellidos() {
        return apellidos;
    }

    public void setApellidos(String apellidos) {
        this.apellidos = apellidos;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getDni() {
        return dni;
    }

    public void setDni(String dni) {
        this.dni = dni;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public LocalDate getFechaAlta() {
        return fechaAlta;
    }

    public void setFechaAlta(LocalDate fechaAlta) {
        this.fechaAlta = fechaAlta;
    }

    public LocalDate getFechaProximoPago() {
        return fechaProximoPago;
    }

    public void setFechaProximoPago(LocalDate fechaProximoPago) {
        this.fechaProximoPago = fechaProximoPago;
    }

    public BigDecimal getCuotaMensual() {
        return cuotaMensual;
    }

    public void setCuotaMensual(BigDecimal cuotaMensual) {
        this.cuotaMensual = cuotaMensual;
    }

    public boolean isActivo() {
        return activo;
    }

    public void setActivo(boolean activo) {
        this.activo = activo;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public Rol getRol() {
        return rol;
    }

    public void setRol(Rol rol) {
        this.rol = rol;
    }

    /**
     * Dos clientes son iguales si tienen el mismo id.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Cliente)) {
            return false;
        }
        Cliente cliente = (Cliente) o;
        return idCliente == cliente.idCliente;
    }

    @Override
    public int hashCode() {
        return Objects.hash(idCliente);
    }

    @Override
    public String toString() {
        return nombre + " " + apellidos;
    }
}
