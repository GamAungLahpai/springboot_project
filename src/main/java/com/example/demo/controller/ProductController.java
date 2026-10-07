package com.example.demo.controller;

import com.example.demo.entity.PriceChange;
import com.example.demo.entity.Product;
import com.example.demo.entity.ProductCategory;
import com.example.demo.entity.Supplier;
import com.example.demo.repository.PriceChangeRepository;
import com.example.demo.repository.ProductCategoryRepository;
import com.example.demo.repository.ProductRepository;
import com.example.demo.repository.SupplierRepository;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/products")
public class ProductController {

    private final ProductRepository productRepository;
    private final ProductCategoryRepository productCategoryRepository;
    private final SupplierRepository supplierRepository;
    private final PriceChangeRepository priceChangeRepository;

    public ProductController(
            ProductRepository productRepository,
            ProductCategoryRepository productCategoryRepository,
            SupplierRepository supplierRepository,
            PriceChangeRepository priceChangeRepository
    ) {
        this.productRepository = productRepository;
        this.productCategoryRepository = productCategoryRepository;
        this.supplierRepository = supplierRepository;
        this.priceChangeRepository = priceChangeRepository;
    }

    @GetMapping
    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    @PostMapping
    public Product createProduct(
            @RequestParam Integer categoryId,
            @RequestParam Integer supplierId,
            @RequestBody Product product
    ) {
        ProductCategory category = productCategoryRepository
                .findById(categoryId)
                .orElseThrow();

        Supplier supplier = supplierRepository
                .findById(supplierId)
                .orElseThrow();

        product.setCategory(category);
        product.setSupplier(supplier);

        return productRepository.save(product);
    }

    @PatchMapping("/{productId}/price")
    public Product updatePrice(
            @PathVariable Integer productId,
            @RequestParam BigDecimal newPrice
    ) {
        Product product = productRepository.findById(productId)
                .orElseThrow();

        product.setPrice(newPrice);

        return productRepository.save(product);
    }

    @GetMapping("/{productId}/price-history")
    public List<PriceChange> getPriceHistory(
            @PathVariable Integer productId
    ) {
        return priceChangeRepository
                .findByProduct_IdOrderByChangeTimeDesc(productId);
    }
}