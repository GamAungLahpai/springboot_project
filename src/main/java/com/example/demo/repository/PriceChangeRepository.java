package com.example.demo.repository;

import com.example.demo.entity.PriceChange;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PriceChangeRepository
        extends JpaRepository<PriceChange, Integer> {
}