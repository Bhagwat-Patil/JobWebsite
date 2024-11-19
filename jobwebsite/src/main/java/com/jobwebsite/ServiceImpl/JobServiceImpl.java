package com.jobwebsite.ServiceImpl;

import com.jobwebsite.Entity.Job;
import com.jobwebsite.Exception.InvalidJobDataException;
import com.jobwebsite.Exception.JobNotFoundException;
import com.jobwebsite.Repository.JobRepository;
import com.jobwebsite.Service.JobService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.List;
import java.util.Optional;

@Service
public class JobServiceImpl implements JobService {

    private static final Logger logger = LoggerFactory.getLogger(JobService.class);

    @Autowired
    private final JobRepository jobRepository;

    public JobServiceImpl(JobRepository jobRepository) {
        this.jobRepository = jobRepository;
    }

    @Override
    public Job saveJob(Job job) {
        try {
            if (job.getTitle() == null || job.getTitle().isEmpty()) {
                logger.error("Invalid job data: Title cannot be empty");
                throw new InvalidJobDataException("Job title cannot be empty");
            }
            logger.info("Saving job: {}", job);
            return jobRepository.save(job);
        } catch (Exception e) {
            logger.error("Failed to save job: {}", job, e);
            throw e;
        } catch (InvalidJobDataException e) {
            throw new RuntimeException(e);
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
    public List<Job> searchJobs(String title, String location, String category, String employmentType, String workModel, String salary, String experience) {
        return jobRepository.findJobsByCriteria(title, location, category, employmentType, workModel, salary, experience);
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

