package com.jobwebsite.Controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jobwebsite.Entity.Form;
import com.jobwebsite.Repository.FormRepository;
import com.jobwebsite.Repository.InternshipRepository;
import com.jobwebsite.Repository.JobRepository;
import com.jobwebsite.Service.FormService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/forms")
@CrossOrigin("*")
public class FormController {

    private static final Logger logger = LoggerFactory.getLogger(FormController.class);

    @Autowired
    private final FormService formService;

    @Autowired
    private JobRepository jobRepository;

    @Autowired
    private InternshipRepository internshipRepository;

    @Autowired
    private FormRepository formRepository;

    public FormController(FormService formService, JobRepository jobRepository, InternshipRepository internshipRepository, FormRepository formRepository) {
        this.formService = formService;
        this.jobRepository = jobRepository;
        this.internshipRepository = internshipRepository;
        this.formRepository = formRepository;
    }

    // Endpoint to save the form with optional CV file
    @PostMapping("/form/saveForm")
    public ResponseEntity<String> saveForm(@RequestPart("formData") String formData,
                                           @RequestPart(value = "cv", required = false) MultipartFile multipartFile) throws JsonProcessingException {
        logger.info("Received request to save user");

        // Convert formData to Form object
        ObjectMapper objectMapper = new ObjectMapper();
        Form form = objectMapper.readValue(formData, Form.class);

        // Handle CV file
        if (multipartFile != null && !multipartFile.isEmpty()) {
            String fileType = multipartFile.getContentType();
            if ("application/pdf".equals(fileType) || "image/jpeg".equals(fileType) || "image/jpg".equals(fileType)) {
                try {
                    form.setCv(multipartFile.getBytes());
                } catch (IOException e) {
                    logger.error("Error saving CV file: {}", e.getMessage());
                    throw new RuntimeException("Error saving CV file.");
                }
            } else {
                logger.error("Unsupported file type: {}", fileType);
                return ResponseEntity.status(HttpStatus.UNSUPPORTED_MEDIA_TYPE)
                        .body("Only PDF and JPEG/JPG files are allowed.");
            }
        } else {
            form.setCv(null); // No CV file provided
        }

        // Save user data
        String message = formService.saveUser(form);
        logger.info("User successfully saved");
        return ResponseEntity.status(HttpStatus.OK).body("User successfully saved");
    }

    // Endpoint for applying to a job
    @PostMapping("/applyForJobForm/{jobId}")
    public ResponseEntity<String> applyForJob(@RequestPart("formData") String formData,
                                              @RequestPart(value = "cv", required = false) MultipartFile cvFile,
                                              @PathVariable Long jobId) {
        logger.info("Received request to apply for job with ID: {}", jobId);

        try {
            // Process the application
            String response = formService.applyForJob(formData, cvFile, jobId);
            logger.info("Job application submitted successfully.");
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (Exception e) {
            logger.error("Error applying for job: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error applying for job.");
        }
    }

    // Endpoint for applying to an internship
    @PostMapping("/applyForInternshipForm/{internshipId}")
    public ResponseEntity<String> applyForInternship(@RequestPart("formData") String formData,
                                                     @RequestPart(value = "cv", required = false) MultipartFile cvFile,
                                                     @PathVariable Long internshipId) {
        logger.info("Received request to apply for internship with ID: {}", internshipId);

        try {
            // Process the internship application
            String response = formService.applyForInternship(formData, cvFile, internshipId);
            logger.info("Internship application submitted successfully.");
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (Exception e) {
            logger.error("Error applying for internship: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error applying for internship.");
        }
    }

    // Endpoint to download CV of a form submission
    @GetMapping("/downloadCv/{formId}")
    public ResponseEntity<byte[]> downloadCv(@PathVariable Long formId) {
        try {
            Form form = formService.getFormById(formId);  // Get the form by ID
            byte[] cv = form.getCv();  // Get the CV as byte array
            if (cv == null) {
                logger.error("No CV found for form with ID: {}", formId);
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("No CV available for this form.".getBytes());
            }
            logger.info("Returning CV for form with ID: {}", formId);
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"cv.pdf\"")
                    .contentType(MediaType.APPLICATION_PDF)
                    .body(cv);
        } catch (Exception e) {
            logger.error("Error downloading CV: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error downloading CV.".getBytes());
        }
    }

    @GetMapping("/getAllForms")
    public ResponseEntity<List<Form>> getAllForms() {
        logger.info("Received request to fetch all forms.");
        try {
            List<Form> forms = formService.getAllForms();
            if (forms.isEmpty()) {
                logger.warn("No forms found in the database.");
                return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
            }
            logger.info("Returning all forms successfully.");
            return ResponseEntity.ok(forms);
        } catch (Exception e) {
            logger.error("Error fetching all forms: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(null);
        }
    }
}
