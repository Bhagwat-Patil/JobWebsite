package com.jobwebsite.Service;


import com.jobwebsite.Entity.Admin;
import com.jobwebsite.Entity.Form;
import com.jobwebsite.Entity.Internship;
import com.jobwebsite.Entity.Job;
import com.jobwebsite.Exception.AdminNotFoundException;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface AdminService {

    Admin registerAdmin(Admin admin);

    Admin loginAdmin(String username, String password);

    @Transactional
    Admin updateAdmin(Long adminId, Admin adminDetails, MultipartFile profilePicture);

    void deleteAdmin(Long adminId);
    List<Form> getAllForms();
    Form getFormByFormId(Long formId);
    List<Job> getAllJobsUploadedByAdmin();
    List<Admin> getAllAdmins();
    Admin getAdminById(Long adminId);

    @Transactional
    String jobpost(Job job, Long adminId);

    String postInternship(Internship internship, Long adminId);

}

