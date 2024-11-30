package com.jobwebsite.ServiceImpl;

import com.jobwebsite.Entity.Admin;
import com.jobwebsite.Repository.AdminRepository;
import com.jobwebsite.Service.EmailService;
import com.jobwebsite.Service.SuperAdminService;
import jakarta.mail.MessagingException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class SuperAdminServiceImpl implements SuperAdminService {

    private static final Logger logger = LoggerFactory.getLogger(AdminServiceImpl.class);

    @Autowired
    private AdminRepository adminRepository;

    @Autowired
    private EmailService emailService;


    @Override
    public void approveAdmin(Long adminId) {
        Admin admin = adminRepository.findById(adminId)
                .orElseThrow(() -> new RuntimeException("Admin not found"));

        admin.setApproved(true);
        adminRepository.save(admin);

        // Send approval email to Admin
        try {
            emailService.sendEmail(
                    "bhagwatkhedkar11@gmail.com", // From superadmin's email
                    admin.getEmail(), // To admin's email
                    "Admin Approval",
                    "Dear " + admin.getName() + ", your admin profile has been approved by the super admin.",
                    true // Use superadmin's email configuration

            );
        } catch (MessagingException e) {
            throw new RuntimeException("Failed to send approval email to admin: " + e.getMessage(), e);
        }
    }


    @Override
    public Admin disableAdmin(Long adminId) {
        Admin admin = adminRepository.findById(adminId)
                .orElseThrow(() -> new RuntimeException("Admin not found with ID: " + adminId));
        admin.setEnabled(false);
        return adminRepository.save(admin);
    }

    @Override
    public List<Admin> getAllAdmins() {
        return adminRepository.findAll();
    }
}
