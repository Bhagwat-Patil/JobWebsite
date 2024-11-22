package com.jobwebsite.Service;

//job service
import com.jobwebsite.Entity.Job;

import java.time.LocalDate;
import java.util.List;

public interface JobService {

    Job saveJob(Job job, Long adminId);

    List<Job> getAllJobs();

    List<Job> searchJobs(
            String title,
            String location,
            String category,
            String employmentType,
            String workModel,
            String salary,
            String experience,
            LocalDate openingStartDate,
            LocalDate lastApplyDate
    );
    Job updateJob(Long id, Job jobDetails);
    void deleteJob(Long id);
    Job getJobById(Long id);
    List<Job> getJobsByStatus(String status);
    List<Job> getJobsByAdminId(Long adminId);
}
