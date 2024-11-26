package com.jobwebsite.ServiceImpl;

import com.jobwebsite.Entity.Plan;
import com.jobwebsite.Entity.User;
import com.jobwebsite.Repository.PlanRepository;
import com.jobwebsite.Repository.UserRepository;
import com.jobwebsite.Service.PlanService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class PlanServiceImpl implements PlanService {
    private static final Logger logger = LoggerFactory.getLogger(PlanServiceImpl.class);

    @Autowired
    private PlanRepository planRepository;

    @Autowired
    private UserRepository userRepository;

    @Override
    public Plan createPlan(Plan plan) {
        try {
            logger.info("Saving a new plan: {}", plan.getName());
            return planRepository.save(plan);
        } catch (Exception e) {
            logger.error("Error occurred while saving plan: {}", e.getMessage(), e);
            throw new RuntimeException("Error occurred while saving the plan", e);
        }
    }

    @Override
    public List<Plan> getAllPlans() {
        try {
            logger.info("Fetching all plans from the repository.");
            List<Plan> plans = planRepository.findAll();
            logger.info("Fetched {} plans.", plans.size());
            return plans;
        } catch (Exception e) {
            logger.error("Error occurred while fetching all plans: {}", e.getMessage(), e);
            throw new RuntimeException("Error occurred while fetching plans", e);
        }
    }

    @Override
    public Plan updatePlan(Long id, Plan plan) throws Exception {
        // Check if the plan with the given ID exists
        Optional<Plan> existingPlanOpt = planRepository.findById(id);

        if (!existingPlanOpt.isPresent()) {
            throw new Exception("Plan with ID " + id + " not found.");
        }

        Plan existingPlan = existingPlanOpt.get();

        // Update the fields of the existing plan
        existingPlan.setName(plan.getName());
        existingPlan.setDescription(plan.getDescription());
        existingPlan.setPrice(plan.getPrice());
        existingPlan.setFeatures(plan.getFeatures());
        existingPlan.setDuration(plan.getDuration());

        // Save the updated plan
        return planRepository.save(existingPlan);
    }

    @Override
    public Plan getPlanById(Long id) {
        try {
            logger.info("Fetching plan by id: {}", id);
            Optional<Plan> planOptional = planRepository.findById(id);
            if (planOptional.isPresent()) {
                logger.info("Plan found with id: {}", id);
                return planOptional.get();
            } else {
                logger.warn("No plan found with id: {}", id);
                throw new RuntimeException("Plan not found with id: " + id);
            }
        } catch (Exception e) {
            logger.error("Error occurred while fetching plan by id {}: {}", id, e.getMessage(), e);
            throw new RuntimeException("Error occurred while fetching plan", e);
        }
    }

    public List<User> getUsersEnrolledInPlan(Long planId) {
        try {
            logger.info("Fetching users enrolled in plan with ID: {}", planId);
            List<User> users = userRepository.findByPlanId(planId);
            logger.info("Fetched {} users enrolled in plan with ID: {}", users.size(), planId);
            return users;
        } catch (Exception e) {
            logger.error("Error occurred while fetching users for plan with ID {}: {}", planId, e.getMessage(), e);
            throw new RuntimeException("Error occurred while fetching users for plan", e);
        }
    }
}