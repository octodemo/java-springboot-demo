package net.codejava;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;

@Service
public class SaleService {

    private final SalesRecordRepository salesRecordRepository;

    @Autowired
    public SaleService(SalesRecordRepository salesRecordRepository) {
        this.salesRecordRepository = salesRecordRepository;
    }

    public Page<Sale> getAllSales(Pageable pageable) {
        if (pageable == null) {
            // Handle the case where pageable is null, for example by throwing an exception
            throw new IllegalArgumentException("Pageable must not be null");
        }
        System.out.println("Pageable: " + pageable); // print out the pageable parameter
        Page<Sale> result = salesRecordRepository.findAll(pageable);
        System.out.println("Result: " + result); // print out the result
        return result;
    }
}