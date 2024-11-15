package com.jobwebsite.Entity;// class form

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor

// Used to Apply JOb which will upload by Admin
public class Form {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String firstName;
    private String lastName;
    private String email;
    private String country;
    private Long mobileNumber;
    private String location;
    @Lob
    private byte[] cv;

    @ManyToOne(fetch =FetchType.LAZY )
    @JoinColumn(name = "admin_id",nullable = false)
    @JsonBackReference
    private Admin admin;
}
