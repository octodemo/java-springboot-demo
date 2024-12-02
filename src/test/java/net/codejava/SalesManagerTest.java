package net.codejava;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
public class SalesManagerTest {

    @Test
    void contextLoads() {
        SalesManager salesManager = new SalesManager();
        assertNotNull(salesManager);
    }
}
