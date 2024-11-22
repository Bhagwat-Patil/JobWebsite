package com.jobwebsite.Controller;

import com.jobwebsite.Entity.Job;
import com.jobwebsite.Exception.InvalidJobDataException;
import com.jobwebsite.Service.JobService;
import com.jobwebsite.Exception.JobNotFoundException; // Ensure that the custom exception is imported
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/jobs")
@CrossOrigin("*")
public class JobController {

    private static final Logger logger = LoggerFactory.getLogger(JobController.class);

    @Autowired
    private final JobService jobService;

    public JobController(JobService jobService) {
        this.jobService = jobService;
    }


    @PostMapping("/saveJob/{adminId}")
    public ResponseEntity<Job> saveJob(@PathVariable Long adminId, @RequestBody Job job) {
        try {
            logger.info("Received request to save job with adminId: {}", adminId);
            Job savedJob = jobService.saveJob(job, adminId);
            if (savedJob == null) {
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }
            return new ResponseEntity<>(savedJob, HttpStatus.CREATED);
        } catch (Exception e) {
            logger.error("Error while saving job: {}", e.getMessage(), e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    // Get all jobs
    @GetMapping("/getAllJobs")
    public ResponseEntity<List<Job>> getAllJobs() {
        try {
            logger.info("Received request to get all jobs");
            List<Job> jobs = jobService.getAllJobs();
            return new ResponseEntity<>(jobs, HttpStatus.OK);  // Returns 200 status
        } catch (Exception e) {
            logger.error("Error while fetching jobs: {}", e.getMessage(), e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);  // Returns 500 status for server error
        }
    }

    // Get jobs by one or more sorting
    @GetMapping("/searchJobs")
    public ResponseEntity<List<Job>> searchJobs(
            @RequestParam(required = false) String title,
            @RequestParam(required = false) String location,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String employmentType,
            @RequestParam(required = false) String workModel,
            @RequestParam(required = false) String salary,
            @RequestParam(required = false) String experience,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate openingStartDate, // Added openingStartDate
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate lastApplyDate // Added lastApplyDate
    ) {
        try {
            logger.info("Received request to search jobs with parameters: title={}, location={}, category={}, employmentType={}, openingStartDate={}, lastApplyDate={}",
                    title, location, category, employmentType, openingStartDate, lastApplyDate);

            List<Job> jobs = jobService.searchJobs(
                    title, location, category, employmentType, workModel, salary, experience, openingStartDate, lastApplyDate
            );

            return new ResponseEntity<>(jobs, HttpStatus.OK);  // Returns 200 status
        } catch (Exception e) {
            logger.error("Error while searching jobs: {}", e.getMessage(), e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);  // Returns 500 status for server error
        }
    }


    // Update a job
    @PutMapping("/updateJob/{id}")
    public ResponseEntity<Job> updateJob(@PathVariable Long id, @RequestBody Job jobDetails) {
        try {
            logger.info("Received request to update job with id: {}", id);
            Job updatedJob = jobService.updateJob(id, jobDetails);
            return new ResponseEntity<>(updatedJob, HttpStatus.OK);  // Returns 200 status
        } catch (JobNotFoundException e) {
            logger.error("Job with ID {} not found : {}", id, e.getMessage());
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);  // Returns 404 if job is not found
        } catch (Exception e) {
            logger.error("Error while updating job with ID {}: {}", id, e.getMessage(), e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);  // Returns 500 status for server error
        }
    }

    // Delete a job
    @DeleteMapping("/deleteJobById/{id}")
    public ResponseEntity<String> deleteJob(@PathVariable Long id) {
        try {
            logger.info("Received request to delete job with id: {}", id);
            jobService.deleteJob(id);
            return new ResponseEntity<>("Job deleted successfully", HttpStatus.OK);  // Returns 200 status
        } catch (JobNotFoundException e) {
            logger.error("Job with ID {} not found: {}", id, e.getMessage());
            return new ResponseEntity<>("Job not found", HttpStatus.NOT_FOUND);  // Returns 404 if job is not found
        } catch (Exception e) {
            logger.error("Error while deleting job with ID {}: {}", id, e.getMessage(), e);
            return new ResponseEntity<>("Error deleting job", HttpStatus.INTERNAL_SERVER_ERROR);  // Returns 500 for internal errors
        }
    }

    // Get job by ID
    @GetMapping("/getJobById/{id}")
    public ResponseEntity<Job> getJobById(@PathVariable Long id) {
        try {
            logger.info("Received request to get job with id: {}", id);
            Job job = jobService.getJobById(id);
            return job != null ? new ResponseEntity<>(job, HttpStatus.OK) : new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            logger.error("Error while fetching job by ID {}: {}", id, e.getMessage(), e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);  // Returns 500 for internal server error
        }
    }

    // Get jobs by status
    @GetMapping("/getJobsByStatus/{status}")
    public ResponseEntity<List<Job>> getJobsByStatus(@PathVariable String status) {
        try {
            logger.info("Received request to get jobs with status: {}", status);
            List<Job> jobs = jobService.getJobsByStatus(status);
            return new ResponseEntity<>(jobs, HttpStatus.OK);  // Returns 200 status
        } catch (Exception e) {
            logger.error("Error while fetching jobs with status {}: {}", status, e.getMessage(), e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);  // Returns 500 status for server error
        }
    }

    // Get jobs by Admin ID
    @GetMapping("/getJobsByAdmin/{adminId}")
    public ResponseEntity<List<Job>> getJobsByAdminId(@PathVariable Long adminId) {
        try {
            logger.info("Received request to get jobs for Admin ID: {}", adminId);
            List<Job> jobs = jobService.getJobsByAdminId(adminId);
            return new ResponseEntity<>(jobs, HttpStatus.OK);  // Returns 200 status
        } catch (Exception e) {
            logger.error("Error while fetching jobs for Admin ID {}: {}", adminId, e.getMessage(), e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);  // Returns 500 status for server error
        }
    }
}
