package support;

import DAO.ClienteDAO;
import Model.Cliente;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FakeClienteDAO implements ClienteDAO {

    private int nextId = 1;
    private final Map<Integer, Cliente> byId = new HashMap<>();
    private final Map<String, Cliente> byEmailLower = new HashMap<>();
    private final Map<Integer, Boolean> inactivoById = new HashMap<>();
    private final Map<Integer, Boolean> dependenciasById = new HashMap<>();

    public void putCliente(Cliente cliente) {
        if (cliente.getIdCliente() <= 0) {
            cliente.setIdCliente(nextId++);
        }
        byId.put(cliente.getIdCliente(), cliente);
        if (cliente.getEmail() != null) {
            byEmailLower.put(cliente.getEmail().trim().toLowerCase(), cliente);
        }
    }

    @Override
    public void create(Cliente cliente) {
        putCliente(cliente);
    }

    @Override
    public void update(Cliente cliente) {
        putCliente(cliente);
    }

    @Override
    public void delete(int idCliente) {
        Cliente removed = byId.remove(idCliente);
        if (removed != null && removed.getEmail() != null) {
            byEmailLower.remove(removed.getEmail().trim().toLowerCase());
        }
        inactivoById.remove(idCliente);
        dependenciasById.remove(idCliente);
    }

    @Override
    public void setInactivo(int idCliente) {
        inactivoById.put(idCliente, true);
    }

    @Override
    public Cliente findById(int idCliente) {
        return byId.get(idCliente);
    }

    @Override
    public Cliente findByEmail(String email) {
        if (email == null) {
            return null;
        }
        return byEmailLower.get(email.trim().toLowerCase());
    }

    @Override
    public Cliente findDetalleByEmail(String email) {
        return findByEmail(email);
    }

    @Override
    public List<Cliente> findAll() {
        return new ArrayList<>(byId.values());
    }

    @Override
    public boolean tieneDependencias(int idCliente) {
        Boolean v = dependenciasById.get(idCliente);
        return v != null && v;
    }

    @Override
    public boolean estaInactivo(int idCliente) {
        Boolean v = inactivoById.get(idCliente);
        return v != null && v;
    }

    @Override
    public void setActivo(int idCliente) {
        inactivoById.put(idCliente, false);
    }

    @Override
    public List<Cliente> findActivos() {
        return Collections.emptyList();
    }

    @Override
    public List<Cliente> findInactivos() {
        return Collections.emptyList();
    }

    @Override
    public List<Cliente> findAllDetalle() {
        return findAll();
    }
}

