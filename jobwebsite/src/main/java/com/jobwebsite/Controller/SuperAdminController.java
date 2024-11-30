package com.jobwebsite.Controller;

import com.jobwebsite.Entity.Admin;
import com.jobwebsite.Repository.AdminRepository;
import com.jobwebsite.Service.AdminService;
import com.jobwebsite.Service.SuperAdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/superAdmin")
@CrossOrigin("*")
public class SuperAdminController {

    @Autowired
    private SuperAdminService superAdminService;

    @Autowired
    private AdminRepository adminRepository;

    @Autowired
    private AdminService adminService;

    @PostMapping("/disableAdmin/{adminId}")
    public ResponseEntity<Admin> disableAdmin(@PathVariable Long adminId) {
        Admin disabledAdmin = superAdminService.disableAdmin(adminId);
        return ResponseEntity.ok(disabledAdmin);
    }

    @GetMapping("/getAllAdmins")
    public ResponseEntity<List<Admin>> getAllAdmins() {
        List<Admin> admins = superAdminService.getAllAdmins();
        return ResponseEntity.ok(admins);
    }


    @PostMapping("/approveAdmin/{id}")
    public String approveAdmin(@PathVariable Long id) {
        superAdminService.approveAdmin(id);
        return "Admin has been approved successfully.";
    }


}
