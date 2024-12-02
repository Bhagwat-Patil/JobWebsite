package com.jobwebsite.ServiceImpl;

import com.jobwebsite.Entity.Admin;
import com.jobwebsite.Entity.Job;
import com.jobwebsite.Repository.AdminRepository;
import com.jobwebsite.Repository.JobRepository;
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
    private JobRepository jobRepository;

    @Autowired
    private EmailService emailService;


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
                    "bhagwatkhedkar11@gmail.com", // From superadmin's email
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
                .orElseThrow(() -> new RuntimeException("Admin not found with ID: " + adminId));
        admin.setEnabled(false);
        return adminRepository.save(admin);
    }

    @Override
    public List<Admin> getAllAdmins() {
        return adminRepository.findAll();
    }
}
