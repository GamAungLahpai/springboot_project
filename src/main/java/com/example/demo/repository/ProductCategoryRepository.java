package com.example.demo.repository;

import com.example.demo.entity.ProductCategory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductCategoryRepository
        extends JpaRepository<ProductCategory, Integer> {
}