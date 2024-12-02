package net.codejava;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.ui.Model;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpSession;
import java.security.Principal;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

public class AppControllerTest {

    @Mock
    private SalesDAO salesDAO;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private Model model;

    @Mock
    private HttpSession session;

    @Mock
    private RedirectAttributes redirectAttributes;

    @Mock
    private Principal principal;

    @InjectMocks
    private AppController appController;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testViewHomePage() {
        String result = appController.viewHomePage(model, principal, 0, session);
        assertEquals("index", result);
    }

    @Test
    public void testShowNewForm() {
        ModelAndView mav = appController.showNewForm();
        assertEquals("new_form", mav.getViewName());
    }

    @Test
    public void testShowEditForm() {
        Sale sale = new Sale();
        when(salesDAO.get(anyString())).thenReturn(sale);

        ModelAndView mav = appController.showEditForm("123");
        assertEquals("edit_form", mav.getViewName());
    }

    @Test
    public void testSearch() {
        String result = appController.search("query", model, session);
        assertEquals("search", result);
    }

    @Test
    public void testSave() {
        Sale sale = new Sale();
        sale.setDate(new Date());

        String result = appController.save(sale, model, session, redirectAttributes);
        assertEquals("redirect:/", result);
    }

    @Test
    public void testLoginGet() {
        String result = appController.loginGet(model);
        assertEquals("login", result);
    }

    @Test
    public void testLoginPost() {
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getParameter("username")).thenReturn("user");
        when(request.getParameter("password")).thenReturn("pass");

        String result = appController.loginPost(request, model);
        assertEquals("redirect:/", result);
    }

    @Test
    public void testUpdate() {
        Sale sale = new Sale();
        sale.setDate(new Date());

        String result = appController.update(sale, session, redirectAttributes);
        assertEquals("redirect:/", result);
    }

    @Test
    public void testDelete() {
        Sale sale = new Sale();
        when(salesDAO.get(anyString())).thenReturn(sale);

        String result = appController.delete("123", session, redirectAttributes);
        assertEquals("redirect:/", result);
    }

    @Test
    public void testClearRecord() {
        Sale sale = new Sale();
        when(salesDAO.get(anyString())).thenReturn(sale);

        String result = appController.clearRecord("123", session, redirectAttributes);
        assertEquals("redirect:/", result);
    }

    @Test
    public void testExportToCSV() throws IOException {
        HttpServletResponse response = mock(HttpServletResponse.class);
        appController.exportToCSV(response);
        verify(response).setContentType("text/csv");
    }

    @Test
    public void testUploadFile() {
        MultipartFile file = mock(MultipartFile.class);
        when(file.getInputStream()).thenReturn(new ByteArrayInputStream("Serial Number,Item Name,Amount,Quantity,Date\n123,Item,100.0,1,2021-01-01".getBytes()));

        String result = appController.uploadFile(file, redirectAttributes);
        assertEquals("redirect:/", result);
    }
}
