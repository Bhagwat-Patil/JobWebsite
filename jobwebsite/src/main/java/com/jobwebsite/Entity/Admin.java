package com.jobwebsite.Entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Admin {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @Column(unique = true)
    private String mobileNo;

    @Column(nullable = false, unique = true)
    private String username;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false, unique = true)
    private String email;

    private boolean approved;

    private boolean enabled = true;

    @OneToMany(mappedBy ="admin" ,cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonManagedReference("admin-forms")
    private List<Form> form;

    @OneToMany(mappedBy ="admin" ,cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JsonManagedReference("admin-jobs")
    private List<Job> job;

    @OneToMany(mappedBy = "admin")
    @JsonManagedReference  // Serialize this side of the relationship
    private List<Internship> internships;
}
