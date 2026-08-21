package com.bprflavorshub.bpr_flavors_hub.service.impl;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bprflavorshub.bpr_flavors_hub.dto.PaymentRequest;
import com.bprflavorshub.bpr_flavors_hub.dto.PaymentResponse;
import com.bprflavorshub.bpr_flavors_hub.entity.Order;
import com.bprflavorshub.bpr_flavors_hub.entity.Payment;
import com.bprflavorshub.bpr_flavors_hub.entity.User;
import com.bprflavorshub.bpr_flavors_hub.repository.OrderRepository;
import com.bprflavorshub.bpr_flavors_hub.repository.PaymentRepository;
import com.bprflavorshub.bpr_flavors_hub.repository.UserRepository;
import com.bprflavorshub.bpr_flavors_hub.service.PaymentService;

@Service
@Transactional
public class PaymentServiceImpl
        implements PaymentService {

    private final PaymentRepository paymentRepository;
    private final OrderRepository orderRepository;
    private final UserRepository userRepository;

    public PaymentServiceImpl(
            PaymentRepository paymentRepository,
            OrderRepository orderRepository,
            UserRepository userRepository) {

        this.paymentRepository = paymentRepository;
        this.orderRepository = orderRepository;
        this.userRepository = userRepository;
    }

    // =========================================================
    // MAKE PAYMENT
    // =========================================================

    @Override
    public PaymentResponse makePayment(
            String phone,
            PaymentRequest request) {

        User user = getUserByPhone(phone);

        if (request == null ||
                request.getOrderId() == null) {

            throw new RuntimeException(
                    "Order ID is required");
        }

        if (request.getPaymentMethod() == null ||
                request.getPaymentMethod()
                        .trim()
                        .isEmpty()) {

            throw new RuntimeException(
                    "Payment method is required");
        }

        Order order = orderRepository
                .findById(request.getOrderId())
                .orElseThrow(() ->
                        new RuntimeException(
                                "Order not found"));

        // -----------------------------------------------------
        // SECURITY: ORDER MUST BELONG TO LOGGED-IN USER
        // -----------------------------------------------------

        if (order.getUserId() == null ||
                !order.getUserId()
                        .equals(user.getId())) {

            throw new RuntimeException(
                    "Unauthorized payment access");
        }

        // -----------------------------------------------------
        // PREVENT DOUBLE PAYMENT
        // -----------------------------------------------------

        if ("PAID".equalsIgnoreCase(
                order.getStatus())) {

            Payment existingPayment =
                    paymentRepository
                            .findByOrderId(
                                    order.getId())
                            .orElse(null);

            if (existingPayment != null) {
                return map(existingPayment);
            }

            throw new RuntimeException(
                    "Order is already paid");
        }

        // -----------------------------------------------------
        // CREATE PAYMENT
        // -----------------------------------------------------

        Payment payment = Payment.builder()
                .orderId(order.getId())
                .userId(user.getId())
                .amount(order.getTotalAmount())
                .paymentMethod(
                        request.getPaymentMethod())
                .paymentStatus("SUCCESS")
                .transactionId(
                        UUID.randomUUID()
                                .toString())
                .build();

        Payment savedPayment =
                paymentRepository.save(payment);

        // -----------------------------------------------------
        // UPDATE ORDER
        // -----------------------------------------------------

        order.setPaymentMethod(
                request.getPaymentMethod());

        order.setStatus("PAID");

        orderRepository.save(order);

        return map(savedPayment);
    }

    // =========================================================
    // GET MY PAYMENTS
    // =========================================================

    @Override
    @Transactional(readOnly = true)
    public List<PaymentResponse> getMyPayments(
            String phone) {

        User user = getUserByPhone(phone);

        return paymentRepository
                .findByUserId(user.getId())
                .stream()
                .map(this::map)
                .collect(Collectors.toList());
    }

    // =========================================================
    // GET PAYMENT BY ID
    // =========================================================

    @Override
    @Transactional(readOnly = true)
    public PaymentResponse getPaymentById(
            String phone,
            Long paymentId) {

        User user = getUserByPhone(phone);

        Payment payment =
                paymentRepository
                        .findById(paymentId)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Payment not found"));

        if (payment.getUserId() == null ||
                !payment.getUserId()
                        .equals(user.getId())) {

            throw new RuntimeException(
                    "Unauthorized payment access");
        }

        return map(payment);
    }

    // =========================================================
    // FIND USER BY PHONE
    // =========================================================

    private User getUserByPhone(String phone) {

        if (phone == null ||
                phone.trim().isEmpty()) {

            throw new RuntimeException(
                    "Authenticated phone number is missing");
        }

        return userRepository
                .findByPhone(phone)
                .orElseThrow(() ->
                        new RuntimeException(
                                "User not found with phone: "
                                        + phone));
    }

    // =========================================================
    // MAP
    // =========================================================

    private PaymentResponse map(
            Payment payment) {

        return PaymentResponse.builder()
                .id(payment.getId())
                .orderId(payment.getOrderId())
                .amount(payment.getAmount())
                .paymentMethod(
                        payment.getPaymentMethod())
                .paymentStatus(
                        payment.getPaymentStatus())
                .transactionId(
                        payment.getTransactionId())
                .build();
    }
}