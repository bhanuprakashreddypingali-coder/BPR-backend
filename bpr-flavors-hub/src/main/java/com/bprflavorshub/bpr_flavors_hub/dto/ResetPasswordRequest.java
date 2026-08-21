package com.bprflavorshub.bpr_flavors_hub.dto;

public class ResetPasswordRequest {

    private String phone;
    private String newPassword;

    public ResetPasswordRequest() {
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getNewPassword() {
        return newPassword;
    }

    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }
}