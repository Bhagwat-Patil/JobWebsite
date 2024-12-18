package com.jobwebsite.Service;

import com.jobwebsite.Entity.Admin;
import com.jobwebsite.Entity.PendingPost;
import com.jobwebsite.Entity.SuperAdmin;
import jakarta.transaction.Transactional;

import java.util.List;

public interface SuperAdminService {
    SuperAdmin registerSuperAdmin(SuperAdmin superAdmin);

    SuperAdmin loginSuperAdmin(String username, String password);

    @Transactional
    SuperAdmin updateSuperAdmin(Long id, SuperAdmin updatedDetails);

    @Transactional
    void deleteSuperAdmin(Long id);

    void approveAdmin(Long adminId);
    Admin disableAdmin(Long adminId);

    @Transactional
    String approveOrDisapprovePost(Long pendingPostId, boolean isApproved);

    List<PendingPost> getAllPendingPosts();

    List<Admin> getAllAdmins();

    List<Admin> getAllAdminNotApproved();

    List<Admin> getAllAdminApproved();

    List<Admin> getAllAdminDisabled();

    List<Admin> getAllAdminEnabled();
}
