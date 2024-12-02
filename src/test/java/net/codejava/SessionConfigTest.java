package net.codejava;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;

import static org.junit.jupiter.api.Assertions.assertNotNull;

public class SessionConfigTest {

    @InjectMocks
    private SessionConfig sessionConfig;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testSessionConfigNotNull() {
        assertNotNull(sessionConfig);
    }

    @Test
    public void testEnableRedisHttpSessionAnnotation() {
        EnableRedisHttpSession annotation = sessionConfig.getClass().getAnnotation(EnableRedisHttpSession.class);
        assertNotNull(annotation);
    }
}
