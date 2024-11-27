package com.jobwebsite.Entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import java.time.LocalDateTime;
import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Payment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String razorpayPaymentId;
    private String razorpayOrderId;
    private Double amount;
    private String currency;
    private String paymentStatus;
    private Date paymentDate;

    @CreationTimestamp
    private LocalDateTime createdAt;

    @ManyToOne
    @JoinColumn(name = "user_id")
    @JsonBackReference(value = "user-payments")
    private User user;

    @ManyToOne
    @JoinColumn(name = "plan_id")
    @JsonBackReference(value = "plan-payments")
    private Plan plan;

}