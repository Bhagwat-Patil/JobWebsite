package com.jobwebsite.Entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Plan {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String description;
    private Double price;
    private String duration;
    private List<String> features;

    @OneToMany(mappedBy = "plan", cascade = CascadeType.ALL)
    @JsonManagedReference(value = "plan-payments")
    private List<Payment> payments;

}