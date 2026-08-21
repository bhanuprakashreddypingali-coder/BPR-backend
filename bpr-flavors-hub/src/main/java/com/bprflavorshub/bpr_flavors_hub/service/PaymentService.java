package com.bprflavorshub.bpr_flavors_hub.service;

import java.util.List;

import com.bprflavorshub.bpr_flavors_hub.dto.PaymentRequest;
import com.bprflavorshub.bpr_flavors_hub.dto.PaymentResponse;

public interface PaymentService {

    PaymentResponse makePayment(
            String phone,
            PaymentRequest request);

    List<PaymentResponse> getMyPayments(
            String phone);

    PaymentResponse getPaymentById(
            String phone,
            Long paymentId);
}