package net.codejava;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.rememberme.PersistentTokenBasedRememberMeServices;

import javax.servlet.http.Cookie;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class AppControllerTest {

    @Autowired
    private AppController appController;

    @Autowired
    private PersistentTokenBasedRememberMeServices rememberMeServices;

    @Test
    public void testRememberMeFunctionality() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();

        // Simulate login request with 'Remember Me' checked
        request.setParameter("username", "testuser");
        request.setParameter("password", "testpassword");
        request.setParameter("rememberMe", "on");

        String view = appController.loginPost(request, response, null);

        // Assert that the user is redirected to the home page
        assertEquals("redirect:/", view);

        // Assert that the 'Remember Me' cookie is set
        Cookie rememberMeCookie = response.getCookie("rememberMe");
        assertNotNull(rememberMeCookie);
        assertEquals("true", rememberMeCookie.getValue());
        assertTrue(rememberMeCookie.getMaxAge() > 0);

        // Assert that the security context is populated
        assertNotNull(SecurityContextHolder.getContext().getAuthentication());
    }
}