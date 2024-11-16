package com.jobwebsite.Repository;

import com.jobwebsite.Entity.Internship;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface InternshipRepository extends JpaRepository<Internship, Long> {
    List<Internship> findByStatus(String status);
    @Query("SELECT i FROM Internship i WHERE i.admin.id = :adminId")
    List<Internship> findByAdminId(@Param("adminId") Long adminId);

}
