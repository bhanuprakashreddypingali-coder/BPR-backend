package com.bprflavorshub.bpr_flavors_hub.controller;

import com.bprflavorshub.bpr_flavors_hub.dto.AddressRequest;
import com.bprflavorshub.bpr_flavors_hub.dto.AddressResponse;
import com.bprflavorshub.bpr_flavors_hub.service.AddressService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/addresses")
@RequiredArgsConstructor
public class AddressController {

    private final AddressService addressService;

    @PostMapping
    public ResponseEntity<AddressResponse> addAddress(
            @RequestBody AddressRequest request) {

        return new ResponseEntity<>(
                addressService.addAddress(request),
                HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<AddressResponse>> getMyAddresses() {

        return ResponseEntity.ok(
                addressService.getMyAddresses());
    }

    @GetMapping("/{id}")
    public ResponseEntity<AddressResponse> getAddress(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                addressService.getAddress(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<AddressResponse> updateAddress(
            @PathVariable Long id,
            @RequestBody AddressRequest request) {

        return ResponseEntity.ok(
                addressService.updateAddress(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteAddress(
            @PathVariable Long id) {

        addressService.deleteAddress(id);

        return ResponseEntity.ok("Address deleted successfully.");
    }

    @PutMapping("/{id}/default")
    public ResponseEntity<String> setDefaultAddress(
            @PathVariable Long id) {

        addressService.setDefaultAddress(id);

        return ResponseEntity.ok("Default address updated.");
    }
}