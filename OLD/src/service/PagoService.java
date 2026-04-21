/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package service;

import DAO.PagoDAO;
import Model.Cliente;
import Model.Pago;
import java.math.BigDecimal;
import java.util.List;

/**
 * Servicio encargado de gestionar la lógica de negocio relacionada con los pagos.
 * <p>
 * Actúa como intermediario entre la capa de presentación y el DAO de pagos,
 * encapsulando el acceso a datos y permitiendo añadir validaciones futuras
 * sin afectar a los controladores.
 * </p>
 *
 * <h2>Responsabilidades:</h2>
 * <ul>
 *     <li>Registrar pagos de clientes</li>
 *     <li>Consultar el historial de pagos de un cliente</li>
 * </ul>
 *
 * @author Alejandro  
 * @version 1.0

 */
public class PagoService {

    /** DAO encargado de la persistencia de pagos */
    private final PagoDAO pagoDAO;

    /**
     * Constructor del servicio de pagos.
     *
     * @param pagoDAO DAO responsable del acceso a datos de pagos
     */
    public PagoService(PagoDAO pagoDAO) {
        this.pagoDAO = pagoDAO;
    }

    /**
     * Registra un nuevo pago en el sistema.
     *
     * @param pago objeto {@link Pago} a persistir
     */
    public void registrarPago(Pago pago) {
        pagoDAO.create(pago);
    }

    /**
     * Obtiene el historial de pagos de un cliente.
     *
     * @param idCliente identificador del cliente
     * @return lista de pagos asociados al cliente
     */
    public List<Pago> obtenerPagosCliente(int idCliente) {
        return pagoDAO.findByCliente(idCliente);
    }
    /**
 * Calcula el importe de un pago en función del concepto seleccionado.
 * 
 * Centraliza la lógica de negocio para evitar duplicidad
 * y mantener coherencia en toda la aplicación.
 * 
 * @param concepto tipo de pago seleccionado
 * @param cliente cliente asociado (necesario para cuota mensual)
 * @return importe calculado
 */
public BigDecimal calcularImporte(String concepto, Cliente cliente) {

    switch (concepto.trim()) {

        case "Cuota mensual":
            return cliente.getCuotaMensual();

        case "Bebida":
            return BigDecimal.valueOf(3);

        case "Alquiler de toalla":
            return BigDecimal.valueOf(2);

        default:
            return BigDecimal.ZERO;
    }
}
}
