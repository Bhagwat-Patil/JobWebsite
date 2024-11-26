package com.jobwebsite.Controller;

import com.jobwebsite.Entity.Plan;
import com.jobwebsite.Entity.User;
import com.jobwebsite.Service.PlanService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/plans")
@CrossOrigin("*")
public class PlanController {

    private static final Logger logger = LoggerFactory.getLogger(PlanController.class);

    @Autowired
    private PlanService planService;

    @PostMapping("/createPlan")
    public ResponseEntity<Plan> createPlan(@RequestBody Plan plan) {
        try {
            logger.info("Received request to create a new plan.");
            Plan createdPlan = planService.createPlan(plan);
            logger.info("Successfully created new plan with ID: {}", createdPlan.getId());
            return ResponseEntity.status(HttpStatus.CREATED).body(createdPlan);
        } catch (Exception e) {
            logger.error("Error occurred while creating plan: {}", e.getMessage(), e);
            return ResponseEntity.status(500).body(null);
        }
    }
    // Get all plans
    @GetMapping("/getAllPlans")
    public ResponseEntity<List<Plan>> getAllPlans() {
        try {
            logger.info("Fetching all plans.");
            List<Plan> plans = planService.getAllPlans();
            if (plans.isEmpty()) {
                logger.warn("No plans found.");
                return ResponseEntity.noContent().build();
            }
            logger.info("Successfully fetched {} plans.", plans.size());
            return ResponseEntity.ok(plans);
        } catch (Exception e) {
            logger.error("Error occurred while fetching all plans: {}", e.getMessage(), e);
            return ResponseEntity.status(500).body(null);
        }
    }

    // Get a specific plan by ID
    @GetMapping("/getPlanById/{id}")
    public ResponseEntity<Plan> getPlanById(@PathVariable Long id) {
        try {
            logger.info("Fetching plan with ID: {}", id);
            Plan plan = planService.getPlanById(id);
            if (plan == null) {
                logger.warn("Plan not found with ID: {}", id);
                return ResponseEntity.notFound().build();
            }
            logger.info("Successfully fetched plan with ID: {}", id);
            return ResponseEntity.ok(plan);
        } catch (Exception e) {
            logger.error("Error occurred while fetching plan with ID {}: {}", id, e.getMessage(), e);
            return ResponseEntity.status(500).body(null);
        }
    }

    // Get users enrolled in a specific plan
    @GetMapping("/getUsersEnrolledInPlan/{planId}")
    public ResponseEntity<List<User>> getUsersEnrolledInPlan(@PathVariable Long planId) {
        try {
            logger.info("Fetching users enrolled in plan with ID: {}", planId);
            List<User> users = planService.getUsersEnrolledInPlan(planId);
            if (users.isEmpty()) {
                logger.warn("No users found for plan with ID: {}", planId);
                return ResponseEntity.noContent().build();
            }
            logger.info("Successfully fetched users for plan with ID: {}", planId);
            return ResponseEntity.ok(users);
        } catch (Exception e) {
            logger.error("Error occurred while fetching users for plan with ID {}: {}", planId, e.getMessage(), e);
            return ResponseEntity.status(500).body(null);
        }
    }


    @PutMapping("/updatePlan/{id}")
    public ResponseEntity<Plan> updatePlan(@PathVariable Long id, @RequestBody Plan plan) {
        try {
            logger.info("Updating plan with ID: {}", id);
            Plan updatedPlan = planService.updatePlan(id, plan);
            return ResponseEntity.ok(updatedPlan);
        } catch (Exception e) {
            logger.error("Error occurred while updating plan with ID {}: {}", id, e.getMessage(), e);
            return ResponseEntity.status(500).body(null);
        }
    }

}