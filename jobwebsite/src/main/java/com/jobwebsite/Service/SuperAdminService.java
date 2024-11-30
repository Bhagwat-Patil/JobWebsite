package com.jobwebsite.Service;

import com.jobwebsite.Entity.Admin;
import java.util.List;

public interface SuperAdminService {
    void approveAdmin(Long adminId);
    Admin disableAdmin(Long adminId);
    List<Admin> getAllAdmins();
}
