package com.example.demo.Service;

import com.example.demo.Entity.Customer;
import com.example.demo.Repository.CustomerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CustomerService {

    @Autowired
    private CustomerRepository customerRepo;

    // Get all customers
    public List<Customer> getAllCustomers() {
        return customerRepo.findAll();
    }

    // Save or update a customer
    public Customer saveCustomer(Customer customer) {
        // If the ID is empty or blank, set it to null so MongoDB will auto-generate it
        if (customer.getId() != null && customer.getId().isBlank()) {
            customer.setId(null);
        }
        return customerRepo.save(customer);
    }

    // Delete a customer by ID
    public void deleteCustomer(String id) {
        customerRepo.deleteById(id);
    }

    // Get a customer by ID
    public Customer getCustomerById(String id) {
        return customerRepo.findById(id).orElse(null);
    }

    // Search for customers using name, phone, or address (case-insensitive)
    public List<Customer> searchCustomers(String keyword) {
        return customerRepo.findByNameContainingIgnoreCaseOrPhoneContainingIgnoreCaseOrAddressContainingIgnoreCase(
                keyword, keyword, keyword);
    }
}
