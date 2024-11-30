package com.jobwebsite.ServiceImpl;

import com.jobwebsite.CommonUtil.ValidationClass;
import com.jobwebsite.Entity.Admin;
import com.jobwebsite.Entity.Form;
import com.jobwebsite.Entity.Internship;
import com.jobwebsite.Entity.Job;
import com.jobwebsite.Exception.AdminNotFoundException;
import com.jobwebsite.Exception.FormNotFoundException;
import com.jobwebsite.Repository.AdminRepository;
import com.jobwebsite.Repository.FormRepository;
import com.jobwebsite.Repository.InternshipRepository;
import com.jobwebsite.Repository.JobRepository;
import com.jobwebsite.Service.AdminService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
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


    @Override
    public String registerAdmin(Admin admin) {
        try {
            logger.info("Registering new admin: {}", admin.getUsername());
            // Set the approved field to false by default
            admin.setApproved(false);
            adminRepository.save(admin);
            logger.info("Admin registered successfully: {}", admin.getUsername());
            return "Admin registered successfully. Waiting for Super Admin approval.";
        } catch (Exception e) {
            logger.error("Error registering admin: {}", e.getMessage());
            throw new RuntimeException("Error registering admin: " + e.getMessage());
        }
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

        // Update profile picture if a new one is provided and valid
        if (profilePicture != null && !profilePicture.isEmpty()) {
            String contentType = profilePicture.getContentType();
            if (contentType != null && (contentType.equals("image/jpeg") || contentType.equals("image/png"))) {
                try {
                    existingAdmin.setProfilePicture(profilePicture.getBytes());
                } catch (IOException e) {
                    logger.error("Failed to update profile picture for admin with id: {}", adminId, e);
                    throw new RuntimeException("Failed to update profile picture", e);
                }
            } else {
                logger.warn("Invalid file type: {}", contentType);
                throw new RuntimeException("Only JPEG or PNG images are allowed");
            }
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
        logger.info("Posting job: {}", job);

        // Fetch the admin based on adminId
        Admin admin = adminRepository.findById(adminId)
                .orElseThrow(() -> new AdminNotFoundException("Admin not found with ID: " + adminId));

        // Set the admin for the job
        job.setAdmin(admin);

        // Save the job
        job.setCreatedAt(LocalDateTime.now());
        job.setUpdatedAt(LocalDateTime.now());
        jobRepository.save(job);

        logger.info("Successfully posted job: {}", job.getId());
        return "Job posted successfully";
    }


    @Override
    @Transactional
    public String postInternship(Internship internship, Long adminId) {
        logger.info("Posting internship: {}", internship);

        // Fetch the admin based on adminId
        Admin admin = adminRepository.findById(adminId)
                .orElseThrow(() -> new AdminNotFoundException("Admin not found with ID: " + adminId));

        // Set the admin for the internship
        internship.setAdmin(admin);

        // Save the internship
        internship.setCreatedAt(LocalDateTime.now());
        internship.setUpdatedAt(LocalDateTime.now());
        internshipRepository.save(internship);

        logger.info("Successfully posted internship: {}", internship.getId());
        return "Internship posted successfully";
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


