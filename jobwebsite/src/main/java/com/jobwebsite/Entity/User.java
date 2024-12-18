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

    @Column(name = "email_id", nullable = false, unique = true)
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
    @JsonBackReference(value = "user-plan")
    private Plan plan;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    @JsonManagedReference(value = "user-payments")
    private List<Payment> payments;

    @OneToOne
    @Transient
    private ForgotPasswordOtp forgotPasswordOtp;
}