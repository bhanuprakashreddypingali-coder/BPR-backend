package com.bprflavorshub.bpr_flavors_hub.service;

import java.util.List;

import com.bprflavorshub.bpr_flavors_hub.dto.CartRequest;
import com.bprflavorshub.bpr_flavors_hub.dto.CartResponse;
import com.bprflavorshub.bpr_flavors_hub.dto.CheckoutRequest;
import com.bprflavorshub.bpr_flavors_hub.dto.CheckoutResponse;

public interface CartService {

    CartResponse addToCart(
            String phone,
            CartRequest request);

    CheckoutResponse checkout(
            String phone,
            CheckoutRequest request);

    List<CartResponse> getCart(
            String phone);

    CartResponse updateQuantity(
            String phone,
            Long cartId,
            Integer quantity);

    void removeItem(
            String phone,
            Long cartId);

    void clearCart(
            String phone);
}