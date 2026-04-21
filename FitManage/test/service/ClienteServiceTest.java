package service;

import Model.Cliente;
import org.junit.Before;
import org.junit.Test;
import support.FakeClienteDAO;

import static org.junit.Assert.*;

public class ClienteServiceTest {

    private FakeClienteDAO clienteDAO;
    private ClienteService clienteService;

    @Before
    public void setUp() {
        clienteDAO = new FakeClienteDAO();
        clienteService = new ClienteService(clienteDAO);
    }

    @Test
    public void emailDisponible_siNoExiste_devuelveTrue() {
        assertTrue(clienteService.emailDisponible("nuevo@mail.com", null));
    }

    @Test
    public void emailDisponible_siExisteYEsMismoId_devuelveTrue() {
        Cliente c = new Cliente();
        c.setIdCliente(1);
        c.setEmail("a@mail.com");
        clienteDAO.putCliente(c);

        assertTrue(clienteService.emailDisponible("a@mail.com", 1));
    }

    @Test
    public void emailDisponible_siExisteYOtroId_devuelveFalse() {
        Cliente c = new Cliente();
        c.setIdCliente(1);
        c.setEmail("a@mail.com");
        clienteDAO.putCliente(c);

        assertFalse(clienteService.emailDisponible("a@mail.com", 2));
    }
}
