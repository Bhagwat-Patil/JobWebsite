package com.jobwebsite.CommonUtil;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;

public class PaymentUtils {

    private static final String RAZORPAY_KEY_SECRET = "pN79jJ1oP4WrQYc5biDOqXbD";

    public static String generateMockSignature(String razorpayOrderId, String razorpayPaymentId) throws Exception {
        String payload = razorpayOrderId + "|" + razorpayPaymentId;
        Mac mac = Mac.getInstance("HmacSHA256");
        SecretKeySpec secretKeySpec = new SecretKeySpec(RAZORPAY_KEY_SECRET.getBytes(), "HmacSHA256");
        mac.init(secretKeySpec);
        return Base64.getEncoder().encodeToString(mac.doFinal(payload.getBytes()));
    }
}
