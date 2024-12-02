package com.jobwebsite.Repository;

import com.jobwebsite.Entity.Admin;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface AdminRepository extends JpaRepository<Admin,Long> {
    Optional<Admin> findByUsername(String username);
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);
    boolean existsByMobileNo(String mobileNo);
    Admin findByEmail(String email);

    @Query("SELECT a FROM Admin a WHERE a.approved = false")
    List<Admin> findAllNotApprovedAdmins();

    @Query("SELECT a FROM Admin a WHERE a.approved = true")
    List<Admin> findAllApprovedAdmins();

    @Query("SELECT a FROM Admin a WHERE a.enabled = false")
    List<Admin> findAllDisabledAdmins();

    @Query("SELECT a FROM Admin a WHERE a.enabled = true")
    List<Admin> findAllEnabledAdmins();
}

