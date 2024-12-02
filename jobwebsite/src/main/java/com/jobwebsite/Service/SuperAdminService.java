package com.jobwebsite.Service;

import com.jobwebsite.Entity.Admin;
import com.jobwebsite.Entity.PendingPost;
import jakarta.transaction.Transactional;

import java.util.List;

public interface SuperAdminService {
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
