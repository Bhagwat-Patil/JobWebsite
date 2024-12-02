package com.jobwebsite.Entity;//class job


import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Job {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;
    private String location;
    private String category;
    private String employmentType;
    private String workModel;
    private String experience;
    private Double salary;
    private String skills;

    @Column(nullable = false)
    private String company;

    @Lob
    private String jobDescription;

    @Column(nullable = false)
    private String status;

    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate openingStartDate;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate lastApplyDate;

    private Integer numberOfOpenings;
    private String perks;

    @Lob
    private String companyDescription;

    @ManyToOne
    @JoinColumn(name = "admin_id")
    @JsonBackReference("admin-jobs")
    private Admin admin;

}

