package com.jobwebsite.Controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jobwebsite.Entity.*;
import com.jobwebsite.Exception.*;
import com.jobwebsite.Repository.AdminRepository;
import com.jobwebsite.Service.AdminService;
import com.jobwebsite.Service.InternshipService;
import com.jobwebsite.Service.MockInterviewService;
import com.jobwebsite.Service.PlacementService;
import jakarta.validation.Valid;
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

    @Autowired
    private InternshipService internshipService;

    @Autowired
    private PlacementService placementService;

    @Autowired
    private MockInterviewService mockInterviewService;

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

    // ------------------------ JobPost Endpoints ------------------------

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

    @GetMapping("/job/getAllJobs")
    public ResponseEntity<List<Job>> getAllJobsUploadedByAdmin() {
        try {
            logger.info("Received request to get all jobs uploaded by Admin ID : {}");
            return new ResponseEntity<>(adminService.getAllJobsUploadedByAdmin(), HttpStatus.OK);
        } catch (Exception e) {
            logger.error("Failed to retrieve jobs uploaded by admin: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }

    @DeleteMapping("/deleteJobPost/{adminId}/{postId}")
    public ResponseEntity<String> deleteJobPost(@PathVariable Long adminId, @PathVariable Long postId) {
        try {
            logger.info("Request to delete job post with ID: {} by admin ID: {}", postId, adminId);
            String result = adminService.deleteJobPost(adminId, postId);
            return ResponseEntity.ok(result);
        } catch (AdminNotFoundException e) {
            logger.error("Admin not found: {}", adminId, e);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Admin not found: " + e.getMessage());
        } catch (PostNotFoundException e) {
            logger.error("Job post not found: {}", postId, e);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Job post not found: " + e.getMessage());
        } catch (Exception e) {
            logger.error("Error deleting job post: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error deleting job post.");
        }
    }

    // ------------------------ Internship Endpoints ------------------------

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

    @GetMapping("/internships/getAllInternships")
    public List<Internship> getAllInternships() {
        try {
            logger.info("Fetching all internships.");
            return internshipService.getAllInternships();
        } catch (Exception e) {
            logger.error("Error in fetching internships: {}", e.getMessage(), e);
            throw e;
        }
    }

    @DeleteMapping("/deleteInternshipPost/{adminId}/{postId}")
    public ResponseEntity<String> deleteInternshipPost(@PathVariable Long adminId, @PathVariable Long postId) {
        try {
            logger.info("Request to delete internship post with ID: {} by admin ID: {}", postId, adminId);
            String result = adminService.deleteInternshipPost(adminId, postId);
            return ResponseEntity.ok(result);
        } catch (AdminNotFoundException e) {
            logger.error("Admin not found: {}", adminId, e);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Admin not found: " + e.getMessage());
        } catch (PostNotFoundException e) {
            logger.error("Internship post not found: {}", postId, e);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Internship post not found: " + e.getMessage());
        } catch (Exception e) {
            logger.error("Error deleting internship post: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error deleting internship post.");
        }
    }

    // ------------------------ Forms Endpoints ------------------------

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

    // ------------------------ Placement Endpoints ------------------------

    @PostMapping("/placement/createPlacement")
    public ResponseEntity<?> createPlacement(@Valid @RequestBody Placement placement) {
        try {
            logger.info("Creating a new Placement: {}", placement);
            Placement createdPlacement = placementService.createPlacement(placement);
            logger.info("Placement created successfully with ID: {}", createdPlacement.getId());
            return ResponseEntity.ok(createdPlacement);
        } catch (Exception e) {
            logger.error("Error occurred while creating Placement: {}", e.getMessage(), e);
            return ResponseEntity.status(500).body("Failed to create Placement.");
        }
    }

    @GetMapping("/placement/getPlacementById/{id}")
    public ResponseEntity<?> getPlacementById(@PathVariable Long id) {
        try {
            logger.info("Fetching Placement with ID: {}", id);
            Placement placement = placementService.getPlacementById(id);
            logger.info("Placement fetched successfully: {}", placement);
            return ResponseEntity.ok(placement);
        } catch (Exception e) {
            logger.error("Error occurred while fetching Placement with ID {}: {}", id, e.getMessage(), e);
            return ResponseEntity.status(404).body("Placement not found with ID: " + id);
        }
    }

    @GetMapping("/placement/getAllPlacements")
    public ResponseEntity<?> getAllPlacements() {
        try {
            logger.info("Fetching all Placements...");
            List<Placement> placements = placementService.getAllPlacements();
            logger.info("Successfully fetched {} placements.", placements.size());
            return ResponseEntity.ok(placements);
        } catch (Exception e) {
            logger.error("Error occurred while fetching all Placements: {}", e.getMessage(), e);
            return ResponseEntity.status(500).body("Failed to fetch placements.");
        }
    }

    @PutMapping("/placement/updatePlacement/{id}")
    public ResponseEntity<?> updatePlacement(@PathVariable Long id, @Valid @RequestBody Placement placement) {
        try {
            logger.info("Updating Placement with ID: {}", id);
            Placement updatedPlacement = placementService.updatePlacement(id, placement);
            logger.info("Placement updated successfully: {}", updatedPlacement);
            return ResponseEntity.ok(updatedPlacement);
        } catch (Exception e) {
            logger.error("Error occurred while updating Placement with ID {}: {}", id, e.getMessage(), e);
            return ResponseEntity.status(404).body("Failed to update Placement with ID: " + id);
        }
    }

    @DeleteMapping("/placement/deletePlacement/{id}")
    public ResponseEntity<?> deletePlacement(@PathVariable Long id) {
        try {
            logger.info("Deleting Placement with ID: {}", id);
            placementService.deletePlacement(id);
            logger.info("Placement with ID: {} deleted successfully.", id);
            return ResponseEntity.ok("Placement deleted successfully.");
        } catch (Exception e) {
            logger.error("Error occurred while deleting Placement with ID {}: {}", id, e.getMessage(), e);
            return ResponseEntity.status(404).body("Failed to delete Placement with ID: " + id);
        }
    }

    // ---------------------- Mock Interview Endpoints ---------------------

    @PostMapping("/mockInterview/createMockInterview")
    public ResponseEntity<?> createMockInterview(@Valid @RequestBody MockInterview mockInterview) {
        try {
            logger.info("Creating a new MockInterview: {}", mockInterview);
            MockInterview createdMockInterview = mockInterviewService.createMockInterview(mockInterview);
            logger.info("MockInterview created successfully with ID: {}", createdMockInterview.getId());
            return ResponseEntity.ok(createdMockInterview);
        } catch (Exception e) {
            logger.error("Error occurred while creating MockInterview: {}", e.getMessage(), e);
            return ResponseEntity.status(500).body("Failed to create MockInterview.");
        }
    }

    @GetMapping("/mockInterview/getMockInterviewById/{id}")
    public ResponseEntity<?> getMockInterviewById(@PathVariable Long id) {
        try {
            logger.info("Fetching MockInterview with ID: {}", id);
            MockInterview mockInterview = mockInterviewService.getMockInterviewById(id);
            logger.info("MockInterview fetched successfully: {}", mockInterview);
            return ResponseEntity.ok(mockInterview);
        } catch (Exception e) {
            logger.error("Error occurred while fetching MockInterview with ID {}: {}", id, e.getMessage(), e);
            return ResponseEntity.status(404).body("MockInterview not found with ID: " + id);
        }
    }

    @GetMapping("/mockInterview/getAllMockInterviews")
    public ResponseEntity<?> getAllMockInterviews() {
        try {
            logger.info("Fetching all MockInterviews...");
            List<MockInterview> mockInterviews = mockInterviewService.getAllMockInterviews();
            logger.info("Successfully fetched {} mock interviews.", mockInterviews.size());
            return ResponseEntity.ok(mockInterviews);
        } catch (Exception e) {
            logger.error("Error occurred while fetching all MockInterviews: {}", e.getMessage(), e);
            return ResponseEntity.status(500).body("Failed to fetch mock interviews.");
        }
    }

    @PutMapping("/mockInterview/updateMockInterview/{id}")
    public ResponseEntity<?> updateMockInterview(@PathVariable Long id, @Valid @RequestBody MockInterview mockInterview) {
        try {
            logger.info("Updating MockInterview with ID: {}", id);
            MockInterview updatedMockInterview = mockInterviewService.updateMockInterview(id, mockInterview);
            logger.info("MockInterview updated successfully: {}", updatedMockInterview);
            return ResponseEntity.ok(updatedMockInterview);
        } catch (Exception e) {
            logger.error("Error occurred while updating MockInterview with ID {}: {}", id, e.getMessage(), e);
            return ResponseEntity.status(404).body("Failed to update MockInterview with ID: " + id);
        }
    }

    @DeleteMapping("/mockInterview/deleteMockInterview/{id}")
    public ResponseEntity<?> deleteMockInterview(@PathVariable Long id) {
        try {
            logger.info("Deleting MockInterview with ID: {}", id);
            mockInterviewService.deleteMockInterview(id);
            logger.info("MockInterview with ID: {} deleted successfully.", id);
            return ResponseEntity.ok("MockInterview deleted successfully.");
        } catch (Exception e) {
            logger.error("Error occurred while deleting MockInterview with ID {}: {}", id, e.getMessage(), e);
            return ResponseEntity.status(404).body("Failed to delete MockInterview with ID: " + id);
        }
    }
}

