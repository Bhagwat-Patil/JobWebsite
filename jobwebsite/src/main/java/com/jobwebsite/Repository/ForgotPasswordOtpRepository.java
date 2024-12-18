package com.jobwebsite.Repository;

import com.jobwebsite.Entity.Admin;
import com.jobwebsite.Entity.ForgotPasswordOtp;
import com.jobwebsite.Entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface ForgotPasswordOtpRepository extends JpaRepository<ForgotPasswordOtp, Long> {

    Optional<ForgotPasswordOtp> findByOtp(String otp);

    Optional<ForgotPasswordOtp> findByUser(User user);

    Optional<ForgotPasswordOtp> findByAdmin(Admin admin);
}