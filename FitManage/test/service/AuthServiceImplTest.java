package service;

import Model.Usuario;
import org.junit.Before;
import org.junit.Test;
import support.FakeUsuarioDAO;
import support.AssertEx;
import util.PasswordUtils;

import static org.junit.Assert.*;

public class AuthServiceImplTest {

    private FakeUsuarioDAO usuarioDAO;
    private AuthServiceImpl authService;

    @Before
    public void setUp() {
        usuarioDAO = new FakeUsuarioDAO();
        authService = new AuthServiceImpl(usuarioDAO);
    }

    @Test
    public void login_conCredencialesVacias_devuelveNull() {
        assertNull(authService.login(null, "x"));
        assertNull(authService.login("a@mail.com", null));
        assertNull(authService.login("   ", "x"));
        assertNull(authService.login("a@mail.com", "   "));
    }

    @Test
    public void login_usuarioNoExiste_devuelveNull() {
        assertNull(authService.login("noexiste@mail.com", "pass"));
    }

    @Test
    public void login_passwordCorrecta_devuelveUsuario() {
        Usuario u = new Usuario();
        u.setEmail("u@mail.com");
        u.setContrasenaHash(PasswordUtils.hashPassword("pass"));
        usuarioDAO.putUsuario(u);

        Usuario logged = authService.login("u@mail.com", "pass");
        assertNotNull(logged);
        assertEquals("u@mail.com", logged.getEmail());
    }

    @Test
    public void login_passwordIncorrecta_devuelveNull() {
        Usuario u = new Usuario();
        u.setEmail("u@mail.com");
        u.setContrasenaHash(PasswordUtils.hashPassword("pass"));
        usuarioDAO.putUsuario(u);

        assertNull(authService.login("u@mail.com", "bad"));
    }
}
