package com.example.demo.repository;

import com.example.demo.entity.SupplierAddress;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SupplierAddressRepository
        extends JpaRepository<SupplierAddress, Integer> {
}