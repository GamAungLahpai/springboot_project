package com.example.demo.repository;

import com.example.demo.entity.CompanyCustomer;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CompanyCustomerRepository
        extends JpaRepository<CompanyCustomer, Integer> {
}