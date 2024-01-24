package net.codejava;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.lang.NonNull;

public interface SalesRecordRepository extends PagingAndSortingRepository<Sale, Long> {
    @Override
    @NonNull
    Page<Sale> findAll(@NonNull Pageable pageable);
}