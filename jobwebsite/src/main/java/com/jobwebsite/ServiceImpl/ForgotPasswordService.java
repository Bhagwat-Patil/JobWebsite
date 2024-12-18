package com.jobwebsite.ServiceImpl;

import com.jobwebsite.Controller.AdminController;
import com.jobwebsite.Entity.Admin;
import com.jobwebsite.Entity.ForgotPasswordOtp;
import com.jobwebsite.Entity.User;
import com.jobwebsite.Exception.AdminNotFoundException;
import com.jobwebsite.Exception.UserNotFoundException;
import com.jobwebsite.Repository.AdminRepository;
import com.jobwebsite.Repository.ForgotPasswordOtpRepository;
import com.jobwebsite.Repository.UserRepository;
import com.jobwebsite.Service.EmailService;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.Random;

@Service
public class ForgotPasswordService {

    private static final Logger logger = LoggerFactory.getLogger(AdminController.class);

    @Autowired
    private AdminRepository adminRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ForgotPasswordOtpRepository otpRepository;

    @Autowired
    private EmailService emailService;

    private final PasswordEncoder passwordEncoder;

    private static final int OTP_EXPIRY_MINUTES = 5;  // OTP expiration time in minutes

    public ForgotPasswordService(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    // Request OTP for Admin or User
    public String requestPasswordReset(String email) {
        // First, check if the email corresponds to an Admin or User
        Admin admin = adminRepository.findByEmail(email);
        if (admin != null) {
            return generateOtpForAdmin(admin);
        }

        User user = userRepository.findByEmailId(email);
        if (user != null) {
            return generateOtpForUser(user);
        }

        throw new AdminNotFoundException("No Admin or User found with this email: " + email);
    }

    // Generate OTP for User
    public String generateOtpForUser(User user) {
        String otp = generateOtp();
        ForgotPasswordOtp otpEntity = new ForgotPasswordOtp();
        otpEntity.setOtp(otp);
        otpEntity.setUser(user);  // Link the OTP to the User
        otpEntity.setCreatedAt(LocalDateTime.now());
        otpEntity.setExpiryDate(LocalDateTime.now().plusMinutes(OTP_EXPIRY_MINUTES)); // Set OTP expiration time

        otpRepository.save(otpEntity);

        // Send OTP to the user email
        emailService.sendEmail(user.getEmailId(), "Password Reset OTP", "Your OTP for password reset is: " + otp);

        return otp;
    }

    // Generate OTP for Admin
    public String generateOtpForAdmin(Admin admin) {
        String otp = generateOtp();
        ForgotPasswordOtp otpEntity = new ForgotPasswordOtp();
        otpEntity.setOtp(otp);
        otpEntity.setAdmin(admin);  // Link the OTP to the Admin
        otpEntity.setCreatedAt(LocalDateTime.now());
        otpEntity.setExpiryDate(LocalDateTime.now().plusMinutes(OTP_EXPIRY_MINUTES)); // Set OTP expiration time

        otpRepository.save(otpEntity);

        // Send OTP to the admin email
        emailService.sendEmail(admin.getEmail(), "Password Reset OTP", "Your OTP for password reset is: " + otp);

        return otp;
    }


    // Helper method to generate OTP
    private String generateOtp() {
        return String.valueOf(new Random().nextInt(999999));  // Simple 6-digit OTP
    }

    // Verify OTP for Admin or User
    public boolean verifyOtp(String otp, String email) {
        Admin admin = adminRepository.findByEmail(email);
        if (admin != null) {
            return verifyOtpForAdmin(otp, admin);
        }

        User user = userRepository.findByEmailId(email);
        if (user != null) {
            return verifyOtpForUser(otp, user);
        }

        throw new AdminNotFoundException("No Admin or User found with this email: " + email);
    }

    // Verify OTP for Admin
    private boolean verifyOtpForAdmin(String otp, Admin admin) {
        ForgotPasswordOtp otpEntity = otpRepository.findByOtp(otp)
                .filter(otpRecord -> otpRecord.getAdmin().equals(admin))  // Ensure OTP is for the correct Admin
                .orElseThrow(() -> new IllegalArgumentException("Invalid OTP"));

        // Check if OTP has expired
        if (otpEntity.getCreatedAt().plusMinutes(OTP_EXPIRY_MINUTES).isBefore(LocalDateTime.now())) {
            otpRepository.delete(otpEntity); // Clean up expired OTP
            return false;  // OTP expired
        }

        return true;  // OTP is valid
    }

    // Verify OTP for User
    private boolean verifyOtpForUser(String otp, User user) {
        ForgotPasswordOtp otpEntity = otpRepository.findByOtp(otp)
                .filter(otpRecord -> otpRecord.getUser().equals(user))  // Ensure OTP is for the correct User
                .orElseThrow(() -> new IllegalArgumentException("Invalid OTP"));

        // Check if OTP has expired
        if (otpEntity.getCreatedAt().plusMinutes(OTP_EXPIRY_MINUTES).isBefore(LocalDateTime.now())) {
            otpRepository.delete(otpEntity); // Clean up expired OTP
            return false;  // OTP expired
        }

        return true;  // OTP is valid
    }


    // Reset password for Admin or User
    @Transactional
    public void resetPassword(String email, String newPassword) {
        logger.info("Initiating password reset for email: {}", email);

        // Validate the new password
        if (newPassword == null || newPassword.trim().isEmpty()) {
            throw new IllegalArgumentException("New password cannot be null or empty.");
        }

        // Encode the new password
        String encodedPassword = passwordEncoder.encode(newPassword);
        logger.debug("Encoded new password for email {}: {}", email, encodedPassword);

        boolean isPasswordReset = false;

        // Attempt to update Admin's password
        Admin admin = adminRepository.findByEmail(email);
        if (admin != null) {
            logger.info("Admin found with email: {}", email);
            admin.setPassword(encodedPassword);
            adminRepository.save(admin); // Save updated password
            logger.debug("Password successfully updated for Admin: {}", admin);
            isPasswordReset = true;
        } else {
            // Attempt to update User's password
            User user = userRepository.findByEmailId(email);
            if (user != null) {
                logger.info("User found with email: {}", email);
                logger.debug("Old password for User: {}", user.getPassword());
                user.setPassword(encodedPassword);
                userRepository.save(user); // Save updated password
                logger.debug("Password successfully updated for User: {}", user);
                isPasswordReset = true;
            }
        }

        if (!isPasswordReset) {
            logger.error("No Admin or User account found for email: {}", email);
            throw new UserNotFoundException("No Admin or User found with this email: " + email);
        }

        logger.info("Password reset process completed for email: {}", email);
    }

}
