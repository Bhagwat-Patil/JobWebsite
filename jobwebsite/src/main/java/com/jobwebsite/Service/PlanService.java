package com.jobwebsite.Service;

import com.jobwebsite.Entity.Plan;
import com.jobwebsite.Entity.User;

import java.util.List;

public interface PlanService {
    List<Plan> getAllPlans();
    Plan updatePlan(Long id, Plan plan) throws Exception;
    Plan getPlanById(Long id);
    List<User> getUsersEnrolledInPlan(Long planId);
    Plan createPlan(Plan plan);
}