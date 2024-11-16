package com.jobwebsite.Service;

import com.jobwebsite.Entity.Internship;

import java.util.List;

public interface InternshipService {
    Internship createInternship(Internship internship, Long adminId);
    List<Internship> getAllInternships();
    Internship getInternshipById(Long id);
    void deleteInternship(Long id);
    Internship updateInternship(Long id, Internship updatedInternship);
    List<Internship> getInternshipsByStatus(String status);
    List<Internship> getInternshipsByAdminId(Long adminId);

}
