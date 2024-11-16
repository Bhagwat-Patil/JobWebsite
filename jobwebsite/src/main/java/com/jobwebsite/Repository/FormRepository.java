package com.jobwebsite.Repository;

//form repository
import com.jobwebsite.Entity.Form;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FormRepository extends JpaRepository<Form,Long> {

    List<Form> findByJobId(Long jobId);
    List<Form> findByInternshipId(Long internshipId);
}


