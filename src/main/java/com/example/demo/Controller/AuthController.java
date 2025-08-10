package com.example.demo.Controller;

import com.example.demo.Entity.Admin;
import com.example.demo.Repository.AdminRepository;
import com.example.demo.Service.AdminService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class AuthController {

    @Autowired
    private AdminService adminService;
    @Autowired
    private AdminRepository adminRepository;

    @GetMapping("/admin/login")
    public String showLoginPage() {
        return "login";
    }

    @PostMapping("/admin/login")
    public String processLogin(@RequestParam String username,
                               @RequestParam String password,
                               HttpSession session,
                               Model model) {
        Admin admin = adminRepository.findByUsernameAndPassword(username, password);
        if (admin != null) {
            session.setAttribute("loggedInAdmin", admin);
            return "redirect:/admin/dashboard";
        } else {
            model.addAttribute("error", "Invalid credentials");
            return "login";
        }
    }

    @GetMapping("/admin/dashboard")
    public String dashboard(HttpSession session) {
        Admin admin = (Admin) session.getAttribute("loggedInAdmin");
        if (admin == null) {
            return "redirect:/admin/login";
        }
        return "dashboard";
    }

    @GetMapping("/admin/customers")
    public String customers(HttpSession session) {
        if (session.getAttribute("loggedInAdmin") == null) return "redirect:/admin/login";
        return "customers";
    }

    @GetMapping("/admin/milk-entries")
    public String milkEntries(HttpSession session) {
        if (session.getAttribute("loggedInAdmin") == null) return "redirect:/admin/login";
        return "milk-entries";
    }

    // 🔹 Logout endpoint
    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/admin/login?logout=true";
    }
}
