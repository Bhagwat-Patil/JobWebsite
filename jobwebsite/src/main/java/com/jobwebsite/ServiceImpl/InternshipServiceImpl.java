package com.jobwebsite.ServiceImpl;

import com.jobwebsite.Entity.Admin;
import com.jobwebsite.Entity.Internship;
import com.jobwebsite.Repository.AdminRepository;
import com.jobwebsite.Repository.InternshipRepository;
import com.jobwebsite.Service.InternshipService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class InternshipServiceImpl implements InternshipService {

    private static final Logger logger = LoggerFactory.getLogger(InternshipServiceImpl.class);

    @Autowired
    private InternshipRepository internshipRepository;

    @Autowired
    private AdminRepository adminRepository;

    @Override
    public Internship createInternship(Internship internship, Long adminId) {
        try {
            logger.info("Attempting to create internship for Admin ID: {}", adminId);
            Optional<Admin> adminOptional = adminRepository.findById(adminId);
            if (adminOptional.isPresent()) {
                Admin admin = adminOptional.get();
                internship.setAdmin(admin);
                logger.info("Internship created successfully for Admin ID: {}", adminId);
                return internshipRepository.save(internship);
            } else {
                String errorMessage = "Admin with ID " + adminId + " not found.";
                logger.error(errorMessage);
                throw new RuntimeException(errorMessage);
            }
        } catch (Exception e) {
            logger.error("Error while creating internship: {}", e.getMessage(), e);
            throw e;
        }
    }

    @Override
    public List<Internship> getAllInternships() {
        try {
            logger.info("Fetching all internships.");
            return internshipRepository.findAll();
        } catch (Exception e) {
            logger.error("Error while fetching internships: {}", e.getMessage(), e);
            throw e;
        }
    }

    @Override
    public Internship getInternshipById(Long id) {
        try {
            logger.info("Fetching internship with ID: {}", id);
            return internshipRepository.findById(id)
                    .orElseThrow(() -> {
                        String errorMessage = "Internship with ID " + id + " not found.";
                        logger.error(errorMessage);
                        return new RuntimeException(errorMessage);
                    });
        } catch (Exception e) {
            logger.error("Error while fetching internship with ID {}: {}", id, e.getMessage(), e);
            throw e;
        }
    }

    @Override
    public void deleteInternship(Long id) {
        try {
            logger.info("Attempting to delete internship with ID: {}", id);
            if (internshipRepository.existsById(id)) {
                internshipRepository.deleteById(id);
                logger.info("Internship with ID {} deleted successfully.", id);
            } else {
                String errorMessage = "Internship with ID " + id + " not found.";
                logger.error(errorMessage);
                throw new RuntimeException(errorMessage);
            }
        } catch (Exception e) {
            logger.error("Error while deleting internship with ID {}: {}", id, e.getMessage(), e);
            throw e;
        }
    }

    @Override
    public Internship updateInternship(Long id, Internship updatedInternship) {
        try {
            logger.info("Attempting to update internship with ID: {}", id);
            Optional<Internship> existingInternshipOptional = internshipRepository.findById(id);
            if (existingInternshipOptional.isPresent()) {
                Internship existingInternship = existingInternshipOptional.get();
                existingInternship.setTitle(updatedInternship.getTitle());
                existingInternship.setCompany(updatedInternship.getCompany());
                existingInternship.setLocation(updatedInternship.getLocation());
                existingInternship.setDuration(updatedInternship.getDuration());
                existingInternship.setStipend(updatedInternship.getStipend());
                existingInternship.setQualifications(updatedInternship.getQualifications());
                existingInternship.setStatus(updatedInternship.getStatus());
                existingInternship.setSkills(updatedInternship.getSkills());
                existingInternship.setDescription(updatedInternship.getDescription());
                logger.info("Internship with ID {} updated successfully.", id);
                return internshipRepository.save(existingInternship);
            } else {
                String errorMessage = "Internship with ID " + id + " not found.";
                logger.error(errorMessage);
                throw new RuntimeException(errorMessage);
            }
        } catch (Exception e) {
            logger.error("Error while updating internship with ID {}: {}", id, e.getMessage(), e);
            throw e;
        }
    }

    @Override
    public List<Internship> getInternshipsByStatus(String status) {
        try {
            logger.info("Fetching internships with status: {}", status);
            return internshipRepository.findByStatus(status);
        } catch (Exception e) {
            logger.error("Error while fetching internships with status {}: {}", status, e.getMessage(), e);
            throw e;
        }
    }

    @Override
    public List<Internship> getInternshipsByAdminId(Long adminId) {
        try {
            logger.info("Fetching internships for Admin ID: {}", adminId);
            return internshipRepository.findByAdminId(adminId);
        } catch (Exception e) {
            logger.error("Error while fetching internships for Admin ID {}: {}", adminId, e.getMessage(), e);
            throw e;
        }
    }

}
