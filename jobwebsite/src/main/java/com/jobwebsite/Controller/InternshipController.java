package com.jobwebsite.Controller;

import com.jobwebsite.Entity.Internship;
import com.jobwebsite.Service.InternshipService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/internships")
@CrossOrigin("*")
public class InternshipController {

    private static final Logger logger = LoggerFactory.getLogger(InternshipController.class);

    @Autowired
    private InternshipService internshipService;

    @PostMapping("/createInternship/{adminId}")
    public Internship createInternship(@RequestBody Internship internship, @PathVariable Long adminId) {
        try {
            logger.info("Creating internship for Admin ID: {}", adminId);
            return internshipService.createInternship(internship, adminId);
        } catch (Exception e) {
            logger.error("Error in creating internship for Admin ID {}: {}", adminId, e.getMessage(), e);
            throw e;
        }
    }

    @GetMapping("/getAllInternships")
    public List<Internship> getAllInternships() {
        try {
            logger.info("Fetching all internships.");
            return internshipService.getAllInternships();
        } catch (Exception e) {
            logger.error("Error in fetching internships: {}", e.getMessage(), e);
            throw e;
        }
    }

    @GetMapping("/getInternshipById/{id}")
    public Internship getInternshipById(@PathVariable Long id) {
        try {
            logger.info("Fetching internship with ID: {}", id);
            return internshipService.getInternshipById(id);
        } catch (Exception e) {
            logger.error("Error in fetching internship with ID {}: {}", id, e.getMessage(), e);
            throw e;
        }
    }

    @DeleteMapping("/deleteInternship/{id}")
    public String deleteInternship(@PathVariable Long id) {
        try {
            internshipService.deleteInternship(id);
            logger.info("Internship with ID {} deleted successfully.", id);
            return "Internship with ID " + id + " deleted successfully.";
        } catch (Exception e) {
            logger.error("Error in deleting internship with ID {}: {}", id, e.getMessage(), e);
            throw e;
        }
    }

    @PutMapping("/updateInternship/{id}")
    public Internship updateInternship(@PathVariable Long id, @RequestBody Internship updatedInternship) {
        try {
            logger.info("Updating internship with ID: {}", id);
            return internshipService.updateInternship(id, updatedInternship);
        } catch (Exception e) {
            logger.error("Error in updating internship with ID {}: {}", id, e.getMessage(), e);
            throw e;
        }
    }

    @GetMapping("/getInternshipsByStatus/{status}")
    public List<Internship> getInternshipsByStatus(@PathVariable String status) {
        try {
            logger.info("Fetching internships with status: {}", status);
            return internshipService.getInternshipsByStatus(status);
        } catch (Exception e) {
            logger.error("Error in fetching internships with status {}: {}", status, e.getMessage(), e);
            throw e;
        }
    }

    @GetMapping("/getInternshipsByAdmin/{adminId}")
    public List<Internship> getInternshipsByAdmin(@PathVariable Long adminId) {
        try {
            logger.info("Fetching internships for Admin ID: {}", adminId);
            return internshipService.getInternshipsByAdminId(adminId);
        } catch (Exception e) {
            logger.error("Error while fetching internships for Admin ID {}: {}", adminId, e.getMessage(), e);
            throw e;
        }
    }

}
