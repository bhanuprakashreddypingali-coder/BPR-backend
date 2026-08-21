package com.bprflavorshub.bpr_flavors_hub.service;

import com.bprflavorshub.bpr_flavors_hub.dto.AddressRequest;
import com.bprflavorshub.bpr_flavors_hub.dto.AddressResponse;

import java.util.List;

public interface AddressService {


    AddressResponse addAddress(AddressRequest request);


    List<AddressResponse> getMyAddresses();


    AddressResponse getAddress(Long id);


    AddressResponse updateAddress(Long id, AddressRequest request);


    void deleteAddress(Long id);


    void setDefaultAddress(Long id);

}