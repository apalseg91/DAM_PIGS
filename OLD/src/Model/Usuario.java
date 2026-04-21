package Model;

import java.time.LocalDate;
import java.util.Objects;

/**
 * Entidad que representa un usuario del sistema.Se utiliza para la autenticación 
 * y control de acceso.
 * @author Alejandro
 */
public class Usuario {
    private int idUsuario;
    private String email;
    private String contrasenaHash;
    private LocalDate fechaCreacion;
    private Rol rol;

    public Usuario() {
    }

    public Usuario(int idUsuario, String email, String contrasenaHash,
                   LocalDate fechaCreacion, Rol rol) {
        this.idUsuario = idUsuario;
        this.email = email;
        this.contrasenaHash = contrasenaHash;
        this.fechaCreacion = fechaCreacion;
        this.rol = rol;
    }

    public int getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(int idUsuario) {
        this.idUsuario = idUsuario;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getContrasenaHash() {
        return contrasenaHash;
    }

    public void setContrasenaHash(String contrasenaHash) {
        this.contrasenaHash = contrasenaHash;
    }

    public LocalDate getFechaCreacion() {
        return fechaCreacion;
    }

    public void setFechaCreacion(LocalDate fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }

    public Rol getRol() {
        return rol;
    }

    public void setRol(Rol rol) {
        this.rol = rol;
    }

    /**
     * Dos usuarios son iguales si tienen el mismo id.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Usuario)) return false;
        Usuario usuario = (Usuario) o;
        return idUsuario == usuario.idUsuario;
    }

    @Override
    public int hashCode() {
        return Objects.hash(idUsuario);
    }

    @Override
    public String toString() {
        return email;
    }
}
