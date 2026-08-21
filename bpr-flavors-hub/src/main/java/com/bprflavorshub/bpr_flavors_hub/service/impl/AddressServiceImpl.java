package com.bprflavorshub.bpr_flavors_hub.service.impl;


import com.bprflavorshub.bpr_flavors_hub.dto.AddressRequest;
import com.bprflavorshub.bpr_flavors_hub.dto.AddressResponse;
import com.bprflavorshub.bpr_flavors_hub.entity.Address;
import com.bprflavorshub.bpr_flavors_hub.entity.User;
import com.bprflavorshub.bpr_flavors_hub.repository.AddressRepository;
import com.bprflavorshub.bpr_flavors_hub.repository.UserRepository;
import com.bprflavorshub.bpr_flavors_hub.service.AddressService;

import lombok.RequiredArgsConstructor;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
@RequiredArgsConstructor
public class AddressServiceImpl implements AddressService {


    private final AddressRepository addressRepository;

    private final UserRepository userRepository;



    private User getCurrentUser(){


        String email =
                SecurityContextHolder
                        .getContext()
                        .getAuthentication()
                        .getName();



        return userRepository.findByEmail(email)
                .orElseThrow(
                        () -> new RuntimeException("User not found")
                );

    }





    private AddressResponse mapToResponse(Address address){


        return AddressResponse.builder()

                .id(address.getId())

                .fullName(address.getFullName())

                .phone(address.getPhone())

                .houseNo(address.getHouseNo())

                .street(address.getStreet())

                .city(address.getCity())

                .state(address.getState())

                .pincode(address.getPincode())

                .country(address.getCountry())

                .landmark(address.getLandmark())

                .isDefault(address.getIsDefault())

                .build();

    }





    @Override
    public AddressResponse addAddress(AddressRequest request){


        User user = getCurrentUser();



        if(Boolean.TRUE.equals(request.getIsDefault())){


            List<Address> oldAddresses =
                    addressRepository.findByUserId(user.getId());


            for(Address address : oldAddresses){

                address.setIsDefault(false);

            }


            addressRepository.saveAll(oldAddresses);

        }





        Address address = Address.builder()

                .fullName(request.getFullName())

                .phone(request.getPhone())

                .houseNo(request.getHouseNo())

                .street(request.getStreet())

                .city(request.getCity())

                .state(request.getState())

                .pincode(request.getPincode())

                .country(request.getCountry())

                .landmark(request.getLandmark())

                .isDefault(request.getIsDefault())

                .user(user)

                .build();



        return mapToResponse(
                addressRepository.save(address)
        );

    }







    @Override
    public List<AddressResponse> getMyAddresses(){


        User user = getCurrentUser();


        return addressRepository
                .findByUserIdOrderByIsDefaultDescIdDesc(user.getId())

                .stream()

                .map(this::mapToResponse)

                .toList();

    }







    @Override
    public AddressResponse getAddress(Long id){


        User user = getCurrentUser();



        Address address =
                addressRepository.findById(id)

                .orElseThrow(
                        () -> new RuntimeException("Address not found")
                );



        if(!address.getUser().getId()
                .equals(user.getId())){


            throw new RuntimeException("Unauthorized");

        }



        return mapToResponse(address);

    }








    @Override
    public AddressResponse updateAddress(
            Long id,
            AddressRequest request){



        User user = getCurrentUser();



        Address address =
                addressRepository.findById(id)

                .orElseThrow(
                        () -> new RuntimeException("Address not found")
                );




        if(!address.getUser().getId()
                .equals(user.getId())){


            throw new RuntimeException("Unauthorized");

        }





        if(Boolean.TRUE.equals(request.getIsDefault())){


            List<Address> addresses =
                    addressRepository.findByUserId(user.getId());



            for(Address a : addresses){

                a.setIsDefault(false);

            }


            addressRepository.saveAll(addresses);


            address.setIsDefault(true);

        }




        address.setFullName(request.getFullName());

        address.setPhone(request.getPhone());

        address.setHouseNo(request.getHouseNo());

        address.setStreet(request.getStreet());

        address.setCity(request.getCity());

        address.setState(request.getState());

        address.setPincode(request.getPincode());

        address.setCountry(request.getCountry());

        address.setLandmark(request.getLandmark());




        return mapToResponse(
                addressRepository.save(address)
        );

    }







    @Override
    public void deleteAddress(Long id){


        User user = getCurrentUser();



        Address address =
                addressRepository.findById(id)

                .orElseThrow(
                        () -> new RuntimeException("Address not found")
                );



        if(!address.getUser().getId()
                .equals(user.getId())){


            throw new RuntimeException("Unauthorized");

        }



        addressRepository.delete(address);

    }









    @Override
    public void setDefaultAddress(Long id){



        User user = getCurrentUser();



        List<Address> addresses =
                addressRepository.findByUserId(user.getId());



        for(Address address : addresses){


            address.setIsDefault(
                    address.getId().equals(id)
            );

        }



        addressRepository.saveAll(addresses);


    }


}