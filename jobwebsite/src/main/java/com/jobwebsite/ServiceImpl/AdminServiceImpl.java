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
    @Transactional
    public String registerAdmin(Admin admin) {
        logger.info("Registering admin with username: {}", admin.getUsername());

        validateAdminData(admin);

        // Check if the username or email already exists
        if (adminRepository.existsByUsername(admin.getUsername()) || adminRepository.existsByEmail(admin.getEmail()) || adminRepository.existsByMobileNo(admin.getMobileNo())) {
            logger.error("Admin with username {} or email {} or mobile number {} already exists", admin.getUsername(), admin.getEmail(), admin.getMobileNo());
            throw new RuntimeException("Admin with this username or email or mobile number already exists");
        }

        // Handle profile picture
        if (admin.getProfilePicture() != null && admin.getProfilePicture().length > 0) {
            logger.info("Profile picture uploaded for admin with username: {}", admin.getUsername());
            admin.setProfilePicture(admin.getProfilePicture());
        } else {
            logger.info("No profile picture provided for admin with username: {}", admin.getUsername());
            admin.setProfilePicture(null);
        }
        // Save the admin entity
        Admin savedAdmin = adminRepository.save(admin);
        logger.info("Successfully registered admin with ID: {}", savedAdmin.getId());
        // Return a success message
        return "Admin registered successfully.";
    }

    @Override
    public String loginAdmin(String username, String password) {
        logger.info("Admin login attempt for username: {}", username);

        Admin admin = adminRepository.findByUsername(username)
                .orElseThrow(() -> {
                    logger.error("Admin not found with username: {}", username);
                    return new RuntimeException("Admin not found");
                });

        // Direct password comparison without encoding
        if (password.equals(admin.getPassword())) {
            logger.info("Login successful for username: {}", username);
            return "Login successful!";
        } else {
            logger.error("Invalid credentials for username: {}", username);
            throw new RuntimeException("Invalid credentials");
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


