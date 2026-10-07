package com.example.demo.controller;

import com.example.demo.entity.Supplier;
import com.example.demo.entity.SupplierAddress;
import com.example.demo.repository.SupplierAddressRepository;
import com.example.demo.repository.SupplierRepository;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/suppliers")
public class SupplierController {

    private final SupplierRepository supplierRepository;
    private final SupplierAddressRepository supplierAddressRepository;

    public SupplierController(
            SupplierRepository supplierRepository,
            SupplierAddressRepository supplierAddressRepository
    ) {
        this.supplierRepository = supplierRepository;
        this.supplierAddressRepository = supplierAddressRepository;
    }

    @GetMapping
    public List<Supplier> getAllSuppliers() {
        return supplierRepository.findAll();
    }

    @PostMapping
    public Supplier createSupplier(@RequestBody Supplier supplier) {
        return supplierRepository.save(supplier);
    }

    @PostMapping("/{supplierId}/addresses")
    public SupplierAddress createSupplierAddress(
            @PathVariable Integer supplierId,
            @RequestBody SupplierAddress supplierAddress
    ) {
        Supplier supplier = supplierRepository.findById(supplierId)
                .orElseThrow();

        supplierAddress.setSupplier(supplier);

        return supplierAddressRepository.save(supplierAddress);
    }
}