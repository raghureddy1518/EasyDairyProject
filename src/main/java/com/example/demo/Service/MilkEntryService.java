package com.example.demo.Service;

import com.example.demo.Entity.MilkEntry;
import com.example.demo.Repository.MilkEntryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class MilkEntryService {

    @Autowired
    private MilkEntryRepository milkEntryRepo;

    public List<MilkEntry> getAllEntries() {
        return milkEntryRepo.findAll();
    }

    public List<MilkEntry> getEntriesByDate(LocalDate date) {
        return milkEntryRepo.findByDate(date);
    }

    public List<MilkEntry> getEntriesByCustomer(String customerId) {
        return milkEntryRepo.findByCustomerId(customerId);
    }

    public List<MilkEntry> getEntriesByDateAndCustomer(LocalDate date, String customerId) {
        return milkEntryRepo.findByDateAndCustomerId(date, customerId);
    }

    public MilkEntry getById(String id) {
        return milkEntryRepo.findById(id).orElse(null);
    }

    public MilkEntry save(MilkEntry entry) {
        return milkEntryRepo.save(entry);
    }

    public void delete(String id) {
        milkEntryRepo.deleteById(id);
    }
    public List<MilkEntry> getEntriesByMonthYear(int month, int year) {
        LocalDate start = LocalDate.of(year, month, 1);
        LocalDate end = start.withDayOfMonth(start.lengthOfMonth());
        return milkEntryRepo.findByDateBetween(start, end);
    }

}
