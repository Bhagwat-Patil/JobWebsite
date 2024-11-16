package com.jobwebsite.Entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import org.antlr.v4.runtime.misc.NotNull;

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

    @Column(nullable = false)
    private String status;
}
