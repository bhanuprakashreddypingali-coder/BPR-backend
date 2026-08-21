package com.bprflavorshub.bpr_flavors_hub.dto;

import lombok.Data;

@Data
public class AddressRequest {

    private String fullName;

    private String phone;

    private String houseNo;

    private String street;

    private String city;

    private String state;

    private String pincode;

    private String country;

    private String landmark;

    private Boolean isDefault;
}