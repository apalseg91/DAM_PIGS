/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package service;

import Model.Usuario;

/**
 *Servicio de autenticación del sistema.
 * @author Alejandro  
 * @version 1.0

 */
public interface AuthService {



    /**
     * Autentica un usuario a partir de email y contraseña en texto plano.
     *
     * @param email email introducido por el usuario
     * @param password contraseña en texto plano
     * @return Usuario autenticado o null si las credenciales no son válidas
     */
    Usuario login(String email, String password);
}
