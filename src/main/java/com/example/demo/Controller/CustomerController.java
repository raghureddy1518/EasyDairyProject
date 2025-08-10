package com.example.demo.Controller;

import com.example.demo.Entity.Admin;
import com.example.demo.Entity.Customer;
import com.example.demo.Service.CustomerService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/customers")
public class CustomerController {

    @Autowired
    private CustomerService customerService;

    // ✅ Show customer list with optional search
    @GetMapping
    public String listCustomers(@RequestParam(required = false) String keyword, 
                                 Model model, 
                                 HttpSession session) {
        // Session check
        Admin admin = (Admin) session.getAttribute("loggedInAdmin");
        if (admin == null) {
            return "redirect:/admin/login";
        }

        List<Customer> customers;

        if (keyword != null && !keyword.isEmpty()) {
            customers = customerService.searchCustomers(keyword);
        } else {
            customers = customerService.getAllCustomers();
        }

        model.addAttribute("customers", customers);
        model.addAttribute("customer", new Customer()); // for the form
        model.addAttribute("keyword", keyword);
        model.addAttribute("formTitle", "Add New Customer");
        return "customers";
    }

    // ✅ Show edit form with existing customer data
    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable String id, 
                                Model model, 
                                @RequestParam(required = false) String keyword, 
                                HttpSession session) {
        // Session check
        Admin admin = (Admin) session.getAttribute("loggedInAdmin");
        if (admin == null) {
            return "redirect:/admin/login";
        }

        Customer customer = customerService.getCustomerById(id);
        List<Customer> customers = customerService.getAllCustomers();

        model.addAttribute("customers", customers);
        model.addAttribute("customer", customer);
        model.addAttribute("formTitle", "Edit Customer");
        model.addAttribute("keyword", keyword);
        return "customers";
    }

    // ✅ Handle save (new or edited customer)
    @PostMapping("/save")
    public String saveCustomer(@ModelAttribute Customer customer, HttpSession session) {
        // Session check
        Admin admin = (Admin) session.getAttribute("loggedInAdmin");
        if (admin == null) {
            return "redirect:/admin/login";
        }

        customerService.saveCustomer(customer);
        return "redirect:/customers";
    }

    // ✅ Delete customer
    @GetMapping("/delete/{id}")
    public String deleteCustomer(@PathVariable String id, HttpSession session) {
        // Session check
        Admin admin = (Admin) session.getAttribute("loggedInAdmin");
        if (admin == null) {
            return "redirect:/admin/login";
        }

        customerService.deleteCustomer(id);
        return "redirect:/customers";
    }
}
