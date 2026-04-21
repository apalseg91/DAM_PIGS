package Model;

import java.util.Objects;

/**
 * Entidad que representa el rol en el sistema: ADMINISTRADOR, USUARIO...
 * @author Alejandro
 */
public class Rol {
    
    private int idRol ;
    private String nombreRol;
    private String descripcion;
    
    public Rol(){
    }

    public Rol(int idRol, String nombreRol, String descripcion) {
        this.idRol = idRol;
        this.nombreRol = nombreRol;
        this.descripcion = descripcion;
    }

    public int getIdRol() {
        return idRol;
    }

    public void setIdRol(int idRol) {
        this.idRol = idRol;
    }

    public String getNombreRol() {
        return nombreRol;
    }

    public void setNombreRol(String nombreRol) {
        this.nombreRol = nombreRol;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }
    /**
     * Dos roles se consideran iguales si tienen el mismo id.
    */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Rol)) return false;
        Rol rol = (Rol) o;
        return idRol == rol.idRol;
    }

    @Override
    public int hashCode() {
        return Objects.hash(idRol);
    }

    @Override
    public String toString() {
        return nombreRol;
    }  
    
}
