package net.codejava;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

public class UserTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserDetailsServiceImpl userDetailsService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testGetId() {
        User user = new User();
        user.setId(1L);
        assertEquals(1L, user.getId());
    }

    @Test
    public void testSetId() {
        User user = new User();
        user.setId(1L);
        assertEquals(1L, user.getId());
    }

    @Test
    public void testGetUsername() {
        User user = new User();
        user.setUsername("testuser");
        assertEquals("testuser", user.getUsername());
    }

    @Test
    public void testSetUsername() {
        User user = new User();
        user.setUsername("testuser");
        assertEquals("testuser", user.getUsername());
    }

    @Test
    public void testGetPassword() {
        User user = new User();
        user.setPassword("password");
        assertEquals("password", user.getPassword());
    }

    @Test
    public void testSetPassword() {
        User user = new User();
        user.setPassword("password");
        assertEquals("password", user.getPassword());
    }
}
