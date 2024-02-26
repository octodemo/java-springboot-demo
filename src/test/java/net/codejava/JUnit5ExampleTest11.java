package net.codejava;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import static org.junit.jupiter.api.Assertions.assertEquals;
import java.util.Calendar;
import java.util.List;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
public class JUnit5ExampleTest11 {

    @Autowired
    // create private instance of SalesDAO
    private SalesDAO salesDAO = new SalesDAO();
    // This field is used to inject an instance of the AppController class.
    @Autowired
    private AppController appController;

    @Test
    void testInsert() {
      Calendar calendar = Calendar.getInstance();
      calendar.set(2021, Calendar.FEBRUARY, 1);
      java.util.Date utilDate = calendar.getTime();
      java.sql.Date sqlDate = new java.sql.Date(utilDate.getTime());

      // Generate a unique serial number based on the current timestamp
      String serialNumber = String.valueOf(System.currentTimeMillis());

      Sale sale = new Sale(serialNumber, "Laptop", 1, 1500.00f, sqlDate);
      salesDAO.save(sale);

      // list all the records
      List<Sale> listSale = salesDAO.list(10, 0);

      // Find the sale with the matching serial number
      Sale insertedSale = listSale.stream()
        .filter(s -> s.getSerialNumber().equals(serialNumber))
        .findFirst()
        .orElse(null);

      System.out.println("\n\n");
      System.out.println("--------------------------------------------------------------------------------");
      System.out.println("Expected value of item: Laptop");
      System.out.println("Actual value of item: " + insertedSale.getItem());
      System.out.println("--------------------------------------------------------------------------------");
      assertNotNull(insertedSale, "Inserted sale not found");
      assertEquals("Laptop", insertedSale.getItem(), "Item name does not match");

      // clean up the database
      salesDAO.delete(serialNumber);
      System.out.println("\n\nTest11-1 Successful!\n\n");
    }

    // test the variable enableSearchFeature in AppController.java
    @Test
    void testEnableSearchFeature() {
      // print a comment about the value of enableSearchFeature
      System.out.println("\n\n");
      System.out.println("--------------------------------------------------------------------------------");
      System.out.println("Expected value of enableSearchFeature: true");
      System.out.println("Actual value of enableSearchFeature: " + appController.getEnableSearchFeature());
      System.out.println("--------------------------------------------------------------------------------");

      // assert that the value of enableSearchFeature is true
      assertEquals(true, appController.getEnableSearchFeature());

      System.out.println("\n\nTest11-2 Successful!\n\n");
    }
  }