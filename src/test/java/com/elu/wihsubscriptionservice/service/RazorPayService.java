package com.elu.wihsubscriptionservice.service;

import com.razorpay.Order;
import com.razorpay.RazorpayClient;
import com.razorpay.RazorpayException;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class RazorPayService {

    @Value("${razorpay.api.keyid}")
    private String RAZORPAY_API_KEYID;

    @Value("${razorpat.api.secret}")
    private String RAZORPAY_API_SECRET;

    public String createOrder(int amount, String currency, String receiptId) throws RazorpayException, JSONException {
        RazorpayClient client = new RazorpayClient(RAZORPAY_API_KEYID, RAZORPAY_API_SECRET);

        JSONObject razorpayOrder = new JSONObject();
        razorpayOrder.put("amount", amount);
        razorpayOrder.put("currency", "INR");
        razorpayOrder.put("receipt_id", receiptId);

        Order order = client.orders.create(razorpayOrder);

        return order.toJson().toString();
    }

}
