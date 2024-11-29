package com.jobwebsite.Repository;

import com.jobwebsite.Entity.SuperAdmin;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SuperAdminRepository extends JpaRepository<SuperAdmin, Long> {
    SuperAdmin findByUsername(String username);
}
