package com.jobwebsite.Controller;

//job controller
import com.jobwebsite.Entity.Job;
import com.jobwebsite.Service.JobService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import org.springframework.web.bind.annotation.*;
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

    // Save a job
    @PostMapping("/job/saveJobs")
    public ResponseEntity<Job> saveJob(@RequestBody Job job) {
        logger.info("Received request to save job");
        Job savedJob = jobService.saveJob(job);
        return new ResponseEntity<>(savedJob, HttpStatus.CREATED);  // Returns 201 status for a created resource
    }

    // Get all jobs
    @GetMapping("/job/getAllJobs")
    public ResponseEntity<List<Job>> getAllJobs() {
        logger.info("Received request to get all jobs");
        List<Job> jobs = jobService.getAllJobs();
        return new ResponseEntity<>(jobs, HttpStatus.OK);  // Returns 200 status
    }

    //Get jobs by one or more sorting
    @GetMapping("/search")
    public List<Job> searchJobs(
            @RequestParam(required = false) String title,
            @RequestParam(required = false) String location,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String employmentType,
            @RequestParam(required = false) String workModel,
            @RequestParam(required = false) String salary,
            @RequestParam(required = false) String experience)
    {
        return jobService.searchJobs(title, location, category, employmentType, workModel,salary, experience);
    }

    // Update a job
    @PutMapping("/job/updateJob/{id}")
    public ResponseEntity<Job> updateJob(@PathVariable Long id, @RequestBody Job jobDetails) {
        logger.info("Received request to update job with id: {}", id);
        Job updatedJob = jobService.updateJob(id, jobDetails);
        return new ResponseEntity<>(updatedJob, HttpStatus.OK);  // Returns 200 status
    }

    // Delete a job
    @DeleteMapping("/job/deleteJobById/{id}")
    public ResponseEntity<String> deleteJob(@PathVariable Long id) {
        logger.info("Received request to delete job with id: {}", id);
        jobService.deleteJob(id);
        return new ResponseEntity<>("Job deleted successfully", HttpStatus.OK);  // Returns 200 status
    }
}



