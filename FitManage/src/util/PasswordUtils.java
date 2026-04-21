/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

package util;

import org.mindrot.jbcrypt.BCrypt;

/**
 * Utilidad para el cifrado y verificación de contraseñas con BCrypt.
 * @author Alejandro  
 * @version 1.0
 */
public class PasswordUtils {

 private PasswordUtils() {
    }

    /**
     * Genera el hash BCrypt de una contraseña en texto plano.
     */
    public static String hashPassword(String plainPassword) {
        return BCrypt.hashpw(plainPassword, BCrypt.gensalt(12));
        
    }

    /**
     * Verifica una contraseña en texto plano contra un hash BCrypt.
     */
    public static boolean checkPassword(String plainPassword, String hashedPassword) {
        return BCrypt.checkpw(plainPassword, hashedPassword);
    }
}
