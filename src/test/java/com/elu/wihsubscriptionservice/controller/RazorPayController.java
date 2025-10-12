package com.elu.wihsubscriptionservice.controller;

import com.elu.wihsubscriptionservice.dto.PaymentRequest;
import com.elu.wihsubscriptionservice.dto.PaymentResponse;
import com.elu.wihsubscriptionservice.service.RazorPayService;
import lombok.AllArgsConstructor;
import org.junit.jupiter.api.Order;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.util.Map;

@RestController
@RequestMapping("/payment/razorpay")
@CrossOrigin(origins = "http://localhost:5173")
public class RazorPayController {


    private RazorPayService razorPayService;

    public RazorPayController(RazorPayService razorPayService) {
        this.razorPayService = razorPayService;
    }

    @Value("${razorpat.api.secret}")
    private String RAZORPAY_SECRET;

    @GetMapping("/test")
    public String test() {
        return "test";
    }

    @PostMapping("/create-order")
    public ResponseEntity<PaymentResponse> createOrder(@RequestBody PaymentRequest request) {
        System.out.println("RazorPayController.createOrder");
        try {
            String order = razorPayService.createOrder(
                    request.amount,
                    request.currency,
                    request.receipt_id);
            PaymentResponse response = PaymentResponse.builder()
                    .success(true)
                    .message("Payment was successful")
                    .code("200")
                    .userExist(true)
                    .status("success")
                    .order(order)
                    .build();
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            PaymentResponse response = PaymentResponse.builder()
                    .status("failed")
                    .userExist(false)
                    .code("400")
                    .message("Payment was not complete")
                    .success(false).build();
            return ResponseEntity.ok(response);
        }
    }

    @PostMapping("/verify")
    public String verifyPayment(@RequestBody Map<String, String> data) {
        String orderId = data.get("razorpay_order_id");
        String paymentId = data.get("razorpay_payment_id");
        String signature = data.get("razorpay_signature");

        try {
            String generatedSignature = generateSignature(orderId, paymentId, RAZORPAY_SECRET);

            if (generatedSignature.equals(signature)) {
                // ✅ Payment verified successfully
                System.out.println("✅ Payment verified successfully for Order ID: " + orderId);
                return "Payment verified successfully!";
            } else {
                // ❌ Signature mismatch
                System.out.println("❌ Payment verification failed. Signature mismatch.");
                return "Payment verification failed!";
            }
        } catch (Exception e) {
            e.printStackTrace();
            return "Error verifying payment!";
        }
    }

    // -------------------- HELPER FUNCTION --------------------
    private String generateSignature(String orderId, String paymentId, String secret) throws Exception {
        String payload = orderId + "|" + paymentId;

        Mac mac = Mac.getInstance("HmacSHA256");
        SecretKeySpec secretKey = new SecretKeySpec(secret.getBytes(), "HmacSHA256");
        mac.init(secretKey);

        byte[] hash = mac.doFinal(payload.getBytes());
        return new String(org.apache.commons.codec.binary.Hex.encodeHex(hash));
    }

}
