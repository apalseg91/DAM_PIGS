package integration;

import org.junit.Test;
import util.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import static org.junit.Assert.*;

public class DBConnectionITTest {

    @Test
    public void canConnectToOracle() throws Exception {
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement("SELECT 1 FROM dual");
             ResultSet rs = ps.executeQuery()) {
            assertTrue(rs.next());
            assertEquals(1, rs.getInt(1));
        }
    }
}
