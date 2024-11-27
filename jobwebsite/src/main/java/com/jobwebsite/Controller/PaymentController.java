package com.jobwebsite.Controller;

import com.jobwebsite.CommonUtil.PaymentUtils;
import com.jobwebsite.Entity.Payment;
import com.jobwebsite.Service.PaymentService;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/payment")
@CrossOrigin("*")
public class PaymentController {

    // Logger for logging messages
    private static final Logger logger = LoggerFactory.getLogger(PaymentController.class);

    @Autowired
    private PaymentService paymentService;

    @PostMapping("/createOrder")
    public ResponseEntity<?> createOrder(@RequestParam Long userId, @RequestParam String planName) {
        try {
            JSONObject order = paymentService.createOrder(userId, planName);
            return ResponseEntity.ok(order.toMap()); // Converts JSONObject to a Map for JSON serialization
        } catch (Exception e) {
            logger.error("Error occurred while creating order", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }


    @PostMapping("/verifyPayment")
    public ResponseEntity<Payment> verifyPayment(@RequestBody Map<String, String> paymentDetails) {
        logger.info("Received request to verify payment with details: {}", paymentDetails);
        try {
            // Get the payment details from the request body
            String razorpayPaymentId = paymentDetails.get("razorpayPaymentId");
            String razorpayOrderId = paymentDetails.get("razorpayOrderId");
            String razorpaySignature = paymentDetails.get("razorpaySignature");

            // Call the service to verify the payment and update payment status
            Payment updatedPayment = paymentService.verifyPayment(razorpayPaymentId, razorpayOrderId, razorpaySignature);
            logger.info("Payment verified successfully for Razorpay orderId: {} and paymentId: {}", razorpayOrderId, razorpayPaymentId);
            return ResponseEntity.ok(updatedPayment);
        } catch (Exception e) {
            logger.error("Error occurred while verifying payment with Razorpay orderId: {}: {}", paymentDetails.get("razorpayOrderId"), e.getMessage(), e);
            // Return an error response if verification fails
            return ResponseEntity.status(500).body(null);
        }
    }

    @GetMapping("/paymentsByPlanId")
    public ResponseEntity<List<Payment>> getPaymentsByPlanId(@RequestParam Long planId) {
        logger.info("Received request to fetch payments for planId: {}", planId);
        try {
            List<Payment> payments = paymentService.getPaymentsByPlanId(planId);
            return ResponseEntity.ok(payments);
        } catch (Exception e) {
            logger.error("Error occurred while fetching payments for planId: {}: {}", planId, e.getMessage(), e);
            return ResponseEntity.status(500).body(null);
        }
    }

    @PostMapping("/verifyMockPaymentTest")
    public ResponseEntity<Payment> verifyMockPaymentTest(@RequestBody Map<String, String> paymentDetails) {
        try {
            String razorpayOrderId = paymentDetails.get("razorpayOrderId");

            // Generate mock data for testing
            String mockPaymentId = "mockPaymentId";
            String generatedSignature = PaymentUtils.generateMockSignature(razorpayOrderId, mockPaymentId);

            // Override the request body with generated mock data
            paymentDetails.put("razorpayPaymentId", mockPaymentId);
            paymentDetails.put("razorpaySignature", generatedSignature);

            // Proceed with normal verification logic
            return ResponseEntity.ok(paymentService.verifyPayment(
                    paymentDetails.get("razorpayPaymentId"),
                    paymentDetails.get("razorpayOrderId"),
                    paymentDetails.get("razorpaySignature")
            ));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(null);
        }
    }

    @GetMapping("/getAllPayments")
    public ResponseEntity<?> getAllPayments() {
        logger.info("Request received for fetching all payments");
        try {
            List<Payment> payments = paymentService.getAllPayments();
            logger.info("Successfully fetched all payments, total count: {}", payments.size());
            return ResponseEntity.ok(payments);
        } catch (Exception ex) {
            logger.error("Error occurred while fetching payments: {}", ex.getMessage());
            return ResponseEntity.status(500).body("An error occurred while retrieving payments. Please try again later.");
        }
    }

    @GetMapping("/getPaymentByStatus/{status}")
    public ResponseEntity<?> getPaymentsByStatus(@PathVariable("status") String status) {
        logger.info("Request received to fetch payments with status: {}", status);
        try {
            List<Payment> payments = paymentService.getPaymentsByStatus(status);
            if (payments.isEmpty()) {
                logger.warn("No payments found with status: {}", status);
                return ResponseEntity.ok("No payments found with the given status.");
            }
            logger.info("Successfully fetched payments with status: {}", status);
            return ResponseEntity.ok(payments);
        } catch (Exception ex) {
            logger.error("Error occurred while fetching payments by status: {}", ex.getMessage());
            return ResponseEntity.status(500).body("An error occurred while retrieving payments. Please try again later.");
        }
    }

}
