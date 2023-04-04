package net.codejava;
import org.junit.jupiter.api.Test;
// import org.springframework.boot.test.context.SpringBootTest;
// import java.util.*;
// import static org.junit.Assert.*;
// import org.junit.Test;
// import org.junit.jupiter.api.DisplayName;
// import org.junit.jupiter.api.Nested;
// import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
// import static org.junit.jupiter.api.Assertions.assertTrue;

// @SpringBootTest
class JUnit5ExampleTest10 {

    @Test
    void justAnExample() {
      System.out.println("\n\nTest10-1 Successful!\n\n");
    }

    @Test
  	void contextLoads() {
  	}

    int ACTUAL = 9;
    int EXPECTED = 4;
    @Test
    void shouldNotBeEqual() {
      assertEquals(EXPECTED, ACTUAL-5);
      System.out.println("\n\nTest10-2 Successful!\n\n");
    }
}
