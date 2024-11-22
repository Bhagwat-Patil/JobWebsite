package com.jobwebsite.ServiceImpl;

import com.jobwebsite.Entity.Admin;
import com.jobwebsite.Entity.Job;
import com.jobwebsite.Exception.InvalidJobDataException;
import com.jobwebsite.Exception.JobNotFoundException;
import com.jobwebsite.Repository.AdminRepository;
import com.jobwebsite.Repository.JobRepository;
import com.jobwebsite.Service.JobService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class JobServiceImpl implements JobService {

    private static final Logger logger = LoggerFactory.getLogger(JobService.class);

    @Autowired
    private final JobRepository jobRepository;

    @Autowired
    private final AdminRepository adminRepository;

    public JobServiceImpl(JobRepository jobRepository, AdminRepository adminRepository) {
        this.jobRepository = jobRepository;
        this.adminRepository = adminRepository;
    }


    @Override
    public Job saveJob(Job job, Long adminId) {
        try {
            // Validation checks for Job data
            if (job.getTitle() == null || job.getTitle().isEmpty()) {
                logger.error("Invalid job data: Title cannot be empty");
                throw new InvalidJobDataException("Job title cannot be empty");
            }
            if (job.getOpeningStartDate() == null) {
                logger.error("Invalid job data: Opening start date is required");
                throw new InvalidJobDataException("Opening start date is required");
            }
            if (job.getLastApplyDate() == null) {
                logger.error("Invalid job data: Last apply date is required");
                throw new InvalidJobDataException("Last apply date is required");
            }
            if (job.getNumberOfOpenings() == null || job.getNumberOfOpenings() <= 0) {
                logger.error("Invalid job data: Number of openings must be greater than zero");
                throw new InvalidJobDataException("Number of openings must be greater than zero");
            }

            // Fetch the Admin entity using adminId
            Admin admin = adminRepository.findById(adminId).orElseThrow(() ->
                    new InvalidJobDataException("Admin not found with id: " + adminId));

            // Set the Admin entity to the Job
            job.setAdmin(admin);

            // Save job with the provided adminId
            logger.info("Saving job: {} by Admin ID: {}", job, adminId);
            return jobRepository.save(job);

        } catch (InvalidJobDataException e) {
            // Handling validation-specific exceptions
            logger.error("Validation failed while saving job: {}", e.getMessage());
            return null; // Return a meaningful response or rethrow as RuntimeException if needed
        } catch (Exception e) {
            // Handling generic exceptions
            logger.error("Failed to save job due to unexpected error: {}", e.getMessage(), e);
            throw new RuntimeException("An unexpected error occurred while saving the job", e);
        }
}


    @Override
    public List<Job> getAllJobs() {
        try {
            logger.info("Retrieving all jobs");
            return jobRepository.findAll();
        } catch (Exception e) {
            logger.error("Failed to retrieve all jobs", e);
            throw e;
        }
    }

    @Override
    public List<Job> searchJobs(
            String title,
            String location,
            String category,
            String employmentType,
            String workModel,
            String salary,
            String experience,
            LocalDate openingStartDate,
            LocalDate lastApplyDate
    ) {
        return jobRepository.findJobsByCriteria(
                title,
                location,
                category,
                employmentType,
                workModel,
                salary,
                experience,
                openingStartDate,
                lastApplyDate
        );
    }


    @Override
    public Job updateJob(Long id, Job jobDetails) {
        try {
            Job job = jobRepository.findById(id)
                    .orElseThrow(() -> new JobNotFoundException(id));

            job.setTitle(jobDetails.getTitle());
            job.setLocation(jobDetails.getLocation());
            job.setExperience(jobDetails.getExperience());
            job.setSalary(jobDetails.getSalary());
            job.setCategory(jobDetails.getCategory());
            job.setEmploymentType(jobDetails.getEmploymentType());
            job.setWorkModel(jobDetails.getWorkModel());
            job.setStatus(jobDetails.getStatus());
            job.setCompany(jobDetails.getCompany());
            job.setSkills(jobDetails.getSkills());
            job.setJobDescription(jobDetails.getJobDescription());
            job.setUpdatedAt(jobDetails.getUpdatedAt());
            job.setOpeningStartDate(jobDetails.getOpeningStartDate());
            job.setLastApplyDate(jobDetails.getLastApplyDate());
            job.setNumberOfOpenings(jobDetails.getNumberOfOpenings());
            job.setPerks(jobDetails.getPerks());
            job.setCompanyDescription(jobDetails.getCompanyDescription());
            logger.info("Updating job with id: {}", id);
            return jobRepository.save(job);
        } catch (Exception e) {
            logger.error("Failed to update job with id: {}", id, e);
            throw e;
        }
    }

    @Override
    public void deleteJob(Long id) {
        try {
            Job job = jobRepository.findById(id)
                    .orElseThrow(() -> new JobNotFoundException(id));

            logger.info("Deleting job with id: {}", id);
            jobRepository.delete(job);
        } catch (JobNotFoundException e) {
            logger.warn("Job not found with id: {}", id);
            throw e; // Specific exception for "not found"
        }catch (Exception e) {
            logger.error("Failed to delete job with id: {}", id, e);
            throw e;
        }
    }

    @Override
    public Job getJobById(Long id) {
        logger.info("Fetching job with ID: {}", id);
        Optional<Job> job = jobRepository.findById(id);
        if (job.isPresent()) {
            return job.get();
        } else {
            logger.error("Job with ID {} not found.", id);
            throw new JobNotFoundException("Job with ID " + id + " not found.");
        }
    }

    @Override
    public List<Job> getJobsByStatus(String status) {
        logger.info("Fetching jobs with status: {}", status);
        List<Job> jobs = jobRepository.findByStatus(status);
        if (jobs.isEmpty()) {
            logger.warn("No jobs found with status: {}", status);
            throw new JobNotFoundException("No jobs found with status " + status);
        }
        return jobs;
    }

    @Override
    public List<Job> getJobsByAdminId(Long adminId) {
        logger.info("Fetching jobs for Admin ID: {}", adminId);
        List<Job> jobs = jobRepository.findByAdminId(adminId);
        if (jobs.isEmpty()) {
            logger.warn("No jobs found for Admin ID: {}", adminId);
            throw new JobNotFoundException("No jobs found for Admin ID " + adminId);
        }
        return jobs;
    }
}

