package com.example.demo.repository;

import com.example.demo.entity.CustomerAddress;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CustomerAddressRepository
        extends JpaRepository<CustomerAddress, Integer> {
}