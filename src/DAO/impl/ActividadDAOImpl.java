package DAO.impl;

import DAO.ActividadDAO;
import Model.Actividad;
import util.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.sql.CallableStatement;
import java.sql.Types;

/**
 * Implementación del DAO para la entidad {@link Actividad}.
 *
 * Gestiona las operaciones CRUD sobre la tabla ACTIVIDAD,
 * incluyendo consultas por día asociado.
 *
 * Compatible con Java 8 para garantizar integración con iReport 5.6.0.
 *
 * @author Alejandro
 */
public class ActividadDAOImpl implements ActividadDAO {

    /**
     * Formato estándar para horas (HH:mm).
     */
    private static final DateTimeFormatter TIME_FORMAT =
            DateTimeFormatter.ofPattern("HH:mm");

    /**
     * Inserta una nueva actividad utilizando secuencia Oracle
     * y recupera el ID generado.
     */
    private static final String SQL_INSERT =
            "INSERT INTO ACTIVIDAD " +
            "(id_actividad, nombre, descripcion, hora_inicio, hora_fin, aforo_maximo) " +
            "VALUES (SEQ_ACTIVIDAD.NEXTVAL, ?, ?, ?, ?, ?) " +
            "RETURNING id_actividad INTO ?";

    /**
     * Actualiza los datos de una actividad existente.
     */
    private static final String SQL_UPDATE =
            "UPDATE ACTIVIDAD " +
            "SET nombre = ?, descripcion = ?, hora_inicio = ?, hora_fin = ?, aforo_maximo = ? " +
            "WHERE id_actividad = ?";

    /**
     * Elimina una actividad por su ID.
     */
    private static final String SQL_DELETE =
            "DELETE FROM ACTIVIDAD " +
            "WHERE id_actividad = ?";

    /**
     * Busca una actividad por su ID.
     */
    private static final String SQL_FIND_BY_ID =
            "SELECT id_actividad, nombre, descripcion, hora_inicio, hora_fin, aforo_maximo " +
            "FROM ACTIVIDAD " +
            "WHERE id_actividad = ?";

    /**
     * Obtiene todas las actividades ordenadas por hora de inicio.
     */
    private static final String SQL_FIND_ALL =
            "SELECT id_actividad, nombre, descripcion, hora_inicio, hora_fin, aforo_maximo " +
            "FROM ACTIVIDAD " +
            "ORDER BY hora_inicio";

    /**
     * Obtiene las actividades asociadas a un día concreto.
     */
    private static final String SQL_FIND_BY_DIA =
            "SELECT a.id_actividad, a.nombre, a.descripcion, " +
            "a.hora_inicio, a.hora_fin, a.aforo_maximo " +
            "FROM ACTIVIDAD a " +
            "JOIN ACTIVIDAD_DIA ad ON ad.id_actividad = a.id_actividad " +
            "WHERE ad.id_dia = ?";

    /**
     * Inserta una nueva actividad en la base de datos.
     *
     * @param actividad entidad a persistir
     */
    @Override
    public void create(Actividad actividad) {

        try (Connection con = DBConnection.getConnection();
             CallableStatement cs = con.prepareCall(
                     "{ CALL " + SQL_INSERT + " }")) {

            cs.setString(1, actividad.getNombre());
            cs.setString(2, actividad.getDescripcion());
            cs.setString(3, actividad.getHoraInicio().format(TIME_FORMAT));
            cs.setString(4, actividad.getHoraFin().format(TIME_FORMAT));
            cs.setInt(5, actividad.getAforoMaximo());
            cs.registerOutParameter(6, Types.INTEGER);

            cs.execute();

            actividad.setIdActividad(cs.getInt(6));

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Actualiza una actividad existente.
     *
     * @param actividad entidad con datos actualizados
     */
    @Override
    public void update(Actividad actividad) {

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(SQL_UPDATE)) {

            ps.setString(1, actividad.getNombre());
            ps.setString(2, actividad.getDescripcion());
            ps.setString(3, actividad.getHoraInicio().format(TIME_FORMAT));
            ps.setString(4, actividad.getHoraFin().format(TIME_FORMAT));
            ps.setInt(5, actividad.getAforoMaximo());
            ps.setInt(6, actividad.getIdActividad());

            ps.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Elimina una actividad por su ID.
     *
     * @param idActividad identificador de la actividad
     */
    @Override
    public void delete(int idActividad) {

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(SQL_DELETE)) {

            ps.setInt(1, idActividad);
            ps.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Busca una actividad por su ID.
     *
     * @param idActividad identificador
     * @return actividad encontrada o null
     */
    @Override
    public Actividad findById(int idActividad) {

        Actividad actividad = null;

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(SQL_FIND_BY_ID)) {

            ps.setInt(1, idActividad);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                actividad = mapRowToActividad(rs);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return actividad;
    }

    /**
     * Obtiene todas las actividades.
     *
     * @return lista de actividades
     */
    @Override
    public List<Actividad> findAll() {

        List<Actividad> actividades = new ArrayList<>();

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(SQL_FIND_ALL);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                actividades.add(mapRowToActividad(rs));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return actividades;
    }

    /**
     * Obtiene actividades asociadas a un día concreto.
     *
     * @param idDia identificador del día
     * @return lista de actividades
     */
    @Override
    public List<Actividad> findByDia(int idDia) {

        List<Actividad> actividades = new ArrayList<>();

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(SQL_FIND_BY_DIA)) {

            ps.setInt(1, idDia);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                actividades.add(mapRowToActividad(rs));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return actividades;
    }

    /**
     * Mapea una fila del ResultSet a una entidad Actividad.
     *
     * @param rs resultado de la consulta
     * @return entidad Actividad
     * @throws SQLException si ocurre error de acceso
     */
    private Actividad mapRowToActividad(ResultSet rs) throws SQLException {

        Actividad actividad = new Actividad();
        actividad.setIdActividad(rs.getInt("id_actividad"));
        actividad.setNombre(rs.getString("nombre"));
        actividad.setDescripcion(rs.getString("descripcion"));
        actividad.setHoraInicio(
                LocalTime.parse(rs.getString("hora_inicio"), TIME_FORMAT)
        );
        actividad.setHoraFin(
                LocalTime.parse(rs.getString("hora_fin"), TIME_FORMAT)
        );
        actividad.setAforoMaximo(rs.getInt("aforo_maximo"));

        return actividad;
    }
}
