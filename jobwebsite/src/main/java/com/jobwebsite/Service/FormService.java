package com.jobwebsite.Service;

import com.jobwebsite.Entity.Form;
import org.springframework.web.multipart.MultipartFile;

public interface FormService {

    String applyForJob(String formData, MultipartFile cvFile, Long jobId);

    String applyForInternship(String formData, MultipartFile cvFile, Long internshipId);

    String saveUser(Form form);

    Form getFormById(Long id);
}
