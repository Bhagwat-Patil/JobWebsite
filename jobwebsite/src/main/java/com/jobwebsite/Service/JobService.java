package com.jobwebsite.Service;

//job service
import com.jobwebsite.Entity.Job;

import java.util.List;

public interface JobService {


    Job saveJob(Job job);

    List<Job> getAllJobs();

    List<Job> searchJobs(String title, String location, String category, String employmentType, String workModel, String salary, String experience);

    Job updateJob(Long id, Job jobDetails);

    void deleteJob(Long id);
}
