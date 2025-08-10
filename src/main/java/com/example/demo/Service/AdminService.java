package com.example.demo.Service;

import com.example.demo.Entity.Admin;
import com.example.demo.Repository.AdminRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AdminService {
    @Autowired
    private AdminRepository adminRepository;

    public Admin authenticate(String username, String password) {
        return adminRepository.findByUsernameAndPassword(username, password);
    }
}
