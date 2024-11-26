package com.jobwebsite.Entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import org.antlr.v4.runtime.misc.NotNull;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Column(unique = true)
    private String userName;

    @NotNull
    private String fullName;

    @NotNull
    @Column(unique = true)
    private String emailId;

    @NotNull
    private String password;

    @Transient
    private String confirmPassword;

    private String gender;

    @NotNull
    @Column(unique = true)
    private String mobileNo;

    @Column(nullable = true)
    private String status;

    @ManyToOne
    @JoinColumn(name = "plan_id")
    @JsonBackReference(value = "user-plan")  // This is the back-reference for Plan
    private Plan plan;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    @JsonManagedReference(value = "user-payments")  // This is the forward reference for Payments
    private List<Payment> payments;
}