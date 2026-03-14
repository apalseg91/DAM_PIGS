package support;

import DAO.ActividadDiaDAO;
import Model.ActividadDia;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FakeActividadDiaDAO implements ActividadDiaDAO {

    private int nextId = 1;

    private final Map<Integer, ActividadDia> byId = new HashMap<>();

    public int createCallCount = 0;
    public int deleteByActividadCallCount = 0;
    public Integer lastDeletedByActividadId = null;

    public void putActividadDia(ActividadDia ad) {
        if (ad.getIdActividadDia() <= 0) {
            ad.setIdActividadDia(nextId++);
        }
        byId.put(ad.getIdActividadDia(), ad);
    }

    public void clear() {
        byId.clear();
    }

    @Override
    public void create(ActividadDia actividadDia) {
        createCallCount++;
        putActividadDia(actividadDia);
    }

    @Override
    public void deleteByActividad(int idActividad) {
        deleteByActividadCallCount++;
        lastDeletedByActividadId = idActividad;
    }

    @Override
    public List<ActividadDia> findByActividad(int idActividad) {
        if (byId.isEmpty()) {
            return Collections.emptyList();
        }
        List<ActividadDia> out = new ArrayList<>();
        for (ActividadDia ad : byId.values()) {
            if (ad.getActividad() != null
                    && ad.getActividad().getIdActividad() == idActividad) {
                out.add(ad);
            }
        }
        return out;
    }

    @Override
    public ActividadDia findById(int idActividadDia) {
        return byId.get(idActividadDia);
    }
}

