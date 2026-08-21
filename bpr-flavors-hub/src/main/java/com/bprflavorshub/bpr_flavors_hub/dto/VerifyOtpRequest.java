package com.bprflavorshub.bpr_flavors_hub.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class VerifyOtpRequest {

    private String phone;

    private String otp;

}