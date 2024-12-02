package net.codejava;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestBuilders;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.mockito.Mockito.*;

public class SecurityConfigTest {

    @Mock
    private UserDetailsService userDetailsService;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private AuthenticationManagerBuilder authenticationManagerBuilder;

    @InjectMocks
    private SecurityConfig securityConfig;

    private MockMvc mockMvc;

    @BeforeEach
    public void setUp() throws Exception {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders
                .standaloneSetup(new Object())
                .apply(SecurityMockMvcRequestBuilders.springSecurity())
                .build();

        when(authenticationManagerBuilder.userDetailsService(userDetailsService).passwordEncoder(passwordEncoder)).thenReturn(authenticationManagerBuilder);
        securityConfig.configureGlobal(authenticationManagerBuilder);
    }

    @Test
    @WithMockUser
    public void testAuthenticatedAccess() throws Exception {
        mockMvc.perform(SecurityMockMvcRequestBuilders.formLogin("/login").user("user").password("password"))
                .andExpect(status().isOk());
    }

    @Test
    public void testUnauthenticatedAccess() throws Exception {
        mockMvc.perform(SecurityMockMvcRequestBuilders.get("/"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void testPermitAllAccess() throws Exception {
        mockMvc.perform(SecurityMockMvcRequestBuilders.get("/login"))
                .andExpect(status().isOk());
    }
}
