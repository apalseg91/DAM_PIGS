package support;

import DAO.UsuarioDAO;
import Model.Rol;
import Model.Usuario;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FakeUsuarioDAO implements UsuarioDAO {

    private int nextId = 1;
    private final Map<Integer, Usuario> byId = new HashMap<>();
    private final Map<String, Usuario> byEmailLower = new HashMap<>();

    public void putUsuario(Usuario usuario) {
        if (usuario.getIdUsuario() <= 0) {
            usuario.setIdUsuario(nextId++);
        }
        byId.put(usuario.getIdUsuario(), usuario);
        if (usuario.getEmail() != null) {
            byEmailLower.put(usuario.getEmail().trim().toLowerCase(), usuario);
        }
    }

    @Override
    public Usuario findByEmail(String email) {
        if (email == null) {
            return null;
        }
        return byEmailLower.get(email.trim().toLowerCase());
    }

    @Override
    public Usuario findById(int id) {
        return byId.get(id);
    }

    @Override
    public List<Usuario> findAllByRol(String Rol) {
        return Collections.emptyList();
    }

    @Override
    public void save(Usuario usuario) {
        putUsuario(usuario);
    }

    @Override
    public void update(Usuario usuario) {
        putUsuario(usuario);
    }

    @Override
    public void delete(int id) {
        Usuario removed = byId.remove(id);
        if (removed != null && removed.getEmail() != null) {
            byEmailLower.remove(removed.getEmail().trim().toLowerCase());
        }
    }

    @Override
    public boolean noTieneClienteAsociado(int idUsuario) {
        return true;
    }

    @Override
    public int contarAdministradores() {
        return 0;
    }

    @Override
    public Rol obtenerRolPorNombre(String nombreRol) {
        return null;
    }
}

