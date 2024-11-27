package com.jobwebsite.Service;

import com.jobwebsite.Entity.Payment;
import org.json.JSONObject;

import java.util.List;

public interface PaymentService {
    JSONObject createOrder(Long userId, String planName) throws Exception;
    Payment verifyPayment(String razorpayPaymentId, String razorpayOrderId, String razorpaySignature) throws Exception;
    List<Payment> getPaymentsByPlanId(Long planId);
    List<Payment> getAllPayments();
    List<Payment> getPaymentsByStatus(String status);
}