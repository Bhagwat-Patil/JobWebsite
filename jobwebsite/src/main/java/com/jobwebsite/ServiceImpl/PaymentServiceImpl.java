package com.jobwebsite.ServiceImpl;

import com.jobwebsite.Entity.Payment;
import com.jobwebsite.Entity.Plan;
import com.jobwebsite.Entity.User;
import com.jobwebsite.Repository.PaymentRepository;
import com.jobwebsite.Repository.PlanRepository;
import com.jobwebsite.Repository.UserRepository;
import com.jobwebsite.Service.PaymentService;
import com.razorpay.Order;
import com.razorpay.RazorpayClient;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.time.ZoneId;
import java.util.*;

@Service
public class PaymentServiceImpl implements PaymentService {

    private static final Logger logger = LoggerFactory.getLogger(PaymentServiceImpl.class);

    private static final String RAZORPAY_KEY_ID = "rzp_test_rPVpoELQpw9Mm5";
    private static final String RAZORPAY_KEY_SECRET = "pN79jJ1oP4WrQYc5biDOqXbD";
    private static final String CURRENCY = "INR";
    private static final ZoneId TIME_ZONE = ZoneId.of("Asia/Kolkata");

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PlanRepository planRepository;

    @Autowired
    private PaymentRepository paymentRepository;

    private RazorpayClient razorpayClient;

    public PaymentServiceImpl() throws Exception {
        this.razorpayClient = new RazorpayClient(RAZORPAY_KEY_ID, RAZORPAY_KEY_SECRET);
    }

    @Override
    @Transactional
    public JSONObject createOrder(Long userId, String planName) throws Exception {
        logger.info("Received request to create order for userId: {} and planName: {}", userId, planName);
        try {
            // Validate user
            Optional<User> userOptional = userRepository.findById(userId);
            if (!userOptional.isPresent()) {
                logger.error("User not found with ID: {}", userId);
                throw new RuntimeException("User not found with ID: " + userId);
            }
            User user = userOptional.get();

            // Validate plan
            Plan plan = planRepository.findByName(planName);
            if (plan == null) {
                logger.error("Plan not found with name: {}", planName);
                throw new RuntimeException("Plan not found with name: " + planName);
            }

            // Update the user's plan
            user.setPlan(plan);
            userRepository.save(user);  // Save user with new plan

            // Create Razorpay order
            JSONObject orderRequest = new JSONObject();
            orderRequest.put("amount", (int) (plan.getPrice() * 100));  // Amount in paisa
            orderRequest.put("currency", CURRENCY);
            orderRequest.put("receipt", "receipt_" + planName);

            Order order = razorpayClient.orders.create(orderRequest);

            // Prepare Payment object
            Payment payment = preparePaymentDetails(order, user, plan);

            // Save Payment record
            paymentRepository.save(payment);

            logger.info("Payment order created successfully with Razorpay orderId: {}", (Object) order.get("id"));
            return order.toJson();
        } catch (Exception e) {
            logger.error("Error occurred while creating order for userId: {} and planName: {}: {}", userId, planName, e.getMessage(), e);
            throw e;
        }
    }

    // Method to prepare Payment object from Razorpay order details
    private Payment preparePaymentDetails(Order order, User user, Plan plan) {
        try {
            String orderId = order.get("id").toString();
            Integer amount = order.get("amount");
            String currency = order.get("currency").toString();

            // Create and return Payment object
            Payment payment = new Payment();
            payment.setRazorpayOrderId(orderId);
            payment.setPaymentStatus("PENDING");
            payment.setAmount(amount / 100.0); // Convert back to INR
            payment.setCurrency(currency);
            payment.setUser(user);
            payment.setPlan(plan);

            return payment;

        } catch (Exception e) {
            logger.error("Error while preparing payment details: {}", e.getMessage(), e);
            throw new RuntimeException("Error while preparing payment details", e);
        }
    }


    // Method to verify payment from Razorpay
    @Override
    public Payment verifyPayment(String razorpayPaymentId, String razorpayOrderId, String razorpaySignature) throws Exception {
        logger.info("Received request to verify payment for razorpayPaymentId: {} and razorpayOrderId: {}", razorpayPaymentId, razorpayOrderId);
        try {
            // Verify Razorpay signature
            String payload = razorpayOrderId + "|" + razorpayPaymentId;

            Mac mac = Mac.getInstance("HmacSHA256");
            SecretKeySpec secretKeySpec = new SecretKeySpec(RAZORPAY_KEY_SECRET.getBytes(), "HmacSHA256");
            mac.init(secretKeySpec);
            byte[] hash = mac.doFinal(payload.getBytes());

            String calculatedSignature = new String(Base64.getEncoder().encodeToString(hash));
            if (!calculatedSignature.equals(razorpaySignature)) {
                logger.error("Payment verification failed for razorpayOrderId: {} and razorpayPaymentId: {}", razorpayOrderId, razorpayPaymentId);
                throw new RuntimeException("Payment verification failed.");
            }

            // Fetch the payment record
            Payment payment = paymentRepository.findByRazorpayOrderId(razorpayOrderId);
            if (payment == null) {
                logger.error("Payment not found for Razorpay Order ID: {}", razorpayOrderId);
                throw new RuntimeException("Payment not found with Razorpay Order ID: " + razorpayOrderId);
            }

            // Update payment status
            payment.setRazorpayPaymentId(razorpayPaymentId);
            payment.setPaymentStatus("SUCCESS");
            payment.setPaymentDate(new Date());

            return paymentRepository.save(payment);
        } catch (Exception e) {
            logger.error("Error while verifying payment for razorpayOrderId : {} and razorpayPaymentId : {}", razorpayOrderId, razorpayPaymentId, e.getMessage(), e);
            throw new RuntimeException("Error while verifying payment", e);
        }
    }

    // Method to get payments by plan ID
    @Override
    public List<Payment> getPaymentsByPlanId(Long planId) {
        try {
            return paymentRepository.findPaymentsByPlanId(planId); // Call the repository method to get payments by planId
        } catch (Exception e) {
            logger.error("Error occurred while fetching payments for planId: {}", planId, e);
            throw new RuntimeException("Error occurred while fetching payments", e);
        }
    }
}
