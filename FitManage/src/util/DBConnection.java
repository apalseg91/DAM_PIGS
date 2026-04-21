/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
  * Clase de utilidad para la obtención de conexiones a la base de datos Oracle.
 * @author Alejandro
 * @version 1.0
 */

public class DBConnection {

    private static final String URL = "jdbc:oracle:thin:@//localhost:1522/XEPDB1";
        // "jdbc:oracle:thin:@//localhost:1521/xe"; //"FITMANAGE@//localhost:1521/xe";
    //TODO:
     	//CAMBIAR CONEXION a comentado
    private static final String USER = "FITMANAGE"; // "system"; FITMANAGE
    private static final String PASSWORD = "admin123"; // "system";

    private DBConnection() {
    }

    /**
     * Obtiene una conexión JDBC a la base de datos.
     */
    /*public static Connection getConnection() throws SQLException {
        System.out.println("USER: " + USER);
System.out.println("PASSWORD: " + PASSWORD);
DriverManager.getConnection(URL, USER, PASSWORD).setAutoCommit(true);
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }*/
    public static Connection getConnection() throws SQLException {
    System.out.println("USER: " + USER);
    System.out.println("PASSWORD: " + PASSWORD);


    Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);


    System.out.println("URL REAL: " + conn.getMetaData().getURL());
    System.out.println("USER REAL: " + conn.getMetaData().getUserName());


    conn.setAutoCommit(true);

    return conn;
}
}
