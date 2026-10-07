package com.example.demo.repository;

import com.example.demo.entity.PriceChange;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PriceChangeRepository
        extends JpaRepository<PriceChange, Integer> {

    List<PriceChange> findByProduct_IdOrderByChangeTimeDesc(Integer productId);
}