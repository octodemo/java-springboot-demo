package net.codejava;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class UserRepositoryTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserDetailsServiceImpl userDetailsService;

    private User user;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        user = new User();
        user.setId(1L);
        user.setUsername("testuser");
        user.setPassword("password");
    }

    @Test
    public void testFindByUsername() {
        when(userRepository.findByUsername("testuser")).thenReturn(user);

        User foundUser = userRepository.findByUsername("testuser");

        assertThat(foundUser).isNotNull();
        assertThat(foundUser.getUsername()).isEqualTo("testuser");
    }

    @Test
    public void testFindById() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        Optional<User> foundUser = userRepository.findById(1L);

        assertThat(foundUser).isPresent();
        assertThat(foundUser.get().getId()).isEqualTo(1L);
    }

    @Test
    public void testSaveUser() {
        when(userRepository.save(user)).thenReturn(user);

        User savedUser = userRepository.save(user);

        assertThat(savedUser).isNotNull();
        assertThat(savedUser.getId()).isEqualTo(1L);
    }

    @Test
    public void testDeleteUser() {
        userRepository.delete(user);

        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        Optional<User> deletedUser = userRepository.findById(1L);

        assertThat(deletedUser).isNotPresent();
    }
}
