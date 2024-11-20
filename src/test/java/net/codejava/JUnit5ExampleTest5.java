package net.codejava;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import java.util.Calendar;
import java.util.List;

@SpringBootTest
class JUnit5ExampleTest5 {

    @Autowired
    private SalesDAO salesDAO;

    @Autowired
    private AppController appController;

    @Test
    void justAnExample() {
        System.out.println("\n\nTest5-1 Successful!\n\n");
    }

    @Test
    void contextLoads() {
    }

    @Test
    void testInsert() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(2021, Calendar.FEBRUARY, 1);
        java.util.Date utilDate = calendar.getTime();
        java.sql.Date sqlDate = new java.sql.Date(utilDate.getTime());

        String serialNumber = String.valueOf(System.currentTimeMillis());

        Sale sale = new Sale(serialNumber, "Laptop", 1, 1500.00f, sqlDate);
        salesDAO.save(sale);

        List<Sale> listSale = salesDAO.list(10, 0);

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

        salesDAO.delete(serialNumber);
        System.out.println("\n\nTest5-2 Successful!\n\n");
    }

    @Test
    void testEnableSearchFeature() {
        System.out.println("\n\n");
        System.out.println("--------------------------------------------------------------------------------");
        System.out.println("Expected value of enableSearchFeature: true");
        System.out.println("Actual value of enableSearchFeature: " + appController.getEnableSearchFeature());
        System.out.println("--------------------------------------------------------------------------------");

        assertEquals(true, appController.getEnableSearchFeature());

        System.out.println("\n\nTest5-3 Successful!\n\n");
    }

    @Test
    void testUpdate() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(2021, Calendar.FEBRUARY, 1);
        java.util.Date utilDate = calendar.getTime();
        java.sql.Date sqlDate = new java.sql.Date(utilDate.getTime());

        String serialNumber = String.valueOf(System.currentTimeMillis());

        Sale sale = new Sale(serialNumber, "Laptop", 1, 1500.00f, sqlDate);
        salesDAO.save(sale);

        sale.setItem("Updated Laptop");
        salesDAO.update(sale);

        Sale updatedSale = salesDAO.get(serialNumber);

        System.out.println("\n\n");
        System.out.println("--------------------------------------------------------------------------------");
        System.out.println("Expected value of item: Updated Laptop");
        System.out.println("Actual value of item: " + updatedSale.getItem());
        System.out.println("--------------------------------------------------------------------------------");
        assertNotNull(updatedSale, "Updated sale not found");
        assertEquals("Updated Laptop", updatedSale.getItem(), "Item name does not match");

        salesDAO.delete(serialNumber);
        System.out.println("\n\nTest5-4 Successful!\n\n");
    }

    @Test
    void testDelete() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(2021, Calendar.FEBRUARY, 1);
        java.util.Date utilDate = calendar.getTime();
        java.sql.Date sqlDate = new java.sql.Date(utilDate.getTime());

        String serialNumber = String.valueOf(System.currentTimeMillis());

        Sale sale = new Sale(serialNumber, "Laptop", 1, 1500.00f, sqlDate);
        salesDAO.save(sale);

        salesDAO.delete(serialNumber);

        Sale deletedSale = salesDAO.get(serialNumber);

        System.out.println("\n\n");
        System.out.println("--------------------------------------------------------------------------------");
        System.out.println("Expected value of deleted sale: null");
        System.out.println("Actual value of deleted sale: " + deletedSale);
        System.out.println("--------------------------------------------------------------------------------");
        assertNotNull(deletedSale, "Deleted sale should be null");

        System.out.println("\n\nTest5-5 Successful!\n\n");
    }

    @Test
    void testClearRecord() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(2021, Calendar.FEBRUARY, 1);
        java.util.Date utilDate = calendar.getTime();
        java.sql.Date sqlDate = new java.sql.Date(utilDate.getTime());

        String serialNumber = String.valueOf(System.currentTimeMillis());

        Sale sale = new Sale(serialNumber, "Laptop", 1, 1500.00f, sqlDate);
        salesDAO.save(sale);

        salesDAO.clearRecord(serialNumber);

        Sale clearedSale = salesDAO.get(serialNumber);

        System.out.println("\n\n");
        System.out.println("--------------------------------------------------------------------------------");
        System.out.println("Expected value of quantity: 0");
        System.out.println("Actual value of quantity: " + clearedSale.getQuantity());
        System.out.println("Expected value of amount: 0.0");
        System.out.println("Actual value of amount: " + clearedSale.getAmount());
        System.out.println("--------------------------------------------------------------------------------");
        assertEquals(0, clearedSale.getQuantity(), "Quantity should be 0");
        assertEquals(0.0f, clearedSale.getAmount(), "Amount should be 0.0");

        salesDAO.delete(serialNumber);
        System.out.println("\n\nTest5-6 Successful!\n\n");
    }
}
