package com.elu.wihsubscriptionservice.controller;

import com.elu.wihsubscriptionservice.dto.PaymentRequest;
import com.elu.wihsubscriptionservice.dto.PaymentResponse;
import com.elu.wihsubscriptionservice.dto.RequestDto;
import com.elu.wihsubscriptionservice.dto.ResponseDto;
import com.elu.wihsubscriptionservice.openFeign.UserClient;
import com.elu.wihsubscriptionservice.openFeign.dto.UserRequestDto;
import com.elu.wihsubscriptionservice.openFeign.dto.UserResponseDto;
import com.elu.wihsubscriptionservice.publisher.EmailPublisher;
import com.elu.wihsubscriptionservice.service.PaymentService;
import com.elu.wihsubscriptionservice.service.RazorPayService;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.util.Map;

@RestController
@RequestMapping("/payment/razorpay")
@CrossOrigin(origins = "http://localhost:5173")
public class RazorPayController {


    private final EmailPublisher emailPublisher;
    private RazorPayService razorPayService;
    private PaymentService paymentService;
    private UserClient  userClient;

    public RazorPayController(RazorPayService razorPayService,
                              PaymentService paymentService,
                              EmailPublisher emailPublisher,
                              UserClient userClient) {
        this.razorPayService = razorPayService;
        this.paymentService = paymentService;
        this.emailPublisher = emailPublisher;
        this.userClient = userClient;
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
            JSONObject razorpayOrder = new JSONObject();
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
                    .order(razorpayOrder.getString("id"))
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

        System.out.println("THE INCOMING DATAS ARE " + orderId + " " + paymentId + " " + signature);

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

    @PostMapping("/success")
    public ResponseEntity<ResponseDto> successPayment(@RequestBody RequestDto request) {
        System.out.println("RazorPayController.successPayment");
        try {
            emailPublisher.sendEmail(request.getEmail(), request.getTransactionId(), request.getAmount());
            UserRequestDto responseDto = UserRequestDto.builder()
                    .email(request.getEmail()).build();
            userClient.registerUser(responseDto);
            return paymentService.savePaymentDetails(request);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping("/failure")
    public ResponseEntity<ResponseDto> failurePayment() {
        try {
            ResponseDto responseDto = ResponseDto.builder()
                    .status("failed")
                    .message("Payment was not complete")
                    .code("400")
                    .email(null)
                    .transactionId(null)
                    .success(false)
                    .userExist(false)
                    .build();
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(responseDto);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

}
