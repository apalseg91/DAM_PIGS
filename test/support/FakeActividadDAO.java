package support;

import DAO.ActividadDAO;
import Model.Actividad;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FakeActividadDAO implements ActividadDAO {

    private int nextId = 1;

    private final Map<Integer, Actividad> byId = new HashMap<>();
    private final Map<Integer, List<Actividad>> byDia = new HashMap<>();

    public int createCallCount = 0;
    public int updateCallCount = 0;
    public int deleteCallCount = 0;
    public Integer lastDeletedId = null;
    public Actividad lastUpdated = null;

    public void setActividadesForDia(int idDia, List<Actividad> actividades) {
        byDia.put(idDia, new ArrayList<>(actividades));
    }

    public void clearDia(int idDia) {
        byDia.remove(idDia);
    }

    @Override
    public void create(Actividad actividad) {
        createCallCount++;
        if (actividad.getIdActividad() <= 0) {
            actividad.setIdActividad(nextId++);
        }
        byId.put(actividad.getIdActividad(), actividad);
    }

    @Override
    public void update(Actividad actividad) {
        updateCallCount++;
        lastUpdated = actividad;
        byId.put(actividad.getIdActividad(), actividad);
    }

    @Override
    public void delete(int idActividad) {
        deleteCallCount++;
        lastDeletedId = idActividad;
        byId.remove(idActividad);
    }

    @Override
    public Actividad findById(int idActividad) {
        return byId.get(idActividad);
    }

    @Override
    public List<Actividad> findAll() {
        return new ArrayList<>(byId.values());
    }

    @Override
    public List<Actividad> findByDia(int idDia) {
        return byDia.containsKey(idDia)
                ? new ArrayList<>(byDia.get(idDia))
                : Collections.emptyList();
    }
}

