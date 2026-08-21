package com.bprflavorshub.bpr_flavors_hub.entity;


import jakarta.persistence.*;
import lombok.*;


@Entity
@Table(name = "addresses")

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class Address {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    private String fullName;


    private String phone;


    private String houseNo;


    private String street;


    private String city;


    private String state;


    private String pincode;


    private String country;


    private String landmark;



    // Mapping with existing MySQL column
    @Column(name = "default_address")
    private Boolean isDefault = false;



    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;


}