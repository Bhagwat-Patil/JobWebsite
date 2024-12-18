package com.jobwebsite.ServiceImpl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jobwebsite.Entity.*;
import com.jobwebsite.Repository.*;
import com.jobwebsite.Service.EmailService;
import com.jobwebsite.Service.SuperAdminService;
import jakarta.mail.MessagingException;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class SuperAdminServiceImpl implements SuperAdminService {

    private static final Logger logger = LoggerFactory.getLogger(AdminServiceImpl.class);

    @Autowired
    private AdminRepository adminRepository;

    @Autowired
    private SuperAdminRepository superAdminRepository;

    @Autowired
    private JobRepository jobRepository;

    @Autowired
    private EmailService emailService;

    @Autowired
    private InternshipRepository internshipRepository;

    @Autowired
    private PendingPostRepository pendingPostRepository;

    private final PasswordEncoder passwordEncoder; // Inject PasswordEncoder

    @Autowired
    public SuperAdminServiceImpl(SuperAdminRepository superAdminRepository, PasswordEncoder passwordEncoder) {
        this.superAdminRepository = superAdminRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public SuperAdmin registerSuperAdmin(SuperAdmin superAdmin) {
        // Encrypt the super admin password before saving to database
        superAdmin.setPassword(passwordEncoder.encode(superAdmin.getPassword()));
        return superAdminRepository.save(superAdmin);
    }

    @Override
    public SuperAdmin loginSuperAdmin(String username, String password) {
        SuperAdmin superAdmin = superAdminRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Super Admin not found!"));

        // Validate password with encrypted password in database
        if (!passwordEncoder.matches(password, superAdmin.getPassword())) {
            throw new RuntimeException("Invalid credentials.");
        }

        return superAdmin; // Return super admin details upon successful login
    }

    @Override
    @Transactional
    public SuperAdmin updateSuperAdmin(Long id, SuperAdmin updatedDetails) {
        logger.info("Attempting to update Super Admin with ID: {}", id);

        // Retrieve existing Super Admin
        SuperAdmin existingSuperAdmin = superAdminRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Super Admin with ID " + id + " not found."));

        // Update fields if they are not null and valid
        if (updatedDetails.getSuperAdminName() != null && !updatedDetails.getSuperAdminName().isEmpty()) {
            existingSuperAdmin.setSuperAdminName(updatedDetails.getSuperAdminName());
        }
        if (updatedDetails.getUsername() != null && !updatedDetails.getUsername().isEmpty()) {
            existingSuperAdmin.setUsername(updatedDetails.getUsername());
        }
        if (updatedDetails.getEmail() != null && !updatedDetails.getEmail().isEmpty()) {
            existingSuperAdmin.setEmail(updatedDetails.getEmail());
        }
        if (updatedDetails.getPassword() != null && !updatedDetails.getPassword().isEmpty()) {
            // Encrypt the new password
            String encryptedPassword = passwordEncoder.encode(updatedDetails.getPassword());
            existingSuperAdmin.setPassword(encryptedPassword);
        }

        // Save the updated Super Admin
        SuperAdmin updatedSuperAdmin = superAdminRepository.save(existingSuperAdmin);
        logger.info("Successfully updated Super Admin with ID: {}", id);

        return updatedSuperAdmin;
    }

    @Override
    @Transactional
    public void deleteSuperAdmin(Long id) {
        logger.info("Attempting to delete Super Admin with ID: {}", id);

        // Check if the Super Admin exists
        SuperAdmin existingSuperAdmin = superAdminRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Super Admin with ID " + id + " not found."));

        // Delete the Super Admin
        superAdminRepository.delete(existingSuperAdmin);
        logger.info("Successfully deleted Super Admin with ID: {}", id);
    }


    @Override
    public void approveAdmin(Long adminId) {
        Admin admin = adminRepository.findById(adminId)
                .orElseThrow(() -> new RuntimeException("Admin not found"));

        admin.setApproved(true);
        adminRepository.save(admin);

        // HTML email content
        String emailContent = String.format("""
<html>
<body style="font-family: Arial, sans-serif; line-height: 1.6;">
<p>Dear %s,</p>
<p>A profile has been approved for you in the AcchaJob Portal.</p>
<p>For further use:<br>
                       Log on to: <a href="https://acchajob.com" style="color: #007bff;">acchajob.com</a></p>
<br>
<p>Regards,<br>AcchaJob Team</p>
<hr style="border: 1px solid #ddd;">
<p style="font-size: 12px; color: #666;">
                        Copyright (c) 2024 AcchaJob, All rights reserved.<br>
                        Corp Add. Golden Rocks, Plot No. 21 & 22, Vijaynagar,<br>
                        Gajanan Maharaj Road, Chh. Sambhajinagar. 431005.
</p>
<hr style="border: 1px solid #ddd;">
<p style="font-size: 12px; color: #666;">
                        Disclaimer: You have received this mail because you are registered on AcchaJob.com.<br>
                        This is a system-generated email. Please do not reply to this message.<br>
                        For Terms and Conditions and other legal disclaimers, visit <a href="https://acchajob.com" style="color: #007bff;">acchajob.com</a>.
</p>
</body>
</html>
            """, admin.getName());

        // Send approval email to Admin
        try {
            emailService.sendEmail(
                    "achhajobssuper@gmail.com", // From superadmin's email
                    admin.getEmail(), // To admin's email
                    "Admin Approval",
                    emailContent,
                    true // Use superadmin's email configuration

            );
        } catch (MessagingException e) {
            throw new RuntimeException("Failed to send approval email to admin: " + e.getMessage(), e);
        }
    }

    @Override
    public Admin disableAdmin(Long adminId) {
        Admin admin = adminRepository.findById(adminId)
                .orElseThrow(() -> {
                    logger.warn("Admin with ID {} not found for disabling.", adminId);
                    return new RuntimeException("Admin not found with ID: " + adminId);
                });

        admin.setEnabled(false);
        adminRepository.save(admin);

        logger.info("Admin with ID {} has been disabled successfully.", adminId);
        return admin;
    }

    @Override
    @Transactional
    public String approveOrDisapprovePost(Long pendingPostId, boolean isApproved) {
        PendingPost pendingPost = pendingPostRepository.findById(pendingPostId)
                .orElseThrow(() -> new RuntimeException("Pending post not found with ID: " + pendingPostId));

        logger.info("Fetched pending post: {}", pendingPost);

        if (isApproved) {
            // Fetch the Admin based on the adminId in the pending post
            Admin admin = adminRepository.findById(pendingPost.getAdminId())
                    .orElseThrow(() -> new RuntimeException("Admin not found for ID: " + pendingPost.getAdminId()));

            if (pendingPost.getType() == PostType.JOB) {
                Job job = deserializeJob(pendingPost.getContent());
                job.setAdmin(admin); // Set the Admin in the Job entity
                jobRepository.save(job);
            } else if (pendingPost.getType() == PostType.INTERNSHIP) {
                Internship internship = deserializeInternship(pendingPost.getContent(), admin);
                internshipRepository.save(internship);
            }

            // Delete the approved pending post to avoid occupying unnecessary database space
            pendingPostRepository.delete(pendingPost);
            logger.info("Approved post deleted from pending posts.");
            return "Post approved, saved, and removed from pending posts.";
        } else {
            pendingPostRepository.delete(pendingPost);
            logger.info("Post disapproved and deleted.");
            return "Post disapproved and deleted.";
        }
    }

    private Job deserializeJob(String jobContent) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.registerModule(new com.fasterxml.jackson.datatype.jsr310.JavaTimeModule());
            return objectMapper.readValue(jobContent, Job.class); // Deserialize to Job entity
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Error deserializing Job object: " + e.getMessage(), e);
        }
    }

    private Internship deserializeInternship(String internshipContent, Admin admin) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.registerModule(new com.fasterxml.jackson.datatype.jsr310.JavaTimeModule());
            Internship internship = objectMapper.readValue(internshipContent, Internship.class);

            // Set the admin
            internship.setAdmin(admin);

            return internship;
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Error deserializing Internship object: " + e.getMessage(), e);
        }
    }

    @Override
    public List<PendingPost> getAllPendingPosts() {
        return pendingPostRepository.findAll();
    }

    @Override
    public List<Admin> getAllAdmins() {
        List<Admin> admins = adminRepository.findAll();
        if (admins.isEmpty()) {
            throw new RuntimeException("No admins found.");
        }
        return admins;
    }

    @Override
    public List<Admin> getAllAdminNotApproved() {
        List<Admin> admins = adminRepository.findAllNotApprovedAdmins();
        if (admins.isEmpty()) {
            throw new RuntimeException("No unapproved admins found.");
        }
        return admins;
    }

    @Override
    public List<Admin> getAllAdminApproved() {
        List<Admin> admins = adminRepository.findAllApprovedAdmins();
        if (admins.isEmpty()) {
            throw new RuntimeException("No approved admins found.");
        }
        return admins;
    }

    @Override
    public List<Admin> getAllAdminDisabled() {
        List<Admin> admins = adminRepository.findAllDisabledAdmins();
        if (admins.isEmpty()) {
            throw new RuntimeException("No disabled admins found.");
        }
        return admins;
    }

    @Override
    public List<Admin> getAllAdminEnabled() {
        List<Admin> admins = adminRepository.findAllEnabledAdmins();
        if (admins.isEmpty()) {
            throw new RuntimeException("No enabled admins found.");
        }
        return admins;
    }

}
