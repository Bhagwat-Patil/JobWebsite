package com.jobwebsite.ServiceImpl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jobwebsite.Entity.Form;
import com.jobwebsite.Entity.Job;
import com.jobwebsite.Entity.Internship;
import com.jobwebsite.Entity.Admin;
import com.jobwebsite.Repository.FormRepository;
import com.jobwebsite.Repository.JobRepository;
import com.jobwebsite.Repository.InternshipRepository;
import com.jobwebsite.Repository.AdminRepository;
import com.jobwebsite.Service.FormService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.util.List;

@Service
public class FormServiceImpl implements FormService {

    private static final Logger logger = LoggerFactory.getLogger(FormServiceImpl.class);

    private final FormRepository formRepository;
    private final JobRepository jobRepository;
    private final InternshipRepository internshipRepository;
    private final AdminRepository adminRepository;

    @Autowired
    public FormServiceImpl(FormRepository formRepository, JobRepository jobRepository, InternshipRepository internshipRepository, AdminRepository adminRepository) {
        this.formRepository = formRepository;
        this.jobRepository = jobRepository;
        this.internshipRepository = internshipRepository;
        this.adminRepository = adminRepository;
    }

    @Override
    public String saveUser(Form form) {
        try {
            logger.info("Attempting to save form for user: {}", form);
            formRepository.save(form);
            return "User data saved successfully.";
        } catch (DataAccessException ex) {
            logger.error("Error saving user data: {}", ex.getMessage());
            throw new RuntimeException("Unable to save user data. Please try again.");
        }
    }

    @Override
    public String applyForJob(String formData, MultipartFile cvFile, Long jobId) {
        try {
            logger.info("User applying for job with ID: {}", jobId);

            // Convert formData to Form object
            Form form = new ObjectMapper().readValue(formData, Form.class);

            // Handle CV file if provided
            if (cvFile != null && !cvFile.isEmpty()) {
                String fileType = cvFile.getContentType();
                // Validate file type (PDF, JPEG/JPG)
                if ("application/pdf".equals(fileType) || "image/jpeg".equals(fileType) || "image/jpg".equals(fileType)) {
                    // Convert file to byte[] and set to form
                    form.setCv(cvFile.getBytes());
                } else {
                    return "Only PDF and JPEG/JPG files are allowed.";
                }
            }

            // Fetch Job and associate with the form
            Job job = jobRepository.findById(jobId)
                    .orElseThrow(() -> new RuntimeException("Job not found"));
            form.setJob(job);

            // Fetch Admin and associate (Assume the admin is fetched from session or context)
            Admin admin = adminRepository.findById(1L) // example: this should be dynamically fetched
                    .orElseThrow(() -> new RuntimeException("Admin not found"));
            form.setAdmin(admin);

            // Save form data
            formRepository.save(form);

            return "Application submitted successfully.";
        } catch (IOException e) {
            logger.error("Error reading form data or handling file: {}", e.getMessage());
            throw new RuntimeException("Error applying for job.");
        } catch (Exception e) {
            logger.error("Error applying for job: {}", e.getMessage());
            throw new RuntimeException("Error applying for job.");
        }
    }

    @Override
    public String applyForInternship(String formData, MultipartFile cvFile, Long internshipId) {
        try {
            logger.info("User applying for internship with ID: {}", internshipId);

            // Convert formData to Form object
            Form form = new ObjectMapper().readValue(formData, Form.class);

            // Handle CV file if provided
            if (cvFile != null && !cvFile.isEmpty()) {
                String fileType = cvFile.getContentType();
                // Validate file type (PDF, JPEG/JPG)
                if ("application/pdf".equals(fileType) || "image/jpeg".equals(fileType) || "image/jpg".equals(fileType)) {
                    // Convert file to byte[] and set to form
                    form.setCv(cvFile.getBytes());
                } else {
                    return "Only PDF and JPEG/JPG files are allowed.";
                }
            }

            // Fetch Internship and associate with the form
            Internship internship = internshipRepository.findById(internshipId)
                    .orElseThrow(() -> new RuntimeException("Internship not found"));
            form.setInternship(internship);

            // Fetch Admin and associate (Assume the admin is fetched from session or context)
            Admin admin = adminRepository.findById(1L) // example: this should be dynamically fetched
                    .orElseThrow(() -> new RuntimeException("Admin not found"));
            form.setAdmin(admin);

            // Save form data
            formRepository.save(form);

            return "Application submitted successfully.";
        } catch (IOException e) {
            logger.error("Error reading form data or handling file: {}", e.getMessage());
            throw new RuntimeException("Error applying for internship.");
        } catch (Exception e) {
            logger.error("Error applying for internship: {}", e.getMessage());
            throw new RuntimeException("Error applying for internship.");
        }
    }

    @Override
    public Form getFormById(Long id) {
        // Fetch the form from the database using the repository
        return formRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Form not found with ID: " + id));  // If not found, throw an exception
    }

    @Override
    public List<Form> getAllForms() {
        try {
            logger.info("Fetching all forms from the database.");
            return formRepository.findAll();
        } catch (DataAccessException ex) {
            logger.error("Error fetching all forms: {}", ex.getMessage());
            throw new RuntimeException("Unable to fetch forms. Please try again.");
        }
    }
}
