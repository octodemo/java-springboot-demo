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
    private SalesDAO salesDAO = new SalesDAO();;

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
      salesDAO.delete(listSale.get(listSale.size()-1).getId());
      System.out.println("\n\nTest11-1 Successful!\n\n");
    }

    // write a test to update the record
    // @Test
    // void testUpdate() {
    //   Sale sale = new Sale("test item", 1, 1.0f);
    //   salesDAO.save(sale);
    //   List<Sale> listSale = salesDAO.list();
    //   assertEquals("test item", listSale.get(0).getItem());
    //   sale = listSale.get(0);
    //   sale.setItem("test item 2");
    //   salesDAO.update(sale);
    //   listSale = salesDAO.list();
    //   assertEquals("test item 2", listSale.get(0).getItem());
    // }

    // // write a test to list all the records
    // @Test
    // void testList() {
    //   Sale sale = new Sale("test item", 1, 1.0f);
    //   salesDAO.save(sale);
    //   List<Sale> listSale = salesDAO.list();
    //   assertEquals("test item", listSale.get(0).getItem());
    //   sale = listSale.get(0);
    //   sale.setItem("test item 2");
    //   salesDAO.update(sale);
    //   listSale = salesDAO.list();
    //   assertEquals("test item 2", listSale.get(0).getItem());
    // }
}