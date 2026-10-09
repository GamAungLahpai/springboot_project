package com.example.demo.controller;

import com.example.demo.entity.ProductCategory;
import com.example.demo.repository.ProductCategoryRepository;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/product-categories")
public class ProductCategoryController {

    private final ProductCategoryRepository productCategoryRepository;

    public ProductCategoryController(
            ProductCategoryRepository productCategoryRepository
    ) {
        this.productCategoryRepository = productCategoryRepository;
    }

    @GetMapping
    public List<ProductCategory> getAllCategories() {
        return productCategoryRepository.findAll();
    }

    @PostMapping
    public ProductCategory createCategory(
            @RequestBody ProductCategory productCategory
    ) {
        return productCategoryRepository.save(productCategory);
    }
}