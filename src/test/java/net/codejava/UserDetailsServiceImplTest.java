package net.codejava;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class UserDetailsServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserDetailsServiceImpl userDetailsService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testCreateUser() {
        User user = new User();
        user.setUsername("testuser");
        user.setPassword("password");

        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");

        userDetailsService.createUser(user);

        verify(passwordEncoder, times(1)).encode("password");
        verify(userRepository, times(1)).save(user);
        assertEquals("encodedPassword", user.getPassword());
    }

    @Test
    public void testLoadUserByUsername_UserExists() {
        User user = new User();
        user.setUsername("testuser");
        user.setPassword("password");

        when(userRepository.findByUsername("testuser")).thenReturn(user);

        org.springframework.security.core.userdetails.UserDetails userDetails = userDetailsService.loadUserByUsername("testuser");

        assertNotNull(userDetails);
        assertEquals("testuser", userDetails.getUsername());
        assertEquals("password", userDetails.getPassword());
    }

    @Test
    public void testLoadUserByUsername_UserNotFound() {
        when(userRepository.findByUsername("testuser")).thenReturn(null);

        assertThrows(UsernameNotFoundException.class, () -> {
            userDetailsService.loadUserByUsername("testuser");
        });
    }

    @Test
    public void testLoadUserByUsername_SQLGrammarException() {
        when(userRepository.findByUsername("testuser")).thenThrow(new RuntimeException("SQL error"));

        assertThrows(RuntimeException.class, () -> {
            userDetailsService.loadUserByUsername("testuser");
        });
    }
}
