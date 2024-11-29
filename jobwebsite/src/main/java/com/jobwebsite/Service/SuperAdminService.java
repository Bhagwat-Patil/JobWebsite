package com.jobwebsite.Service;

import com.jobwebsite.Entity.Admin;
import java.util.List;

public interface SuperAdminService {
    String approveAdmin(Long adminId);
    Admin disableAdmin(Long adminId);
    List<Admin> getAllAdmins();
}
