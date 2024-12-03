package com.jobwebsite.Repository;

//job repository
import com.jobwebsite.Entity.Job;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface JobRepository extends JpaRepository<Job,Long> {
    // Enhanced query for dynamic filtering with flexible criteria
    @Query("SELECT j FROM Job j " +
            "WHERE " +
            "(COALESCE(:title, '') = '' OR LOWER(j.title) LIKE LOWER(CONCAT('%', :title, '%'))) AND " +
            "(COALESCE(:location, '') = '' OR LOWER(j.location) LIKE LOWER(CONCAT('%', :location, '%'))) AND " +
            "(COALESCE(:category, '') = '' OR LOWER(j.category) LIKE LOWER(CONCAT('%', :category, '%'))) AND " +
            "(COALESCE(:employmentType, '') = '' OR LOWER(j.employmentType) LIKE LOWER(CONCAT('%', :employmentType, '%'))) AND " +
            "(COALESCE(:workModel, '') = '' OR LOWER(j.workModel) LIKE LOWER(CONCAT('%', :workModel, '%'))) AND " +
            "(COALESCE(:salary, '') = '' OR j.salary = :salary) AND " +
            "(COALESCE(:experience, '') = '' OR j.experience = :experience) AND " +
            "(COALESCE(:openingStartDate, NULL) IS NULL OR j.openingStartDate >= :openingStartDate) AND " +
            "(COALESCE(:lastApplyDate, NULL) IS NULL OR j.lastApplyDate <= :lastApplyDate)")
    List<Job> findJobsByCriteria(
            @Param("title") String title,
            @Param("location") String location,
            @Param("category") String category,
            @Param("employmentType") String employmentType,
            @Param("workModel") String workModel,
            @Param("salary") String salary,
            @Param("experience") String experience,
            @Param("openingStartDate") LocalDate openingStartDate,
            @Param("lastApplyDate") LocalDate lastApplyDate
    );

    List<Job> findByStatus(String status);
    List<Job> findByAdminId(Long adminId);

    @Modifying
    @Transactional
    @Query("DELETE FROM Job j WHERE j.id = :postId")
    void deleteById(@Param("postId") Long postId);

}

