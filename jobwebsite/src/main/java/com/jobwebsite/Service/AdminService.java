package com.jobwebsite.Service;


import com.jobwebsite.Entity.Admin;
import com.jobwebsite.Entity.Form;
import com.jobwebsite.Entity.Job;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface AdminService {

    String registerAdmin(Admin admin);
    String loginAdmin(String username, String password);

    @Transactional
    Admin updateAdmin(Long adminId, Admin adminDetails, MultipartFile profilePicture);

    void deleteAdmin(Long adminId);

    String jobpost(Admin admin);
    List<Form> getAllForms();

    Form getFormByFormId(Long formId);

    List<Job> getAllJobsUploadedByAdmin();
}

