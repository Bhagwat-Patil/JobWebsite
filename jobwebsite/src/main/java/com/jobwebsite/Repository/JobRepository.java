package com.jobwebsite.Repository;

//job repository
import com.jobwebsite.Entity.Job;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface JobRepository extends JpaRepository<Job,Long> {
    @Query("SELECT j FROM Job j " +
            "WHERE (:title IS NULL OR j.title = :title) " +
            "AND (:location IS NULL OR j.location = :location) " +
            "AND (:category IS NULL OR j.category = :category) " +
            "AND (:employmentType IS NULL OR j.employmentType = :employmentType) " +
            "AND (:workModel IS NULL OR j.workModel = :workModel)"+
            "AND (:salary IS NULL OR j.salary = :salary)"+
            "AND (:experience IS NULL OR j.experience = :experience)")
    List<Job> findJobsByCriteria(
            @Param("title") String title,
            @Param("location") String location,
            @Param("category") String category,
            @Param("employmentType") String employmentType,
            @Param("workModel") String workModel,
            @Param("salary") String salary,
            @Param("experience") String experience
    );

    List<Job> findByStatus(String status);
    List<Job> findByAdminId(Long adminId);
}

