package com.jobwebsite.ServiceImpl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.jobwebsite.CommonUtil.ValidationClass;
import com.jobwebsite.Entity.*;
import com.jobwebsite.Exception.AdminNotApprovedException;
import com.jobwebsite.Exception.AdminNotEnabledException;
import com.jobwebsite.Exception.AdminNotFoundException;
import com.jobwebsite.Exception.FormNotFoundException;
import com.jobwebsite.Repository.*;
import com.jobwebsite.Service.AdminService;
import com.jobwebsite.Service.EmailService;
import jakarta.mail.MessagingException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class AdminServiceImpl implements AdminService {

    private static final Logger logger = LoggerFactory.getLogger(AdminServiceImpl.class);

    @Autowired
    private AdminRepository adminRepository;

    @Autowired
    private FormRepository formRepository;

    @Autowired
    private JobRepository jobRepository;

    @Autowired
    private InternshipRepository internshipRepository;

    @Autowired
    private EmailService emailService;

    @Autowired
    private PendingPostRepository pendingPostRepository;


    @Override
    public Admin registerAdmin(Admin admin) {
        admin.setApproved(false);
        adminRepository.save(admin);

        // Send email to Super Admin
        try {
            emailService.sendEmail(
                    admin.getEmail(), // From admin's email
                    "bhagwatkhedkar11@gmail.com", // To superadmin's email
                    "Admin Registration Request",
                    "An admin with email " + admin.getEmail() + " has registered. Please review and approve.",
                    false // Use the admin's own email configuration
            );
        } catch (MessagingException e) {
            throw new RuntimeException("Failed to send email to super admin: " + e.getMessage(), e);
        }

        return admin;
    }

    @Override
    public String loginAdmin(String username, String password) {
        try {
            logger.info("Attempting login for admin: {}", username);

            // Fetch admin by username
            Admin admin = adminRepository.findByUsername(username)
                    .orElseThrow(() -> new RuntimeException("Admin not found!"));

            // Validate password
            if (!admin.getPassword().equals(password)) {
                logger.warn("Invalid credentials for admin: {}", username);
                throw new RuntimeException("Invalid credentials.");
            }

            // Check approval status
            if (!admin.isApproved()) {
                logger.warn("Admin not approved by Super Admin: {}", username);
                throw new RuntimeException("Admin not approved by Super Admin.");
            }

            if(!admin.isEnabled())
            {
                logger.warn("Admin disabled by Super Admin: {}", username);
                throw new RuntimeException("Admin disabled by Super Admin.");
            }

            logger.info("Admin login successful: {}", username);
            return "Login successful!";
        } catch (Exception e) {
            logger.error("Error during admin login: {}", e.getMessage());
            throw new RuntimeException("Error during login: " + e.getMessage());
        }
    }


    @Override
    @Transactional
    public Admin updateAdmin(Long adminId, Admin adminDetails, MultipartFile profilePicture) {
        logger.info("Updating admin by id: {}, data: {}", adminId, adminDetails);

        // Retrieve the existing admin
        Admin existingAdmin = adminRepository.findById(adminId)
                .orElseThrow(() -> new AdminNotFoundException("Admin with ID " + adminId + " not found"));

        // Update fields only if the new value is not null and valid
        if (adminDetails.getName() != null && ValidationClass.NAME_PATTERN.matcher(adminDetails.getName()).matches()) {
            existingAdmin.setName(adminDetails.getName());
        }
        if (adminDetails.getMobileNo() != null && ValidationClass.PHONE_PATTERN.matcher(adminDetails.getMobileNo()).matches()) {
            existingAdmin.setMobileNo(adminDetails.getMobileNo());
        }
        if (adminDetails.getUsername() != null && ValidationClass.USERNAME_PATTERN.matcher(adminDetails.getUsername()).matches()) {
            existingAdmin.setUsername(adminDetails.getUsername());
        }
        if (adminDetails.getPassword() != null && ValidationClass.PASSWORD_PATTERN.matcher(adminDetails.getPassword()).matches()) {
            existingAdmin.setPassword(adminDetails.getPassword());
        }
        if (adminDetails.getEmail() != null && ValidationClass.EMAIL_PATTERN.matcher(adminDetails.getEmail()).matches()) {
            existingAdmin.setEmail(adminDetails.getEmail());
        }
        // Save the updated admin
        Admin updatedAdmin = adminRepository.save(existingAdmin);
        logger.info("Successfully updated admin with id: {}", adminId);
        return updatedAdmin;
    }

    @Override
    public void deleteAdmin(Long adminId) {
        logger.info("Deleting Admin by id: {}", adminId);
        Admin existingAdmin = adminRepository.findById(adminId)
                .orElseThrow(() -> new AdminNotFoundException("Admin with ID " + adminId + " not found"));

        adminRepository.deleteById(adminId);
        logger.info("Successfully deleted admin with id: {}", adminId);
    }

    @Override
    public Admin getAdminById(Long adminId) {
        logger.info("Fetching admin by ID: {}", adminId);
        return adminRepository.findById(adminId)
                .orElseThrow(() -> new AdminNotFoundException("Admin with ID " + adminId + " not found"));
    }

    @Override
    public List<Admin> getAllAdmins() {
        logger.info("Fetching all admins from the database...");

        // Fetch all admins from the repository
        List<Admin> admins = adminRepository.findAll();

        if (admins.isEmpty()) {
            logger.warn("No admins found in the database.");
            throw new IllegalStateException("No admins available.");
        }

        // Optionally, transform the data or filter it if needed
        List<Admin> transformedAdmins = admins.stream()
                .map(admin -> {
                    // Example transformation: Hide passwords for security reasons
                    admin.setPassword(null);
                    return admin;
                })
                .toList();

        logger.info("Successfully fetched {} admins.", transformedAdmins.size());
        return transformedAdmins;
    }

    @Override
    @Transactional
    public String jobpost(Job job, Long adminId) {
        Admin admin = adminRepository.findById(adminId)
                .orElseThrow(() -> new AdminNotFoundException("Admin not found with ID: " + adminId));

        // Check if the admin is approved and enabled
        if (!admin.isApproved()) {  // Assuming you have a field `isApproved` in your Admin entity
            throw new AdminNotApprovedException("Admin not approved. Please wait for approval.");
        }

        if (!admin.isEnabled()) {  // Assuming you have a field `isEnabled` in your Admin entity
            throw new AdminNotEnabledException("Admin is not enabled. Please contact the Super Admin.");
        }

        // Proceed with posting the job if the admin is approved and enabled
        // Serialize the Job object
        String jobContent = serialize(job);

        PendingPost pendingPost = new PendingPost();
        pendingPost.setType(PostType.JOB);
        pendingPost.setContent(jobContent);  // Store the serialized Job object
        pendingPost.setAdminId(adminId);
        pendingPost.setCreatedAt(LocalDateTime.now());
        pendingPost.setApproved(false); // Initially not approved

        // Save to the pending posts table
        pendingPostRepository.save(pendingPost);

        logger.info("Job post sent to super admin for approval.");
        return "Job post sent to super admin for approval.";
    }


    @Override
    @Transactional
    public String postInternship(Internship internship, Long adminId) {
        Admin admin = adminRepository.findById(adminId)
                .orElseThrow(() -> new AdminNotFoundException("Admin not found with ID: " + adminId));

        // Check if the admin is approved and enabled
        if (!admin.isApproved()) {  // Assuming you have a field `isApproved` in your Admin entity
            throw new AdminNotApprovedException("Admin not approved. Please wait for approval.");
        }

        if (!admin.isEnabled()) {  // Assuming you have a field `isEnabled` in your Admin entity
            throw new AdminNotEnabledException("Admin is not enabled. Please contact the Super Admin.");
        }

        // Proceed with posting the internship if the admin is approved and enabled
        // Serialize the Internship object
        String internshipContent = serialize(internship);

        PendingPost pendingPost = new PendingPost();
        pendingPost.setType(PostType.INTERNSHIP);
        pendingPost.setContent(internshipContent);  // Store the serialized Internship object
        pendingPost.setAdminId(adminId);
        pendingPost.setCreatedAt(LocalDateTime.now());
        pendingPost.setApproved(false); // Initially not approved

        // Save to the pending posts table
        pendingPostRepository.save(pendingPost);

        logger.info("Internship post sent to super admin for approval.");
        return "Internship post sent to super admin for approval.";
    }

    private String serialize(Object object) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.registerModule(new JavaTimeModule());  // Register the JavaTimeModule
            objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);  // Optional: to format dates as string
            return objectMapper.writeValueAsString(object);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Error serializing object: " + e.getMessage(), e);
        }
    }

    public List<Form> getAllForms() {
        logger.info("Fetching all forms");
        return formRepository.findAll();
    }

    public Form getFormByFormId(Long formId) {
        logger.info("Fetching form by ID: {}", formId);
        return formRepository.findById(formId)
                .orElseThrow(() -> new FormNotFoundException("Form not found with id: " + formId));
    }
    public List<Job> getAllJobsUploadedByAdmin() {
        logger.info("Fetching all jobs uploaded by Admin : {}");

        return jobRepository.findAll();
    }

    private void validateAdminData(Admin admin) {
        // Validate admin data
        if (admin.getUsername() == null || !ValidationClass.USERNAME_PATTERN.matcher(admin.getUsername()).matches()) {
            throw new IllegalArgumentException("Invalid username");
        }
        if (admin.getEmail() == null || !ValidationClass.EMAIL_PATTERN.matcher(admin.getEmail()).matches()) {
            throw new IllegalArgumentException("Invalid email");
        }
        if (admin.getPassword() == null || !ValidationClass.PASSWORD_PATTERN.matcher(admin.getPassword()).matches()) {
            throw new IllegalArgumentException("Invalid password");
        }
        if (admin.getMobileNo() == null || !ValidationClass.PHONE_PATTERN.matcher(admin.getMobileNo()).matches()) {
            throw new IllegalArgumentException("Invalid mobile number");
        }
    }

}


