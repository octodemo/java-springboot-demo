package net.codejava;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

public class SaleTest {

    @Mock
    private Date date;

    @InjectMocks
    private Sale sale;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        sale = new Sale("123", "item", 10, 100.0f, date);
    }

    @Test
    public void testGetSerialNumber() {
        assertEquals("123", sale.getSerialNumber());
    }

    @Test
    public void testSetSerialNumber() {
        sale.setSerialNumber("456");
        assertEquals("456", sale.getSerialNumber());
    }

    @Test
    public void testGetItem() {
        assertEquals("item", sale.getItem());
    }

    @Test
    public void testSetItem() {
        sale.setItem("newItem");
        assertEquals("newItem", sale.getItem());
    }

    @Test
    public void testGetQuantity() {
        assertEquals(10, sale.getQuantity());
    }

    @Test
    public void testSetQuantity() {
        sale.setQuantity(20);
        assertEquals(20, sale.getQuantity());
    }

    @Test
    public void testGetAmount() {
        assertEquals(100.0f, sale.getAmount());
    }

    @Test
    public void testSetAmount() {
        sale.setAmount(200.0f);
        assertEquals(200.0f, sale.getAmount());
    }

    @Test
    public void testGetDate() {
        assertEquals(date, sale.getDate());
    }

    @Test
    public void testSetDate() {
        Date newDate = new Date();
        sale.setDate(newDate);
        assertEquals(newDate, sale.getDate());
    }

    @Test
    public void testIsEditing() {
        assertFalse(sale.isEditing());
    }

    @Test
    public void testSetEditing() {
        sale.setEditing(true);
        assertTrue(sale.isEditing());
    }

    @Test
    public void testToString() {
        String expected = "Sale [serial_number=123, item=item, quantity=10, amount=100.0, date=" + date + "]";
        assertEquals(expected, sale.toString());
    }
}
