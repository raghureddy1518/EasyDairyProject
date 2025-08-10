package com.example.demo.Controller;

import com.example.demo.Entity.Admin;
import com.example.demo.Entity.Customer;
import com.example.demo.Entity.MilkEntry;
import com.example.demo.Repository.CustomerRepository;
import com.example.demo.Service.MilkEntryService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/milk-entries")
public class MilkEntryController {

    @Autowired
    private MilkEntryService milkEntryService;

    @Autowired
    private CustomerRepository customerRepo;

    // ✅ View all milk entries or filter by date
    @GetMapping
    public String viewEntries(
            @RequestParam(value = "date", required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            Model model,
            HttpSession session) {

        // Session check
        Admin admin = (Admin) session.getAttribute("loggedInAdmin");
        if (admin == null) {
            return "redirect:/admin/login";
        }

        if (date == null) {
            date = LocalDate.now();
        }

        List<MilkEntry> entries = milkEntryService.getEntriesByDate(date);

        // Create map customerId -> MilkEntry for template lookup
        Map<String, MilkEntry> entryMap = entries.stream()
                .collect(Collectors.toMap(MilkEntry::getCustomerId, e -> e));

        model.addAttribute("entries", entries);
        model.addAttribute("entryMap", entryMap);  // Add the map here
        model.addAttribute("customers", customerRepo.findAll());
        model.addAttribute("selectedDate", date);

        return "milk-entries";
    }

    // ✅ Add a new milk entry
    @PostMapping("/add")
    public String addEntry(
            @RequestParam String customerId,
            @RequestParam(required = false) Double liters,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            Model model,
            HttpSession session) {

        // Session check
        Admin admin = (Admin) session.getAttribute("loggedInAdmin");
        if (admin == null) {
            return "redirect:/admin/login";
        }

        if (liters == null || liters <= 0) {
            model.addAttribute("error", "Please enter a valid quantity.");
            model.addAttribute("entries", milkEntryService.getEntriesByDate(date));
            model.addAttribute("customers", customerRepo.findAll());
            model.addAttribute("selectedDate", date);
            return "milk-entries";
        }

        Customer customer = customerRepo.findById(customerId).orElse(null);

        if (customer != null) {
            List<MilkEntry> existingEntries = milkEntryService.getEntriesByDateAndCustomer(date, customerId);

            if (existingEntries.isEmpty()) {
                MilkEntry entry = new MilkEntry();
                entry.setCustomerId(customer.getId());
                entry.setCustomerName(customer.getName());
                entry.setLiters(liters);
                entry.setDate(date);
                milkEntryService.save(entry);
            } else {
                MilkEntry existing = existingEntries.get(0);
                existing.setLiters(existing.getLiters() + liters);
                milkEntryService.save(existing);
            }
        }

        return "redirect:/milk-entries?date=" + date;
    }

    // ✅ Load entry into edit form
    @GetMapping("/edit/{id}")
    public String editEntry(@PathVariable String id, Model model, HttpSession session) {
        // Session check
        Admin admin = (Admin) session.getAttribute("loggedInAdmin");
        if (admin == null) {
            return "redirect:/admin/login";
        }

        MilkEntry entry = milkEntryService.getById(id);
        if (entry == null) {
            return "redirect:/milk-entries";
        }

        // Format LocalDate as yyyy-MM-dd string
        String formattedDate = "";
        LocalDate date = entry.getDate();  // Assuming it's LocalDate
        if (date != null) {
            formattedDate = date.format(DateTimeFormatter.ISO_LOCAL_DATE);
        }

        model.addAttribute("entry", entry);
        model.addAttribute("formattedDate", formattedDate);
        model.addAttribute("customers", customerRepo.findAll());
        return "edit-milk-entry";
    }

    // ✅ Update an existing entry
    @PostMapping("/update")
    public String updateEntry(@ModelAttribute MilkEntry entry, HttpSession session) {
        Admin admin = (Admin) session.getAttribute("loggedInAdmin");
        if (admin == null) {
            return "redirect:/admin/login";
        }

        if (entry.getLiters() <= 0) {
            return "redirect:/milk-entries/edit/" + entry.getId() + "?error=Invalid+quantity";
        }

        // Fetch customer to set customerName before saving
        Customer customer = customerRepo.findById(entry.getCustomerId()).orElse(null);
        if (customer != null) {
            entry.setCustomerName(customer.getName());
        } else {
            // handle error if customer not found
            return "redirect:/milk-entries/edit/" + entry.getId() + "?error=Customer+not+found";
        }

        milkEntryService.save(entry);
        return "redirect:/milk-entries?date=" + entry.getDate();
    }


    // ✅ Delete a milk entry
    @GetMapping("/delete/{id}")
    public String deleteEntry(@PathVariable String id, HttpSession session) {
        // Session check
        Admin admin = (Admin) session.getAttribute("loggedInAdmin");
        if (admin == null) {
            return "redirect:/admin/login";
        }

        MilkEntry entry = milkEntryService.getById(id);
        LocalDate date = (entry != null) ? entry.getDate() : LocalDate.now();
        milkEntryService.delete(id);
        return "redirect:/milk-entries?date=" + date;
    }

    // ✅ Day-wise report
    @GetMapping("/daywise")
    public String viewDaywiseReport(
            @RequestParam(value = "month", required = false) Integer month,
            @RequestParam(value = "year", required = false) Integer year,
            Model model,
            HttpSession session) {

        // Session check
        Admin admin = (Admin) session.getAttribute("loggedInAdmin");
        if (admin == null) {
            return "redirect:/admin/login";
        }

        LocalDate today = LocalDate.now();

        if (month == null) month = today.getMonthValue();
        if (year == null) year = today.getYear();

        List<Customer> customers = customerRepo.findAll();

        int daysInMonth = LocalDate.of(year, month, 1).lengthOfMonth();
        double[][] litersMatrix = new double[daysInMonth][customers.size()];

        List<MilkEntry> entries = milkEntryService.getEntriesByMonthYear(month, year);
        for (MilkEntry entry : entries) {
            int dayIndex = entry.getDate().getDayOfMonth() - 1;
            int customerIndex = -1;
            for (int i = 0; i < customers.size(); i++) {
                if (customers.get(i).getId().equals(entry.getCustomerId())) {
                    customerIndex = i;
                    break;
                }
            }
            if (customerIndex >= 0) {
                litersMatrix[dayIndex][customerIndex] = entry.getLiters();
            }
        }

        List<Double> totals = new ArrayList<>();
        for (int custIndex = 0; custIndex < customers.size(); custIndex++) {
            double sum = 0;
            for (int day = 0; day < daysInMonth; day++) {
                sum += litersMatrix[day][custIndex];
            }
            totals.add(sum);
        }

        model.addAttribute("customers", customers);
        model.addAttribute("litersMatrix", litersMatrix);
        model.addAttribute("month", month);
        model.addAttribute("year", year);
        model.addAttribute("daysInMonth", daysInMonth);
        model.addAttribute("totals", totals);

        return "daywise-report";
    }
}
