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

    @PutMapping("/adminApproval/{adminId}")
    public ResponseEntity<String> adminApproval(@PathVariable Long adminId) {
        try {
            // Call service method to approve admin
            String response = superAdminService.approveAdmin(adminId);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }


}
