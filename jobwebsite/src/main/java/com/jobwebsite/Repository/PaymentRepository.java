package com.jobwebsite.Repository;

import com.jobwebsite.Entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
    Payment findByRazorpayOrderId(String razorpayOrderId);

    // Custom query to fetch payments by planId with JOIN FETCH for related entities
    @Query("SELECT p FROM Payment p JOIN FETCH p.user u JOIN FETCH p.plan pl WHERE p.plan.id = :planId")
    List<Payment> findPaymentsByPlanId(@Param("planId") Long planId);
}