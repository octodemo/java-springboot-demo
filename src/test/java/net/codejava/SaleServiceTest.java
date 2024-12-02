package net.codejava;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

public class SaleServiceTest {

    @Mock
    private SalesRecordRepository salesRecordRepository;

    @InjectMocks
    private SaleService saleService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testGetAllSales() {
        Pageable pageable = PageRequest.of(0, 10);
        Sale sale = new Sale();
        Page<Sale> salePage = new PageImpl<>(Collections.singletonList(sale));

        when(salesRecordRepository.findAll(pageable)).thenReturn(salePage);

        Page<Sale> result = saleService.getAllSales(pageable);

        assertEquals(1, result.getTotalElements());
        verify(salesRecordRepository, times(1)).findAll(pageable);
    }
}
