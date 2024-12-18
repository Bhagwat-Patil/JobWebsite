package com.jobwebsite.Controller;

import com.jobwebsite.Entity.Admin;
import com.jobwebsite.Entity.PendingPost;
import com.jobwebsite.Entity.SuperAdmin;
import com.jobwebsite.Repository.AdminRepository;
import com.jobwebsite.Service.AdminService;
import com.jobwebsite.Service.SuperAdminService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/superAdmin")
@CrossOrigin("*")
public class SuperAdminController {

    private static final Logger logger = LoggerFactory.getLogger(AdminController.class);

    @Autowired
    private SuperAdminService superAdminService;

    @Autowired
    private AdminRepository adminRepository;

    @Autowired
    private AdminService adminService;

    @PostMapping("/registerSuperAdmin")
    public ResponseEntity<String> registerSuperAdmin(@RequestBody SuperAdmin superAdmin) {
        try {
            // Call the service method to register the super admin
            superAdminService.registerSuperAdmin(superAdmin);
            return ResponseEntity.status(HttpStatus.CREATED).body("Super Admin registered successfully.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error during registration: " + e.getMessage());
        }
    }

    @PostMapping("/loginSuperAdmin")
    public ResponseEntity<Object> loginSuperAdmin(@RequestBody Map<String, String> credentials) {
        try {
            String username = credentials.get("username");
            String password = credentials.get("password");

            if (username == null || password == null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Username and password are required.");
            }

            // Call the service method to login the super admin
            SuperAdmin superAdmin = superAdminService.loginSuperAdmin(username, password);
            return ResponseEntity.ok(superAdmin); // Return super admin details on successful login
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error during login: " + e.getMessage());
        }
    }

    @PutMapping("/updateSuperAdmin/{id}")
    public ResponseEntity<Object> updateSuperAdmin(@PathVariable Long id, @RequestBody SuperAdmin updatedDetails) {
        try {
            SuperAdmin updatedSuperAdmin = superAdminService.updateSuperAdmin(id, updatedDetails);
            return ResponseEntity.ok(updatedSuperAdmin); // Return updated Super Admin
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error during update: " + e.getMessage());
        }
    }

    @DeleteMapping("/deleteSuperAdmin/{id}")
    public ResponseEntity<String> deleteSuperAdmin(@PathVariable Long id) {
        try {
            superAdminService.deleteSuperAdmin(id);
            return ResponseEntity.ok("Super Admin with ID " + id + " deleted successfully.");
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error during deletion: " + e.getMessage());
        }
    }

    @PostMapping("/approveAdmin/{id}")
    public ResponseEntity<String> approveAdmin(@PathVariable Long id) {
        try {
            superAdminService.approveAdmin(id);
            return ResponseEntity.ok("Admin has been approved successfully.");
        } catch (RuntimeException e) {
            logger.error("Error approving admin with ID {}: {}", id, e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            logger.error("Unexpected error while approving admin with ID {}: {}", id, e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("An unexpected error occurred. Please try again later.");
        }
    }


    @PostMapping("/disableAdmin/{adminId}")
    public ResponseEntity<?> disableAdmin(@PathVariable Long adminId) {
        try {
            Admin disabledAdmin = superAdminService.disableAdmin(adminId);
            return ResponseEntity.ok(disabledAdmin);
        } catch (RuntimeException e) {
            logger.error("Error disabling admin with ID {}: {}", adminId, e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            logger.error("Unexpected error while disabling admin with ID {}: {}", adminId, e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("An unexpected error occurred. Please try again later.");
        }
    }

    @GetMapping("/getAllPendingPosts")
    public ResponseEntity<List<PendingPost>> getAllPendingPosts() {
        List<PendingPost> pendingPosts = superAdminService.getAllPendingPosts();
        return ResponseEntity.ok(pendingPosts);
    }

    @PutMapping("/approvePost/{pendingPostId}")
    public ResponseEntity<String> approvePost(@PathVariable Long pendingPostId, @RequestParam boolean isApproved) {
        try {
            logger.info("Approving post with ID: {}", pendingPostId);
            String result = superAdminService.approveOrDisapprovePost(pendingPostId, isApproved);
            return ResponseEntity.ok(result);
        } catch (RuntimeException e) {
            logger.error("Error occurred while approving post with ID {}: {}", pendingPostId, e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Post not found or approval failed.");
        } catch (Exception e) {
            logger.error("Unexpected error: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An unexpected error occurred.");
        }
    }


    @PutMapping("/disapprovePost/{pendingPostId}")
    public ResponseEntity<String> disapprovePost(@PathVariable Long pendingPostId) {
        try {
            String result = superAdminService.approveOrDisapprovePost(pendingPostId, false);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error disapproving post.");
        }
    }

    @GetMapping("/getAllAdmins")
    public ResponseEntity<?> getAllAdmins() {
        try {
            List<Admin> admins = superAdminService.getAllAdmins();
            return ResponseEntity.ok(admins);
        } catch (RuntimeException e) {
            logger.error("Error fetching all admins: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @GetMapping("/getAllAdminsNotApproved")
    public ResponseEntity<?> getAllAdminsNotApproved() {
        try {
            List<Admin> admins = superAdminService.getAllAdminNotApproved();
            return ResponseEntity.ok(admins);
        } catch (RuntimeException e) {
            logger.error("Error fetching unapproved admins: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @GetMapping("/getAllAdminsApproved")
    public ResponseEntity<?> getAllAdminsApproved() {
        try {
            List<Admin> admins = superAdminService.getAllAdminApproved();
            return ResponseEntity.ok(admins);
        } catch (RuntimeException e) {
            logger.error("Error fetching approved admins: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @GetMapping("/getAllAdminsDisabled")
    public ResponseEntity<?> getAllAdminsDisabled() {
        try {
            List<Admin> admins = superAdminService.getAllAdminDisabled();
            return ResponseEntity.ok(admins);
        } catch (RuntimeException e) {
            logger.error("Error fetching disabled admins: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @GetMapping("/getAllAdminsEnabled")
    public ResponseEntity<?> getAllAdminsEnabled() {
        try {
            List<Admin> admins = superAdminService.getAllAdminEnabled();
            return ResponseEntity.ok(admins);
        } catch (RuntimeException e) {
            logger.error("Error fetching enabled admins: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

}
