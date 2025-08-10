package com.example.demo.Repository;

import com.example.demo.Entity.MilkEntry;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.time.LocalDate;
import java.util.List;

public interface MilkEntryRepository extends MongoRepository<MilkEntry, String> {
    List<MilkEntry> findByDate(LocalDate date);
    List<MilkEntry> findByCustomerId(String customerId);
    List<MilkEntry> findByDateAndCustomerId(LocalDate date, String customerId);
    List<MilkEntry> findByDateBetween(LocalDate start, LocalDate end);

}
