package net.codejava;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import static org.junit.jupiter.api.Assertions.assertEquals;
import java.util.List;

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
      Sale sale = new Sale("test item 123", 1, 1);
      salesDAO.save(sale);
      // list all the records
      List<Sale> listSale = salesDAO.list();
      // print a comment about the latest item on the list
      System.out.println("Expected item on the list: test item 123");
      System.out.println("Actual item on the list: " + listSale.get(listSale.size()-1).getItem());
      // assert that the latest item on the list is "test item 123"
      assertEquals("test item 123", listSale.get(listSale.size()-1).getItem());
      // clean up the database
      salesDAO.delete(listSale.get(listSale.size()-1).getId().intValue());
      System.out.println("\n\nTest11-1 Successful!\n\n");
    }

    // test the variable enableSearchFeature in AppController.java
    @Test
    void testEnableSearchFeature() {
      // print a comment about the value of enableSearchFeature
      System.out.println("Expected value of enableSearchFeature: true");
      System.out.println("Actual value of enableSearchFeature: " + appController.getEnableSearchFeature());

      // assert that the value of enableSearchFeature is true
      assertEquals(true, appController.getEnableSearchFeature());

      System.out.println("\n\nTest11-2 Successful!\n\n");
    }
  }