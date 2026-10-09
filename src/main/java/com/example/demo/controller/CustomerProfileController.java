package com.example.demo.controller;

import com.example.demo.entity.Customer;
import com.example.demo.entity.CustomerProfile;
import com.example.demo.repository.CustomerProfileRepository;
import com.example.demo.repository.CustomerRepository;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/customer-profiles")
public class CustomerProfileController {

    private final CustomerProfileRepository customerProfileRepository;
    private final CustomerRepository customerRepository;

    public CustomerProfileController(
            CustomerProfileRepository customerProfileRepository,
            CustomerRepository customerRepository
    ) {
        this.customerProfileRepository = customerProfileRepository;
        this.customerRepository = customerRepository;
    }

    @PostMapping
    public CustomerProfile createProfile(
            @RequestParam Integer customerId,
            @RequestParam LocalDate dateOfBirth,
            @RequestParam String preferredLanguage
    ) {

        Customer customer = customerRepository.findById(customerId)
                .orElseThrow();

        CustomerProfile profile = new CustomerProfile();

        profile.setCustomer(customer);
        profile.setDateOfBirth(dateOfBirth);
        profile.setPreferredLanguage(preferredLanguage);

        return customerProfileRepository.save(profile);
    }

    @GetMapping("/{id}")
    public CustomerProfile getProfile(@PathVariable Integer id) {
        return customerProfileRepository.findById(id)
                .orElseThrow();
    }
}