package com.jobwebsite.Controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jobwebsite.Entity.Admin;
import com.jobwebsite.Entity.Form;
import com.jobwebsite.Entity.Internship;
import com.jobwebsite.Entity.Job;
import com.jobwebsite.Exception.AdminNotApprovedException;
import com.jobwebsite.Exception.AdminNotEnabledException;
import com.jobwebsite.Exception.AdminNotFoundException;
import com.jobwebsite.Exception.UserNotFoundException;
import com.jobwebsite.Repository.AdminRepository;
import com.jobwebsite.Service.AdminService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
@CrossOrigin("*")
public class AdminController {

    private static final Logger logger = LoggerFactory.getLogger(AdminController.class);

    @Autowired
    private AdminService adminService;

    @Autowired
    private AdminRepository adminRepository;


    @PostMapping("/registerAdmin")
    public String registerAdmin(@RequestBody Admin admin) {
        adminService.registerAdmin(admin);
        return "Admin registration request sent to super admin.";
    }

    @PostMapping("/loginAdmin")
    public ResponseEntity<Object> loginAdmin(@RequestBody Map<String, String> credentials) {
        try {
            String username = credentials.get("username");
            String password = credentials.get("password");

            if (username == null || password == null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Username and password are required.");
            }

            // Call the service method
            Admin admin = adminService.loginAdmin(username, password);
            return ResponseEntity.ok(admin); // Return admin details on successful login
        } catch (RuntimeException e) {
            if (e.getMessage().contains("Invalid credentials")) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
            } else if (e.getMessage().contains("not approved") || e.getMessage().contains("disabled")) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
            } else {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error during login: " + e.getMessage());
            }
        }
    }


    @PutMapping("/updateAdmin/{adminId}")
    public ResponseEntity<?> updateAdmin(
            @PathVariable Long adminId,
            @RequestPart("adminData") String adminData,
            @RequestPart(value = "profilePicture", required = false) MultipartFile profilePicture) {

        try {
            // Convert JSON string to Admin object
            ObjectMapper objectMapper = new ObjectMapper();
            Admin adminDetails = objectMapper.readValue(adminData, Admin.class);

            // Update the admin
            Admin updatedAdmin = adminService.updateAdmin(adminId, adminDetails, profilePicture);

            // Return the updated admin
            return ResponseEntity.ok(updatedAdmin);

        } catch (AdminNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (JsonProcessingException e) {
            logger.error("Failed to parse admin data: {}", e.getMessage(), e);
            return ResponseEntity.badRequest().body("Invalid admin data format");
        } catch (Exception e) {
            logger.error("An unexpected error occurred: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("An unexpected error occurred: " + e.getMessage());
        }
    }

    @DeleteMapping("/deleteAdmin/{adminId}")
    public ResponseEntity<?> deleteAdmin(@PathVariable Long adminId) {
        try {
            if (adminId == null) {
                throw new UserNotFoundException("Admin id cannot be null");
            }
            adminService.deleteAdmin(adminId);
            return ResponseEntity.status(HttpStatus.OK).body("successfully deleted");
        } catch (UserNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Admin not found with id: " + adminId);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An unexpected error occurred: " + e.getMessage());
        }
    }

    @GetMapping("/getAdminById/{adminId}")
    public ResponseEntity<Admin> getAdminById(@PathVariable Long adminId) {
        try {
            Admin admin = adminService.getAdminById(adminId);
            return new ResponseEntity<>(admin, HttpStatus.OK);
        } catch (AdminNotFoundException e) {
            logger.error("Admin not found: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        } catch (Exception e) {
            logger.error("Error occurred while fetching admin: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @GetMapping("/getAllAdmins")
    public ResponseEntity<List<Admin>> getAllAdmins() {
        try {
            logger.info("Fetching all admins...");
            List<Admin> admins = adminService.getAllAdmins();
            return new ResponseEntity<>(admins, HttpStatus.OK);
        } catch (Exception e) {
            logger.error("Error while fetching all admins: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }


    @PostMapping("/JobPost/{adminId}")
    public ResponseEntity<String> jobPost(@PathVariable Long adminId, @RequestBody Job job) {
        try {
            logger.info("Received request to post job by adminId: {}", adminId);

            // Send the job post to super admin for approval (not directly saving to DB)
            String result = adminService.jobpost(job, adminId);

            // Respond with a message indicating the post is sent for approval
            return ResponseEntity.status(HttpStatus.CREATED).body(result);
        } catch (AdminNotFoundException e) {
            // Admin not found
            logger.error("Admin not found with ID: {}", adminId, e);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Admin not found: " + e.getMessage());
        } catch (AdminNotApprovedException e) {
            // Admin not approved
            logger.error("Admin not approved with ID: {}", adminId, e);
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Admin not approved. Please wait for approval.");
        } catch (AdminNotEnabledException e) {
            // Admin not enabled
            logger.error("Admin not enabled with ID: {}", adminId, e);
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Admin is not enabled. Please contact the Super Admin.");
        } catch (Exception e) {
            // Generic error
            logger.error("Error posting job: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error posting job. Please try again later.");
        }
    }


    @PostMapping("/postInternship/{adminId}")
    public ResponseEntity<String> postInternship(@PathVariable Long adminId, @RequestBody Internship internship) {
        try {
            logger.info("Received request to post internship by adminId: {}", adminId);

            // Send the internship post to super admin for approval (not directly saving to DB)
            String result = adminService.postInternship(internship, adminId);

            // Respond with a message indicating the post is sent for approval
            return ResponseEntity.status(HttpStatus.CREATED).body(result);
        } catch (AdminNotFoundException e) {
            // Admin not found
            logger.error("Admin not found with ID: {}", adminId, e);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Admin not found: " + e.getMessage());
        } catch (AdminNotApprovedException e) {
            // Admin not approved
            logger.error("Admin not approved with ID: {}", adminId, e);
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Admin not approved. Please wait for approval.");
        } catch (AdminNotEnabledException e) {
            // Admin not enabled
            logger.error("Admin not enabled with ID: {}", adminId, e);
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Admin is not enabled. Please contact the Super Admin.");
        } catch (Exception e) {
            // Generic error
            logger.error("Error posting internship: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error posting internship. Please try again later.");
        }
    }

    // to get All User forms
    @GetMapping("/form/getAllForms")
    public ResponseEntity<List<Form>> getAllForms() {

        try {
            logger.info("Received request to get all forms");
            return new ResponseEntity<>(adminService.getAllForms(), HttpStatus.OK);
        } catch (Exception e) {
            logger.error("Failed to retrieve forms: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @GetMapping("/forms/{formId}")
    public ResponseEntity<Form> getFormByFormId(@PathVariable Long formId) {
        try {
            logger.info("Received request to get form by ID: {}", formId);
            return new ResponseEntity<>(adminService.getFormByFormId(formId), HttpStatus.OK);
        } catch (Exception e) {
            logger.error("Failed to retrieve form: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }


    @GetMapping("/job/getAllJobs")
    public ResponseEntity<List<Job>> getAllJobsUploadedByAdmin() {
        try {
            logger.info("Received request to get all jobs uploaded by Admin ID: {}");
            return new ResponseEntity<>(adminService.getAllJobsUploadedByAdmin(), HttpStatus.OK);
        } catch (Exception e) {
            logger.error("Failed to retrieve jobs uploaded by admin: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }

}

