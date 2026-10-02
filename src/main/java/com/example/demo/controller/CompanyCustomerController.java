package com.example.demo.controller;


import com.example.demo.entity.CompanyCustomer;
import com.example.demo.repository.CompanyCustomerRepository;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/company-customers")
public class CompanyCustomerController {
    private final CompanyCustomerRepository companyCustomerRepository;

    public CompanyCustomerController(
            CompanyCustomerRepository companyCustomerRepository
    ) {
        this.companyCustomerRepository = companyCustomerRepository;
    }

    @PostMapping
    public CompanyCustomer createCompanyCustomer(
            @RequestBody CompanyCustomer companyCustomer
    ){
        return companyCustomerRepository.save(companyCustomer);
    }
}
