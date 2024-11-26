package com.jobwebsite.Repository;

import com.jobwebsite.Entity.Plan;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PlanRepository extends JpaRepository<Plan, Long> {
    Plan findByName(String name);
}