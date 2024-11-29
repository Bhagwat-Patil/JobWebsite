package com.jobwebsite.ServiceImpl;

import com.jobwebsite.Entity.Admin;
import com.jobwebsite.Repository.AdminRepository;
import com.jobwebsite.Service.SuperAdminService;
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


    @Override
    public String approveAdmin(Long adminId) {
        try {
            logger.info("Attempting to approve admin with ID: {}", adminId);

            // Fetch admin by ID
            Admin admin = adminRepository.findById(adminId)
                    .orElseThrow(() -> new RuntimeException("Admin not found!"));

            // Approve the admin
            admin.setApproved(true);
            adminRepository.save(admin);

            logger.info("Admin with ID: {} approved successfully.", adminId);
            return "Admin approved successfully.";
        } catch (Exception e) {
            logger.error("Error approving admin with ID: {}: {}", adminId, e.getMessage());
            throw new RuntimeException("Error approving admin: " + e.getMessage());
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
